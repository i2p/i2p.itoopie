package net.i2p.itoopie.security;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class CertificateManager {

	private static final String DEFAULT_KEYSTORE_TYPE = "JKS";
	private static final String DEFAULT_KEYSTORE_PROVIDER = "SUN";
	private static final String DEFAULT_KEYSTORE_LOCATION = "key.store";
	private static final String DEFAULT_KEYSTORE_PASSWORD = "nut'nfancy";
	private static final String DEFAULT_KEYSTORE_ALGORITHM  = "SunX509";
	private static KeyStore _ks;
	private static Log _log;
	

	static {
		_log = LogFactory.getLog(CertificateManager.class);
	}

	/**
	 * Export X509Certificate as a file.
	 * 
	 * @param cert - X509Certificate to export
	 * @param file - Destination file for certificate
	 */
	@SuppressWarnings("unused")
	private static void export(X509Certificate cert, File file) {
		try {
			// Get the encoded form which is suitable for exporting
			byte[] buf = cert.getEncoded();

			FileOutputStream os = new FileOutputStream(file);
			// Never write certificate in binary form.
			if (false) {
				// Write in binary form
				os.write(buf);
			} else {
				// Write in text form
				Writer wr = new OutputStreamWriter(os, Charset.forName("UTF-8"));
				wr.write("-----BEGIN CERTIFICATE-----\n");
				wr.write(new sun.misc.BASE64Encoder().encode(buf));
				wr.write("\n-----END CERTIFICATE-----\n");
				wr.flush();
			}
			os.close();
		} catch (CertificateEncodingException e) {
			_log.error(
					"Bad certificate, can't be base64 encoded as a X509Certificate",
					e);
		} catch (IOException e) {
			_log.error("File " + file.getAbsolutePath().toString()
					+ " couldn't be written", e);
		}
	}

	public static boolean contains(String certName) {
		try {
			return getDefaultKeyStore().containsAlias(certName);
		} catch (KeyStoreException e) {
			_log.error("Error reading certificate with alias, " + certName + " from KeyStore", e);
		}
		return false;
	}
	

	/**
	 * Store server X509Certificate under the name provided
	 * 
	 * @param name - Name of the certificate
	 * @param cert - X509Certificate to store
	 * @return - True if store was successful, false in other cases.
	 */
	public static boolean putServerCert(String name, X509Certificate cert) {
		try {
			if (getDefaultKeyStore().containsAlias(name)){
				return false;
			} else {
				getDefaultKeyStore().setCertificateEntry(name, cert);
				return true;
			}
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Overwrite current X509Certificate with this name. Will only work if the
	 * name already has a certificate associated with it.
	 * 
	 * @param name - Name of the certificate
	 * @param cert - X509Certificate to overwrite
	 * @return - True if the overwrite was successful, false in other cases
	 */
	public static boolean overwriteServerCert(String name, X509Certificate cert){
		try {
			if (getDefaultKeyStore().containsAlias(name)){
				return false;
			} else {
				getDefaultKeyStore().setCertificateEntry(name, cert);
				return true;
			}
		} catch (KeyStoreException e) {
			_log.error("Error while reading alias, " + name + " from KeyStore",e);
		}
		return false;
	}
	
	/**
	 * Get trustManagers for the currently loaded certificates
	 * @return - Returns trustmanagers for currently loaded certificates 
	 */
	public static TrustManager[] getTrustManagers(){
		try {
			TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509","SunJSSE");
			tmf.init(getDefaultKeyStore());
			return tmf.getTrustManagers();
		} catch (NoSuchAlgorithmException e) {
			_log.error("Algorithm SunX509 couldn't be found for TrustManagerFactory", e);
		} catch (NoSuchProviderException e) {
			_log.error("Security provider SunJSSE couldn't be found", e);
		} catch (KeyStoreException e) {
			_log.error("Error reading from loaded KeyStore", e);
		}
		return null;
	}
	
	
	/**
	 * Get KeyStore containing server certs.
	 * @return - KeyStore used for keeping track of server.
	 */
	private static synchronized KeyStore getDefaultKeyStore(){
		if (_ks == null){
			KeyStore ks = null;
			try {
				ks = KeyStore.getInstance(DEFAULT_KEYSTORE_TYPE);
				if ((new File(DEFAULT_KEYSTORE_LOCATION)).exists()){
					InputStream is = new FileInputStream(DEFAULT_KEYSTORE_LOCATION);
					ks.load(is, DEFAULT_KEYSTORE_PASSWORD.toCharArray());
					return ks;
				} else {
					throw new IOException("KeyStore file " + DEFAULT_KEYSTORE_LOCATION + "wasn't readable");
				}
			} catch (Exception e) {
				// Ignore. Not an issue. Let's just create a new keystore instead.
			}
			try {
				ks = KeyStore.getInstance(DEFAULT_KEYSTORE_TYPE);
				ks.load(null, DEFAULT_KEYSTORE_PASSWORD.toCharArray());
				ks.store(new FileOutputStream(DEFAULT_KEYSTORE_LOCATION), DEFAULT_KEYSTORE_PASSWORD.toCharArray());
				return ks;
			} catch (Exception e){
				// Log perhaps?
			}
			return null;
		} else {
			return _ks;
		}
	}
}
