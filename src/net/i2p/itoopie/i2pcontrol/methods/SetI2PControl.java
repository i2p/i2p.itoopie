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
import net.i2p.itoopie.i2pcontrol.methods.I2PControl.I2P_CONTROL;

import com.thetransactioncompany.jsonrpc2.JSONRPC2Request;
import com.thetransactioncompany.jsonrpc2.JSONRPC2Response;
import com.thetransactioncompany.jsonrpc2.client.JSONRPC2SessionException;

public class SetI2PControl {
	
	public static EnumMap<I2P_CONTROL, Object> execute(Map<I2P_CONTROL,String> settings) 
			throws InvalidPasswordException, JSONRPC2SessionException, InvalidParametersException{
		
		JSONRPC2Request req = new JSONRPC2Request("I2PControl", JSONRPC2Interface.incrNonce());
		
		Map outParams = new HashMap();

		Set<Entry<I2P_CONTROL,String>> set = settings.entrySet();
		for (Entry<I2P_CONTROL,String> e : set){
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
				EnumMap<I2P_CONTROL, Object> output = new EnumMap<I2P_CONTROL, Object>(I2P_CONTROL.class);
				// Present the result as an <Enum,Object> map.
				for (Entry e: inputSet){
					String key = (String) e.getKey();
					I2P_CONTROL I2PC = I2PControl.getEnum(key);
					// If the enum exists. They should exists, but safety first.
					if (I2PC != null){
						output.put(I2PC, e.getValue());
					}
				}
				return output;
			} else {
				return new EnumMap<I2P_CONTROL, Object>(I2P_CONTROL.class);
			}
		} catch (UnrecoverableFailedRequestException e) {
			Log _log = LogFactory.getLog(SetI2PControl.class);
			_log.error("setI2PControl failed.", e);
		}
		return null;
	}
}
