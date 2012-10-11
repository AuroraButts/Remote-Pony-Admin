package me.corriekay.remoteponyadmin;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.LineBorder;

import me.corriekay.packets.client.ClientChatPacket;
import me.corriekay.packets.client.OPonyResponsePacket;

public abstract class ManeWindow {
	
	//window vars
	public static JFrame window;
	public static SystemTray tray = SystemTray.getSystemTray();
	
	//icon vars
	public static TrayIcon trayIcon;
	public static PopupMenu tiMenu;
	public static MenuItem tiExit;
	
	//tab, and tabs
	public static JTabbedPane tabs;
	public static JPanel chatTab;
	public static JPanel playerTab;
	
	//menu bar
	public static JMenuBar menubar;
	public static JMenu file;
	public static JMenuItem fileExit;
	public static JMenu channel;
	public static JMenu allChannels;
	public static ArrayList<JCheckBox> selectedAllChannels = new ArrayList<JCheckBox>();
	public static JMenu allChannel;
	public static ButtonGroup allChannelButtonGroup;
	public static HashSet<JRadioButton> allChannelSelector = new HashSet<JRadioButton>();
	
	//chatTab components
	public static JTextArea chatText;
	public static JScrollPane chatWindow;
	
	public static JPanel sidePanel;
	public static JList playerList;
	public static JScrollPane plScrollPane;
	public static JComboBox channelSelector;
	
	public static JTextArea textBox;
	public static JButton send;
	
	//playerTab components
	public static JTextArea placeholder;
	
	//has the window been created already?
	static boolean created = false;
	
