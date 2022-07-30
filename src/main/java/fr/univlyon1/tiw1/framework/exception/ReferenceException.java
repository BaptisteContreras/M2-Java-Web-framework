package fr.univlyon1.tiw1.framework.exception;

public class ReferenceException extends Exception {
    public ReferenceException() {
    }

    public ReferenceException(String message) {
        super(message);
    }

    public ReferenceException(String message, Throwable cause) {
        super(message, cause);
    }

    public ReferenceException(Throwable cause) {
        super(cause);
    }

    public ReferenceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
