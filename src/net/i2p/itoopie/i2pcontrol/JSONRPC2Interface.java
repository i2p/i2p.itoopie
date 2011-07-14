package net.i2p.itoopie.i2pcontrol;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Random;

import javax.net.ssl.HttpsURLConnection;

import net.i2p.itoopie.configuration.ConfigurationManager;
import net.i2p.itoopie.i2pcontrol.methods.Authenticate;
import net.i2p.itoopie.security.CertificateHelper;

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
		int srvPort = _conf.getConf("server.port", 7650);
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
				}
			}
			return resp;
		} catch (FailedRequestException e) {
			return sendReq(req, ++tryNbr);
		}
	}
}
