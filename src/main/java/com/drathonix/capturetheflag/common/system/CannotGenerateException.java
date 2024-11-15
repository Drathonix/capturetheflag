package com.drathonix.capturetheflag.common.system;

public class CannotGenerateException extends RuntimeException {

    public CannotGenerateException() {
    }

    public CannotGenerateException(String message) {
        super(message);
    }

    public CannotGenerateException(String message, Throwable cause) {
        super(message, cause);
    }

    public CannotGenerateException(Throwable cause) {
        super(cause);
    }

    public CannotGenerateException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
