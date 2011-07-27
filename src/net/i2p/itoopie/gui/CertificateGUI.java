package net.i2p.itoopie.gui;

import java.awt.BorderLayout;

import javax.security.cert.X509Certificate;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import net.i2p.itoopie.i18n.Transl;
import net.i2p.itoopie.security.CertificateHelper;
import net.i2p.itoopie.security.CertificateManager;

public class CertificateGUI {
	private static boolean isVerifying = false;
	
	public static void main(String[] args){
		System.out.println("Save new cert: " + saveNewCert(null,null));	
		System.out.println("Overwrite cert: " + overwriteCert(null,null));		
	}

	public static synchronized boolean saveNewCert(String hostname, X509Certificate cert){
		if (!isVerifying){
			isVerifying = true;
			JFrame frame = new JFrame();
			frame.setLayout(new BorderLayout());
			JButton bt = new JButton();
			bt.setText("text");
			frame.add(bt, BorderLayout.NORTH);
			
			String title = Transl._("New remote host detected");
			String hostString = Transl._("Would you like permanently trust the certificate from the remote host " + hostname + "?");
			
			String certName = "N/A";
			String certAlgo = "N/A";
			String certSerial = "N/A";
			String certThumb = "N/A";
			if (cert != null){
				certName = cert.getSubjectDN().getName();
				String certString = cert.getPublicKey().toString();
				certAlgo = certString.substring(0,certString.indexOf("\n"));
				certSerial = String.valueOf(cert.getPublicKey().serialVersionUID);
				certThumb = CertificateHelper.getThumbPrint(cert);
			}
			String certInfo = "<html>"+Transl._("Certificate info") + "<br><br>" +
					Transl._("Name: ") + certName + "<br>" +
					Transl._("Algorithm: ") + certAlgo + "<br>" +
					Transl._("Serial: ") + certSerial + "<br>" +
					Transl._("SHA-1 ID-hash: ") + certThumb;
			
			String textContent = certInfo + "<br><br>" + hostString;
			 
			int n = JOptionPane.showConfirmDialog(
			    frame,
			    textContent,
			    title,
			    JOptionPane.YES_NO_OPTION,
			    JOptionPane.INFORMATION_MESSAGE);
			
			if (n == JOptionPane.YES_OPTION){
				CertificateManager.forcePutServerCert(hostname, CertificateHelper.convert(cert));
				updateUI();
				isVerifying = false;
				return true;
			} else {
				isVerifying = false;
				return false;
			}
		} else {
			return false;
		}
	}
	

	public static boolean overwriteCert(String hostname, X509Certificate cert){
		if (!isVerifying){
			isVerifying = true;
			JFrame frame = new JFrame();
			
			String title = Transl._("Warning, new remote host detected");
			String hostString = Transl._("The certificate of " + hostname + " has changed! <br>" +
					"Are you sure you like permanently trust the new certificate from the remote host?");
			
			String certName = "N/A";
			String certAlgo = "N/A";
			String certSerial = "N/A";
			String certThumb = "N/A";
			if (cert != null){
				certName = cert.getSubjectDN().getName();
				String certString = cert.getPublicKey().toString();
				certAlgo = certString.substring(0,certString.indexOf("\n"));
				certSerial = String.valueOf(cert.getPublicKey().serialVersionUID);
				certThumb = CertificateHelper.getThumbPrint(cert);
			}
			String certInfo = "<html>"+Transl._("Certificate info") + "<br><br>" +
					Transl._("Name: ") + certName + "<br>" +
					Transl._("Algorithm: ") + certAlgo + "<br>" +
					Transl._("Serial: ") + certSerial + "<br>" +
					Transl._("SHA-1 ID-hash: ") + certThumb;
			
			String textContent = certInfo + "<br><br>" + hostString;
			 
			int n = JOptionPane.showConfirmDialog(
			    frame,
			    textContent,
			    title,
			    JOptionPane.YES_NO_OPTION,
			    JOptionPane.WARNING_MESSAGE);
			
			if (n == JOptionPane.YES_OPTION){
				n = JOptionPane.showConfirmDialog(
					    frame,
					    Transl._("Are you sure that you trust the new certificate?"),
					    Transl._("Is that you final answer?"),
					    JOptionPane.YES_NO_OPTION,
					    JOptionPane.ERROR_MESSAGE);
				if (n == JOptionPane.YES_OPTION){
					CertificateManager.forcePutServerCert(hostname, CertificateHelper.convert(cert));
					updateUI();
					isVerifying = false;
					return true; // Confirmation positive
				} else {
					isVerifying = false;
					return false; // Confirmation negative
				}
			} else {
				return false; // No
			}
		} else {
			return false;
		}
	}
	
	/**
	 * Upon new cert accepted it is probable a good idea to show it by updating the GUI.
	 */
	private static void updateUI(){
		// Sleep before updating.
		(new Thread(){
			@Override
			public void run(){
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {} 
				SwingUtilities.invokeLater(new Runnable(){
		
					@Override
					public void run() {
						Main.fireNewChange();
					}
				});
			}
		}).start();
	}
}
