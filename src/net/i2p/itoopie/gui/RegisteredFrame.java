package net.i2p.itoopie.gui;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;

import net.i2p.itoopie.util.IconLoader;

public class RegisteredFrame extends JFrame implements WindowListener{
	private static final long serialVersionUID = 3351260168651061327L;

	public RegisteredFrame(String name) {
        super(name);
        super.addWindowListener(this);
        WindowHandler.register(this);
        this.setIconImage(IconLoader.getIcon("itoopie", 128));
	}

    
    public RegisteredFrame() {
        super();
        super.addWindowListener(this);
        WindowHandler.register(this);
        this.setIconImage(IconLoader.getIcon("itoopie", 128));
    }


    public void windowClosing(WindowEvent e) {
        WindowHandler.deRegister(this);
        this.dispose();
    }

	@Override
	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}
}
