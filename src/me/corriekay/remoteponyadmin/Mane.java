package me.corriekay.remoteponyadmin;


public class Mane {
	
	public static PonyPrefs prefs;
	public static boolean start;
	public static void main(String[] args) throws Exception {
		prefs = new PonyPrefs();
		ManeWindow m = new ManeWindow();
		PonyClient pc = new PonyClient();
		start = pc.startClient();
		//TODO write information box stating recieving connection information.
		while(!start){
			Thread.sleep(1);
		}
		try {
			m.createWindow();
		} catch (Exception e) {
		e.printStackTrace();
			System.exit(1);
		}
	}
}
