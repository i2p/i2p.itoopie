package com.thetransactioncompany.jsonrpc2.client;


/**
 * Thrown to indicate a JSON-RPC 2.0 client session exception. Allows a general
 * cause type to be specified to ease diagnostics and exception reporting.
 *
 * @author Vladimir Dzhuvinov
 * @version 1.2 (2011-03-09)
 */
public class JSONRPC2SessionException extends Exception {

	
	/**
	 * The exception cause is network or I/O related.
	 */
	public static final int NETWORK_EXCEPTION = 1;
	
	
	/**
	 * Unexpected "Content-Type" header value of the HTTP response.
	 */
	public static final int UNEXPECTED_CONTENT_TYPE = 2;
	
	
	/**
	 * Invalid JSON-RPC 2.0 response.
	 */
	public static final int BAD_RESPONSE = 3;
	
	
	/**
	 * Indicates the type of cause for this exception, see
	 * constants.
	 */
	private int causeType;
	
	
	/**
	 * Creates a new JSON-RPC 2.0 session exception with the specified 
	 * message and cause type.
	 *
	 * @param message   The message.
	 * @param causeType The cause type, see the constants.
	 */
	public JSONRPC2SessionException(final String message, final int causeType) {
	
		super(message);
		this.causeType = causeType;
	}
	
	
	/**
	 * Creates a new JSON-RPC 2.0 session exception with the specified 
	 * message, cause type and cause.
	 *
	 * @param message   The message.
	 * @param causeType The cause type, see the constants.
	 * @param cause     The original exception.
	 */
	public JSONRPC2SessionException(final String message, final int causeType, final Throwable cause) {
	
		super(message, cause);
		this.causeType = causeType;
	}
	
	
	/**
	 * Returns the exception cause type.
	 *
	 * @return The cause type constant.
	 */
	public int getCauseType() {
	
		return causeType;
	}
}
