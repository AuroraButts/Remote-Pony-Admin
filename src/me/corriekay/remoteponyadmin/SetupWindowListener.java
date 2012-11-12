package me.corriekay.remoteponyadmin;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JTextField;

public class SetupWindowListener implements ActionListener {
	
	Component[] components;
	JFrame frame;
	boolean exitAfter;
	public SetupWindowListener(boolean exitAfter,JFrame frame, Component... components){
		this.components = components;
		this.frame = frame;
		this.exitAfter = exitAfter;
	}

	@Override
	public void actionPerformed(ActionEvent a) {
		for(Component c : components){
			if(c instanceof JCheckBox){
				Mane.prefs.setValue("notify", ((JCheckBox)c).isSelected()+"");
				continue;
			}
			Mane.prefs.setValue(c.getName(),((JTextField)c).getText());
		}
		try{
			Mane.prefs.saveConfig();
		} catch (IOException e){
			e.printStackTrace();
			System.exit(1);
		}
		frame.setVisible(false);
		frame.setEnabled(false);
		frame.dispose();
		if(exitAfter){
			Utils.exit("Settings saved!");
		} else Mane.start = true;
	}

}
