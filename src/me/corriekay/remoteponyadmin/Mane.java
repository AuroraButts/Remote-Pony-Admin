package me.corriekay.remoteponyadmin;

import javax.swing.UIManager;




public class Mane {
	
	public static PonyPrefs prefs;
	public static boolean start;
	public static final String version = "v2.0";
	public static void main(String[] args) throws Exception {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		Utils.init();
		prefs = new PonyPrefs();
		/**
		 * first, check if the prefs are null. if they are, set up the prefs.
		 * then initialize window,but dont show yet.
		 * Boot up the client, handshake the server, obtain data, 
		 * wait for the all clear packet to show that you've not recieved the packet.
		 */
		String username, pw;
		username = prefs.getValue("username");
		pw = prefs.getValue("password");
		if(username == null || pw == null){
			Utils.exit("There was a derp with your config! Please delete it and then reboot!");
		}
		if(username.equals("null")||pw.equals("null")){
			SetupWindow.showSetupWindow(false);
		} else {
			start = true;
		}
		while(!start){
			Thread.sleep(1);
		}
		PonyClient.initClient();
		ManeWindow.createWindow();
		PonyClient.startClient();
	}
	
}
