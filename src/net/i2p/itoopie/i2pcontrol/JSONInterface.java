package net.i2p.itoopie.i2pcontrol;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;

import net.i2p.itoopie.configuration.ConfigurationManager;
import net.i2p.itoopie.security.CertificateHelper;

import org.GNOME.Accessibility.CollectionHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.thetransactioncompany.jsonrpc2.JSONRPC2Error;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Request;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Response;
import com.thetransactioncompany.jsonrpc2.client.JSONRPC2Session;
import com.thetransactioncompany.jsonrpc2.client.JSONRPC2SessionException;

public class JSONInterface {
	private static Log _log;
	private static ConfigurationManager _conf;
	private static String DEFAULT_PASSWORD = "itoopie";
	private static int nonce;
	private static final int MAX_NBR_RETRIES = 2;
	private static JSONRPC2Session session;
	private static String token;
	/*
	 * i2p.router.net.ntcp.port
	 * i2p.router.net.ntcp.hostname
	 * i2p.router.net.ntcp.autoip // true|always|false //disables autodetect|disabled //disables ntcp
	 * i2p.router.net.ssu.port
	 * i2p.router.net.ssu.hostname
	 * i2p.router.net.ssu.detectedip
	 * i2p.router.net.ssu.autoip //[local,upnp,ssu] any of prev., in order |fixed // fixed = no detection
	 * i2p.router.net.upnp //
	 * i2p.router.net.bw.share
	 * i2p.router.net.bw.in
	 * i2p.router.net.bw.out
	 * i2p.router.net.laptopmode
	 */
	public interface RemoteSetable{	public boolean isSetable(); }
	public static enum NETWORK_INFO implements RemoteSetable{
		DETECTED_IP { 	public boolean isSetable(){ return false;} public String toString() { return "i2p.router.net.ssu.detectedip"; }},
		TCP_PORT { 		public boolean isSetable(){ return true;}  public String toString() { return "i2p.router.net.ntcp.port"; }},
		TCP_HOSTNAME { 	public boolean isSetable(){ return true;}  public String toString() { return "i2p.router.net.ntcp.hostname"; }},
		TCP_AUTOIP { 	public boolean isSetable(){ return true;}  public String toString() { return "i2p.router.net.ntcp.autoip"; }},
		UDP_PORT { 		public boolean isSetable(){ return true;}  public String toString() { return "i2p.router.net.ssu.port"; }},
		UDP_HOSTNAME { 	public boolean isSetable(){ return true;}  public String toString() { return "i2p.router.net.ssu.hostname"; }},
		UDP_AUTO_IP { 	public boolean isSetable(){ return true;}  public String toString() { return "i2p.router.net.ssu.autoip"; }},
		UPNP { 			public boolean isSetable(){ return true;}  public String toString() { return "i2p.router.net.upnp"; }},
		BW_SHARE { 		public boolean isSetable(){ return true;}  public String toString() { return "i2p.router.net.bw.share"; }},
		BW_IN { 		public boolean isSetable(){ return true;}  public String toString() { return "i2p.router.net.bw.in"; }},
		BW_OUT { 		public boolean isSetable(){ return true;}  public String toString() { return "i2p.router.net.bw.out"; }},
		LAPTOP_MODE { 	public boolean isSetable(){ return true;}  public String toString() { return "i2p.router.net.laptopmode"; }}
		};
	private final static HashMap<String,NETWORK_INFO> enumMap;
		
	static {
		_log = LogFactory.getLog(JSONInterface.class);
		_conf = ConfigurationManager.getInstance();
		Random rnd = new Random();
		nonce = rnd.nextInt();
		setupSession();
		enumMap = new HashMap<String,NETWORK_INFO>();
		for (NETWORK_INFO n : NETWORK_INFO.values()){
			enumMap.put(n.toString(), n);
		}
	}

	private static synchronized int incrNonce() {
		return ++nonce;
	}

	public static void setupSession() {
		URL srvURL = null;
		String srvHost = _conf.getConf("server.hostname", "localhost");
		int srvPort = _conf.getConf("server.port", 7656);
		String srvTarget = _conf.getConf("server.target", "jsonrpc");
		try {
			srvURL = new URL("https://" + srvHost + ":" + srvPort + "/"
					+ srvTarget);
		} catch (MalformedURLException e) {
			_log.error("Bad URL: https://" + srvHost + ":" + srvPort + "/"
					+ srvTarget, e);
		}
		session = new JSONRPC2Session(srvURL);
		session.trustAllCerts(true);
	}
	
	public static void testSettings() throws InvalidPasswordException, JSONRPC2SessionException{
        HttpsURLConnection.setDefaultHostnameVerifier(CertificateHelper.getHostnameVerifier());
		setupSession();
		getNewToken();
	}

	private static JSONRPC2Response sendReq(JSONRPC2Request req)
			throws InvalidPasswordException, UnrecoverableFailedRequestException,
			InvalidParametersException, JSONRPC2SessionException{
		return sendReq(req, 1);
	}
	
