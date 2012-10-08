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
	public SetupWindowListener(JFrame frame, Component... components){
		this.components = components;
		this.frame = frame;
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
		Mane.start = true;
		System.out.print("Setting to true!");
	}

}
