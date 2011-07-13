package net.i2p.itoopie.i2pcontrol.methods;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.i2p.itoopie.configuration.ConfigurationManager;
import net.i2p.itoopie.i2pcontrol.InvalidParametersException;
import net.i2p.itoopie.i2pcontrol.InvalidPasswordException;
import net.i2p.itoopie.i2pcontrol.JSONInterface;
import net.i2p.itoopie.i2pcontrol.UnrecoverableFailedRequestException;

import com.thetransactioncompany.jsonrpc2.JSONRPC2Request;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Response;
import com.thetransactioncompany.jsonrpc2.client.JSONRPC2SessionException;

public class Authenticate {
	private final static ConfigurationManager _conf = ConfigurationManager.getInstance();
	private final static Log _log = LogFactory.getLog(Authenticate.class);
	private final static String DEFAULT_PASSWORD = "itoopie";
	
	public static String execute()
			throws InvalidPasswordException, JSONRPC2SessionException {
		JSONRPC2Request req = new JSONRPC2Request("Authenticate", JSONInterface.incrNonce());

		Map outParams = new HashMap();
		outParams.put("Password",
				_conf.getConf("server.password", _conf.getConf("server.password", DEFAULT_PASSWORD)));
		req.setParams(outParams);

		JSONRPC2Response resp = null;
		try {
			resp = JSONInterface.sendReq(req);
			Map inParams = (HashMap) resp.getResult();
			return (String) inParams.get("Token");
		}catch (UnrecoverableFailedRequestException e) {
			return null; // Shouldn't normally happen.
		} catch (InvalidParametersException e) {
			_log.error("getNewToken() invalid parameters used");
		}
		return null;
	}
	
	
}
