package net.i2p.itoopie.i2pcontrol.methods;

import java.util.Arrays;
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
import net.i2p.itoopie.i2pcontrol.methods.NetworkSetting.NETWORK_SETTING;

import com.thetransactioncompany.jsonrpc2.JSONRPC2Request;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Response;
import com.thetransactioncompany.jsonrpc2.client.JSONRPC2SessionException;

public class GetNetworkSetting {
	private final static Log _log = LogFactory.getLog(GetNetworkSetting.class);
	
	public static HashMap execute(NETWORK_SETTING ... options) 
			throws InvalidPasswordException, JSONRPC2SessionException{
		
		JSONRPC2Request req = new JSONRPC2Request("NetworkSetting", JSONRPC2Interface.incrNonce());
		@SuppressWarnings("rawtypes")
		Map outParams = new HashMap();
		List<NETWORK_SETTING> list = Arrays.asList(options);
		
		for (NETWORK_SETTING i : list){
			outParams.put(i.toString(), null);
		}

		req.setParams(outParams);

		JSONRPC2Response resp = null;
		try {
			resp = JSONRPC2Interface.sendReq(req);
			HashMap map = (HashMap) resp.getResult();
			if (map != null){
				Set<Entry> set = map.entrySet();
				HashMap output = new HashMap();
				for (Entry e: set){
					output.put(NetworkSetting.enumMap.get(e.getKey()), e.getValue());
				}
				return map;
			} else {
				return new HashMap();
			}		
		} catch (UnrecoverableFailedRequestException e) {
			_log.error("getNetworkInfo failed.", e);
		} catch (InvalidParametersException e) {
			_log.error("Remote host rejected provided parameters: " + req.toJSON().toJSONString());
		}
		return null;
	}
}
