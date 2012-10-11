package me.corriekay.remoteponyadmin;

import java.io.File;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

public class Utils {

	public static Icon derpWarning;
	
	public static void init() throws Exception{
		derpWarning = new ImageIcon(System.getProperty("user.dir")+File.separator+"derpwarning.png");
		
	}
	public static void displayDialog(String message, int dialogType){
		JOptionPane.showMessageDialog(null,message,"I just don't know what went wrong!",dialogType,derpWarning);
	}
	public static void exit(String msg){
		for(StackTraceElement ste : Thread.currentThread().getStackTrace()){
			System.out.println(ste);
		}
		displayDialog(msg,JOptionPane.WARNING_MESSAGE);
		System.exit(0);
	}
}
