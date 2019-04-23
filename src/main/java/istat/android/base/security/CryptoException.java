package istat.android.base.security;

public class CryptoException
        extends Exception {
    public CryptoException() {
        super();
    }

    /**
     * @param reason the reason why the exception was thrown.
     */
    public CryptoException(String reason) {
        super(reason);
    }
}