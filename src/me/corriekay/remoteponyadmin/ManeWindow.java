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
import java.awt.TrayIcon.MessageType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JComponent;
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
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.DefaultCaret;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

import me.corriekay.packets.PoniFile;
import me.corriekay.packets.PoniFolder;
import me.corriekay.packets.client.ClientChatPacket;
import me.corriekay.packets.client.FileRequestPacket;
import me.corriekay.packets.client.PlayerInfoPacket;

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
	public static JPanel alertTab;
	public static JPanel fileTab;
	
	//menu bar
	public static JMenuBar menubar;
	public static JMenu file;
	public static JMenuItem fileDisconnect;
	public static JMenuItem fileReconnect;
	public static JMenuItem fileExit;
	public static JMenu edit;
	public static JMenuItem editUsername;
	public static JMenuItem editPassword;
	public static JMenuItem editHost;
	public static JMenuItem editPort;
	public static JMenu player;
	public static JMenuItem playerLookup;
	public static JMenu channel;
	public static JMenu allChannels;
	public static ArrayList<JCheckBox> selectedAllChannels = new ArrayList<JCheckBox>();
	public static JMenu allChannel;
	public static ButtonGroup allChannelButtonGroup;
	public static HashSet<JRadioButton> allChannelSelector = new HashSet<JRadioButton>();
	public static JCheckBoxMenuItem chatAutoscroll;
	public static JMenu help;
	public static JMenuItem helpAbout;
	
	//chatTab components
	public static JTextArea chatText;
	public static DefaultCaret chatTextCaret;
	public static JScrollPane chatWindow;
	
	public static JPanel sidePanel;
	public static JList playerList;
	public static DefaultListModel playerListModel;
	public static JScrollPane plScrollPane;
	public static JComboBox channelSelector;
	
	public static JTextArea textBox;
	public static JButton send;
	
	//alertTab components
	public static JScrollPane alertScrollpane;
	public static JPanel alertScrollPanel;
	public static HashMap<Integer,JComponent> alerts = new HashMap<Integer,JComponent>();
	
	//fileTab components
	public static JScrollPane fileScrollPane;
	public static JTree fileTree;
	public static DefaultMutableTreeNode fileTreeRootNode;
	
	//has the window been created already?
	public static boolean created = false;
	
	//Has the chat channels list been populated yet?
	public static boolean channelsPopulated = false;
	
	//preserve the data, for reconnects?
	public static boolean preserveData = false;

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
		File img = new File(System.getProperty("user.dir")+File.separator+"res","icon.png");
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
		File tasktrayimg = new File(System.getProperty("user.dir")+File.separator+"res","taskicon.png");
		trayIcon = new TrayIcon(ImageIO.read(tasktrayimg));
		trayIcon.setToolTip("Remote Pony Admin");
		trayIcon.addMouseListener(new TrayIconMouseListener());//restore on doubleclick
		
		//Build popup menu
		tiMenu = new PopupMenu();
		tiExit = new MenuItem("exit");
		tiExit.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int askExit = JOptionPane.showConfirmDialog(null, "Are you sure you wish to close Remote Pony Admin?", "Don't make derpy sad :c", JOptionPane.YES_NO_OPTION, MessageType.INFO.ordinal(), Utils.quitIcon);
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
		chatTab.setName("Chat");
		alertTab = new JPanel();
		alertTab.setName("Alerts");
		fileTab = new JPanel();
		fileTab.setName("Files");
		tabs.add("Chat",chatTab);
		tabs.add("Alerts",alertTab);
		tabs.add("Files", fileTab);
		tabs.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent arg0) {
				int index = tabs.getSelectedIndex();
				tabs.setTitleAt(index, tabs.getComponent(index).getName());
			}});
		
		//Build Menu, add to frame
		menubar = new JMenuBar();
		file = new JMenu("File");
		fileDisconnect= new JMenuItem("disconnect");
		fileDisconnect.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				PonyClient.disconnect();
			}});
		fileReconnect = new JMenuItem("reconnect");
		fileReconnect.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				PonyClient.reconnect();
			}});
		
		fileExit = new JMenuItem("exit");
		fileExit.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int askExit;
				askExit = JOptionPane.showConfirmDialog(null,"Are you sure you wish to close Remote Pony Admin?","Dont make Derpy sad :c", JOptionPane.YES_NO_OPTION, MessageType.INFO.ordinal(),Utils.quitIcon);
				if(askExit == 0){
					System.exit(0);
				}
			}
		});
		file.add(fileDisconnect);
		file.add(fileReconnect);
		file.addSeparator();
		file.add(fileExit);
		edit = new JMenu("Edit");
		editUsername = new JMenuItem("Set Username");
		editPassword = new JMenuItem("Set Password");
		editHost = new JMenuItem("Set Host");
		editPort = new JMenuItem("Set Port");
		edit.add(editUsername);
		edit.add(editPassword);
		edit.addSeparator();
		edit.add(editHost);
		edit.add(editPort);
		editUsername.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Mane.prefs.setValue("username", ManeWindow.changeSetting("Username"));
				if(Utils.displayConfirmDialog("Please restart your client!","To utilize your new setting...")){
					System.exit(0);
				}
			}});
		editPassword.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Mane.prefs.setValue("password", ManeWindow.changeSetting("Password"));
				if(Utils.displayConfirmDialog("Please restart your client!","To utilize your new setting...")){
					System.exit(0);
				}
			}});
		editHost.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Mane.prefs.setValue("host", ManeWindow.changeSetting("Host"));
				if(Utils.displayConfirmDialog("Please restart your client!","To utilize your new setting...")){
					System.exit(0);
				}
			}});
		editPort.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Mane.prefs.setValue("port", ManeWindow.changeSetting("Port"));
				if(Utils.displayConfirmDialog("Please restart your client!","To utilize your new setting...")){
					System.exit(0);
				}
			}});
		player = new JMenu("Player");
		playerLookup = new JMenuItem("Player Lookup");
		playerLookup.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String req = requestString("Which pony are you looking for information on?");
				if(req != null){
					playerLookup(req);
				}
			}});
		player.add(playerLookup);
		channel = new JMenu("Channel");
		allChannels = new JMenu("allchat channel filter");
		allChannel = new JMenu("allchat selected channel");
		chatAutoscroll = new JCheckBoxMenuItem("chat box autoscroll");
		chatAutoscroll.setSelected(true);
		chatAutoscroll.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(chatAutoscroll.isSelected()){
					chatTextCaret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
				} else {
					chatTextCaret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
				}
			}
		});
		channel.add(allChannel);
		channel.add(allChannels);
		channel.addSeparator();
		channel.add(chatAutoscroll);
		help = new JMenu("Help");
		helpAbout = new JMenuItem("About RPA");
		helpAbout.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Utils.displayDialog("Created by Corrie Kay\n\nWith help from:\nErika Springer\nAndrew McWatters\nBrandon Beecroft\nVersion: "+Mane.version,"About RPA");
			}});
		help.add(helpAbout);
		menubar.add(file);
		menubar.add(edit);
		menubar.add(player);
		menubar.add(channel);
		menubar.add(help);
		window.setJMenuBar(menubar);
		
		/**
		 * Chat tab
		 */
		//Chat Window
		chatText = new JTextArea();
		chatText.setEditable(false);
		chatText.setWrapStyleWord(true);
		chatText.setLineWrap(true);
		chatTextCaret = (DefaultCaret) chatText.getCaret();
		chatTextCaret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		chatWindow = new JScrollPane(chatText);
		chatWindow.getVerticalScrollBar().setAutoscrolls(true);
		chatWindow.setPreferredSize(new Dimension(600,400));
		chatWindow.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		chatTab.add(chatWindow);
		
		//Players in channel
		playerList = new JList();
		playerList.addMouseListener(new MouseListener(){
			@Override
			public void mouseClicked(MouseEvent arg0) {
				JList list = (JList)arg0.getSource();
				if(arg0.getClickCount() == 2){
					int index = list.getSelectedIndex();
					String player = list.getModel().getElementAt(index).toString();
					playerLookup(player);
				}
			}
			@Override public void mouseEntered(MouseEvent arg0) {}
			@Override public void mouseExited(MouseEvent arg0) {}
			@Override public void mousePressed(MouseEvent arg0) {}
			@Override public void mouseReleased(MouseEvent arg0) {}
		});
		playerList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		playerListModel = new DefaultListModel();
		playerList.setModel(playerListModel);
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
								System.out.println("Null!");
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
		 * Alert tab
		 */
		alertScrollPanel = new JPanel();
		alertScrollPanel.setPreferredSize(new Dimension(776,510));
		alertScrollpane = new JScrollPane(alertScrollPanel);
		alertScrollpane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		alertScrollpane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		alertScrollpane.getVerticalScrollBar().setUnitIncrement(alertScrollpane.getVerticalScrollBar().getUnitIncrement()*15);
		alertScrollpane.setPreferredSize(new Dimension(792,520));
		alertTab.add(alertScrollpane);
		
		/**
		 * File Tab
		 */
		PoniFolder pppopp3 = new PoniFolder();
		pppopp3.name = "PPPoPP3";
		fileTreeRootNode = new DefaultMutableTreeNode(pppopp3);
		fileTree = new JTree(fileTreeRootNode);
		fileTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		fileTree.addMouseListener(new MouseListener(){

			@Override
			public void mouseClicked(MouseEvent arg0) {
				if(arg0.getClickCount()>1&&fileTree.getSelectionCount()>0){
					DefaultMutableTreeNode dmtn = (DefaultMutableTreeNode)fileTree.getSelectionPath().getLastPathComponent();
					if(dmtn.getUserObject() instanceof PoniFile){
						PoniFile file = (PoniFile)dmtn.getUserObject();
						DefaultMutableTreeNode parentNode = ((DefaultMutableTreeNode)dmtn.getParent());
						System.out.println(parentNode.getUserObject().toString());
						PoniFolder folder = (PoniFolder)((DefaultMutableTreeNode)dmtn.getParent()).getUserObject();
						//System.out.println("valid file! Directory: "+folder.directory+". File name: "+file.name); TODO DEBUG
						FileRequestPacket frp = new FileRequestPacket();
						frp.fileDirectory = folder.directory;
						frp.fileName = file.name;
						frp.clientDirectory = folder.directory.substring(folder.directory.indexOf("PPPoPP3"),folder.directory.length());
						PonyClient.sendPacket(frp);
						return;
					}
				}
				
			}
			public void mouseEntered(MouseEvent arg0){}
			public void mouseExited(MouseEvent arg0){}
			public void mousePressed(MouseEvent arg0){}
			public void mouseReleased(MouseEvent arg0){}
			
		});
		fileScrollPane = new JScrollPane(fileTree);
		fileScrollPane.setPreferredSize(new Dimension(776,510));
		fileTab.add(fileScrollPane);
		
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
		final boolean iconed = (window.getState() == JFrame.ICONIFIED);
		playerListModel.clear();
		playerListModel.setSize(ponies.size());
		Object[] ponyArray = ponies.keySet().toArray();
		for(int i = 0; i<ponies.size(); i++){
			playerListModel.set(i, ponyArray[i]);
		}
		Thread t = new Thread("iconification thread"){
			@Override
			public void run(){
				try {
					Thread.sleep(3);
				} catch (InterruptedException e) {
					return;
				}
				if(iconed){
					window.setState(JFrame.ICONIFIED);
					window.setVisible(false);
				}
			}
		};
		t.start();
	}
	public static void updateFolderList(PoniFolder folder){
		fileTreeRootNode.removeAllChildren();
		((PoniFolder)fileTreeRootNode.getUserObject()).directory = folder.directory;
		fillNode(fileTreeRootNode, folder);
	}
	private static void fillNode(DefaultMutableTreeNode node, PoniFolder folder){
		for(PoniFile file : folder.files){
			DefaultMutableTreeNode dmtn = new DefaultMutableTreeNode(file);
			dmtn.setAllowsChildren(false);
			dmtn.setUserObject(file);
			node.add(dmtn);
		}
		for(PoniFolder subFolder : folder.subfolders){
			DefaultMutableTreeNode dmtn = new DefaultMutableTreeNode(subFolder);
			dmtn.setUserObject(subFolder);
			node.add(dmtn);
			fillNode(dmtn,subFolder);
		}
	}
	public static void updateChannelList(String[] channames){
		channelsPopulated = true;
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
		chans[i++] = new ChatChannel("allchat");
		chans[i] = new ChatChannel("ponyspy");
		channelSelector.setModel(new JComboBox(chans).getModel());
		channelSelector.setSelectedIndex(--i);
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
	public static String changeSetting(String setting){
		return JOptionPane.showInputDialog(null, "Set your "+setting);
	}
	public static String requestString(String query){
		return JOptionPane.showInputDialog(null, query);
	}
	public static void playerLookup(String playername){
		PlayerInfoPacket pip = new PlayerInfoPacket();
		pip.player = playername;
		PonyClient.sendPacket(pip);
	}
}
