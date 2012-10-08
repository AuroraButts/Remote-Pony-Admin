package me.corriekay.remoteponyadmin;

import static me.corriekay.remoteponyadmin.Mane.prefs;
import static me.corriekay.remoteponyadmin.SetupWindow.showSetupWindow;

import java.awt.TrayIcon.MessageType;

import javax.swing.JOptionPane;

import me.corriekay.packets.PacketUtils;
import me.corriekay.packets.PoniPacket;
import me.corriekay.packets.client.ClientHoofshake;
import me.corriekay.packets.server.PonyList;
import me.corriekay.packets.server.ServerHoofshake;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

public class PonyClient {

	private boolean isConnected = false;
	private ManeWindow mw;

	public boolean startClient() throws Exception{
		Client c = new Client();
		PacketUtils.registerClientPackets(c);
		String username = prefs.getValue("username");
		String password = prefs.getValue("password");
		if(username.equals("null")||password.equals("null")){
			JOptionPane.showMessageDialog(null, "Your settings were derpy, Lets set them up now!", "Please setup", MessageType.INFO.ordinal());
			showSetupWindow();
			return false;
		}
		c.start();
		c.addListener(new RPAListener());
		try {
			c.connect(5000, prefs.getValue("host"), Integer.parseInt(prefs.getValue("port")));
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Unable to connect to server!", "I just don't know what went wrong!", MessageType.WARNING.ordinal());
			System.exit(1);
		}
		return true;
	}
	public class RPAListener extends Listener{
		public void received(Connection c, Object p){
			if(p instanceof ServerHoofshake){
				if(isConnected){
					return;
				} else {
					ServerHoofshake shs = (ServerHoofshake)p;
					if(!shs.ver.equals(PacketUtils.version)){
						JOptionPane.showMessageDialog(null, "VERSION MISMATCH! Your version: "+PacketUtils.version+". Current version: "+shs.ver, "I just don't know what went wrong!", MessageType.ERROR.ordinal());
						System.exit(1);
						return;
					} else {
						ClientHoofshake chs = new ClientHoofshake();
						chs.name = prefs.getValue("username");
						c.sendTCP(chs);
						isConnected = true;
						mw = ManeWindow.mw;
						return;
					}
				}
			} else if(p instanceof PoniPacket){
				if(p instanceof PonyList){
					PonyList pl = (PonyList)p;
					if(mw == null){
						System.out.print("null");
					}
					mw.updatePlayerList(pl.ponies);
				}
			}
		}
		public void disconnected(Connection c){
			//lets figure out how to handle a disconnection later.
			JOptionPane.showMessageDialog(null, "Disconnected from server", "I just don't know what went wrong!", MessageType.ERROR.ordinal());
		}
	}
}
