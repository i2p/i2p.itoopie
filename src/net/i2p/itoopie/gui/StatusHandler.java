package net.i2p.itoopie.gui;

import javax.swing.JLabel;

import net.i2p.itoopie.i18n.Transl;

public class StatusHandler {
	private static final JLabel statusLbl;
	private static final String DEFAULT_STATUS = "Status";
	
	private interface StatusMessage{ public String getStatusMessage(); };
	public static enum DEFAULT_STATUS implements StatusMessage {
		CONNECTED 			{ public String getStatusMessage(){ return Transl._t("Connected to I2P router."); }},
		NOT_CONNECTED	 	{ public String getStatusMessage(){ return Transl._t("Unable to connect to I2P router."); }},
		INVALID_PASSWORD	{ public String getStatusMessage(){ return Transl._t("I2P router rejected password."); }}
	}
	
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
	
	public static void setDefaultStatus(DEFAULT_STATUS status){
		statusLbl.setText(status.getStatusMessage());
	}
	
}
