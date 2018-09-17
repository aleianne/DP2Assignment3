package it.polito.dp2.NFV.sol3.service.Exceptions;

public class NoNodeException extends Exception {

    public NoNodeException() {
    }

    public NoNodeException(String message) {
        super(message);
    }

    public NoNodeException(Throwable cause) {
        super(cause);
    }

    public NoNodeException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoNodeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
