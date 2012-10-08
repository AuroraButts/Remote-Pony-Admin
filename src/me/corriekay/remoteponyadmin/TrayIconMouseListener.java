package me.corriekay.remoteponyadmin;

import java.awt.Frame;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class TrayIconMouseListener implements MouseListener{

	@Override
	public void mouseClicked(MouseEvent arg0) {
		if(arg0.getClickCount()>1){
			if(!ManeWindow.jf.isVisible()){
				ManeWindow.jf.setVisible(true);
				ManeWindow.jf.setState(Frame.NORMAL);
			}
		}
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
