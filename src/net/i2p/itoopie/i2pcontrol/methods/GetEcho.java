package net.i2p.itoopie.i2pcontrol.methods;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.i2p.itoopie.i2pcontrol.InvalidParametersException;
import net.i2p.itoopie.i2pcontrol.InvalidPasswordException;
import net.i2p.itoopie.i2pcontrol.JSONRPC2Interface;
import net.i2p.itoopie.i2pcontrol.UnrecoverableFailedRequestException;

import com.thetransactioncompany.jsonrpc2.JSONRPC2Request;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Response;
import com.thetransactioncompany.jsonrpc2.client.JSONRPC2SessionException;

public class GetEcho {
	private final static Log _log = LogFactory.getLog(GetEcho.class);
	
	@SuppressWarnings("unchecked")
	public static String execute(String str) 
			throws InvalidPasswordException, JSONRPC2SessionException {

		JSONRPC2Request req = new JSONRPC2Request("Echo", JSONRPC2Interface.incrNonce());
		@SuppressWarnings("rawtypes")
		Map params = new HashMap();
		params.put("Echo", str);
		req.setParams(params);

		JSONRPC2Response resp = null;
		try {
			resp = JSONRPC2Interface.sendReq(req);
			Map inParams = (HashMap) resp.getResult();
			return (String) inParams.get("Result");
		} catch (UnrecoverableFailedRequestException e) {
			_log.error("GetEcho failed.", e);
		} catch (InvalidParametersException e) {
			_log.error("Remote host rejected provided parameters: " + req.toJSON().toJSONString());
		}
		return null;
	}
}
