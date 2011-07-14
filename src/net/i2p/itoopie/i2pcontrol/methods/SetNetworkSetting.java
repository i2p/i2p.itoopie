package net.i2p.itoopie.i2pcontrol.methods;

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
import net.i2p.itoopie.i2pcontrol.methods.NetworkSetting.NETWORK_SETTING;

import com.thetransactioncompany.jsonrpc2.JSONRPC2Request;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Response;
import com.thetransactioncompany.jsonrpc2.client.JSONRPC2SessionException;

public class SetNetworkSetting {
	private final static Log _log = LogFactory.getLog(SetNetworkSetting.class);
	
	
	public static HashMap<NETWORK_SETTING, Boolean> execute(Map<NETWORK_SETTING,String> settings) 
			throws InvalidPasswordException, JSONRPC2SessionException, InvalidParametersException{
		
		JSONRPC2Request req = new JSONRPC2Request("NetworkSetting", JSONRPC2Interface.incrNonce());
		
		Map outParams = new HashMap();

		Set<Entry<NETWORK_SETTING,String>> set = settings.entrySet();
		for (Entry<NETWORK_SETTING,String> e : set){
			if(e.getKey().isSetable()){
				outParams.put(e.getKey().toString(), e.getValue());
			}
		}
		req.setParams(outParams);

		JSONRPC2Response resp = null;
		try {
			resp = JSONRPC2Interface.sendReq(req);
			HashMap map = (HashMap) resp.getResult();
			return map;
		} catch (UnrecoverableFailedRequestException e) {
			_log.error("setNetworkInfo failed.", e);
		}
		return null;
	}
}
