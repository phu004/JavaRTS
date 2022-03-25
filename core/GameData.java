package core;

import java.awt.Image;
import java.awt.image.PixelGrabber;

import javax.imageio.ImageIO;

//Store useful arithmetic data for the game engine such as 
//Cos/Sin look up table, color palette, etc...
public class gameData {
	public static int[] random;
	public static int randomIndex;
	public static float[] sin;  
	public static float[] cos;
	public static int[][] colorTable, colorTableTemp;
	public static float[] intensityTable;
	public static int[][] size;
	public static byte[][] cloakTextures;
	
	public static String imageFolder = "../images/";
	

	
	
	public static void makeData(){
		
		//Make random number table
		random = new int[1024];
		for(int i = 0; i < 1024; i ++){
			random[i] = (int)(Math.random()*1024);
		
		}
		
		
		//Make sin and cos look up tables
		sin = new float[361];
		cos = new float[361];
		for(int i = 0; i < 361; i ++){
			sin[i] = (float)Math.sin(Math.PI*i/180);
			cos[i] = (float)Math.cos(Math.PI*i/180);
		}
		
		//make color palette.
		//The main color palette has 32768 (15bits) different colors with 128 different intensity levels, 
		//the default intensity is at level 31 .
		
		if(colorTable == null)
			colorTable = new int[128][32768];
		if(colorTableTemp == null)
			colorTableTemp = new int[32768][128];
		
		intensityTable = new float[128];
		
		double r, g, b, dr, dg, db;
		int r_, g_, b_;
		
		for(int i = 0; i < 32768; i++){
			r = (double)((i & 31744) >> 10)*8;
			g = (double)((i & 992) >> 5)*8;
			b = (double)((i & 31))*8;
			
			dr = r*0.75/64;
			dg = g*0.75/64;
			db = b*0.75/64;
			
			
			
			//calculated the intensity from lvl 0 ~ 63
		    for(int j = 0; j < 64; j++){
				r_ = (int)(r-dr*j);
				g_ = (int)(g-dg*j);
				b_ = (int)(b-db*j);
				
				
				colorTableTemp[i][63 - j] = b_ + (g_<<8)+ (r_<<16);
				intensityTable[63 - j] = 1 - (0.75f/64)*j;
			}
		    
			
		    
		    dr = r*0.75/64;
			dg = g*0.75/64;
			db = b*0.75/64;
			
		
		    
			double d = (dr + dg + db)/3;
			
		    //calculated the intensity from lvl 64 ~ 127
		    for(int j = 1; j <= 64; j++){
				r_ = (int)(r+d*j);
				g_ = (int)(g+d*j);
				b_ = (int)(b+d*j);
				if(r_ > 255)
					r_ = 255;
				if(g_ > 255)
					g_ = 255;
				if(b_ > 255)
					b_ = 255;
			
				
				colorTableTemp[i][63 + j] = b_ + (g_<<8)+ (r_<<16);
				intensityTable[63 + j] = 1 + (0.75f/64)*j;
			}
		}
		
		for(int i = 0; i < 128; i++){
			for(int j = 0; j <32768; j++ )
				colorTable[i][j] = colorTableTemp[j][i];
		}
		
		//free memory used for creating color table
		colorTableTemp = null;
		
		
		//create particle bitmap
		size = new int[9][];
		//size[0] = new int[]{0,-1, -768};     
		//size[1] = new int[]{-769,0,-1, -768};
		//size[2] = new int[]{1, 0,-1, -768,768};
		//size[3] = new int[]{-769,-767,1, 0,-1, -768,768};
		//size[4] = new int[]{-769,-767,1, 0,-1, -768,768, 767, 769};
		//size[5] = new int[]{-1536, -1537, -770,-769,-768, -767, -2, -1, 0, 1, 767, 768};
		//size[6] = new int[]{-1537, -1535, -770,-766,766, 770,1535, 1537,-1536,-769,-2,-767,1, 2, 0,-1, -768,768, 767, 769, 1536};
		//size[7] = new int[]{-1534, -1538, 1538, 1534, -2304, 2304, -3, 3, -1537, -1535, -770,-766,766, 770,1535, 1537,-1536,-769,-2,-767,1, 2, 0,-1, -768,768, 767, 769, 1536};
		//size[8] = new int[]{0};
		
		int w = mainThread.screen_width;
		
		size[0] = new int[]{0,-1, -w};  
		size[1] = new int[]{-(w+1), 0, -1, -w};
		size[2] = new int[]{1, 0,-1, -w, w};
		size[3] = new int[]{-(w+1),-(w-1),1, 0,-1, -w,w};
		size[4] = new int[]{-(w+1),-(w-1),1, 0,-1, -w,w, (w-1), (w+1)};
		size[5] = new int[]{-(w*2), -(w*2+1), -(w+2),-(w+1),-w, -(w-1), -2, -1, 0, 1, w-1, w};
		size[6] = new int[]{-(w*2 + 1), -(w*2-1), -(w+2),-(w-2),w-2, w+2,w*2-1, w*2+1,-(w*2),-(w+1),-2,-(w-1),1, 2, 0,-1, -w,w, w-1, w+1, w*2};
		size[7] = new int[]{-(w*2-2), -(w*2+2), w*2+2, w*2-2, -(w*3), w*3, -3, 3, -(w*2+1), -(w*2-1), -(w+2),-(w-2),w-2, w+2,w*2-1, w*2+1,-(w*2),-(w+1),-2,-(w-1),1, 2, 0,-1, -w,w, w-1, w+1, w*2};
		size[8] = new int[]{0};
		
		
		//create cloack textures
		cloakTextures= new byte[120][64*64];
		
		int[] buffer = new int[64*64];
		
		loadTexture("69.jpg", buffer, cloakTextures[0], 64, 64);

		loadTexture("70.jpg", buffer, cloakTextures[40], 64, 64);
		
		loadTexture("71.jpg", buffer, cloakTextures[80], 64, 64);
		
		for(int i = 1; i < 40; i++){
			for(int j = 0; j < 64*64; j++){
				cloakTextures[i][j] = (byte)(cloakTextures[0][j] + (cloakTextures[40][j] - cloakTextures[0][j])* i / 40);
				cloakTextures[40 + i][j] = (byte)(cloakTextures[40][j] + (cloakTextures[80][j] - cloakTextures[40][j])* i / 40);
				cloakTextures[80 + i][j] = (byte)(cloakTextures[80][j] + (cloakTextures[0][j] - cloakTextures[80][j])* i / 40);
			}
		}
		
		
		
		
		System.gc();
		
	}
	
	//get a random number
	public static int getRandom(){
		randomIndex++;
		if(randomIndex >= 1024)
			randomIndex=0;
		return random[randomIndex];
		
	}
	
	
	
	//It frees the data stored when the applet is finished
	public static void destory(){
		random = null;
		sin = null;  
		cos = null;
		colorTable = null;
	}
	
	public static void loadTexture(String imgName, int[] buffer, byte[] dest, int width, int height){
		Image img = null;
		try{
			img = ImageIO.read(gameData.class.getResource(imageFolder + imgName));
		}catch(Exception e){
			e.printStackTrace();
		}
		

		PixelGrabber pg = new PixelGrabber(img, 0, 0, width, height, buffer, 0, width);
		try {
			pg.grabPixels();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		for(int i = 0; i < buffer.length; i++){
			dest[i] = (byte)((buffer[i]&255)/2);
			
		}
	
	}
	

}
