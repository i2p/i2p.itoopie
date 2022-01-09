package net.i2p.itoopie.gui;


import java.util.HashSet;

import javax.swing.JFrame;

public class WindowHandler {
	private static final HashSet<JFrame> _frames = new HashSet<JFrame>();
	private static JFrame mainFrame;
	private static boolean areFramesShown = false;
	
	public static void register(JFrame frame){
		_frames.add(frame);
	}
	
	public static void registerMain(JFrame frame){
		mainFrame = frame;
	}
	
	public static void deRegister(JFrame frame){
		// don't remove the main frame when
		// the user clicks on the X, so we have the updated
		// graph when the user clicks on the icon again
		if (frame == mainFrame)
			hideFrames();
		else
			_frames.remove(frame);
	}
	
	public static void hideFrames(){
		for (JFrame frame : _frames){
			frame.setVisible(false);
		}
		if (mainFrame != null){
			mainFrame.setVisible(false);
		}
		areFramesShown = false;
	}
	
	public static void showFrames(){
		for (JFrame frame : _frames){
			frame.setVisible(true);
		}
		if (mainFrame != null){
			mainFrame.setVisible(true);
		}
		areFramesShown = true;
	}
	
	public static void toggleFrames(){
		if (_frames.isEmpty()){
			new Main();
		} else {
			if (areFramesShown){
				hideFrames();
			} else {
				showFrames();
			}
		}
	}
}
