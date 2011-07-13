package net.i2p.itoopie.gui;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JPanel;

import net.i2p.itoopie.gui.component.LogoPanel;
import net.i2p.itoopie.i18n.Transl;

public class ConfigurationPanel extends LogoPanel {

	private static final long serialVersionUID = 328657255717753899L;


	public ConfigurationPanel(String imageName) {
		super(imageName);
		setLayout(new BorderLayout());
		
		JPanel buttonArea = new JPanel();
		buttonArea.setLayout(new BorderLayout());
		add(buttonArea, BorderLayout.NORTH);
		
		JPanel configArea = new JPanel();
		add(configArea, BorderLayout.CENTER);
		
		JPanel positionedButtonArea = new JPanel();
		positionedButtonArea.setLayout(new BorderLayout());
		buttonArea.add(positionedButtonArea, BorderLayout.NORTH);
		
		JButton btnApply = new JButton();
		btnApply.setText(Transl._("Apply"));
		positionedButtonArea.add(btnApply, BorderLayout.EAST);
	}

}