	private static JSONRPC2Response sendReq(JSONRPC2Request req, int tryNbr)
			throws InvalidPasswordException, UnrecoverableFailedRequestException,
			InvalidParametersException, JSONRPC2SessionException {
		if (tryNbr > MAX_NBR_RETRIES){
			throw new UnrecoverableFailedRequestException(); // Max retries reached. Throw exception.
		}
		HashMap outParams = (HashMap) req.getParams();
		outParams.put("Token", token); // Add authentication token
		req.setParams(outParams);

		JSONRPC2Response resp = null;
		try {
			resp = session.send(req);
			JSONRPC2Error err = resp.getError();
			if (err != null) {
				switch (err.getCode()) {
				case -32700:
					// Parse error
					_log.error(err.getMessage());
					break;
				case -32600:
					// Invalid request
					_log.error(err.getMessage());
					break;
				case -32601:
					// Method not found
					_log.error(err.getMessage());
					break;
				case -32602:
					// Invalid params
					_log.error(err.getMessage());
					throw new InvalidParametersException();
					//break;
				case -32603:
					// Internal error
					_log.error("Remote host: " + err.getMessage());
					break;

				// Custom errors (as defined by the I2PControl API)
				case -32001:
					// Invalid password
					_log.info("Provided password was rejected by the remote host");
					throw new InvalidPasswordException();
					// break;
				case -32002:
					// No token
					token = getNewToken();
					throw new FailedRequestException();
					// break;
				case -32003:
					// Invalid token
					token = getNewToken();
					throw new FailedRequestException();
					//break;
				case -32004:
					// Token expired
					token = getNewToken();
					throw new FailedRequestException();
					// break;
				}
			}
			return resp;
		}catch (FailedRequestException e) {
			return sendReq(req, ++tryNbr);
		}
	}

	private static synchronized String getNewToken()
			throws InvalidPasswordException, JSONRPC2SessionException {
		JSONRPC2Request req = new JSONRPC2Request("Authenticate", incrNonce());

		Map outParams = new HashMap();
		outParams.put("Password",
				_conf.getConf("server.password", _conf.getConf("server.password", DEFAULT_PASSWORD)));
		req.setParams(outParams);

		JSONRPC2Response resp = null;
		try {
			resp = sendReq(req);
			Map inParams = (HashMap) resp.getResult();
			return (String) inParams.get("Token");
		}catch (UnrecoverableFailedRequestException e) {
			return null; // Shouldn't normally happen.
		} catch (InvalidParametersException e) {
			_log.error("getNewToken() invalid parameters used");
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static Double getRateStat(String stat, long period)
			throws InvalidPasswordException, JSONRPC2SessionException, InvalidParametersException {

		JSONRPC2Request req = new JSONRPC2Request("GetRate", incrNonce());
		@SuppressWarnings("rawtypes")
		Map params = new HashMap();
		params.put("Stat", stat);
		params.put("Period", period);
		req.setParams(params);

		JSONRPC2Response resp = null;
		try {
			resp = sendReq(req);
			Map inParams = (HashMap) resp.getResult();
			return (Double) inParams.get("Result");
		}catch (UnrecoverableFailedRequestException e) {
			_log.error("getRateStat failed.", e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static String getEcho(String str) 
			throws InvalidPasswordException, JSONRPC2SessionException {

		JSONRPC2Request req = new JSONRPC2Request("Echo", incrNonce());
		@SuppressWarnings("rawtypes")
		Map params = new HashMap();
		params.put("Echo", str);
		req.setParams(params);

		JSONRPC2Response resp = null;
		try {
			resp = sendReq(req);
			Map inParams = (HashMap) resp.getResult();
			return (String) inParams.get("Result");
		} catch (UnrecoverableFailedRequestException e) {
			_log.error("GetEcho failed.", e);
		} catch (InvalidParametersException e) {
			_log.error("Remote host rejected provided parameters: " + req.toJSON().toJSONString());
		}
		return null;
	}
	
	public static HashMap getNetworkInfo(NETWORK_INFO ... options) 
			throws InvalidPasswordException, JSONRPC2SessionException{
		
		JSONRPC2Request req = new JSONRPC2Request("NetworkSetting", incrNonce());
		@SuppressWarnings("rawtypes")
		Map outParams = new HashMap();
		List<NETWORK_INFO> list = Arrays.asList(options);
		
		for (NETWORK_INFO i : list){
			outParams.put(i.toString(), null);
		}

		req.setParams(outParams);

		JSONRPC2Response resp = null;
		try {
			resp = sendReq(req);
			HashMap map = (HashMap) resp.getResult();
			if (map != null){
				Set<Entry> set = map.entrySet();
				HashMap output = new HashMap();
				for (Entry e: set){
					output.put(enumMap.get(e.getKey()), e.getValue());
				}
				return map;
			} else {
				return new HashMap();
			}		
		} catch (UnrecoverableFailedRequestException e) {
			_log.error("getNetworkInfo failed.", e);
		} catch (InvalidParametersException e) {
			_log.error("Remote host rejected provided parameters: " + req.toJSON().toJSONString());
		}
		return null;
	}
	
	public static HashMap<NETWORK_INFO, Boolean> setNetworkSetting(Map<NETWORK_INFO,String> settings) 
			throws InvalidPasswordException, JSONRPC2SessionException, InvalidParametersException{
		
		JSONRPC2Request req = new JSONRPC2Request("NetworkSetting", incrNonce());
		
		Map outParams = new HashMap();

		Set<Entry<NETWORK_INFO,String>> set = settings.entrySet();
		for (Entry<NETWORK_INFO,String> e : set){
			if(e.getKey().isSetable()){
				outParams.put(e.getKey().toString(), e.getValue());
			}
		}
		req.setParams(outParams);

		JSONRPC2Response resp = null;
		try {
			resp = sendReq(req);
			HashMap map = (HashMap) resp.getResult();
			return map;
		} catch (UnrecoverableFailedRequestException e) {
			_log.error("setNetworkInfo failed.", e);
		}
		return null;
	}
}
