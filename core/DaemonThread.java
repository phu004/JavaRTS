package core;

public class DaemonThread implements Runnable {
	public   void   run(){ 
		try{
			Thread.sleep(Long.MAX_VALUE);
		}
		catch(Exception e){}
	} 
}
