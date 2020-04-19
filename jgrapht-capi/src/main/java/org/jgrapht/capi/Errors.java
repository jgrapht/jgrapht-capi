package org.jgrapht.capi;

import org.graalvm.nativeimage.c.type.CCharPointer;
import org.graalvm.nativeimage.c.type.CTypeConversion;
import org.graalvm.nativeimage.c.type.CTypeConversion.CCharPointerHolder;

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

}
