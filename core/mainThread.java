package core;

//Java real time strategy 

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

import enemyAI.*;
import gui.*;

public class mainThread extends JFrame implements KeyListener, ActionListener, MouseMotionListener, MouseListener, FocusListener{

	public static int[] screen;
	public static int[] screen2;
	public static int[] zBuffer;
	public static int[] zBuffer2;
	public static BufferedImage doubleBuffer;
	public static BufferedImage doubleBuffer2;
	public static BufferedImage bf;
	public static Ticker t;
	public static int frameInterval;
	public static int frameIndex;
	public static int gameFrame;
	public static long lastDraw;
	public static int sleepTime;
	public static int framePerSecond, cpuUsage;
	public static double thisTime, lastTime;
	public static boolean JavaRTSLoaded;
	public static boolean gamePaused, gameStarted, gameEnded;
	public static texture[] textures;
	public static byte[][] lightMapTextures;
	public static int[][] lightMapTexturesInfo;
	public static camera Camera;
	public static playerCommander pc;
	public static enemyCommander ec;
	public static AssetManager theAssetManager;
	public static grid gridMap;
	public static postProcessingThread PPT;
	public static Object PPT_Lock;
	public static JPanel panel;
	public static Turn2DTo3DFactory my2Dto3DFactory;
	public static byte[] shadowBitmap;
	public static byte[] shadowBitmap2;
	
	public static short[] displacementBuffer;
	public static short[] displacementBuffer2;
	
	public static boolean leftMouseButtonReleased, escapeKeyPressed;
	public static String buttonAction; 
	public static int menuStatus = 0;
	public static final int mainMenu = 0;
	public static final int difficulitySelectionMenu = 1;
	public static final int helpMenu = 2;
	public static final int endGameMenu = 3;
	
	public static String timeString;
	public static boolean fogOfWarDisabled;
	
	public mainThread(){
		setTitle("Battle Tank 3");
		panel= (JPanel) this.getContentPane();
		panel.setPreferredSize(new Dimension(768, 512));
		panel.setMinimumSize(new Dimension(768,512));
		panel.setLayout(null);     
		
		setResizable(false); 
		pack();
		setVisible(true);
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
    	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	
		//create screen buffer
		doubleBuffer =  new BufferedImage(768, 512, BufferedImage.TYPE_INT_RGB);
		DataBuffer dest = doubleBuffer.getRaster().getDataBuffer();
		screen = ((DataBufferInt)dest).getData();
		
		doubleBuffer2 =  new BufferedImage(768, 512, BufferedImage.TYPE_INT_RGB);
		DataBuffer dest2 = doubleBuffer2.getRaster().getDataBuffer();
		screen2 = ((DataBufferInt)dest2).getData();
		
		//create depth buffer
		zBuffer = new int[393216];
		zBuffer2 = new int[393216];
		
		//create shadoow bitmap
		shadowBitmap = new byte[393216];
		shadowBitmap2 = new byte[393216];
		
		for(int i = 0; i < 393216; i++){
			shadowBitmap[i] = 127;
			shadowBitmap2[i] = 127;
		}
		
		//create displacement buffer
		displacementBuffer = new short[393216];
		displacementBuffer2 = new short[393216];
		for(int i = 0; i < 393216; i++){
			displacementBuffer[i] = 12345;
			
		}
		
		frameIndex = 0;
		frameInterval = 28;
		lastDraw = 0;
	
		//create main thread
		t = new Ticker(0);
		t.addActionListener(this);
		
		//create a daemon thread which will sleep for the duration of the game
		Thread   dt   =   new   Thread(new   DaemonThread() );
        dt.setDaemon(true);
      
        
        //create another thread to create post processing effects
        PPT_Lock = new Object();
        PPT = new postProcessingThread();
		Thread theTread = new Thread(PPT);
		
		
		
		//start threads
		t.start();
		dt.start(); 
		theTread.start();
	
  
	}
	
	
	//This method is called every time the ticker ticks. To take advantage of modern multicore cpu, 
	//The graphic engine does polygon rasterization on the main thread and post processing stuff (explosion, 
	//smokes, user interface etc...) on a second thread. One draw back is that the post processing 
	//thread is always lag the main thread by 1 frame. However it is barely noticeable.
	
