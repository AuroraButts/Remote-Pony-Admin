package me.corriekay.remoteponyadmin;

import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ChatChannel {

	String name;
	boolean activeWindow = false;
	ConcurrentLinkedQueue<String> messages = new ConcurrentLinkedQueue<String>();

	public ChatChannel(String name){
		this.name = name;
	}
	@Override
	public String toString(){
		return name;
	}
	public void addMessage(String msg){
		messages.add(msg);
		while(messages.size()>250){
			messages.remove();
		}
		updateMessages(activeWindow);
	}
	public void updateMessages(boolean display){
		String message = "";
		Iterator<String> messages = this.messages.iterator();
		while(messages.hasNext()){
			message+=messages.next();
			if(messages.hasNext()){
				message+="\n";
			}
		}
		if(display){
			/*JScrollBar vsb = ManeWindow.chatWindow.getVerticalScrollBar();
			int location = vsb.getValue();
			int height = vsb.getSize().height;
			int totalHeight = ManeWindow.chatWindow.getSize().height-3;
			System.out.println("Location - height - totalHeight: "+location+" - "+ height+" - "+totalHeight);
			if(location < (totalHeight - height)){
				System.out.println("Not at the bottom of the scroll window!");
			}*/
			ManeWindow.chatText.setText(message);
		}
	}
}
