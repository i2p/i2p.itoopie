package net.i2p.itoopie.gui;


import java.util.HashSet;

import javax.swing.JFrame;

import net.i2p.itoopie.configuration.ConfigurationManager;

public class WindowHandler {
	private final HashSet<JFrame> _frames = new HashSet<JFrame>();
	private JFrame mainFrame;
	private boolean areFramesShown;
	private final ConfigurationManager _conf;

	public WindowHandler(ConfigurationManager conf) {
		_conf = conf;
	}

	public void register(JFrame frame){
		_frames.add(frame);
	}
	
	public void registerMain(JFrame frame){
		mainFrame = frame;
	}
	
	public void deRegister(JFrame frame){
		// don't remove the main frame when
		// the user clicks on the X, so we have the updated
		// graph when the user clicks on the icon again
		if (frame == mainFrame)
			hideFrames();
		else
			_frames.remove(frame);
	}
	
	public void hideFrames(){
		for (JFrame frame : _frames){
			frame.setVisible(false);
		}
		if (mainFrame != null){
			mainFrame.setVisible(false);
		}
		areFramesShown = false;
	}
	
	public void showFrames(){
		for (JFrame frame : _frames){
			frame.setVisible(true);
		}
		if (mainFrame != null){
			mainFrame.setVisible(true);
		}
		areFramesShown = true;
	}
	
	public void toggleFrames(){
		if (_frames.isEmpty()){
			new Main(this, _conf);
		} else {
			if (areFramesShown){
				hideFrames();
			} else {
				showFrames();
			}
		}
	}
}
