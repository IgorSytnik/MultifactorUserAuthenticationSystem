package MFA;

import java.math.BigInteger;
import java.util.Random;

/**
 * Class that represents Shamir secret share scheme.
 *
 * @author Igor Sytnik
 */
public class Shamir {

    /**
     * Splits <b>secret</b> into <b>available</b> shares of class {@link SecretShare},
     * <b>needed</b> number of which are needed for the <b>secret</b> reconstruction.
     *
     * @param secret a {@link BigInteger} that has to be split into <b>shares</b>.
     * @param needed a number of <b>shares</b> needed for restoring the <b>secret</b>.
     * @param available a number of resulting <b>shares</b>.
     * @param prime a prime number to make the scheme more secure.
     * @param random an instance of a random number generator.
     * @return generated shares.
     */
    public static SecretShare[] split(BigInteger secret, int needed, int available, BigInteger prime, Random random) {
        final BigInteger[] coeff = new BigInteger[needed];
        coeff[0] = secret;
        /* Generates **needed** number of BigInteger objects that more than 0 and less than prime */
        for (int i = 1; i < needed; i++) {
            BigInteger r = new BigInteger(prime.bitLength(), random);
            while (r.compareTo(BigInteger.ZERO) < 1 && r.compareTo(prime) > -1) {
                r = new BigInteger(prime.bitLength(), random);
            }
            coeff[i] = r;
        }

        final SecretShare[] shares = new SecretShare[available];
        /* Splits into shares */
        for (int x = 1; x <= available; x++) {
            BigInteger accum = secret;

            for (int exp = 1; exp < needed; exp++) {
                accum = accum.add(coeff[exp].multiply(BigInteger.valueOf(x).pow(exp).mod(prime))).mod(prime);
                // accum = (accum + coef[exp] * (x ^ exp % prime)) % prime
            }
            shares[x - 1] = new SecretShare(x, accum);
        }

        return shares;
    }

    /**
     * Combines <b>shares</b> into a secret.
     *
     * @param shares shares that the <b>secret</b> was split into.
     * @param prime a prime number to make the scheme more secure.
     *              This prime number was used to split the secret.
     * @return a secret from combining all the <b>shares</b>.
     */
    public static BigInteger combine(final SecretShare[] shares, final BigInteger prime) {
        BigInteger accum = BigInteger.ZERO;

        for(int formula = 0; formula < shares.length; formula++) {
            BigInteger numerator = BigInteger.ONE;
            BigInteger denominator = BigInteger.ONE;

            for(int count = 0; count < shares.length; count++) {
                if(formula == count)
                    continue; // If the same value

                int startPosition = shares[formula].getNumber();
                int nextPosition = shares[count].getNumber();

                numerator = numerator.multiply(BigInteger.valueOf(nextPosition).negate()).mod(prime); // (numerator * -nextPosition) % prime;
                denominator = denominator.multiply(BigInteger.valueOf(startPosition - nextPosition)).mod(prime); // (denominator * (startPosition - nextPosition)) % prime;
            }
            BigInteger value = shares[formula].getShare();
            BigInteger tmp = value.multiply(numerator).multiply(denominator.modInverse(prime));

            accum = accum.add(tmp).mod(prime);
        }

        return accum;
    }
}
