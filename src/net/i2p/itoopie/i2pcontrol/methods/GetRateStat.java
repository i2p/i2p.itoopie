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

public class GetRateStat{
	private final static Log _log = LogFactory.getLog(GetRateStat.class);
	
	@SuppressWarnings("unchecked")
	public static Double execute(String stat, long period)
			throws InvalidPasswordException, JSONRPC2SessionException, InvalidParametersException {

		JSONRPC2Request req = new JSONRPC2Request("GetRate", JSONRPC2Interface.incrNonce());
		@SuppressWarnings("rawtypes")
		Map params = new HashMap();
		params.put("Stat", stat);
		params.put("Period", period);
		req.setParams(params);

		JSONRPC2Response resp = null;
		try {
			resp = JSONRPC2Interface.sendReq(req);
			HashMap inParams = (HashMap) resp.getResult();
			Double dbl = (Double) inParams.get("Result");
			return dbl;
		}catch (UnrecoverableFailedRequestException e) {
			_log.error("getRateStat failed.", e);
		}
		return null;
	}
}
