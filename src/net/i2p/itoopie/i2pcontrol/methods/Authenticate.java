package net.i2p.itoopie.i2pcontrol.methods;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.i2p.itoopie.ItoopieVersion;
import net.i2p.itoopie.configuration.ConfigurationManager;
import net.i2p.itoopie.i2pcontrol.InvalidParametersException;
import net.i2p.itoopie.i2pcontrol.InvalidPasswordException;
import net.i2p.itoopie.i2pcontrol.JSONRPC2Interface;
import net.i2p.itoopie.i2pcontrol.UnrecoverableFailedRequestException;

import com.thetransactioncompany.jsonrpc2.JSONRPC2Request;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Response;
import com.thetransactioncompany.jsonrpc2.client.JSONRPC2SessionException;

public class Authenticate {
	
	public static String execute(String password)
			throws InvalidPasswordException, JSONRPC2SessionException {
		JSONRPC2Request req = new JSONRPC2Request("Authenticate", JSONRPC2Interface.incrNonce());

		Map outParams = new HashMap();
		outParams.put("Password", password);
		outParams.put("API", ItoopieVersion.I2PCONTROL_API_VERSION);
		req.setParams(outParams);

		JSONRPC2Response resp = null;
		try {
			resp = JSONRPC2Interface.sendReq(req);
			Map inParams = (HashMap) resp.getResult();
			return (String) inParams.get("Token");
		}catch (UnrecoverableFailedRequestException e) {
			return null; // Shouldn't normally happen.
		} catch (InvalidParametersException e) {
			Log _log = LogFactory.getLog(Authenticate.class);
			_log.error("getNewToken() invalid parameters used");
		}
		return null;
	}
	
	
}