	public void actionPerformed(ActionEvent e){	
		if(frameIndex == 0) {
			
			//Add key handler
			panel.addKeyListener(this);
			panel.addMouseMotionListener(this);
			panel.addMouseListener(this);
			panel.addFocusListener(this);
			panel.requestFocus();
			
			//create camera
			Camera = new camera(new vector(3,2f,-1.25f), 0, 300);
		
			//Create look up tables
			gameData.makeData();
			
			//init grid 
			gridMap = new grid(128);
			
			//init light source
			sunLight.init();
			
			//init rasterizer
			rasterizer.init();
			
			//init 2d to 3d factory
			my2Dto3DFactory = new Turn2DTo3DFactory();
			my2Dto3DFactory.init();
	       
			loadTexture();
				
			theAssetManager = new AssetManager();
			theAssetManager.init();
		}
		
		frameIndex++;		
		
		inputHandler.processInput();
		
		if(!gamePaused) {
			if(gameStarted)
				gameFrame++;
			
			timeString = secondsToString((int)(gameFrame*0.028));
			
			//handle user's interaction with game GUI
			if(gameFrame == 1 && gameStarted){
				theAssetManager.prepareAssetForNewGame();
			}
			
			gridMap.update();
			
			//Clears the z-buffer. All depth values are set to 0.
			clearDepthBuffer();
			
			//update camera
			Camera.update();
			
			//update light source
			sunLight.update();
					
			//update and draw 3D mashes from  game objects
			theAssetManager.updateAndDraw();
			
			if(gameStarted) {
				pc.update();
				ec.update();
			}
		}else {
			
			
		}
		
		//show unpassable obstacle 
		//gridMap.draw();
		
		if(this.getGraphics() != null && PPT!= null){
			//wait for the Post processing Thread if it is still working
			waitForPostProcessingThread();
			
			//prepare resources for the post processing thread
			postProcessingThread.prepareResources();
			
			//Signal  post processing thread that it can proceed
			synchronized(PPT) {
				PPT.notify();
			}
			
			if(frameIndex %2 == 0 && frameIndex > 3){
				bf = doubleBuffer;
				paintComponent(panel.getGraphics());
			}else if(frameIndex != 1 && frameIndex > 3){
				bf = doubleBuffer2;
				paintComponent(panel.getGraphics());
			}
			
			swapResources();
		
			//maintain a constant frame rate
			regulateFramerate();
		}else{
			System.exit(-1);
		}
	}
	
	public void paintComponent(Graphics g){		
		
		//copy the pixel information to the video memory
		Graphics2D g2 =(Graphics2D)bf.getGraphics(); //(Graphics2D)g;
		
		//display polygon count and frame rate
		//g2.setColor(Color.WHITE);
		//g2.drawString("FPS: " + framePerSecond + "   "  +  "Polygons: "  + theAssetManager.polygonCount + "    " + "Thread1 Sleep: " + sleepTime +  "ms    " + "Thread2 Sleep: " + postProcessingThread.sleepTime +  "ms    " , 5, 15);
		
		//copy the screen buffer to video memory
		g.drawImage(bf, 0, 0, this);
	}
	
	
	
	
	public void clearDepthBuffer(){
		zBuffer[0] = 0;
		for(int i = 1; i < 393216; i+=i)
			System.arraycopy(zBuffer, 0, zBuffer, i, 393216 - i >= i ? i : 393216 - i);
	}
	
	
	//read keyboard inputs
	public void keyPressed(KeyEvent e){
		
		if(e.getKeyCode() == KeyEvent.VK_LEFT)
			inputHandler.leftKeyPressed = true;
		else if(e.getKeyCode() == KeyEvent.VK_RIGHT)
			inputHandler.rightKeyPressed = true;
		else if(e.getKeyCode() == KeyEvent.VK_CONTROL)
			inputHandler.controlKeyPressed = true;
		else if(e.getKeyCode() == KeyEvent.VK_ESCAPE)
			inputHandler.escapeKeyPressed = true;
			
		inputHandler.readCharacter(e.getKeyChar());
		
	}

	public void keyReleased(KeyEvent e){
		 if(e.getKeyCode() == KeyEvent.VK_LEFT)
			 inputHandler.leftKeyPressed = false;
		 else if(e.getKeyCode() == KeyEvent.VK_RIGHT)
			 inputHandler.rightKeyPressed = false;
		 else if(e.getKeyCode() == KeyEvent.VK_CONTROL)
			 inputHandler.controlKeyPressed = false;
		 else if(e.getKeyCode() == KeyEvent.VK_ESCAPE) 
				inputHandler.escapeKeyReleased = true;
			
		 inputHandler.handleKeyRelease(e.getKeyChar());
	}


	
	public void keyTyped(KeyEvent e) {
		
	
	}


