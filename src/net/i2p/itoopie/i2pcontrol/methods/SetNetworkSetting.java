package net.i2p.itoopie.i2pcontrol.methods;

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
import net.i2p.itoopie.i2pcontrol.methods.NetworkSetting.NETWORK_SETTING;

import com.thetransactioncompany.jsonrpc2.JSONRPC2Request;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Response;
import com.thetransactioncompany.jsonrpc2.client.JSONRPC2SessionException;

public class SetNetworkSetting {
	
	public static EnumMap<NETWORK_SETTING, Object> execute(Map<NETWORK_SETTING,String> settings) 
			throws InvalidPasswordException, JSONRPC2SessionException, InvalidParametersException{
		
		JSONRPC2Request req = new JSONRPC2Request("NetworkSetting", JSONRPC2Interface.incrNonce());
		
		Map outParams = new HashMap();

		Set<Entry<NETWORK_SETTING,String>> set = settings.entrySet();
		for (Entry<NETWORK_SETTING,String> e : set){
			if(e.getKey().isWritable()){
				outParams.put(e.getKey().toString(), e.getValue());
			}
		}
		req.setParams(outParams);

		JSONRPC2Response resp = null;
		try {
			resp = JSONRPC2Interface.sendReq(req);
			HashMap map = (HashMap) resp.getResult();
			if (map != null){
				Set<Entry> inputSet = map.entrySet();
				EnumMap<NETWORK_SETTING, Object> output = new EnumMap<NETWORK_SETTING, Object>(NETWORK_SETTING.class);
				// Present the result as an <Enum,Object> map.
				for (Entry e: inputSet){
					String key = (String) e.getKey();
					NETWORK_SETTING NS = NetworkSetting.getEnum(key);
					// If the enum exists. They should exists, but safety first.
					if (NS != null){
						output.put(NS, e.getValue());
					}
				}
				return output;
			} else {
				return new EnumMap<NETWORK_SETTING, Object>(NETWORK_SETTING.class);
			}
		} catch (UnrecoverableFailedRequestException e) {
			Log _log = LogFactory.getLog(SetNetworkSetting.class);
			_log.error("setNetworkInfo failed.", e);
		}
		return null;
	}
}
