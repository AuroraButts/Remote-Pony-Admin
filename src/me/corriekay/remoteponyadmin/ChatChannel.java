package me.corriekay.remoteponyadmin;

import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.swing.JPanel;

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
			ManeWindow.chatText.setText(message);
			JPanel chatWindow = ManeWindow.chatTab;
			JPanel activeWindow = (JPanel) ManeWindow.tabs.getSelectedComponent();
			if(!chatWindow.equals(activeWindow)){
				ManeWindow.tabs.setTitleAt(0,"!Chat");
			}
		}
	}
}
