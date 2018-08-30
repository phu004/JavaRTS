package core;

//the texture dimension should only be power of 2
import java.awt.image.PixelGrabber;
import java.awt.*;

public class texture {
	//holds the pixel data in 16bits color format
	public static int[] textureBuffer;
	
	//hold mipmaps of the original texture
	public short[] pixelData;
	
	public byte[] pixelDataByte;
	
	//stores a sequence of explosion texture
	public int[][] explosions;
	
	//stores a sequence of smoke texture
	public int[][] smoke;
	
	//store height map
	public int[] heightmap;
	
	//stores animated light maps created by an explosion 
	public short[][] explosionAura;
	
	//store displacement map
	public short[] displacementMap;
	
	//store height map that associated with water texture
	public byte[] waterHeightMap;
	public byte[][] waterHeightMaps;
	
	
	//store information that determine water surface movement direction (can be either up or down)
	public boolean[] waterSurfaceDirections;
	
	//dimension of the texture
	public int height, width, heightMask, widthMask, widthBits, heightBits;
	
	public String type;
	
	public int ID;
	
	
	
	
	

	//produce texture based on input image
	public texture(String type, Image img, int widthBits , int heightBits){
		this.widthBits = widthBits;
		this.heightBits = heightBits;
		
		height = (int)Math.pow(2, heightBits);
		width = (int)Math.pow(2, widthBits);
		
		heightMask = height -1;
		widthMask = width - 1;
		
		this.type = type;
		
		
		if(textureBuffer == null)
			textureBuffer = new int[1024*1024];  //support a max texture size of 1024 * 1024
		
		//load texture image and store it as an array of int
		PixelGrabber pg = new PixelGrabber(img, 0, 0, width, height, textureBuffer, 0, width);
		try {
			pg.grabPixels();
		}catch(Exception e){
			e.printStackTrace();
			
		}
	
		int r, g, b;
		
		
		//create displacement map
		if(type.equals("displacement")){
			displacementMap = new short[height*width];
			int dh = height;
			int dw = width;
			int i = 0;
			
		
			for ( int y = 0; y < dh; y++ ) {
				for ( int x = 0; x < dw; x++ ) {
					int rgb = textureBuffer[i];
					r = (rgb >> 16) & 0xff;
					g = (rgb >> 8) & 0xff;
					b = rgb & 0xff;
					textureBuffer[i] = (r+g+b) / 8; // An arbitrary scaling factor which gives a good range for "amount"
					i++;
				}
			}
			
			
			i = 0;
			for ( int y = 0; y < dh; y++ ) {
				int j1 = ((y+dh-1) % dh) * dw;
				int j2 = y*dw;
				int j3 = ((y+1) % dh) * dw;
				for ( int x = 0; x < dw; x++ ) {
					int k1 = (x+dw-1) % dw;
					int k2 = x;
					int k3 = (x+1) % dw;	
					int xMap = (textureBuffer[k1+j1] + textureBuffer[k1+j2] + textureBuffer[k1+j3] - textureBuffer[k3+j1] - textureBuffer[k3+j2] - textureBuffer[k3+j3]);
					int yMap = (textureBuffer[k1+j3] + textureBuffer[k2+j3] + textureBuffer[k3+j3] - textureBuffer[k1+j1] - textureBuffer[k2+j1] - textureBuffer[k3+j1]);
					
					displacementMap[i] = (short)( ((textureBuffer[i] * 2 / 3)  << 10) |  ((xMap + 16) << 5) | (yMap + 16));
					
					
					i++;
				}
			}
			
		}
		
		
		//create basic 15bits version of the original texture 
		if(type.equals("basic")){
			pixelData = new short[width*height];
			for(int i = 0; i < width*height; i ++){
				r = (textureBuffer[i] & 0x00ff0000)>>16;
				g = (textureBuffer[i] & 0x0000ff00)>>8;
				b = (textureBuffer[i] & 0x000000ff);
				r = r/8;
				g = g/8;
				b = b/8;
				pixelData[i] = (short)(r <<10 | g << 5 | b);
			}
		}
		
		//create a series of explosion texture
		if(type.equals("explosion")){
			explosions = new int[16][64*64];
			
			
			Color temp = new Color(0,0,0);
			for(int i = 0; i < 16; i++){
				int x = (i%4)*64;
				int y = (i/4)*64;
				
				for(int j = 0; j < 64*64; j++){
					int color = textureBuffer[x+y*256 + j%64 + (j/64)*256];
					temp = new Color(color);
					if(temp.getRed() < 40)
						color = 0;
					explosions[i][j] = color;
				}
			}	
		}
		
		
		//create series of smoke texture
		if(type.equals("smoke")){
			smoke = new  int[40][64*64];
			Color temp = new Color(0,0,0);
			for(int i = 0; i < 40; i++){
				int x = (i%8)*64;
				int y = (i/8)*64;
				
				for(int j = 0; j < 64*64; j++){
					int color = textureBuffer[x+y*512 + j%64 + (j/64)*512];
					temp = new Color(color);
					
					r = ((color & 0xff0000) >> 16);
					g = ((color & 0xff00) >> 8);
					b = ((color & 0xff));
					
					if(r < 0)
						r = 0;
					if(g < 0)
						g = 0;
					if(b < 0)
						b = 0;
					color = r;
					if(r < 5)
						color = 0;
					
					
					smoke[i][j] = color ;
					
				}
			}	
		}
		
		//create height map
		if(type.equals("heightmap")){
			heightmap = new int[(width+1)*(height+1)];
			
			for(int i = 0; i < height; i++){
				for(int j = 0; j < width; j++){
					r = (textureBuffer[j + i* width] & 0xff);
					if(i ==0 || i == (height-1) || j == 0 || j == (width -1))
						r = 0;
					heightmap[j + i * (width+1)] = r;
					
				}
			}
		}
		
		
		//create a series of light maps to stimulate the illumination created during an explosion
		if(type.equals("explosion aura")){
			explosionAura = new short[16][width*height];
			for(int j = 0; j < 16; j++){
				for(int i = 0; i < width*height; i ++){
					int I = (((textureBuffer[i] & 0x00ff0000)>>16) - 20) * 5/9;
					if(I < 80)
						I = I*I*I*I/80/80/80;
					explosionAura[j][i] = (short)(I/2.5 - 4*j);
				}
			}
		}
	}
	
	
	
