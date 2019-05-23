package core;

import java.sql.*;

public class highscoreManager implements Runnable{
	public Connection connect;
	public int counter;
	
	public int status;
	public static final int idle = 0;
	public static final int processing = 1;
	public static final int error = 2;
	
	public int task;
	public static final int none = 0;
	public static final int loadHighscores = 1;
	
	public boolean isSleeping;
	
	public String[] result;
	
	public highscoreManager(){
		status = processing;
	}


	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(true) {
			if(counter == 0) {
				try {
					
					connect = DriverManager.getConnection("jdbc:mysql://remotemysql.com/TDYAgrQ1Ny?useSSL=false",  "TDYAgrQ1Ny", "SrexYcsOSv");
					
					status = idle;
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					status = error;
				}
			}
			
			if(status == idle) {
				
				if(task !=  none) {
					status = processing;
					
					//get high scroes from remote database
					result = new String[] {};
					
					status = idle;
					task = none;
				}
			}
			
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			counter++;
		}
		
	}
}
