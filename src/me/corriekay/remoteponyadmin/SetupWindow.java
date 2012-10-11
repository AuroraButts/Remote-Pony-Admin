package me.corriekay.remoteponyadmin;

import static me.corriekay.remoteponyadmin.Mane.prefs;

import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.GraphicsEnvironment;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class SetupWindow {

	public static void showSetupWindow() throws Exception{
		
		//setup frame
		JFrame setupFrame = new JFrame();
		setupFrame.setSize(250,200);
		DisplayMode monitor = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()[0].getDisplayMode();
		setupFrame.setLocation(monitor.getWidth()/2-(setupFrame.getWidth()/2),monitor.getHeight()/2-(setupFrame.getHeight()/2));
		setupFrame.setTitle("Setup RPA");
		setupFrame.setResizable(false);
		File img = new File(System.getProperty("user.dir"),"icon.png");
		setupFrame.setIconImage(ImageIO.read(img));
		setupFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//setup panel
		JPanel panel = new JPanel();
		panel.setSize(new Dimension(250,130));
		
		Dimension d = new Dimension();
		d.height = 20;
		d.width = 100;
		
		//Username
		JTextField username = new JTextField("Username");
		username.setPreferredSize(d);
		username.setEditable(false);
		JTextField unInput = new JTextField(prefs.getValue("username"));
		unInput.setPreferredSize(d);
		unInput.setName("username");
		unInput.setToolTipText("Your exact minecraft username, case sensitive!");
		
		//Password
		JTextField password = new JTextField("Password");
		password.setPreferredSize(d);
		password.setEditable(false);
		JTextField pwInput = new JTextField(prefs.getValue("password"));
		pwInput.setPreferredSize(d);
		pwInput.setName("password");
		pwInput.setToolTipText("Your password. If you dont have a password on the server, it will not authenticate");
		
		//Hostname
		JTextField hostname = new JTextField("Hostname");
		hostname.setPreferredSize(d);
		hostname.setEditable(false);
		JTextField hnInput = new JTextField(prefs.getValue("host"));
		hnInput.setPreferredSize(d);
		hnInput.setName("host");
		hnInput.setToolTipText("The server address! Currently: minelittlepony.se");
		
		//Port
		JTextField port = new JTextField("port");
		port.setPreferredSize(d);
		port.setEditable(false);
		JTextField portInput = new JTextField(prefs.getValue("port"));
		portInput.setPreferredSize(d);
		portInput.setName("port");
		portInput.setToolTipText("The port! Currently set to 25566");
		
		//Notify
		JCheckBox notify = new JCheckBox("Notfiy",prefs.getValue("notify").equals("true"));
		notify.setPreferredSize(d);
		
		//Ok button
		JButton ok = new JButton("okay!");
		ok.setPreferredSize(new Dimension(100,50));
		ok.addActionListener(new SetupWindowListener(setupFrame,unInput,pwInput,hnInput,portInput,notify));
		
		//Add to the frame
		panel.add(username);
		panel.add(unInput);
		panel.add(password);
		panel.add(pwInput);
		panel.add(hostname);
		panel.add(hnInput);
		panel.add(port);
		panel.add(portInput);
		panel.add(notify);
		panel.add(ok);
		setupFrame.add(panel);
		
		setupFrame.setVisible(true);
	}
}
