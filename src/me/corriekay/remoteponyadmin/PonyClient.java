package me.corriekay.remoteponyadmin;

import static me.corriekay.remoteponyadmin.Mane.prefs;
import me.corriekay.packets.PacketUtils;
import me.corriekay.packets.PoniPacket;

import com.esotericsoftware.kryonet.Client;

public abstract class PonyClient {

	public static Client c;
	public static ManeWindow mw;
	public static RPAListener rpaListener;

	public static void initClient(){
		c = new Client();
		PacketUtils.registerClientPackets(c);
		c.start();
		rpaListener = new RPAListener();
		c.addListener(rpaListener);
	}
	public static void startClient(){
		reconnect();
	}
	public static void sendPacket(PoniPacket p){
		c.sendTCP(p);
	}
	public static boolean reconnect(){
		if(c.isConnected()){
			Utils.displayDialog("You're still connected to the server!");
			return false;
		}
		Thread t = new Thread("ReconnectTryThread"){
			@Override
			public void run(){
				try {
					Thread.sleep(3);
				} catch (InterruptedException e1) {
					System.exit(0);
				}
				int maxTries = 5;
				for(int tries = 0; tries<maxTries; tries++){
					try{
						c.connect(5000, prefs.getValue("host"), Integer.parseInt(prefs.getValue("port")));
						return;
					} catch (Exception e){
						continue;
					}
				}
				Utils.exit("Unable to connect to server!");
			}
		};
		t.start();
		return true;
	}
	public static void disconnect(){
		rpaListener.isConnected = false;
		c.close();
	}
}
