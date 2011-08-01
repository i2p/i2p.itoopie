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

public class GetI2PControl {
	private final static Log _log = LogFactory.getLog(GetI2PControl.class);
	
	
	public static EnumMap<I2P_CONTROL, Object> execute(I2P_CONTROL ... settings) 
			throws InvalidPasswordException, JSONRPC2SessionException{
		
		JSONRPC2Request req = new JSONRPC2Request("I2PControl", JSONRPC2Interface.incrNonce());
		
		Map outParams = new HashMap();

		for (I2P_CONTROL s : settings){
			if(s.isReadable()){
				outParams.put(s.toString(), null);
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
			_log.error("getI2PControl failed.", e);
		} catch (InvalidParametersException e) {
			_log.error("getI2PControl was rejected by remote host as invalid.", e);
		}
		return null;
	}
}
