package me.corriekay.remoteponyadmin;

import static me.corriekay.remoteponyadmin.Mane.prefs;
import static me.corriekay.remoteponyadmin.ManeWindow.getChannel;
import static me.corriekay.remoteponyadmin.ManeWindow.updateChannelList;
import static me.corriekay.remoteponyadmin.ManeWindow.updatePlayerList;

import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.math.BigInteger;
import java.net.URI;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollBar;

import me.corriekay.packets.PacketUtils;
import me.corriekay.packets.PoniPacket;
import me.corriekay.packets.client.ClientHoofshake;
import me.corriekay.packets.client.OPonyResponsePacket;
import me.corriekay.packets.server.AssassinateClient;
import me.corriekay.packets.server.BroadcastMessage;
import me.corriekay.packets.server.ChanNamesPacket;
import me.corriekay.packets.server.ChatPacket;
import me.corriekay.packets.server.OPonyAlertPacket;
import me.corriekay.packets.server.PlayerInfoResponsePacket;
import me.corriekay.packets.server.PonyList;
import me.corriekay.packets.server.SendAlerts;
import me.corriekay.packets.server.ServerHoofshake;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;


public class RPAListener extends Listener{
	
	//is the client connected?
	public boolean isConnected = false;
	//recieved first channelName packet
	public boolean chanNamesRecieved = false;
	
