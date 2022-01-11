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
import net.i2p.itoopie.i2pcontrol.methods.NetworkSetting.NETWORK_SETTING;
import net.i2p.itoopie.i2pcontrol.methods.RouterManager.ROUTER_MANAGER;

import com.thetransactioncompany.jsonrpc2.JSONRPC2Request;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Response;
import com.thetransactioncompany.jsonrpc2.client.JSONRPC2SessionException;

public class SetRouterManager {
	
	public static EnumMap<ROUTER_MANAGER, Object> execute(Map<ROUTER_MANAGER,String> commands) 
			throws InvalidPasswordException, JSONRPC2SessionException{
		
		JSONRPC2Request req = new JSONRPC2Request("RouterManager", JSONRPC2Interface.incrNonce());
		
		Map outParams = new HashMap();

		Set<Entry<ROUTER_MANAGER,String>> set = commands.entrySet();
		for (Entry<ROUTER_MANAGER,String> e : set){
			if(e.getKey().isWritable()){
				outParams.put(e.getKey().toString(), e.getValue());
			} else if (e.getKey().isReadable()){
				outParams.put(e.getKey().toString(), null);
			}
		}
		req.setParams(outParams);

		JSONRPC2Response resp = null;
		try {
			resp = JSONRPC2Interface.sendReq(req);
			HashMap map = (HashMap) resp.getResult();
			if (map != null){
				Set<Entry> inputSet = map.entrySet();
				EnumMap<ROUTER_MANAGER, Object> output = new EnumMap<ROUTER_MANAGER, Object>(ROUTER_MANAGER.class);
				// Present the result as an <Enum,Object> map.
				for (Entry e: inputSet){
					String key = (String) e.getKey();
					ROUTER_MANAGER RR = RouterManager.getEnum(key);
					// If the enum exists. They should exists, but safety first.
					if (RR != null){
						output.put(RR, e.getValue());
					}
				}
				return output;
			} else {
				return new EnumMap<ROUTER_MANAGER, Object>(ROUTER_MANAGER.class);
			}
		} catch (UnrecoverableFailedRequestException e) {
			Log _log = LogFactory.getLog(SetRouterManager.class);
			_log.error("setRouterManager failed.", e);
		} catch (InvalidParametersException e) {
			Log _log = LogFactory.getLog(SetRouterManager.class);
			_log.error("Remote host rejected provided parameters: " + req.toJSON().toJSONString());
		}
		return null;
	}
}
