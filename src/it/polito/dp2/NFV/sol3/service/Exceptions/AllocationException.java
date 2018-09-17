package it.polito.dp2.NFV.sol3.service.Exceptions;

public class AllocationException extends Exception {

    public AllocationException() {
    }

    public AllocationException(String message) {
        super(message);
    }

    public AllocationException(Throwable cause) {
        super(cause);
    }

    public AllocationException(String message, Throwable cause) {
        super(message, cause);
    }

    public AllocationException(String message, Throwable cause, boolean enableSuppression,
                               boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
