package me.corriekay.remoteponyadmin;

import java.awt.TrayIcon.MessageType;
import java.io.File;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

public class Utils {

	public static Icon derpWarning;
	public static Icon quitIcon;
	public static Icon question;
	
	public static void init() throws Exception{
		derpWarning = new ImageIcon(System.getProperty("user.dir")+File.separator+"res"+File.separator+"derpwarning.png");
		quitIcon = new ImageIcon(System.getProperty("user.dir")+File.separator+"res"+File.separator+"quit.png");
		question = new ImageIcon(System.getProperty("user.dir")+File.separator+"res"+File.separator+"question.png");
		
		
	}
	public static void displayDialog(String message){
		displayDialog(message,"I just don't know what went wrong!");
	}
	public static void displayDialog(String message, String title){
		JOptionPane.showMessageDialog(null,message,title,JOptionPane.OK_OPTION,derpWarning);
	}
	public static boolean displayConfirmDialog(String message,String title){
		int i =	JOptionPane.showConfirmDialog(null, message, title, JOptionPane.OK_CANCEL_OPTION, MessageType.NONE.ordinal(), question);
		if(i == 0){
			return true;
		} else return false;
	}
	public static void exit(String msg){
		displayDialog(msg);
		System.exit(0);
	}
}
