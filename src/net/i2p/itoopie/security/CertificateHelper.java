package net.i2p.itoopie.security;

import java.io.ByteArrayInputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

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
}
