package me.corriekay.remoteponyadmin;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.JOptionPane;

public class PonyPrefs {

	HashMap<String,String> options = new HashMap<String,String>();
	File config;

	public PonyPrefs() throws Exception{
		config = new File(System.getProperty("user.dir"),"preferences.txt");
		if(config.exists()){
			setupConfig();
		} else {
			try{
				config.createNewFile();
				BufferedWriter out = new BufferedWriter(new FileWriter(config,true));
				out.write("host: minelittlepony.se");
				out.newLine();
				out.write("port: 25566");
				out.newLine();
				out.write("username: null");
				out.newLine();
				out.write("password: null");
				out.newLine();
				out.write("notify: true");
				out.close();
				setupConfig();
			} catch (IOException e){
				e.printStackTrace();
				System.exit(1);
				return;
			}
		}
	}
	public String getValue(String key){
		return options.get(key);
	}
	public void setValue(String key, String value){
		options.put(key, value);
		try {
			saveConfig();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Utils.exit("Unable to save config!");
		}
	}
	public void saveConfig() throws IOException{
		config.delete();
		config.createNewFile();
		BufferedWriter out = new BufferedWriter(new FileWriter(config,true));
		Iterator<String> stringit = options.keySet().iterator();
		String key = null;
		while(stringit.hasNext()){
			key = stringit.next();
			out.write(key+": "+options.get(key));
			if(stringit.hasNext()){
				out.newLine();
			}
		}
		out.close();
	}
	public void setupConfig() throws Exception{
		BufferedReader in = new BufferedReader(new FileReader(config));
		String line = null;
		while((line = in.readLine())!=null){
			String[] stuff = line.split(": ");
			if(stuff.length != 2){
				JOptionPane.showMessageDialog(null,"Preference malformation: \""+line+"\"");
				System.exit(1);
				in.close();
				return;
			} else{
				String key = stuff[0];
				String value = stuff[1];
				options.put(key, value);
			}
		}
		in.close();
	}
}