	public static void createWindow() throws Exception{
		if(created){
			System.out.println("WINDOW ALREADY CREATED.");
			return;
		}
		
		//Initialize and set frame properties
		window = new JFrame();
		window.setSize(800,600);
		DisplayMode monitor = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()[0].getDisplayMode();
		window.setLocation(monitor.getWidth()/2-(window.getWidth()/2),monitor.getHeight()/2-(window.getHeight()/2));
		window.setTitle("Remote Pony Admin");
		File img = new File(System.getProperty("user.dir"),"icon.png");
		Image i = ImageIO.read(img);
		window.setIconImage(i);
		
		//Initialize window listener
		window.addWindowListener(new WindowAdapter(){
			
			//minimize to icon
			public void windowIconified(WindowEvent e){
				window.setVisible(false);
			}
			//close to icon
			public void windowClosing(WindowEvent e){
				window.setVisible(false);
				window.setState(JFrame.ICONIFIED);
			}
		});
		
		//Initialize and set tasktray icon properties
		File tasktrayimg = new File(System.getProperty("user.dir"),"taskicon.png");
		trayIcon = new TrayIcon(ImageIO.read(tasktrayimg));
		trayIcon.setToolTip("Remote Pony Admin");
		trayIcon.addMouseListener(new TrayIconMouseListener());//restore on doubleclick
		trayIcon.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String response = arg0.getActionCommand();
				if(response == null){
					return;
				} else {
					trayIcon.setActionCommand(null);
				}
				OPonyResponsePacket oprp = new OPonyResponsePacket();
				oprp.name = response;
				PonyClient.c.sendTCP(oprp);
				return;
			}
		});
		//Build popup menu
		tiMenu = new PopupMenu();
		tiExit = new MenuItem("exit");
		tiExit.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int askExit;
				askExit = JOptionPane.showConfirmDialog(null,"Are you sure you wish to close Remote Pony Admin?","Dont make Derpy sad :c", JOptionPane.YES_NO_OPTION);
				if(askExit == 0){
					System.exit(0);
				}
			}
		});
		tiMenu.add(tiExit);
		trayIcon.setPopupMenu(tiMenu);
		tray.add(trayIcon);
		
		//Set close and resize
		window.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		window.setResizable(false);
		
		//Build Window
		tabs = new JTabbedPane();
		chatTab = new JPanel();
		playerTab = new JPanel();
		tabs.add("Chat",chatTab);
		tabs.add("Players",playerTab);
		
		//Build Menu, add to frame
		menubar = new JMenuBar();
		file = new JMenu("File");
		fileExit = new JMenuItem("exit");
		fileExit.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int askExit;
				askExit = JOptionPane.showConfirmDialog(null,"Are you sure you wish to close Remote Pony Admin?","Dont make Derpy sad :c", JOptionPane.YES_NO_OPTION);
				if(askExit == 0){
					System.exit(0);
				}
			}
		});
		file.add(fileExit);
		channel = new JMenu("Channel");
		allChannels = new JMenu("allchat channel filter");
		allChannel = new JMenu("allchat selected channel");
		channel.add(allChannel);
		channel.add(allChannels);
		menubar.add(file);
		menubar.add(channel);
		window.setJMenuBar(menubar);
		
		/**
		 * Chat tab
		 */
		//Chat Window
		chatText = new JTextArea();
		chatText.setEditable(false);
		chatText.setWrapStyleWord(true);
		chatText.setLineWrap(true);
		chatWindow = new JScrollPane(chatText);
		chatWindow.getVerticalScrollBar().setAutoscrolls(true);
		chatWindow.setPreferredSize(new Dimension(600,400));
		chatWindow.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		chatTab.add(chatWindow);
		
		//Players in channel
		playerList = new JList();
		playerList.setListData(new String[]{});
		plScrollPane = new JScrollPane(playerList);
		plScrollPane.setPreferredSize(new Dimension(150,350));
		
		//Channel selector
		channelSelector = new JComboBox();
		channelSelector.setEditable(false);
		channelSelector.setPreferredSize(new Dimension(150,44));
		
		
		//Side panel
		sidePanel = new JPanel();
		sidePanel.add(plScrollPane);
		sidePanel.add(channelSelector);
		sidePanel.setPreferredSize(new Dimension(150,410));
		chatTab.add(sidePanel);
		
		//Chat Input
		textBox = new JTextArea();
		textBox.setText("");
		textBox.setEditable(true);
		textBox.setLineWrap(true);
		textBox.setWrapStyleWord(true);
		textBox.setBorder(new LineBorder(Color.gray));
		textBox.setPreferredSize(new Dimension(600,100));
		textBox.addKeyListener(new KeyAdapter(){
			public void keyPressed(KeyEvent k){
				if(k.getKeyChar() == KeyEvent.VK_ENTER){
					{
						String name = getSelectedChannel().name;
						if(name.equals("allchat")){
							if(textBox.getText().equals("")){
								k.consume();
								return;
							}
							ClientChatPacket ccp = new ClientChatPacket();
							JRadioButton button = null;
							for(JRadioButton b : allChannelSelector){
								if(b.isSelected()){
									button = b;
								}
							}
							if(button == null){
								return;
							}
							ccp.channel = button.getName();
							ccp.message = textBox.getText();
							PonyClient.sendPacket(ccp);
							textBox.setText("");
							k.consume();
							return;
						} else if(name.equals("ponyspy")){
							return;
						}
					}
					if(k.isShiftDown()){
						return;
					} else {
						if(!textBox.getText().equals("")){
							ClientChatPacket ccp = new ClientChatPacket();
							ccp.channel = getSelectedChannel().name;
							ccp.message = textBox.getText();
							PonyClient.sendPacket(ccp);
							textBox.setText("");
						}
					k.consume();
					}
				}
			}
		});
		chatTab.add(textBox);
		
		//Send Button
		send = new JButton("send");
		send.setPreferredSize(new Dimension(150,75));
		send.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				{
					String name = getSelectedChannel().name;
					if(name.equals("allchat")){
						if(textBox.getText().equals("")){
							return;
						}
						ClientChatPacket ccp = new ClientChatPacket();
						JRadioButton button = null;
						for(JRadioButton b : allChannelSelector){
							if(b.isSelected()){
								button = b;
							}
						}
						if(button == null){
							return;
						}
						ccp.channel = button.getName();
						ccp.message = textBox.getText();
						PonyClient.sendPacket(ccp);
						textBox.setText("");
						return;
					} else if(name.equals("ponyspy")){
						return;
					}
				}
				if(textBox.getText().equals("")){
					return;
				} else {
					ClientChatPacket ccp = new ClientChatPacket();
					ccp.channel = getSelectedChannel().name;
					ccp.message = textBox.getText();
					PonyClient.sendPacket(ccp);
					textBox.setText("");
				}
			}
			
		});
		chatTab.add(send);
		
		/**
		 * Player tab
		 */
		//placeholder
		placeholder = new JTextArea();
		placeholder.setText("Placeholder");
		placeholder.setEditable(false);
		playerTab.add(placeholder);
		
		/**
		 * Finalization
		 */
		window.add(tabs);
		window.validate();
		window.pack();
		window.setSize(800,600);
		created = true;
	}
	public static void setVisible(boolean arg){
		window.setVisible(arg);
	}
	public static void updatePlayerList(HashMap<String,String> ponies){
		playerList.setListData(ponies.keySet().toArray());
	}
	public static void updateChannelList(String[] channames){
		ChatChannel[] chans = new ChatChannel[channames.length+2];
		int i = 0;
		allChannelButtonGroup = new ButtonGroup();
		boolean derp = false;
		for(String c : channames){
			chans[i] = new ChatChannel(c);
			JCheckBox box = new JCheckBox(c);
			box.setSelected(true);
			box.setName(c);
			selectedAllChannels.add(box);
			JRadioButton b = new JRadioButton(c);
			b.setName(c);
			allChannelButtonGroup.add(b);
			allChannel.add(b);
			allChannelSelector.add(b);
			if(!derp){
				derp = true;
				b.setSelected(true);
			}
			i++;
		}
		for(JCheckBox box : selectedAllChannels){
			allChannels.add(box);
		}
		chans[i] = new ChatChannel("allchat");
		i++;
		chans[i] = new ChatChannel("ponyspy");
		channelSelector.setModel(new JComboBox(chans).getModel());
		((ChatChannel)channelSelector.getSelectedItem()).updateMessages(true);
		channelSelector.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				ChatChannel c = (ChatChannel)channelSelector.getSelectedItem();
				for(ChatChannel chans : getChans()){
					chans.activeWindow = false;
				}
				c.activeWindow = true;
				c.updateMessages(true);
			}
			
		});
		getSelectedChannel().activeWindow = true;
	}
	public static ChatChannel getChannel(String chan){
		ChatChannel[] chans = new ChatChannel[channelSelector.getItemCount()];
		for(int i = 0; i<chans.length; i++){
			chans[i] = (ChatChannel) channelSelector.getItemAt(i);
		}
		for(ChatChannel c : chans){
			if(c.name.equalsIgnoreCase(chan)){
				return c;
			}
		}
		return null;
	}
	public static ChatChannel[] getChans(){
		ChatChannel[] chans = new ChatChannel[channelSelector.getItemCount()];
		for(int i = 0; i<chans.length; i++){
			chans[i] = (ChatChannel) channelSelector.getItemAt(i);
		}
		return chans;
	}
	public static ChatChannel getSelectedChannel(){
		return (ChatChannel)channelSelector.getItemAt(channelSelector.getSelectedIndex());
	}
}
