/** 
 * Classes to represent, parse and serialise JSON-RPC 2.0 requests, 
 * notifications and responses.
 *
 * <p>JSON-RPC is a protocol for 
 * <a href="http://en.wikipedia.org/wiki/Remote_procedure_call">remote 
 * procedure calls</a> (RPC) using <a href="http://www.json.org" >JSON</a>
 * - encoded requests and responses. It can be easily relayed over HTTP 
 * and is of JavaScript origin, making it ideal for use in interactive web 
 * applications (popularly known as AJAX or Web 2.0).
 *
 * <p>This package implements <b>version 2.0</b> of the protocol, with the 
 * exception of <i>batching/multicall</i>. This feature was deliberately left
 * out, as it tends to confuse users (judging by posts in the JSON-RPC forum).
 *
 * The JSON-RPC 2.0 specification and user group forum can be found 
 * <a href="http://groups.google.com/group/json-rpc">here</a>.
 *
 * <p><b>Package dependencies:</b> The classes in this package rely on the 
 * {@code net.minidev.json} and {@code net.minidev.json.parser} packages 
 * (version 1.0.6.4 and compabile) for JSON encoding and decoding. You can 
 * obtain them from the 
 * <a href="http://code.google.com/p/json-smart/">JSON-Smart</a> website.
 *
 * @author <a href="http://dzhuvinov.com">Vladimir Dzhuvinov</a>
 * @version 1.17 (2011-06-11)
 */
package com.thetransactioncompany.jsonrpc2;


  