	public void mouseDragged(MouseEvent e) {
		inputHandler.mouse_x = e.getX();
		inputHandler.mouse_y = e.getY();
		
	}


	public void mouseMoved(MouseEvent e) {
		inputHandler.mouse_x = e.getX();
		inputHandler.mouse_y = e.getY();
	
	}


	@Override
	public void mouseClicked(MouseEvent arg0) {}


	@Override
	public void mouseEntered(MouseEvent arg0) {
		inputHandler.mouseIsInsideScreen = true;
	}


	@Override
	public void mouseExited(MouseEvent arg0) {
		inputHandler.mouseIsInsideScreen = false;
	}


	@Override
	public void mousePressed(MouseEvent e) {
		if(e.getButton() == 1){
			inputHandler.leftMouseButtonPressed = true;
		
		}
		
		if(e.getButton() == 3){
			inputHandler.rightMouseButtonPressed = true;
		}
		
	}


	@Override
	public void mouseReleased(MouseEvent e) {
		if(e.getButton() == 1){
			
			inputHandler.leftMouseButtonReleased = true;
		}
		
		if(e.getButton() == 3){
			inputHandler.rightMouseButtonReleased = true;
		}
		
	}
	
	public void loadTexture(){
		textures = new texture[73];
		String imageFolder = "../images/";
		try{
			textures[0] = new texture("basic", ImageIO.read(getClass().getResource(imageFolder + "1.jpg")), 9, 9);
			textures[1] = new texture("explosion aura", ImageIO.read(getClass().getResource(imageFolder + "2.jpg")), 7, 7);
			textures[2] = new texture("basic", ImageIO.read(getClass().getResource(imageFolder + "3.jpg")), 6, 6);
			textures[3] = new texture("basic", ImageIO.read(getClass().getResource(imageFolder + "4.jpg")), 8, 6);
			textures[4] = new texture("basic", ImageIO.read(getClass().getResource(imageFolder + "5.jpg")), 7, 7);
			textures[5] = new texture("basic", ImageIO.read(getClass().getResource(imageFolder + "6.jpg")), 5, 7);
			textures[6] = new texture("explosion", ImageIO.read(getClass().getResource(imageFolder + "7.jpg")), 8, 8);
			textures[7] = new texture("explosion", ImageIO.read(getClass().getResource(imageFolder + "8.jpg")), 8, 8);
			textures[8] = new texture("explosion", ImageIO.read(getClass().getResource(imageFolder + "9.jpg")), 8, 8);
			textures[9] = new texture("explosion", ImageIO.read(getClass().getResource(imageFolder + "10.jpg")), 8, 8);
			textures[10] = new texture("basic", ImageIO.read(getClass().getResource(imageFolder + "12.jpg")), 6, 6);
			textures[11] = new texture("smoke", ImageIO.read(getClass().getResource(imageFolder + "11.jpg")), 9, 9);
			textures[12] = new texture("basic", ImageIO.read(getClass().getResource(imageFolder + "13.jpg")), 7, 7);
			textures[13] = new texture("basic", ImageIO.read(getClass().getResource(imageFolder + "14.jpg")), 7, 7);
			textures[14] = new texture("basic", ImageIO.read(getClass().getResource(imageFolder + "15.jpg")), 5, 5);
			textures[15] = new texture("basic", ImageIO.read(getClass().getResource(imageFolder + "16.jpg")), 5, 5);
			textures[16] = new texture("basic", ImageIO.read(getClass().getResource(imageFolder + "17.jpg")), 5, 5);
			textures[17] = new texture("basic", ImageIO.read(getClass().getResource(imageFolder + "18.jpg")), 7, 7);
			textures[18] = new texture("basic", ImageIO.read(getClass().getResource(imageFolder + "19.jpg")), 6, 6);
			textures[19] = new texture("basic", ImageIO.read(getClass().getResource(imageFolder + "20.jpg")), 6, 6);
			textures[20] = new texture("basic", ImageIO.read(getClass().getResource(imageFolder + "21.jpg")), 6, 6);
			textures[21] = new texture("basic", ImageIO.read(getClass().getResource(imageFolder + "22.jpg")), 6, 6);
			textures[22] = new texture("basic", ImageIO.read(getClass().getResource(imageFolder + "23.jpg")), 6, 6);
			textures[23] = new texture("basic", ImageIO.read(getClass().getResource(imageFolder + "25.jpg")), 6, 6);
			textures[24] = new texture("basic", ImageIO.read(getClass().getResource(imageFolder + "24.jpg")), 5, 5);
			textures[25] = new texture("solid color", 160 << 16 | 160 << 8 | 160, 5, 5);
			textures[26] = new texture("solid color", 80 << 16 | 80 << 8 | 80, 5, 5);
			textures[27] = new texture("basic", ImageIO.read(getClass().getResource(imageFolder + "26.jpg")), 5, 5);
			textures[28] = new texture("solid color", 173 << 16 | 161 << 8 | 89, 5, 5);
			textures[29] = new texture("solid color", 200 << 16 | 200 << 8 | 200, 5, 5);
			textures[30] = new texture("basic", ImageIO.read(getClass().getResource(imageFolder + "27.jpg")), 8, 8);
			textures[31] = new texture("basic", ImageIO.read(getClass().getResource(imageFolder + "28.jpg")), 6, 6);
			textures[32] = new texture("basic", ImageIO.read(getClass().getResource(imageFolder + "29.jpg")), 6, 6);
			textures[33] = new texture("solid color", 130 << 16 | 130 << 8 | 130, 5, 5);
			textures[34] = new texture("basic", ImageIO.read(getClass().getResource(imageFolder + "30.jpg")), 7, 7);
			textures[35] = new texture("basic", ImageIO.read(getClass().getResource(imageFolder + "31.jpg")), 7, 7);
			textures[36] = new texture("basic", ImageIO.read(getClass().getResource(imageFolder + "32.jpg")), 7, 7);
			textures[37] = new texture("basic", ImageIO.read(getClass().getResource(imageFolder + "33.jpg")), 7, 7);
			textures[38] = new texture("heightmap", ImageIO.read(getClass().getResource(imageFolder + "34.jpg")), 8, 8);
			textures[39] = new texture("basic", ImageIO.read(getClass().getResource(imageFolder + "35.jpg")), 8, 8);
			textures[40] = new texture("basic", ImageIO.read(getClass().getResource(imageFolder + "36.jpg")), 7, 7);
			textures[41] = new texture("basic", ImageIO.read(getClass().getResource(imageFolder + "37.jpg")), 8, 8);
			textures[42] = new texture("basic", ImageIO.read(getClass().getResource(imageFolder + "38.jpg")), 6, 6);
			textures[43] = new texture("basic", ImageIO.read(getClass().getResource(imageFolder + "39.jpg")), 6, 6);
			textures[44] = new texture("basic", ImageIO.read(getClass().getResource(imageFolder + "40.jpg")), 5, 5);
			textures[45] = new texture("solid color", 0 << 16 | 131 << 8 | 243, 5, 5);
			textures[46] = new texture("basic", ImageIO.read(getClass().getResource(imageFolder + "41.jpg")), 5, 5);
			textures[47] = new texture("solid color", 50 << 16 | 50 << 8 | 50, 5, 5);
			textures[48] = new texture("solid color", 149 << 16 | 137 << 8 | 97, 5, 5);
			textures[49] = new texture("basic", ImageIO.read(getClass().getResource(imageFolder + "42.jpg")), 6, 6);
			textures[50] = new texture("basic", ImageIO.read(getClass().getResource(imageFolder + "43.jpg")), 5, 5);
			textures[51] = new texture("basic", ImageIO.read(getClass().getResource(imageFolder + "45.jpg")), 8, 8);
			textures[52] = new texture("basic", ImageIO.read(getClass().getResource(imageFolder + "46.jpg")), 7, 7);
			textures[53] = new texture("solid color", 179 << 16 | 0 << 8 | 0, 5, 5);
			textures[54] = new texture("water", ImageIO.read(getClass().getResource(imageFolder + "51.jpg")), ImageIO.read(getClass().getResource(imageFolder + "90.jpg")), 8, 8);
			textures[55] = new texture("heightmap", ImageIO.read(getClass().getResource(imageFolder + "52.jpg")), 8, 8);
			textures[56] = new texture("basic", ImageIO.read(getClass().getResource(imageFolder + "53.jpg")), 7, 7);
			textures[57] = new texture("heightmap", ImageIO.read(getClass().getResource(imageFolder + "54.jpg")), 8, 8);
			textures[58] = new texture("heightmap", ImageIO.read(getClass().getResource(imageFolder + "55.jpg")), 8, 8);
			textures[59] = new texture("heightmap", ImageIO.read(getClass().getResource(imageFolder + "56.jpg")), 8, 8);
			textures[60] = new texture("basic", ImageIO.read(getClass().getResource(imageFolder + "61.jpg")), 4, 7);
			textures[61] = new texture("basic", ImageIO.read(getClass().getResource(imageFolder + "62.jpg")), 7, 7);
			textures[62] = new texture("basic", ImageIO.read(getClass().getResource(imageFolder + "63.jpg")), 7, 7);
			textures[63] = new texture("basic", ImageIO.read(getClass().getResource(imageFolder + "64.jpg")), 5, 7);
			textures[64] = new texture("solid color", 56 << 16 |79 << 8 | 167, 5, 5);
			textures[65] = new texture("basic", ImageIO.read(getClass().getResource(imageFolder + "68.jpg")), 8, 8);
			textures[66] = new texture("basic", ImageIO.read(getClass().getResource(imageFolder + "73.jpg")), 6, 6);
			textures[67] = new texture("basic", ImageIO.read(getClass().getResource(imageFolder + "74.jpg")), 6, 6);
			textures[68] = new texture("solid color", 255 << 16 | 255 << 8 | 255, 5, 5);
			textures[69] = new texture("solid color", 255 << 16 | 0 << 8 | 0, 5, 5);
			textures[70] = new texture("basic", ImageIO.read(getClass().getResource(imageFolder + "80.jpg")), 5, 5);
			textures[71] = new texture("basic", ImageIO.read(getClass().getResource(imageFolder + "82.jpg")), 6, 6);
			textures[72] = new texture("solid color", 120 << 16 | 120 << 8 | 100, 5, 5);
		
			for(int i = 0; i < textures.length; i++)
				textures[i].ID = i;
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
	}
	
	public void waitForPostProcessingThread(){
		//wait till post processing thread finishes
		synchronized(PPT_Lock) {
			while(PPT.isWorking()){
				
				try {
					PPT_Lock.wait();
					
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
	}
	
	
	public void swapResources(){
		int[] s;
		s = screen;
		screen = screen2;
		screen2 = s;
		
		int[] buffer;
		buffer = zBuffer;
		zBuffer = zBuffer2;
		zBuffer2 = buffer;
		
		byte[] b;
		b = shadowBitmap;
		shadowBitmap = shadowBitmap2;
		shadowBitmap2 = b;
		
		short[] Dbuffer;
		Dbuffer = displacementBuffer;
		displacementBuffer = displacementBuffer2;
		displacementBuffer2 = Dbuffer;
	
		rasterizer.screen = mainThread.screen;
		rasterizer.shadowBitmap = mainThread.shadowBitmap;
		rasterizer.zBuffer = mainThread.zBuffer;
		rasterizer.displacementBuffer = mainThread.displacementBuffer;
		
		theAssetManager.swapResources();
		if(gameStarted)
			pc.theSideBarManager.swapResources();
		
	}
	
	public void regulateFramerate(){
		if(frameIndex%35==0){
			double thisTime = System.currentTimeMillis();
			framePerSecond = (int)(1000/((thisTime - lastTime)/35));
			lastTime = thisTime;
		}


		sleepTime = 0; 
		while(System.currentTimeMillis()-lastDraw<frameInterval){

			try {
				Thread.sleep(1);
				sleepTime++;
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
		
		
		lastDraw=System.currentTimeMillis();
	}
	
	public static String secondsToString(int pTime) {
	    int min = pTime/60;
	    int sec = pTime-(min*60);

	    String strMin = placeZeroIfNeede(min);
	    String strSec = placeZeroIfNeede(sec);
	    return String.format("%s:%s",strMin,strSec);
	}

	public static String placeZeroIfNeede(int number) {
	    return (number >=10)? Integer.toString(number):String.format("0%s",Integer.toString(number));
	}

	@Override
	public void focusGained(FocusEvent arg0) {
		// TODO Auto-generated method stub
		
		
		
	}


	@Override
	public void focusLost(FocusEvent arg0) {
		// TODO Auto-generated method stub
		
		
	}

}

