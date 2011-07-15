package net.i2p.itoopie.i2pcontrol.methods;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.i2p.itoopie.i2pcontrol.InvalidParametersException;
import net.i2p.itoopie.i2pcontrol.InvalidPasswordException;
import net.i2p.itoopie.i2pcontrol.JSONRPC2Interface;
import net.i2p.itoopie.i2pcontrol.UnrecoverableFailedRequestException;
import net.i2p.itoopie.i2pcontrol.methods.RouterInfo.ROUTER_INFO;

import com.thetransactioncompany.jsonrpc2.JSONRPC2Request;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Response;
import com.thetransactioncompany.jsonrpc2.client.JSONRPC2SessionException;

public class GetRouterInfo {
	private final static Log _log = LogFactory.getLog(GetRouterInfo.class);
	
	public static EnumMap<ROUTER_INFO, Object> execute(ROUTER_INFO ... info) 
			throws InvalidPasswordException, JSONRPC2SessionException{
		
		JSONRPC2Request req = new JSONRPC2Request("RouterInfo", JSONRPC2Interface.incrNonce());
		@SuppressWarnings("rawtypes")
		Map outParams = new HashMap();
		List<ROUTER_INFO> list = Arrays.asList(info);
		
		for (ROUTER_INFO e : list){
			if(e.isReadable()){
				outParams.put(e.toString(), null);
			}
		}

		req.setParams(outParams);

		JSONRPC2Response resp = null;
		try {
			resp = JSONRPC2Interface.sendReq(req);
			HashMap map = (HashMap) resp.getResult();
			if (map != null){
				Set<Entry> set = map.entrySet();
				EnumMap<ROUTER_INFO, Object> output = new EnumMap<ROUTER_INFO, Object>(ROUTER_INFO.class);
				// Present the result as an <Enum,Object> map.
				for (Entry e: set){
					String key = (String) e.getKey();
					ROUTER_INFO RI = RouterInfo.getEnum(key);
					// If the enum exists. They should exists, but safety first.
					if (RI != null){
						output.put(RI, e.getValue());
					}
				}
				return output;
			} else {
				return new EnumMap<ROUTER_INFO, Object>(ROUTER_INFO.class);
			}		
		} catch (UnrecoverableFailedRequestException e) {
			_log.error("getRouterInfo failed.", e);
		} catch (InvalidParametersException e) {
			_log.error("Remote host rejected provided parameters: " + req.toJSON().toJSONString());
		}
		return null;
	}
}
