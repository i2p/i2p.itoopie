package net.i2p.itoopie.gui;

import javax.swing.JLabel;

public class StatusHandler {
	private static final JLabel statusLbl;
	private static final String DEFAULT_STATUS = "Status";
	
	static{
		statusLbl = new JLabel();
		statusLbl.setText(DEFAULT_STATUS);
	}
	
	public static synchronized void setStatus(String msg){
		statusLbl.setText(msg);
	}
	
	public static synchronized String getStatus(){
		return statusLbl.getText();
	}
	
	public static JLabel getStatusLbl(){
		return statusLbl;
	}
	
}
