package MFA;

import java.math.BigInteger;

/**
 * Class that represents a secret share.
 *
 * @author Igor Sytnik
 */
public class SecretShare {

    private final int number;
    private final BigInteger share;

    public SecretShare(final int number, final BigInteger share) {
        this.number = number;
        this.share = share;
    }

    public int getNumber() {
        return number;
    }

    public BigInteger getShare() {
        return share;
    }

    @Override
    public String toString() {
        return String.format("%d-%s", getNumber(), getShare().toString());
    }
}