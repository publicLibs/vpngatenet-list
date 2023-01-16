/**
 *
 */
package com.github.publiclibs.vpngatenet.list.exceptions;

/**
 * @author freedom1b2830
 * @date 2023-января-15 15:47:37
 */
public class InputException extends RuntimeException {

	/**
	 *
	 */
	public InputException() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 */
	public InputException(final String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param cause
	 */
	public InputException(final String message, final Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public InputException(final String message, final Throwable cause, final boolean enableSuppression,
			final boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param cause
	 */
	public InputException(final Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

}
