package com.thetransactioncompany.jsonrpc2.client;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.security.*;
import java.security.cert.*;
import java.util.regex.*;

import javax.net.ssl.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.thetransactioncompany.jsonrpc2.*;


/** 
 * Sends requests and/or notifications to a specified JSON-RPC 2.0 server URL.
 * The JSON-RPC 2.0 messages are sent by HTTP(S) POST.
 *
 * <p>Optional client-session settings:
 *
 * <ul>
 *     <li>Customise the "Content-Type" header in HTTP POST requests.</li>
 *     <li>Set an "Origin" header in HTTP POST requests to simulate Cross-Origin
 *         Resource Sharing (CORS) requests from a browser.</li>
 *     <li>Customise the allowable "Content-Type" header values in HTTP POST
 *         responses.</li>
 *     <li>Preserve parse order of JSON object members in JSON-RPC 2.0 response
 *         results (for human facing clients, e.g. the JSON-RPC 2.0 Shell).</li>
 *     <li>Disable strict JSON-RPC 2.0 parsing of responses to allow sessions
 *         with older JSON-RPC (1.0) servers.</li>
 *     <li>Trust all X.509 server certificates (for HTTPS connections), 
 *         including self-signed.</li>
 * </ul>
 *
 * <p>Example:
 *
 * <pre>
 * // First, import the required packages:
 * 
 * // The Client sessions package
 * import com.thetransactioncompany.jsonrpc2.client.*;
 * 
 * // The Base package for representing JSON-RPC 2.0 messages
 * import com.thetransactioncompany.jsonrpc2.*;
 * 
 * // The JSON.simple package for JSON encoding/decoding (optional)
 * import org.json.simple.*;
 * 
 * // For creating URLs
 * import java.net.*;
 * 
 * // ...
 * 
 * 
 * // Creating a new session to a JSON-RPC 2.0 web service at a specified URL
 * 
 * // The JSON-RPC 2.0 server URL
 * URL serverURL = null;
 * 
 * try {
 * 	serverURL = new URL("http://jsonrpc.example.com:8080");
 * 	
 * } catch (MalformedURLException e) {
 * 	// handle exception...
 * }
 * 
 * // Create new JSON-RPC 2.0 client session
 *  JSONRPC2Session mySession = new JSONRPC2Session(serverURL);
 * 
 * 
 * // Once the client session object is created, you can use to send a series
 * // of JSON-RPC 2.0 requests and notifications to it.
 * 
 * // Sending an example "getServerTime" request:
 * 
 *  // Construct new request
 *  String method = "getServerTime";
 *  int requestID = 0;
 *  JSONRPC2Request request = new JSONRPC2Request(method, requestID);
 * 
 *  // Send request
 *  JSONRPC2Response response = null;
 * 
 *  try {
 *          response = mySession.send(request);
 * 
 *  } catch (JSONRPC2SessionException e) {
 * 
 *          System.err.println(e.getMessage());
 *          // handle exception...
 *  }
 * 
 *  // Print response result / error
 *  if (response.indicatesSuccess())
 * 	System.out.println(response.getResult());
 *  else
 * 	System.out.println(response.getError().getMessage());
 * 
 * </pre>
 *
 * @author <a href="http://dzhuvinov.com">Vladimir Dzhuvinov</a>
 * @version 1.2 (2011-03-29)
 */
public class JSONRPC2Session {
	private static final Log _log;
	
	static {
		_log = LogFactory.getLog(JSONRPC2Session.class);
	}

	/** 
	 * The server URL, which protocol must be HTTP or HTTPS. 
	 *
	 * <p>Example URL: "http://jsonrpc.example.com:8080"
	 */
	private URL url;
	
	
	/**
	 * The "Content-Type" (MIME) header value of HTTP POST requests. If
	 * {@code null} the header will not be set.
	 */
	private String requestContentType = DEFAULT_CONTENT_TYPE;
	
	
	/**
	 * The default "Content-Type" (MIME) header value of HTTP POST requests.
	 * If {@code null} the header will not be set.
	 */
	public static final String DEFAULT_CONTENT_TYPE = "application/json";
	
	
	/**
	 * The allowed "Content-Type" (MIME) header values of HTTP responses. If
	 * {@code null} any header value will be accepted.
	 */
	private String[] allowedResponseContentTypes = DEFAULT_ALLOWED_RESPONSE_CONTENT_TYPES;
	
	
	/**
	 * The default allowed "Content-Type" (MIME) header values of HTTP
	 * responses.
	 */
	public static final String[] DEFAULT_ALLOWED_RESPONSE_CONTENT_TYPES = {"application/json", "text/plain"};
	
	
	/** 
	 * Optional CORS Origin header. If {@code null} the header will not be
	 * set.
	 */
	private String origin = null;
	
	
	/**
	 * If {@code true} the order of parsed JSON object members must be
	 * preserved.
	 */
	private boolean preserveObjectMemberOrder = false;
	
	
	/**
	 * If {@code false} received JSON-RPC 2.0 responses must conform strictly
	 * to the JSON-RPC 2.0 specification. If {@code true} parsing will be
	 * relaxed and the "jsonrpc" version field in responses is not checked.
	 */
	private boolean strictParsingDisabled = false;
	
	
	/**
	 * Custom socket factory for HTTPS connections (if not {@code null}).
	 */
	private SSLSocketFactory sslSocketFactory = null;


