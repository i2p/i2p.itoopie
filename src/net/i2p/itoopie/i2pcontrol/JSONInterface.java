package net.i2p.itoopie.i2pcontrol;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import net.i2p.itoopie.configuration.ConfigurationManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.thetransactioncompany.jsonrpc2.JSONRPC2Error;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Request;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Response;
import com.thetransactioncompany.jsonrpc2.client.JSONRPC2Session;
import com.thetransactioncompany.jsonrpc2.client.JSONRPC2SessionException;


public class JSONInterface{
	private static Log _log;
	private static ConfigurationManager _conf;
	private static int nonce;
	private static JSONRPC2Session session;
	
	
	static {
		_log = LogFactory.getLog(JSONInterface.class);
		_conf = ConfigurationManager.getInstance();
		Random rnd = new Random();
		nonce = rnd.nextInt();
		setupSession();
	}
	
	private static synchronized int incrNonce(){
		return ++nonce;
	}
	
	public static void setupSession(){
		URL srvURL = null;
		String srvHost = _conf.getConf("server-hostname", "localhost");
		int srvPort = _conf.getConf("server-port", 7656);
		String srvTarget = _conf.getConf("server-target", "jsonrpc");
		try {
			srvURL = new URL("https://"+srvHost+":"+srvPort+"/"+srvTarget);
		} catch (MalformedURLException e){
			_log.error("Bad URL: https://"+srvHost+":"+srvPort+"/"+srvTarget, e);
		}
		session = new JSONRPC2Session(srvURL);
		session.trustAllCerts(true);
	}
	
	
	private static JSONRPC2Response sendReq(JSONRPC2Request req){
		JSONRPC2Response resp = null;
		try {
			resp = session.send(req);
		} catch (JSONRPC2SessionException e) {
			_log.error(req.toString(), e);
		}
		return resp;
	}
	
	@SuppressWarnings("unchecked")
	public static Double getRateStat(String stat, long period) throws JSONRPC2Error{
		
		JSONRPC2Request req = new JSONRPC2Request("getRate", incrNonce());
		@SuppressWarnings("rawtypes")
		Map params = new HashMap();
		params.put("stat", stat);
		params.put("period", period);
		req.setParams(params);
		
		JSONRPC2Response resp = sendReq(req);
		if (resp.indicatesSuccess()){
			Map inParams = (HashMap)resp.getResult();
			return (Double) inParams.get("result");
		} else {
				throw resp.getError();
		}	
	}
	
	
	@SuppressWarnings("unchecked")
	public static String getEcho(String str) throws JSONRPC2Error{
		
		JSONRPC2Request req = new JSONRPC2Request("echo", incrNonce());
		@SuppressWarnings("rawtypes")
		Map params = new HashMap();
		params.put("echo", str);
		req.setParams(params);
		
		JSONRPC2Response resp = sendReq(req);
		if (resp.indicatesSuccess()){
			Map inParams = (HashMap)resp.getResult();
			return (String) inParams.get("result");
		} else {
				throw resp.getError();
		}	
	}
	
	@SuppressWarnings("unchecked")
	public static String getServerCert(String str) throws JSONRPC2Error{
		
		JSONRPC2Request req = new JSONRPC2Request("echo", incrNonce());
		@SuppressWarnings("rawtypes")
		Map params = new HashMap();
		params.put("echo", str);
		req.setParams(params);
		
		JSONRPC2Response resp = sendReq(req);
		if (resp.indicatesSuccess()){
			Map inParams = (HashMap)resp.getResult();
			return (String) inParams.get("serverCert");
		} else {
				throw resp.getError();
		}	
	}
}
