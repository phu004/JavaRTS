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
	public static final int uploadScore = 2;
	
	public boolean isSleeping;
	
	public String playerName;
	
	public String[][] result;
	
	public highscoreManager(){
		status = processing;
		playerName = "";
	}


	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(true) {
			if(counter == 0) {
				
					status = idle;
							
			}
			
			if(status == idle) {
				
				if(task !=  none) {
					status = processing;
					Statement stmt = null;
					ResultSet rs = null;
					
					try {
						
						connect = DriverManager.getConnection("jdbc:mysql://db4free.net:3306/javarts",  "javarts", "kgiFO3nGzT");
	
						
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						status = error;
					}
					
					
					
					if(task == loadHighscores) {
						//get high scores from remote database
						
						try {
							String[][] myResult = new String[30][2];
							int numOfRows = 0;
							
							stmt = connect.createStatement();
							rs=stmt.executeQuery("select * from highscore where skillLevel = 0 order by finishingTime");  
							while(rs.next()) {
								playerName = rs.getString(1);
								if(!hasDuplicateName(0, numOfRows, myResult, playerName)) {
								
									myResult[numOfRows][0] = playerName;
									myResult[numOfRows][1] = secondsToString(rs.getInt(2));
								
									numOfRows++;
									if(numOfRows == 10)
										break;
								}
							}
							rs.close();
							
							numOfRows = 10;
							rs=stmt.executeQuery("select * from highscore where skillLevel = 1 order by finishingTime");  
							while(rs.next()) {
								playerName = rs.getString(1);
								if(!hasDuplicateName(10, numOfRows, myResult, playerName)) {
									myResult[numOfRows][0] = rs.getString(1);
									myResult[numOfRows][1] = secondsToString(rs.getInt(2));
								
									numOfRows++;
									if(numOfRows == 20)
										break;
								}
							}
							rs.close();
							
							numOfRows = 20;
							rs=stmt.executeQuery("select * from highscore where skillLevel = 2 order by finishingTime");  
							while(rs.next()) {
								playerName = rs.getString(1);
								if(!hasDuplicateName(20, numOfRows, myResult, playerName)) {
									myResult[numOfRows][0] = rs.getString(1);
									myResult[numOfRows][1] = secondsToString(rs.getInt(2));
								
									numOfRows++;
									if(numOfRows == 30)
										break;
								}
							}
							
							result = myResult;
							playerName ="";
							
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							status = error;
							result = null;
							playerName = "";
						}finally {
							 if (rs != null) {
							        try {
							            rs.close();
							        } catch (SQLException e) { /* ignored */}
							    }
							 if (stmt != null) {
							        try {
							        	stmt.close();
							        } catch (SQLException e) { /* ignored */}
							    }
							    if (connect != null) {
							        try {
							        	connect.close();
							        } catch (SQLException e) { /* ignored */}
							    }
						}
						
						
					}else if(task == uploadScore) {
						PreparedStatement preparedStmt = null;
						try {
							
							// the mysql insert statement
						    String query = " insert into highscore" + " values (?, ?, ?)";
						    preparedStmt = connect.prepareStatement(query);
						    
						    preparedStmt.setString (1, playerName);
						    preparedStmt.setInt (2, (int)(mainThread.gameFrame*0.025));
						    preparedStmt.setInt  (3, mainThread.ec.difficulty);
						    preparedStmt.execute();
							
							
						}catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							status = error;
							playerName = "";
						}finally {
							 if (rs != null) {
							        try {
							            rs.close();
							        } catch (SQLException e) { /* ignored */}
							    }
							 if (preparedStmt != null) {
							        try {
							        	preparedStmt.close();
							        } catch (SQLException e) { /* ignored */}
							    }
							    if (connect != null) {
							        try {
							        	connect.close();
							        } catch (SQLException e) { /* ignored */}
							    }
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
	
	public boolean hasDuplicateName(int start,  int current, String[][] myResult,  String name) {
		for(int i = start; i < current; i++) {
			if(myResult[i][0].toLowerCase().equals(name.toLowerCase())) {
				return true;
			}
		}
		
		return false;
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
