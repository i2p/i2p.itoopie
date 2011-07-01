package net.i2p.itoopie.security;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
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
	public static final String DEFAULT_CERT_SPI = "X.509";
	private static KeyStore _ks;
	private static Log _log;
	
	static {
		_log = LogFactory.getLog(CertificateManager.class);
	}

	
	public static boolean verifyCert(String storedCertAlias, X509Certificate cert){
		try {
			X509Certificate storedCert = (X509Certificate) getDefaultKeyStore().getCertificate(storedCertAlias);
			storedCert.verify(cert.getPublicKey());
			return true;
		} catch (KeyStoreException e) {
			return false; // Was unable to read cert with given alias. Which is fine.
		} catch (Exception e) {
			return false; // Something is wrong with the provided key.
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
		KeyStore ks = getDefaultKeyStore();
		try {
			if (ks.containsAlias(name)){
				return false;
			} else {
				ks.setCertificateEntry(name, cert);
				saveKeyStore(ks);
				return true;
			}
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	
	/**
	 * Force store server X509Certificate under the name provided even if a certificate with the given alias already exists.
	 * 
	 * @param name - Name of the certificate
	 * @param cert - X509Certificate to store
	 * @return - True if store was successful, false in other cases.
	 */
	public static boolean forcePutServerCert(String name, X509Certificate cert) {
		KeyStore ks = getDefaultKeyStore();
		try {
			ks.setCertificateEntry(name, cert);
			saveKeyStore(ks);
			return true;
		} catch (KeyStoreException e) {
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
		KeyStore ks = getDefaultKeyStore();
		try {
			if (ks.containsAlias(name)){
				return false;
			} else {
				getDefaultKeyStore().setCertificateEntry(name, cert);
				saveKeyStore(ks);
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
			try {
				_ks = KeyStore.getInstance(DEFAULT_KEYSTORE_TYPE);
				if ((new File(DEFAULT_KEYSTORE_LOCATION)).exists()){
					InputStream is = new FileInputStream(DEFAULT_KEYSTORE_LOCATION);
					_ks.load(is, DEFAULT_KEYSTORE_PASSWORD.toCharArray());
					return _ks;
				} else {
					throw new IOException("KeyStore file " + DEFAULT_KEYSTORE_LOCATION + "wasn't readable");
				}
			} catch (Exception e) {
				// Ignore. Not an issue. Let's just create a new keystore instead.
			}
			try {
				_ks = KeyStore.getInstance(DEFAULT_KEYSTORE_TYPE);
				_ks.load(null, DEFAULT_KEYSTORE_PASSWORD.toCharArray());
				saveKeyStore(_ks);
				return _ks;
			} catch (Exception e){
				// Log perhaps?
			}
			return null;
		} else {
			return _ks;
		}
	}
	
	private static void saveKeyStore(KeyStore ks){
		try {
			ks.store(new FileOutputStream(DEFAULT_KEYSTORE_LOCATION), DEFAULT_KEYSTORE_PASSWORD.toCharArray());
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CertificateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
