package net.i2p.itoopie.i2pcontrol.methods;

import java.security.InvalidParameterException;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.i2p.itoopie.i2pcontrol.InvalidParametersException;
import net.i2p.itoopie.i2pcontrol.InvalidPasswordException;
import net.i2p.itoopie.i2pcontrol.JSONRPC2Interface;
import net.i2p.itoopie.i2pcontrol.UnrecoverableFailedRequestException;
import net.i2p.itoopie.i2pcontrol.methods.RouterRunner.ROUTER_RUNNER;

import com.thetransactioncompany.jsonrpc2.JSONRPC2Request;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Response;
import com.thetransactioncompany.jsonrpc2.client.JSONRPC2SessionException;

public class SetRouterRunner {
	private final static Log _log = LogFactory.getLog(SetRouterRunner.class);
	
	
	public static EnumMap<ROUTER_RUNNER, Object> execute(ROUTER_RUNNER cmd) 
			throws InvalidPasswordException, JSONRPC2SessionException{
		
		JSONRPC2Request req = new JSONRPC2Request("RouterRunner", JSONRPC2Interface.incrNonce());
		
		Map outParams = new HashMap();


		outParams.put(cmd.toString(), null);
		req.setParams(outParams);

		JSONRPC2Response resp = null;
		try {
			resp = JSONRPC2Interface.sendReq(req);
			HashMap map = (HashMap) resp.getResult();
			if (map != null){
				Set<Entry> inputSet = map.entrySet();
				EnumMap<ROUTER_RUNNER, Object> output = new EnumMap<ROUTER_RUNNER, Object>(ROUTER_RUNNER.class);
				// Present the result as an <Enum,Object> map.
				for (Entry e: inputSet){
					String key = (String) e.getKey();
					ROUTER_RUNNER RR = RouterRunner.getEnum(key);
					// If the enum exists. They should exists, but safety first.
					if (RR != null){
						output.put(RR, e.getValue());
					}
				}
				return output;
			} else {
				return new EnumMap<ROUTER_RUNNER, Object>(ROUTER_RUNNER.class);
			}
		} catch (UnrecoverableFailedRequestException e) {
			_log.error("setRouterRunner failed.", e);
		} catch (InvalidParametersException e) {
			_log.error("Remote host rejected provided parameters: " + req.toJSON().toJSONString());
		}
		return null;
	}
}
