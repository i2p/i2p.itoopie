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

public class JSONInterface {
	private static Log _log;
	private static ConfigurationManager _conf;
	private static String DEFAULT_PASSWORD = "itoopie";
	private static int nonce;
	private static final int MAX_NBR_RETRIES = 2;
	private static JSONRPC2Session session;
	private static String token;

	static {
		_log = LogFactory.getLog(JSONInterface.class);
		_conf = ConfigurationManager.getInstance();
		Random rnd = new Random();
		nonce = rnd.nextInt();
		setupSession();
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

	private static JSONRPC2Response sendReq(JSONRPC2Request req)
			throws InvalidPasswordException, UnrecoverableFailedRequestException{
		return sendReq(req, 1);
	}
	
	private static JSONRPC2Response sendReq(JSONRPC2Request req, int tryNbr)
			throws InvalidPasswordException, UnrecoverableFailedRequestException{
		if (tryNbr <= MAX_NBR_RETRIES){
			HashMap outParams = (HashMap) req.getParams();
			outParams.put("token", token); // Add authentication token
			req.setParams(outParams);
	
			JSONRPC2Response resp = null;
			try {
				resp = session.send(req);
				JSONRPC2Error err = resp.getError();
				if (err != null) {
					switch (err.getCode()) {
					case -32700:
						// Parse error
						break;
					case -32600:
						// Invalid request
						break;
					case -32601:
						// Method not found
						break;
					case -32602:
						// Invalid params
						break;
					case -32603:
						// Internal error
						break;
	
					// Custom errors (as defined by the I2PControl API)
					case -32001:
						// Invalid password
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
			} catch (JSONRPC2SessionException e) {
				_log.error(req.toString(), e);
			} catch (FailedRequestException e) {
				// Try sending the same request again with a current authentication token.
				return sendReq(req, ++tryNbr);
			}
			return resp;
		} else {
			throw new UnrecoverableFailedRequestException(); // Max retries reached. Throw exception.
		}
	}

	private static synchronized String getNewToken()
			throws InvalidPasswordException {
		JSONRPC2Request req = new JSONRPC2Request("authenticate", incrNonce());

		Map outParams = new HashMap();
		outParams.put("password",
				_conf.getConf("server.password", DEFAULT_PASSWORD));
		req.setParams(outParams);

		JSONRPC2Response resp = null;
		try {
			resp = sendReq(req);
		}catch (UnrecoverableFailedRequestException e) {
			return null; // Shouldn't normaly happen.
		}
		Map inParams = (HashMap) resp.getResult();
		return (String) inParams.get("token");
	}

	@SuppressWarnings("unchecked")
	public static Double getRateStat(String stat, long period)
			throws InvalidPasswordException {

		JSONRPC2Request req = new JSONRPC2Request("getRate", incrNonce());
		@SuppressWarnings("rawtypes")
		Map params = new HashMap();
		params.put("stat", stat);
		params.put("period", period);
		req.setParams(params);

		JSONRPC2Response resp = null;
		try {
			resp = sendReq(req);
		}catch (UnrecoverableFailedRequestException e) {
			_log.error("getRateStat failed.", e);
		}

		Map inParams = (HashMap) resp.getResult();
		return (Double) inParams.get("result");
	}

	@SuppressWarnings("unchecked")
	public static String getEcho(String str) throws InvalidPasswordException {

		JSONRPC2Request req = new JSONRPC2Request("echo", incrNonce());
		@SuppressWarnings("rawtypes")
		Map params = new HashMap();
		params.put("echo", str);
		req.setParams(params);

		JSONRPC2Response resp = null;
		try {
			resp = sendReq(req);
		} catch (UnrecoverableFailedRequestException e) {
			_log.error("getEcho failed.", e);
		}
		System.out.println("Response: " + resp.toJSON().toJSONString());
		Map inParams = (HashMap) resp.getResult();
		return (String) inParams.get("result");
	}
}
