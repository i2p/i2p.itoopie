package net.i2p.itoopie.security;

import java.util.HashSet;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import javax.security.cert.X509Certificate;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.i2p.itoopie.gui.CertificateGUI;

public class ItoopieHostnameVerifier implements HostnameVerifier {
	private static final Log _log = LogFactory.getLog(ItoopieHostnameVerifier.class);
	private static final HashSet<String> recentlyDeniedHosts = new HashSet<String>();
	private static final Object _uiLock = new Object();

	public boolean verify(String urlHostName, SSLSession session) {
		String serverHost = session.getPeerHost() + ":" + session.getPeerPort();
		synchronized (_uiLock) {
			try {
				javax.security.cert.X509Certificate[] certs = session.getPeerCertificateChain();

				if (recentlyDeniedHosts.contains(session.getPeerHost() + ":" + session.getPeerPort())) {
					return false; // Deny recently denied hosts.
				}

				if (CertificateManager.contains(serverHost)) {
					if (CertificateManager.verifyCert(serverHost, CertificateHelper.convert(certs[0]))) {
						return true; // Remote host has provided valid certificate that is stored locally.
					} else {
						// Remote host has provided a certificate that != the stored certificate for this host
						if (CertificateGUI.overwriteCert(serverHost, certs[0])) {
							return true;
						} else {
							recentlyDeniedHosts.add(session.getPeerHost() + ":"
									+ session.getPeerPort());
							return false;
						}
					}
				} else {
					// GUI, Add new host! new host
					if (CertificateGUI.saveNewCert(serverHost, certs[0])) {
						return true;
					} else {
						recentlyDeniedHosts.add(session.getPeerHost() + ":" + session.getPeerPort());
						return false;
					}
				}
			} catch (SSLPeerUnverifiedException e) {
				_log.fatal("Remote host could not be verified, possibly due to using not using athentication");
				return false;
			}
		}
	}


	public static void clearRecentlyDenied() {
		recentlyDeniedHosts.clear();
	}
}
