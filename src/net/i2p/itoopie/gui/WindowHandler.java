package net.i2p.itoopie.gui;


import java.util.HashSet;

import javax.swing.JFrame;

public class WindowHandler {
	private static final HashSet<JFrame> _frames = new HashSet<JFrame>();
	private static Boolean areFramesShown = false;
	
	public static void registerFrame(JFrame frame){
		_frames.add(frame);
	}
	
	public static void deRegister(JFrame frame){
		_frames.remove(frame);
	}
	
	public static void hideFrames(){
		for (JFrame frame : _frames){
			frame.setVisible(false);
		}
		areFramesShown = false;
	}
	
	public static void showFrames(){
		for (JFrame frame : _frames){
			frame.setVisible(true);
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