	//produce texture based on input image
	public texture(String type, Image img, Image img2, int widthBits , int heightBits){
		this.widthBits = widthBits;
		this.heightBits = heightBits;
		
		height = (int)Math.pow(2, heightBits);
		width = (int)Math.pow(2, widthBits);
		
		heightMask = height -1;
		widthMask = width - 1;
		
		this.type = type;
	
		if(textureBuffer == null)
			textureBuffer = new int[1024*1024];  //support a max texture size of 1024 * 1024
		
		//load texture image and store it as an array of int
		PixelGrabber pg = new PixelGrabber(img, 0, 0, width, height, textureBuffer, 0, width);
		try {
			pg.grabPixels();
		}catch(Exception e){
			e.printStackTrace();
			
		}
	
		int r, g, b;
		
		
		//create displacement map
		if(type.equals("water")){
			displacementMap = new short[height*width];
			waterHeightMap = new byte[height*width];
			waterHeightMaps = new byte[48][height*width];
			waterSurfaceDirections = new boolean[height*width];
			int dh = height;
			int dw = width;
			int i = 0;
			
		
			for ( int y = 0; y < dh; y++ ) {
				for ( int x = 0; x < dw; x++ ) {
					int rgb = textureBuffer[i];
					r = (rgb >> 16) & 0xff;
					g = (rgb >> 8) & 0xff;
					b = rgb & 0xff;
					textureBuffer[i] = (r+g+b) / 8; // An arbitrary scaling factor which gives a good range for "amount"
					i++;
				}
			}
			
			
			i = 0;
			for ( int y = 0; y < dh; y++ ) {
				int j1 = ((y+dh-1) % dh) * dw;
				int j2 = y*dw;
				int j3 = ((y+1) % dh) * dw;
				for ( int x = 0; x < dw; x++ ) {
					int k1 = (x+dw-1) % dw;
					int k2 = x;
					int k3 = (x+1) % dw;	
					int xMap = (textureBuffer[k1+j1] + textureBuffer[k1+j2] + textureBuffer[k1+j3] - textureBuffer[k3+j1] - textureBuffer[k3+j2] - textureBuffer[k3+j3]);
					int yMap = (textureBuffer[k1+j3] + textureBuffer[k2+j3] + textureBuffer[k3+j3] - textureBuffer[k1+j1] - textureBuffer[k2+j1] - textureBuffer[k3+j1]);
					
					displacementMap[i] = (short)( ((xMap + 16) << 5) | (yMap + 16));
					
					
					i++;
				}
			}
			
			//load height map for the water surface
			pg = new PixelGrabber(img2, 0, 0, width, height, textureBuffer, 0, width);
			try {
				pg.grabPixels();
			}catch(Exception e){
				e.printStackTrace();
			}
			i = 0;
			
			for ( int y = 0; y < dh; y++ ) {
				for ( int x = 0; x < dw; x++ ) {
					int h = (int)(((textureBuffer[i]&0xff) - 160) * 0.9);
					if(h < 0)
						h = 0;
					waterHeightMap[i] = (byte)(h); 
					if(waterHeightMap[i] > 63){
						System.out.println(waterHeightMap[i]);
						waterHeightMap[i] = 63;
					}
					i++;
				}
			}
			
			for(i = 0; i < 256*256; i++){
				waterHeightMaps[0][i] = waterHeightMap[i];
				waterHeightMaps[15][i] = waterHeightMap[(3456 + 256*256-i-1)%(256*256)];
				waterHeightMaps[31][i] = waterHeightMap[i - i%256+ (255- i%256)];
			}
			
			for(int j = 1; j < 15; j++){
				for(i = 0; i < 256*256; i++ ){
					waterHeightMaps[j][i] =  (byte)(waterHeightMaps[0][i] + (float)(waterHeightMaps[15][i] - waterHeightMaps[0][i])/15f*j);
				}
			}
			
			for(int j = 16; j < 31; j++){
				for(i = 0; i < 256*256; i++ ){
					waterHeightMaps[j][i] = (byte)(waterHeightMaps[15][i] + (float)(waterHeightMaps[31][i] - waterHeightMaps[15][i])/15f*(j-15));
				}
			}
			
			for(int j = 32; j < 48; j++){
				for(i = 0; i < 256*256; i++ ){
					waterHeightMaps[j][i] = (byte)(waterHeightMaps[31][i] + (float)(waterHeightMaps[0][i] - waterHeightMaps[31][i])/16f*(j-31));
				}
			}
			
			
		}
		
		
	}
	
	
	//produce texture based on raw data 
	public texture(String type, short[] pixelData,  int widthBits, int heightBits){
		this.widthBits = widthBits;
		this.heightBits = heightBits;
		
		height = (int)Math.pow(2, heightBits);
		width = (int)Math.pow(2, widthBits);
		
		heightMask = height -1;
		widthMask = width - 1;
		
		this.pixelData = pixelData;
		
		this.type = type;
		
	}
	
	//produce texture based on a color
	public texture(String type, int color,  int widthBits, int heightBits){
		this.widthBits = widthBits;
		this.heightBits = heightBits;
		
		height = (int)Math.pow(2, heightBits);
		width = (int)Math.pow(2, widthBits);
		
		heightMask = height -1;
		widthMask = width - 1;
		
		int r = ((color & 0xff0000) >> 16)/8;
		int g = ((color & 0xff00) >> 8)/8;
		int b = (color & 0xff)/8;
		
		short c = (short)(r <<10 | g << 5 | b);
		
		this.pixelData = new short[height * width];
		for(int i = 0; i < pixelData.length; i ++){
			pixelData[i] = c;
			
		}
		
		this.type = type;
		
	}
}
