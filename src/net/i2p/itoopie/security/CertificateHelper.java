package net.i2p.itoopie.security;

import java.io.ByteArrayInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import javax.security.cert.CertificateEncodingException;

import net.i2p.itoopie.gui.CertificateGUI;
import net.i2p.itoopie.i18n.Transl;
import net.i2p.itoopie.util.Base64;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class CertificateHelper {

	private static Log _log;
	
	static {
		_log = LogFactory.getLog(CertificateHelper.class);
	}
	
	public static X509Certificate certFromBase64(String base64){
		try {
			CertificateFactory cf = CertificateFactory.getInstance(CertificateManager.DEFAULT_CERT_SPI);
			byte[] bytes = Base64.decode(base64);
			return (X509Certificate) cf.generateCertificate(new ByteArrayInputStream(bytes));
		} catch (CertificateException e) {
			_log.fatal("Unable to load service interface provider, " + 
					CertificateManager.DEFAULT_CERT_SPI +
					" used for reading base64 encoded certificates", e);
		}
		
		return null;
	}
	
	// Converts to java.security
	public static java.security.cert.X509Certificate convert(javax.security.cert.X509Certificate cert) {
	    try {
	        byte[] encoded = cert.getEncoded();
	        ByteArrayInputStream bis = new ByteArrayInputStream(encoded);
	        java.security.cert.CertificateFactory cf
	            = java.security.cert.CertificateFactory.getInstance("X.509");
	        return (java.security.cert.X509Certificate)cf.generateCertificate(bis);
	    } catch (java.security.cert.CertificateEncodingException e) {
	    } catch (javax.security.cert.CertificateEncodingException e) {
	    } catch (java.security.cert.CertificateException e) {
	    }
	    return null;
	}

	// Converts to javax.security
	public static javax.security.cert.X509Certificate convert(java.security.cert.X509Certificate cert) {
	    try {
	        byte[] encoded = cert.getEncoded();
	        return javax.security.cert.X509Certificate.getInstance(encoded);
	    } catch (java.security.cert.CertificateEncodingException e) {
	    } catch (javax.security.cert.CertificateEncodingException e) {
	    } catch (javax.security.cert.CertificateException e) {
	    }
	    return null;
	}
	
	
	public static String getThumbPrint(javax.security.cert.X509Certificate cert){
		try {
			return getThumbPrint(convert(cert));
		} catch (Exception e){
			return Transl._t("Unable to create hash of the given cert, ") + cert;
		}
	}
	
    public static String getThumbPrint(java.security.cert.X509Certificate cert) 
            throws NoSuchAlgorithmException, CertificateEncodingException {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] der = null;
			try {
				der = cert.getEncoded();
			} catch (java.security.cert.CertificateEncodingException e) {
				e.printStackTrace();
			}
            md.update(der);
            byte[] digest = md.digest();
            return hexify(digest);

        }

        private static String hexify (byte bytes[]) {

            char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', 
                            '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

            StringBuffer buf = new StringBuffer(bytes.length * 2);

            for (int i = 0; i < bytes.length; ++i) {
                    buf.append(hexDigits[(bytes[i] & 0xf0) >> 4]);
                buf.append(hexDigits[bytes[i] & 0x0f]);
            }

            return buf.toString();
        }
}
