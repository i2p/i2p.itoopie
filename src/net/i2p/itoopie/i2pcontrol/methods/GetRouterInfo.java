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

import net.i2p.itoopie.i18n.Transl;
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
	private static HashMap<Integer, NETWORK_STATUS> enumMap;
	
	public static enum NETWORK_STATUS{
		OK													{	public String toString(){ return Transl._("Ok."); }},
		TESTING												{	public String toString(){ return Transl._("Testing."); }},
		FIREWALLED											{	public String toString(){ return Transl._("Firewalled."); }},
		HIDDEN												{	public String toString(){ return Transl._("Hidden."); }},
		WARN_FIREWALLED_AND_FAST							{	public String toString(){ return Transl._("Warning, firewalled and fast."); }},
		WARN_FIREWALLED_AND_FLOODFILL						{	public String toString(){ return Transl._("Warning, firewalled and floodfill."); }},
		WARN_FIREWALLED_WITH_INBOUND_TCP					{	public String toString(){ return Transl._("Warning, firewalled with inbound TCP enabled."); }},
		WARN_FIREWALLED_WITH_UDP_DISABLED					{	public String toString(){ return Transl._("Warning, firewalled with UDP disabled."); }},
		ERROR_I2CP											{	public String toString(){ return Transl._("Error, I2CP issue. Check logs."); }},
		ERROR_CLOCK_SKEW									{	public String toString(){ return Transl._("Error, clock skew. Try setting system clock."); }},
		ERROR_PRIVATE_TCP_ADDRESS							{	public String toString(){ return Transl._("Error, private TCP address."); }},
		ERROR_SYMMETRIC_NAT									{	public String toString(){ return Transl._("Error, behind symmetric NAT. Can't recieve connections."); }},
		ERROR_UDP_PORT_IN_USE								{	public String toString(){ return Transl._("Error, UDP port already in use."); }},
		ERROR_NO_ACTIVE_PEERS_CHECK_CONNECTION_AND_FIREWALL	{	public String toString(){ return Transl._("Error, no active peers. Check connection and firewall."); }},
		ERROR_UDP_DISABLED_AND_TCP_UNSET					{	public String toString(){ return Transl._("Error, UDP disabled and TCP unset."); }}
	};
	
	
	static {
		enumMap = new HashMap<Integer, NETWORK_STATUS>();
		for (NETWORK_STATUS n : NETWORK_STATUS.values()){
			enumMap.put(n.ordinal(), n);
		}
	}
	
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
	
	
	public static NETWORK_STATUS getEnum(Integer key){
		return enumMap.get(key);
	}
}
