package net.i2p.itoopie.gui;

import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;

import net.i2p.itoopie.ItoopieVersion;
import net.i2p.itoopie.gui.component.MultiLineLabel;
import net.i2p.itoopie.gui.component.TabLogoPanel;
import net.i2p.itoopie.i18n.Transl;

public class AboutTab extends TabLogoPanel {
	
	MultiLineLabel lblThankYou;
	JLabel lblitoopie;
	JLabel lblVersion;
	JLabel lblVersionSpecified;

	public AboutTab(String imageName) {
		super(imageName);
		setLayout(null);
		
		lblThankYou = new MultiLineLabel();
		add(lblThankYou);
		lblThankYou.setBounds(10, 30, 250, 250);
		lblThankYou.setVerticalTextAlignment(JLabel.TOP);
		lblThankYou.setText(Transl._("itoopie and I2PControl were sponsored by Relakks & Ipredator.\n" + 
				"Development began during the summer of 2011 and the first alpha was launched in Juli.\n\n"+
				"I'd like to thank Jan-Erik Fiske and Peter Sunde for actively helping privacy and anonymity online.\n" +
				"// hottuna"));
		
		lblitoopie = new JLabel();
		add(lblitoopie);
		lblitoopie.setBounds(285, 30, 100, 15);
		lblitoopie.setHorizontalAlignment(SwingConstants.RIGHT);
		lblitoopie.setText("itoopie");
		
		lblVersion = new JLabel();
		add(lblVersion);
		lblVersion.setBounds(285, 50, 100, 15);
		lblVersion.setText("Version:");
		lblVersion.setHorizontalAlignment(SwingConstants.RIGHT);
		
		
		lblVersionSpecified = new JLabel();
		add(lblVersionSpecified);
		lblVersionSpecified.setBounds(395, 50, 100, 15);
		lblVersionSpecified.setText("v" + ItoopieVersion.VERSION);
		
		validate();
		
	}
	
	

	@Override
	public void onTabFocus(ChangeEvent e) {
		// Do nothing :)
	}

}
