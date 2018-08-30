package core;

import entity.solidObject;

//this clase define a rectangle in Cartesian coordinate
public class Rect {
	
	public int x1,x2,y1,y2,width,height;
	
	public solidObject owner;
	
	public Rect(int x1, int y1, int width, int height){
		this.x1 = x1;
		this.y1 = y1;
		this.width = width;
		this.height = height;
		x2 = x1 + width - 1;
		y2 = y1 - height + 1;
	}
	
	public void setOrigin(int x1, int y1){
		this.x1 = x1;
		this.y1 = y1;
		x2 = x1 + width - 1;
		y2 = y1 - height + 1;
	}
	
	public boolean intersect(Rect r){
		return !(x1  > r.x2 || r.x1 > x2 || y2 > r.y1 || r.y2 > y1);
	}
	
	public boolean contains(int x, int y){
		return x >= x1 && x <=x2 && y <= y1 && y >= y2;
	}
	
	public void expand(int r){
		x1-=r;
		x2+=r;
		y1+=r;
		y2-=r;
		width+=2*r;
		height+=2*r;
	}
	
	public void shrink(int r){
		x1+=r;
		x2-=r;
		y1-=r;
		y2+=r;
		width-=2*r;
		height-=2*r;
	}
	
	public String toString(){
		return x1 + " " + y1 + " " + width + " " + height;
	}
	
}
