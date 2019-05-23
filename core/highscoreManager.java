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
	
	public String[][] result;
	
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
					
					if(task == loadHighscores) {
						//get high scores from remote database
						Statement stmt;
						try {
							String[][] myResult = new String[30][2];
							int numOfRows = 0;
							
							stmt = connect.createStatement();
							ResultSet rs=stmt.executeQuery("select * from highscore where skillLevel = 0 order by finishingTime");  
							while(rs.next()) {
								myResult[numOfRows][0] = rs.getString(1);
								myResult[numOfRows][1] = secondsToString(rs.getInt(2));
							
								numOfRows++;
								if(numOfRows == 10)
									break;
							}
							
							numOfRows = 10;
							rs=stmt.executeQuery("select * from highscore where skillLevel = 1 order by finishingTime");  
							while(rs.next()) {
								myResult[numOfRows][0] = rs.getString(1);
								myResult[numOfRows][1] = secondsToString(rs.getInt(2));
							
								numOfRows++;
								if(numOfRows == 20)
									break;
							}
							
							numOfRows = 20;
							rs=stmt.executeQuery("select * from highscore where skillLevel = 2 order by finishingTime");  
							while(rs.next()) {
								myResult[numOfRows][0] = rs.getString(1);
								myResult[numOfRows][1] = secondsToString(rs.getInt(2));
							
								numOfRows++;
								if(numOfRows == 30)
									break;
							}
							
							result = myResult;
							
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							status = error;
							result = null;
						}  
					}
					
					if(status != error)
						status = idle;
					task = none;
				}
			}
			
			isSleeping = true;
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			isSleeping = false;
			
			counter++;
		}
		
	}
	
	public String secondsToString(int pTime) {
	    int min = pTime/60;
	    int sec = pTime-(min*60);

	    String strMin = placeZeroIfNeede(min);
	    String strSec = placeZeroIfNeede(sec);
	    return String.format("%s:%s",strMin,strSec);
	}

	public String placeZeroIfNeede(int number) {
	    return (number >=10)? Integer.toString(number):String.format("0%s",Integer.toString(number));
	}
}
