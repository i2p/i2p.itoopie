package net.i2p.itoopie.gui;

import java.awt.BorderLayout;
import java.io.File;

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
	
/*
	public static void main(String[] args){
		System.out.println("Save new cert: " + saveNewCert(null,null));	
		System.out.println("Overwrite cert: " + overwriteCert(null,null));		
	}
*/

	public static synchronized boolean saveNewCert(Main main, File dir, String hostname, X509Certificate cert){
		JFrame frame = new JFrame();
		frame.setLayout(new BorderLayout());
		JButton bt = new JButton();
		bt.setText("text");
		frame.add(bt, BorderLayout.NORTH);
		
		String title = Transl._t("New remote host detected");
		String hostString = Transl._t("Would you like permanently trust the certificate from the remote host {0}?", hostname);
		
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
		String certInfo = "<html>"+Transl._t("Certificate info") + "<br><br>" +
				Transl._t("Name: ") + certName + "<br>" +
				Transl._t("Algorithm: ") + certAlgo + "<br>" +
				Transl._t("Serial: ") + certSerial + "<br>" +
				Transl._t("SHA-1 ID-hash: ") + certThumb;
		
		String textContent = certInfo + "<br><br>" + hostString;
		 
		int n = JOptionPane.showConfirmDialog(
		    frame,
		    textContent,
		    title,
		    JOptionPane.YES_NO_OPTION,
		    JOptionPane.INFORMATION_MESSAGE);
		
		if (n == JOptionPane.YES_OPTION){
			CertificateManager.forcePutServerCert(dir, hostname, CertificateHelper.convert(cert));
			updateUI(main);
			return true;
		} else {
			return false;
		}
	}
	

	public static boolean overwriteCert(Main main, File dir, String hostname, X509Certificate cert){
		JFrame frame = new JFrame();
		
		String title = Transl._t("Warning, new remote host detected");
		String hostString = Transl._t("The certificate of {0} has changed!", hostname) + "<br>" +
				Transl._t("Are you sure you want to permanently trust the new certificate from the remote host?");
		
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
		String certInfo = "<html>"+Transl._t("Certificate info") + "<br><br>" +
				Transl._t("Name: ") + certName + "<br>" +
				Transl._t("Algorithm: ") + certAlgo + "<br>" +
				Transl._t("Serial: ") + certSerial + "<br>" +
				Transl._t("SHA-1 ID-hash: ") + certThumb;
		
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
				    Transl._t("Are you sure that you trust the new certificate?"),
				    Transl._t("Is that your final answer?"),
				    JOptionPane.YES_NO_OPTION,
				    JOptionPane.ERROR_MESSAGE);
			if (n == JOptionPane.YES_OPTION){
				CertificateManager.forcePutServerCert(dir, hostname, CertificateHelper.convert(cert));
				updateUI(main);
				return true; // Confirmation positive
			} else {
				return false; // Confirmation negative
			}
		} else {
			return false; // No
		}
	}
	
	/**
	 * Upon new cert accepted it is probable a good idea to show it by updating the GUI.
	 */
	private static void updateUI(final Main main) {
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
						main.fireNewChange();
					}
				});
			}
		}).start();
	}
}
