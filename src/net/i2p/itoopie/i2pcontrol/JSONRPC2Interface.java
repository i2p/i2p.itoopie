package net.i2p.itoopie.i2pcontrol;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Random;

import javax.net.ssl.HttpsURLConnection;

import net.i2p.itoopie.ItoopieVersion;
import net.i2p.itoopie.configuration.ConfigurationManager;
import net.i2p.itoopie.i2pcontrol.methods.Authenticate;
import net.i2p.itoopie.security.CertificateHelper;
import net.i2p.itoopie.security.ItoopieHostnameVerifier;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.thetransactioncompany.jsonrpc2.JSONRPC2Error;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Request;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Response;
import com.thetransactioncompany.jsonrpc2.client.JSONRPC2Session;
import com.thetransactioncompany.jsonrpc2.client.JSONRPC2SessionException;

public class JSONRPC2Interface {
	private static Log _log;
	private static ConfigurationManager _conf;
	private static int nonce;
	private static final int MAX_NBR_RETRIES = 2;
	private static JSONRPC2Session session;
	private static String token;
		
	static {
		_log = LogFactory.getLog(JSONRPC2Interface.class);
		_conf = ConfigurationManager.getInstance();
		Random rnd = new Random();
		nonce = rnd.nextInt();
		setupSession();
	}

	public static synchronized int incrNonce() {
		return ++nonce;
	}

	public static void setupSession() {
		URL srvURL = null;
		String srvHost = _conf.getConf("server.hostname", "localhost");
		if (srvHost.contains(":"))
			srvHost = '[' + srvHost + ']';
		int srvPort = _conf.getConf("server.port", 7650);
		String srvTarget = _conf.getConf("server.target", "jsonrpc");
		String method;
		if (srvPort == 7657) {
			// Use HTTP for the xmlrpc webapp in the HTTP router console
			method = "http";
			// target MUST contain a /, or else console will redirect
			// jsonrpc to jsonrpc/ which will be fetched as a GET
			// and will return the HTML password form.
			if (!srvTarget.contains("/"))
				srvTarget += "/";
		} else {
			method = "https";
		}
		try {
			srvURL = new URL(method + "://" + srvHost + ":" + srvPort + "/"
					+ srvTarget);
		} catch (MalformedURLException e) {
			_log.error("Bad URL: " + method + "://" + srvHost + ":" + srvPort + "/"
					+ srvTarget, e);
		}
		session = new JSONRPC2Session(srvURL);
		session.trustAllCerts(true);
	}
	
	public static void testSettings() throws InvalidPasswordException, JSONRPC2SessionException{
        HttpsURLConnection.setDefaultHostnameVerifier(new ItoopieHostnameVerifier());
		setupSession();
		Authenticate.execute();
	}

	public static JSONRPC2Response sendReq(JSONRPC2Request req)
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
			//System.out.println("Request: " + req.toString());
			//System.out.println("Response: " + resp.toString());
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
					token = Authenticate.execute();
					throw new FailedRequestException();
					// break;
				case -32003:
					// Invalid token
					token = Authenticate.execute();
					throw new FailedRequestException();
					//break;
				case -32004:
					// Token expired
					token = Authenticate.execute();
					throw new FailedRequestException();
					// break;
				case -32005:
					// I2PControl API version not provided
					throw new InvalidI2PControlAPI(err.getMessage());
				case -32006:
					// I2PControl API version not supported
					throw new InvalidI2PControlAPI(err.getMessage());
				}
			}
			return resp;
		} catch (FailedRequestException e) {
			return sendReq(req, ++tryNbr);
		} catch (InvalidI2PControlAPI e) {
			_log.error(e);
			return null;
		}
	}
}
