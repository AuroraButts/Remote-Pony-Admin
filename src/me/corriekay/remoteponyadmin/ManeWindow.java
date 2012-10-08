package me.corriekay.remoteponyadmin;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.LineBorder;

public class ManeWindow {

	public static JFrame jf = new JFrame();
	public SystemTray st = SystemTray.getSystemTray();
	JTextArea textBox;
	JComboBox channelSelector;
	JList playerslist;
	JButton send;
	TrayIcon ti;
	static ManeWindow mw;
	
	//private HashMap<String,String> ponies = new HashMap<String,String>();
	
	public ManeWindow(){
		mw = this;
	}
	
	public void createWindow() throws Exception{
		
		//set size, title, icon, register close event
		jf.setSize(800, 600);
		DisplayMode monitor = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()[0].getDisplayMode();
		jf.setLocation(monitor.getWidth()/2-(jf.getWidth()/2),monitor.getHeight()/2-(jf.getHeight()/2));
		
		jf.setTitle("Remote Pony Admin");
		jf.addWindowListener(new WindowAdapter(){
			public void windowIconified(WindowEvent e){
				jf.setVisible(false);
			}
		});
		
		File img = new File(System.getProperty("user.dir"),"icon.png");
		Image i = ImageIO.read(img);
		jf.setIconImage(i);
		
		//set task tray icon
		File tasktrayimg = new File(System.getProperty("user.dir"),"taskicon.png");
		ti = new TrayIcon(ImageIO.read(tasktrayimg));
		ti.setToolTip("Remote Pony Admin");
		ti.addMouseListener(new TrayIconMouseListener());
		st.add(ti);
		
		
		//set close and resize
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.setResizable(false);
		
		//build window.
		JTabbedPane tabs = new JTabbedPane();
		JPanel chatTab = new JPanel();
		JPanel playerTab = new JPanel();
		
		//Menu
		JMenuBar menubar = new JMenuBar();
		JMenu file = new JMenu("File");
		menubar.add(file);
		
		//Chat Window
		JTextArea chatText = new JTextArea();
		chatText.setEditable(false);
		chatText.setText("");
		JScrollPane chatWindow = new JScrollPane(chatText);
		chatWindow.setPreferredSize(new Dimension(600,400));
		chatWindow.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		
		//Active players in channel
		playerslist = new JList();
		String[] strlist = new String[]{};
		playerslist.setListData(strlist);
		JScrollPane playerScrollPane = new JScrollPane(playerslist);
		playerScrollPane.setPreferredSize(new Dimension(150,350));
		JPanel playersList = new JPanel();
		playersList.add(playerScrollPane);
		playersList.setPreferredSize(new Dimension(150,410));
		
		//Channel lists
		channelSelector = new JComboBox(new String[]{"herp","derp"});
		channelSelector.setEditable(false);
		channelSelector.setPreferredSize(new Dimension(150, 44));
		
		//Chat Box
		textBox = new JTextArea();
		textBox.setText("");
		textBox.setPreferredSize(new Dimension(600,100));
		textBox.setBorder(new LineBorder(Color.gray));
		
		//Send button
		send = new JButton("Send");
		send.setPreferredSize(new Dimension(150, 75));
		
		
		//Set up Chat Tab
		chatTab.add(chatWindow);
		playersList.add(channelSelector);
		chatTab.add(playersList);
		chatTab.add(textBox);
		chatTab.add(send);
		
		//Set up player tab
		playerTab.add(new JTextField(" Herp a derp! "));
		
		//Add menu and tabs
		tabs.add("Chat",chatTab);
		tabs.add("Players", playerTab);
		jf.setJMenuBar(menubar);
		jf.add(tabs);
		
		//set visible
		jf.validate();
		jf.pack();
		jf.setSize(800,600);
		jf.setVisible(true);
	}
	public void updatePlayerList(HashMap<String,String> ponies){
		//this.ponies = ponies;
		playerslist.setListData(ponies.keySet().toArray());
	}
}
