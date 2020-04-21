package org.jgrapht.capi.error;

import java.util.NoSuchElementException;

import org.graalvm.nativeimage.c.type.CCharPointer;
import org.graalvm.nativeimage.c.type.CTypeConversion;
import org.graalvm.nativeimage.c.type.CTypeConversion.CCharPointerHolder;
import org.jgrapht.capi.Status;

/**
 * Error handling
 */
public class Errors {

	private static final String NO_MESSAGE = "";
	private static final CCharPointerHolder NO_MESSAGE_PIN = CTypeConversion.toCString(NO_MESSAGE);

	private static Status error = Status.SUCCESS;
	private static String message = NO_MESSAGE;
	private static CCharPointerHolder messagePin = NO_MESSAGE_PIN;

	public static Status getError() {
		return error;
	}

	public static String getMessage() {
		return message;
	}

	public static CCharPointer getMessageCCharPointer() {
		return messagePin.get();
	}

	public static void setError(Status newError) {
		setError(newError, NO_MESSAGE);
	}

	public static void setError(Status newError, String newMessage) {
		error = newError;
		message = newMessage;
		messagePin = CTypeConversion.toCString(message);
	}

	public static void clearError() {
		error = Status.SUCCESS;
		message = NO_MESSAGE;
		messagePin = NO_MESSAGE_PIN;
	}

	public static void setError(Throwable e) {
		Status newError;
		if (e instanceof IllegalArgumentException) {
			newError = Status.ILLEGAL_ARGUMENT;
		} else if (e instanceof UnsupportedOperationException) {
			newError = Status.UNSUPPORTED_OPERATION;
		} else if (e instanceof IndexOutOfBoundsException) {
			newError = Status.INDEX_OUT_OF_BOUNDS;
		} else if (e instanceof NoSuchElementException) {
			newError = Status.NO_SUCH_ELEMENT;
		} else if (e instanceof NullPointerException) {
			newError = Status.NULL_POINTER;
		} else {
			newError = Status.ERROR;
		}
		String newMsg = e.getMessage();
		if (newMsg == null) {
			StringBuilder sb = new StringBuilder();
			sb.append("Error");
			String exceptionClassName = e.getClass().getSimpleName();
			if (exceptionClassName != null) {
				sb.append(" (");
				sb.append(exceptionClassName);
				sb.append(")");
			}
			newMsg = sb.toString();
		}
		setError(newError, newMsg);
	}

}