	public void received(Connection c, Object p){
		if(p instanceof AssassinateClient){
			AssassinateClient ac = (AssassinateClient)p;
			Utils.displayDialog(ac.note);
			if(Utils.displayConfirmDialog("Would you like to modify your settings before restarting?", "I just don't know what went wrong!")){
				try {
					SetupWindow.showSetupWindow(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				System.exit(0);
			}
			return;
		}
		if(p instanceof ServerHoofshake){
			if(isConnected){
				return;
			} else {
				ServerHoofshake shs = (ServerHoofshake)p;
				if(!shs.ver.equals(PacketUtils.version)){
					Utils.displayDialog("VERSION MISMATCH! Your version: "+PacketUtils.version+". Current version: "+shs.ver+". Please click okay to download the newest version.");
					if(Desktop.isDesktopSupported()){
						Desktop d = Desktop.getDesktop();
						try {
							d.browse(new URI("https://www.dropbox.com/sh/mcyxg10xz5klgb3/91514k3mDh/RemotePonyAdmin"));
						} catch (Exception e) {}
					}
					System.exit(0);
				} else {
					KeyFactory kf;
					RSAPublicKey pubkey;
					byte[] pw;
					try {
						kf = KeyFactory.getInstance("RSA");
						pubkey = (RSAPublicKey)kf.generatePublic(new RSAPublicKeySpec(new BigInteger(shs.pubMod),new BigInteger(shs.pubExp)));
						pw = PacketUtils.rsaEncrypt(Mane.prefs.getValue("password").getBytes(), pubkey);
					} catch (Exception e) {
						e.printStackTrace();
						c.close();
						return;
					}
					ClientHoofshake chs = new ClientHoofshake();
					chs.name = prefs.getValue("username");
					chs.pw = pw;
					chs.requestData = !ManeWindow.preserveData;
					c.sendTCP(chs);
					isConnected = true;
					ManeWindow.preserveData = true;
					ManeWindow.setVisible(true);
					return;
				}
			}
		} else if(p instanceof PoniPacket){
			if(p instanceof PonyList){
				PonyList pl = (PonyList)p;
				updatePlayerList(pl.ponies);
				return;
			}
			if(p instanceof ChanNamesPacket){
				updateChannelList(((ChanNamesPacket)p).names);
				chanNamesRecieved = true;
				return;
			}
			if(p instanceof ChatPacket){
				if(!ManeWindow.channelsPopulated)
					return;
				ChatPacket cp = (ChatPacket)p;
				ChatChannel chan = getChannel(cp.channel);
				ChatChannel allchat = getChannel("allchat");
				if(chan == null){
					String channelstring = "";
					for(ChatChannel channelst : ManeWindow.getChans()){
						channelstring += channelst+" ";
					}
					Utils.exit("Malformed chat packet! requested channel: "+cp.channel+". Channels available: "+channelstring);
				} else {
					chan.addMessage(cp.message);
					JCheckBox box = null;
					for(JCheckBox b : ManeWindow.selectedAllChannels){
						if(b.getName().equals(chan.name)){
							box = b;
						}
					}
					if(!chan.equals(allchat)){
						if (box == null || box.isSelected()) {
							allchat.addMessage(cp.message);
						}
					}
					return;
				}
			}
			if(p instanceof SendAlerts){
				SendAlerts sa = (SendAlerts)p;
				JPanel asp = ManeWindow.alertScrollPanel;
				int oldsize,newsize;
				oldsize = ManeWindow.alerts.size();
				asp.removeAll();
				ManeWindow.alerts.clear();
				ArrayList<JPanel> panels = new ArrayList<JPanel>();
				for(Integer id : sa.ids){
					String submitter, timestamp,op;
					submitter = sa.whosents.get(id);
					timestamp = getTimeStamp(sa.timestamps.get(id));
					op = sa.responders.get(id);
					AlertPanel ap = new AlertPanel(submitter,timestamp,op, id);
					JPanel panel = new JPanel();
					panel.add(ap);
					ap.getButton().addActionListener(new ActionListener(){
						@Override
						public void actionPerformed(ActionEvent arg0) {
							AlertPanel ap = ((AlertPanel)((JButton)arg0.getSource()).getParent().getParent());
							int id = ap.getId();
							OPonyResponsePacket oprp = new OPonyResponsePacket();
							oprp.id = id;
							oprp.player = ap.getSubmitter();
							PonyClient.c.sendTCP(oprp);
							((JButton)arg0.getSource()).setEnabled(false);
							((JButton)arg0.getSource()).setText("Saved!");
							return;
						}
					});
					panels.add(panel);
					ManeWindow.alerts.put(id, ap);
				}
				newsize = ManeWindow.alerts.size();
				if(oldsize<newsize){
					if(!ManeWindow.tabs.getSelectedComponent().equals(ManeWindow.alertTab)){
						ManeWindow.tabs.setTitleAt(1, "!Alerts");
					}
				}
				asp.setLayout(new GridLayout(newsize,1));
				int height = 0;
				for(JPanel panel : panels){
					panel.setSize(asp.getWidth(),90);
					height+=panel.getHeight();
					asp.add(panel);
				}
				asp.setPreferredSize(new Dimension(asp.getWidth(),height));
				asp.validate();
				ManeWindow.alertScrollpane.validate();
				JScrollBar vsb = ManeWindow.alertScrollpane.getVerticalScrollBar();
				vsb.setValue(vsb.getMaximum());
			}
			if(p instanceof OPonyAlertPacket){
				OPonyAlertPacket oprp = (OPonyAlertPacket)p;
				TrayIcon ti = ManeWindow.trayIcon;
				ti.displayMessage("I brought you a letter!", oprp.player+" is requesting an OPony on the server!", TrayIcon.MessageType.INFO);
				playNotification();
				ti.setActionCommand(oprp.id+":"+oprp.player);
				return;
			}
			if(p instanceof PlayerInfoResponsePacket){
				PlayerInfoResponsePacket pirp = (PlayerInfoResponsePacket)p;
				if(!pirp.configExists){
					Utils.displayDialog("That player doesnt exist!");
					return;
				}
				StringBuilder sb = new StringBuilder();
				for(String title : pirp.playerInfo.keySet()){
					sb.append(title+": "+pirp.playerInfo.get(title)+"\n");
				}
				Utils.displayDialog(sb.toString(), pirp.playername+"'s info!");
				return;
			}
			if(p instanceof BroadcastMessage){
				if(!ManeWindow.channelsPopulated)
					return;
				BroadcastMessage bm = (BroadcastMessage)p;
				for(ChatChannel channel : ManeWindow.getChans()){
					channel.addMessage(bm.message);
				}
			}
		}
	}
	public void disconnected(Connection c){
		if(isConnected){
			PonyClient.disconnect();
			PonyClient.reconnect();
		}
	}
	public void playNotification(){
		try {
		    File yourFile = new File(System.getProperty("user.dir")+File.separator+"res","notification.wav");
		    AudioInputStream stream;
		    AudioFormat format;
		    DataLine.Info info;
		    Clip clip;

		    stream = AudioSystem.getAudioInputStream(yourFile);
		    format = stream.getFormat();
		    info = new DataLine.Info(Clip.class, format);
		    clip = (Clip) AudioSystem.getLine(info);
		    clip.open(stream);
		    clip.start();
		}
		catch (Exception e) {
		    //whatevers
		}
	}	
	public static String getTimeStamp(long time){
		DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
		Date cal = Calendar.getInstance().getTime();
		cal.setTime(time);
		return dateFormat.format(cal.getTime());
	}
}
