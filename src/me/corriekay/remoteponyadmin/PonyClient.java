package me.corriekay.remoteponyadmin;

import static me.corriekay.remoteponyadmin.Mane.prefs;
import me.corriekay.packets.PacketUtils;
import me.corriekay.packets.PoniPacket;

import com.esotericsoftware.kryonet.Client;

public abstract class PonyClient {

	public static Client c;
	public static ManeWindow mw;
	public static boolean isConnected = false;

	public static void initClient(){
		c = new Client();
		PacketUtils.registerClientPackets(c);
		c.start();
		c.addListener(new RPAListener());
	}
	public static void startClient(){
		try{
			c.connect(5000, prefs.getValue("host"), Integer.parseInt(prefs.getValue("port")));
		} catch (Exception e){
			Utils.exit("Unable to connect to server!");
		}
	}
	public static void stopClient(){
		c.stop();
		isConnected = false;
	}
	public static void sendPacket(PoniPacket p){
		c.sendTCP(p);
	}
}