	/**
	 * Creates a new client session to a JSON-RPC 2.0 server at the
	 * specified URL.
	 *
	 * <p>The "Content-Type" (MIME) header value of HTTP POST requests will
	 * be set to "application/json". To change it use 
	 * {@link #setRequestContentType}.
	 *
	 * <p>"Origin" headers will not be added. To add one use
	 * {@link #setOrigin}.
	 *
	 * <p>The allowed HTTP response content types are set to 
	 * "application/json" and "text/plain".
	 *
	 * @param url The server URL, e.g. "http://jsonrpc.example.com:8080".
	 */
	public JSONRPC2Session (final URL url) {
		
		if (! url.getProtocol().equals("http") && ! url.getProtocol().equals("https"))	
			throw new IllegalArgumentException("The URL protocol must be HTTP or HTTPS");
		
		this.url = url;
	}
	
	
	/**
	 * Gets the JSON-RPC 2.0 server URL.
	 *
	 * @return The server URL.
	 */
	public URL getURL() {
		
		return url;
	}
	
	
	/**
	 * Sets the JSON-RPC 2.0 server URL.
	 *
	 * @param url The server URL.
	 */
	public void setURL(final URL url) {
	
		this.url = url;
	}
	
	
	/**
	 * Gets the value of the "Content-Type" (MIME) header for HTTP POST
	 * requests.
	 *
	 * @return The "Content-Type" (MIME) header value, {@code null} if the
	 *         header is not added to HTTP POST requests.
	 */
	public String getRequestContentType() {
	
		return requestContentType;
	}
	
	
	/**
	 * Sets the value of the "Content-Type" (MIME) header. Use this method
	 * if you wish to change the default "application/json" content type.
	 *
	 * @param contentType The value of the "Content-Type" (MIME) header
	 *                    in HTTP POST requests, {@code null} to suppress
	 *                    the header.
	 */
	public void setRequestContentType(final String contentType) {
	
		this.requestContentType = contentType;
	}
	
	
	/**
	 * Gets the value of the "Origin" HTTP header.
	 *
	 * <p>This header can be used to simulate Cross-Origin Resource Sharing
	 * (CORS) requests from a browser.
	 *
	 * @return The "Origin" header value, {@code null} if the header is not
	 *         added to HTTP requests.
	 */
	public String getOrigin() {
		
		return origin;
	}
	
	
	/**
	 * Sets the value of the "Origin" HTTP header.
	 *
	 * <p>This header can be used to simulate Cross-Origin Resource Sharing
	 * (CORS) requests from a browser.
	 *
	 * @param origin The value of the "Origin" header in HTTP requests, 
	 *               {@code null} to suppress the header.
	 */
	public void setOrigin(final String origin) {
	
		this.origin = origin;
	}
	
	
	/**
	 * Gets the allowed "Content-Type" (MIME) header values of HTTP 
	 * responses. 
	 *
	 * <p>The {@code send(...)} method will throw an 
	 * {@link JSONRPC2SessionException#UNEXPECTED_CONTENT_TYPE} if the
	 * received HTTP response "Content-Type" (MIME) header value is not
	 * allowed.
	 * 
	 *
	 * @return The allowed header values, if {@code null} any header value 
	 *         is allowed.
	 */
	public String[] getAllowedResponseContentTypes() {
	
		return allowedResponseContentTypes;
	}
	
	
	/**
	 * Sets the allowed "Content-Type" (MIME) header values of HTTP 
	 * responses.
	 *
	 * <p>The {@code send(...)} method will throw an 
	 * {@link JSONRPC2SessionException#UNEXPECTED_CONTENT_TYPE} if the
	 * received HTTP response "Content-Type" (MIME) header value is not
	 * allowed.
	 *
	 * @param contentTypes The allowed header values, {@code null} to allow
	 *                     any header value.
	 */
	public void setAllowedResponseContentTypes(final String[] contentTypes) {
	
		this.allowedResponseContentTypes = contentTypes;
	}
	
	
	/**
	 * Checks if the specified "Content-Type" (MIME) header value is 
	 * allowed.
	 *
	 * @param contentType The "Content-Type" (MIME) header value.
	 *
	 * @return {@code true} if the content type is allowed, else 
	 *         {@code false}.
	 */
	public boolean isAllowedResponseContentType(final String contentType) {
	
		// Allow any?
		if (allowedResponseContentTypes == null)
			return true;
		
		if (contentType == null)
			return false; // missing
		
		for (String t: allowedResponseContentTypes) {

			// Note: the content type may include optional parameters, e.g.
			// "application/json; charset=ISO-8859-1; ..."	
			if (contentType.matches("^" + Pattern.quote(t) + "(\\s|;|$)?.*"))
				return true;
		}
		
		return false; // nothing matched
	}
	
	
	/**
	 * Returns {@code true} if the member order of parsed JSON objects in
	 * JSON-RPC 2.0 response results is preserved.
	 *
	 * @return {@code true} if the parse order of JSON object members is
	 *         preserved, else {@code false}.
	 */
	public boolean preservesParseOrder() {
	
		return preserveObjectMemberOrder;
	}
	
	
	/**
	 * Controls the behaviour of the JSON parser when processing object
	 * members in JSON-RPC 2.0 response results. The default behaviour is
	 * to store the members in a {@code HashMap} in a non-deterministic
	 * order. To preserve the original parse order pass a boolean 
	 * {@code true}. Note that this will slow down parsing and retrieval
	 * performance somewhat.
	 *
	 * @param preserve If {@code true} the parse order of JSON object 
	 *                 members will be preserved, else not.
	 */
	public void preserveParseOrder(final boolean preserve) {
	
		preserveObjectMemberOrder = preserve;
	}
	
	
	/**
	 * Returns {@code true} if strict parsing of received JSON-RPC 2.0
	 * responses is disabled and the "jsonrpc" version field is not checked.
	 * Returns {@code false} if received JSON-RPC 2.0 responses must 
	 * strictly conform to the JSON-RPC 2.0 specification.
	 *
	 * @return {@code true} if strict parsing of JSON-RPC 2.0 is disabled,
	 *         else {@code false}.
	 */
	public boolean strictParsingDisabled() {
	
		return strictParsingDisabled;
	}
	
	
	/**
	 * Controls the strictness of the JSON-RPC 2.0 response parser. The
	 * default behaviour is to check responses for strict compliance to
	 * the JSON-RPC 2.0 specification. By passing a boolean {@code true}
	 * parsing is relaxed and the "jsonrpc" version field will not be
	 * checked.
	 *
	 * @param disable If {@code true} strict will be disabled, else not.
	 */
	public void disableStrictParsing(final boolean disable) {
	
		strictParsingDisabled = disable;
	}
	
	
	/**
	 * Controls checking of X.509 certificates presented by the server when
	 * establishing a secure HTTPS connection. The default behaviour is to 
	 * accept only certicates issued by a trusted certificate authority 
	 * (CA), as determined by the default Java trust store. By passing a
	 * boolean {@code false} this security check is disabled and all 
	 * certificates will be trusted, including self-signed ones. Use this
	 * for testing and development purposes only.
	 *
	 * @param trustAll If {@code true} all X.509 certificates presented by 
	 *                 the web server will be trusted, including self-signed
	 *                 ones. If {@code false} the default security policy
	 *                 will be restored.
	 */
	public void trustAllCerts(final boolean trustAll) {
	
		if (trustAll) {
		
			TrustManager[] trustAllCerts = new TrustManager[] {
				new X509TrustManager() {
					public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[]{}; }
					public void checkClientTrusted(X509Certificate[] certs, String authType) { }
					public void checkServerTrusted(X509Certificate[] certs, String authType) { }
				}
			};

		
			try {
				SSLContext sc = SSLContext.getInstance("SSL");
    				sc.init(null, trustAllCerts, new SecureRandom());
				this.sslSocketFactory = sc.getSocketFactory();
				
			} catch (Exception e) {
				// ignore
			}
		}
		else {
			// clear custom socket factory
			this.sslSocketFactory = null;
		}
	}
	
	
	/**
	 * Applies the required headers to the specified URL connection.
	 *
	 * @param con The URL connection which must be open.
	 */
	private void applyHeaders(final URLConnection con) {
	
		// Add "Content-Type" header?
		if (requestContentType != null)
			con.setRequestProperty("Content-Type", requestContentType);
		
		// Add "Origin" header?
		if (origin != null)
			con.setRequestProperty("Origin", origin);
	}
	
	
	/** 
	 * Sends a JSON-RPC 2.0 request using HTTP POST and returns the server
	 * response.
	 *
	 * @param request The JSON-RPC 2.0 request to send.
	 *
	 * @return The JSON-RPC 2.0 response returned by the server.
	 *
	 * @throws JSONRPC2SessionException On a network error, unexpected
	 *                                  HTTP response content type or 
	 *                                  invalid JSON-RPC 2.0 response.
	 */
	public JSONRPC2Response send(final JSONRPC2Request request)
		throws JSONRPC2SessionException {
	
		// Open HTTP connection
		URLConnection con = null;
		
		try {
			con = url.openConnection();
			
		} catch (IOException e) {
		
			throw new JSONRPC2SessionException(
				"Network exception", 
				JSONRPC2SessionException.NETWORK_EXCEPTION,
				e);
		}
		
		applyHeaders(con);
		
		// Set POST mode
		con.setDoOutput(true);
		
		// Set trust all certs SSL factory?
		if (con instanceof HttpsURLConnection && sslSocketFactory != null) 
			((HttpsURLConnection)con).setSSLSocketFactory(sslSocketFactory);
		
		// Send request encoded as JSON
		try {
			OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
			wr.write(request.toString());
			wr.flush();
			wr.close();
			
		} catch (IOException e) {
			
			throw new JSONRPC2SessionException(
				"Network exception",
				JSONRPC2SessionException.NETWORK_EXCEPTION,
				e);
		}
		
		StringBuilder responseText = new StringBuilder();
			
		// Get the response
		
		try {
			BufferedReader input = new BufferedReader(new InputStreamReader(con.getInputStream()));
			
			String line;
			while ((line = input.readLine()) != null) {
				responseText.append(line);
				responseText.append(System.getProperty("line.separator"));
			}
			
			input.close();
		
		} catch (IOException e) {
			
			throw new JSONRPC2SessionException(
				"Network exception",
				JSONRPC2SessionException.NETWORK_EXCEPTION,
				e);
		}
		
		// Check response content type?
		if (allowedResponseContentTypes != null) {

			String mimeType = con.getHeaderField("Content-Type");
			
			if (! isAllowedResponseContentType(mimeType)) {
			
				throw new JSONRPC2SessionException(
					"The server returned an unexpected content type '" + mimeType + "' response", 
					JSONRPC2SessionException.UNEXPECTED_CONTENT_TYPE);
			}
		}

		// Parse and return the response
		JSONRPC2Response response = null;
		
		try {
			response = JSONRPC2Response.parse(responseText.toString(), preserveObjectMemberOrder, strictParsingDisabled);
			
		} catch (JSONRPC2ParseException e) {
		
			throw new JSONRPC2SessionException(
				"Invalid JSON-RPC 2.0 response",
				JSONRPC2SessionException.BAD_RESPONSE,
				e);
		}
		
		// Response ID must match the request ID, except for
		// -32700 (parse error), -32600 (invalid request) and 
		// -32603 (internal error)
		
		Object reqID = request.getID();
		Object resID = response.getID();
		
		if (reqID != null && resID !=null && reqID.toString().equals(resID.toString()) ) {
		
			// ok
		}
		else if (reqID == null && resID == null) {
		
			// ok
		}
		else if (! response.indicatesSuccess() && ( response.getError().getCode() == -32700 ||
		                                            response.getError().getCode() == -32600 ||
							    response.getError().getCode() == -32603    )) {
							    
			// ok
		}
		else {
			throw new JSONRPC2SessionException(
				"Invalid JSON-RPC 2.0 response: ID mismatch: Returned " + resID.toString() + ", expected " + reqID.toString(),
				JSONRPC2SessionException.BAD_RESPONSE);
		}
			                                   
		
		return response;
	}
	
	
	/**
	 * Sends a JSON-RPC 2.0 notification using HTTP POST. Note that 
	 * contrary to requests, notifications produce no server response.
	 *
	 * @param notification The JSON-RPC 2.0 notification to send.
	 *
	 * @throws JSONRPC2SessionException On a network error.
	 */
	public void send(final JSONRPC2Notification notification)
		throws JSONRPC2SessionException {
		
		// Open HTTP connection
		URLConnection con = null;
		
		try {
			con = url.openConnection();
			
		} catch (IOException e) {
		
			throw new JSONRPC2SessionException(
				"Network exception", 
				JSONRPC2SessionException.NETWORK_EXCEPTION,
				e);
		}
		
		applyHeaders(con);
		
		// Set POST mode
		con.setDoOutput(true);
		
		// Set trust all certs SSL factory?
		if (con instanceof HttpsURLConnection && sslSocketFactory != null) 
			((HttpsURLConnection)con).setSSLSocketFactory(sslSocketFactory);
		
		
		// Send notification encoded as JSON
		try {
			OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
			wr.write(notification.toString());
			wr.flush();
			wr.close();
			
		} catch (IOException e) {
			
			throw new JSONRPC2SessionException(
				"Network exception",
				JSONRPC2SessionException.NETWORK_EXCEPTION,
				e);
		}
	}
}

