package audio;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;

//Small non-blocking sound engine on top of javax.sound.sampled. Every effect keeps a
//pool of Clips so overlapping triggers (e.g. several units firing the same frame)
//don't cut each other off. play() never blocks the caller - Clip playback runs on the
//Java Sound mixer thread.
public class SoundEngine {

	public enum Sfx {
		CLICK, SELECT, MOVE, ATTACK, HARVEST,
		SHOOT_CANNON, SHOOT_CANNON_HEAVY, SHOOT_AUTOCANNON,
		SHOOT_ROCKET, SHOOT_MISSILE, SHOOT_RAILGUN,
		EXPLOSION_SMALL, EXPLOSION_LARGE,
		DEPLOY_BUILDING, DEPLOY_VEHICLE,
		VICTORY, DEFEAT,
		CREDIT_EARN, CREDIT_SPEND
	}

	public static volatile boolean enabled = true;

	private static final int POOL_SIZE = 4;
	private static final AudioFormat FORMAT = new AudioFormat(SoundSynth.SAMPLE_RATE, 16, 1, true, false);

	private static final Clip[][] pools = new Clip[Sfx.values().length][];
	private static final int[] nextSlot = new int[Sfx.values().length];
	private static boolean initialized;

	public static synchronized void init() {
		if (initialized)
			return;
		initialized = true;

		load(Sfx.CLICK, SoundSynth.click());
		load(Sfx.SELECT, SoundSynth.select());
		load(Sfx.MOVE, SoundSynth.move());
		load(Sfx.ATTACK, SoundSynth.attack());
		load(Sfx.HARVEST, SoundSynth.harvest());
		load(Sfx.SHOOT_CANNON, SoundSynth.shootCannon());
		load(Sfx.SHOOT_CANNON_HEAVY, SoundSynth.shootCannonHeavy());
		load(Sfx.SHOOT_AUTOCANNON, SoundSynth.shootAutocannon());
		load(Sfx.SHOOT_ROCKET, SoundSynth.shootRocket());
		load(Sfx.SHOOT_MISSILE, SoundSynth.shootMissile());
		load(Sfx.SHOOT_RAILGUN, SoundSynth.shootRailgun());
		load(Sfx.EXPLOSION_SMALL, SoundSynth.explosionSmall());
		load(Sfx.EXPLOSION_LARGE, SoundSynth.explosionLarge());
		load(Sfx.DEPLOY_BUILDING, SoundSynth.deployBuilding());
		load(Sfx.DEPLOY_VEHICLE, SoundSynth.deployVehicle());
		load(Sfx.VICTORY, SoundSynth.victory());
		load(Sfx.DEFEAT, SoundSynth.defeat());
		load(Sfx.CREDIT_EARN, SoundSynth.creditEarn());
		load(Sfx.CREDIT_SPEND, SoundSynth.creditSpend());
	}

	private static void load(Sfx sfx, byte[] pcm) {
		Clip[] clips = new Clip[POOL_SIZE];
		for (int i = 0; i < POOL_SIZE; i++) {
			try {
				Clip clip = AudioSystem.getClip();
				clip.open(FORMAT, pcm, 0, pcm.length);
				clips[i] = clip;
			} catch (LineUnavailableException e) {
				clips[i] = null;
			}
		}
		pools[sfx.ordinal()] = clips;
	}

	public static void play(Sfx sfx) {
		play(sfx, 1f);
	}

	public static void play(Sfx sfx, float volume) {
		if (!enabled || !initialized)
			return;

		Clip[] clips = pools[sfx.ordinal()];
		if (clips == null)
			return;

		int slot;
		synchronized (nextSlot) {
			slot = nextSlot[sfx.ordinal()];
			nextSlot[sfx.ordinal()] = (slot + 1) % clips.length;
		}

		Clip clip = clips[slot];
		if (clip == null)
			return;

		synchronized (clip) {
			if (clip.isRunning())
				clip.stop();
			clip.setFramePosition(0);
			applyVolume(clip, volume);
			clip.start();
		}
	}

	private static void applyVolume(Clip clip, float volume) {
		if (!clip.isControlSupported(FloatControl.Type.MASTER_GAIN))
			return;
		FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
		float clamped = Math.max(0.0001f, Math.min(1f, volume));
		float dB = (float) (Math.log10(clamped) * 20.0);
		dB = Math.max(gainControl.getMinimum(), Math.min(gainControl.getMaximum(), dB));
		gainControl.setValue(dB);
	}
}
