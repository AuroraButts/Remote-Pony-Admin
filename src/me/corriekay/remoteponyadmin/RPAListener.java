package me.corriekay.remoteponyadmin;

import static me.corriekay.remoteponyadmin.Mane.prefs;
import static me.corriekay.remoteponyadmin.ManeWindow.getChannel;
import static me.corriekay.remoteponyadmin.ManeWindow.setVisible;
import static me.corriekay.remoteponyadmin.ManeWindow.updateChannelList;
import static me.corriekay.remoteponyadmin.ManeWindow.updatePlayerList;
import static me.corriekay.remoteponyadmin.PonyClient.isConnected;

import java.awt.TrayIcon;
import java.io.File;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPublicKeySpec;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.swing.JCheckBox;

import me.corriekay.packets.PacketUtils;
import me.corriekay.packets.PoniPacket;
import me.corriekay.packets.client.ClientHoofshake;
import me.corriekay.packets.server.ChanNamesPacket;
import me.corriekay.packets.server.ChatPacket;
import me.corriekay.packets.server.OPonyAlertPacket;
import me.corriekay.packets.server.PonyList;
import me.corriekay.packets.server.ServerHoofshake;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;


public class RPAListener extends Listener{
	public void received(Connection c, Object p){
		if(p instanceof ServerHoofshake){
			if(isConnected){
				return;
			} else {
				ServerHoofshake shs = (ServerHoofshake)p;
				if(!shs.ver.equals(PacketUtils.version)){
					Utils.exit("VERSION MISMATCH! Your version: "+PacketUtils.version+". Current version: "+shs.ver);
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
					c.sendTCP(chs);
					isConnected = true;
					return;
				}
			}
		} else if(p instanceof PoniPacket){
			if(p instanceof PonyList){
				PonyList pl = (PonyList)p;
				updatePlayerList(pl.ponies);
				setVisible(true);
				return;
			}
			if(p instanceof ChanNamesPacket){
				updateChannelList(((ChanNamesPacket)p).names);
				ManeWindow.setVisible(true);
				return;
			}
			if(p instanceof ChatPacket){
				ChatPacket cp = (ChatPacket)p;
				ChatChannel chan = getChannel(cp.channel);
				ChatChannel allchat = getChannel("allchat");
				if(chan == null){
					Utils.exit("Malformed chat packet!");
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
			if(p instanceof OPonyAlertPacket){
				OPonyAlertPacket oprp = (OPonyAlertPacket)p;
				TrayIcon ti = ManeWindow.trayIcon;
				ti.displayMessage("I brought you a letter!", oprp.player+" is requesting an OPony on the server!", TrayIcon.MessageType.INFO);
				playNotification();
				ti.setActionCommand(oprp.player);
				return;
			}
		}
	}
	public void disconnected(Connection c){
		//lets figure out how to handle a disconnection later.
		Utils.exit("Disconnected from server");
	}
	public void playNotification(){
		try {
		    File yourFile = new File(System.getProperty("user.dir"),"notification.wav");
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
}
