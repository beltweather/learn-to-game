package com.jharter.game.util.id;

import java.util.UUID;

/**
 * The id class to use for generic ids. This is just a clone of Java's
 * UUID with unnecessary methods removed and an empty constructor.
 * 
 * @author Jon
 *
 */
public class ID implements java.io.Serializable, Comparable<ID> {
	
	private static final long serialVersionUID = 1L;
	
	private long mostSigBits;
	private long leastSigBits;
	
	@SuppressWarnings("unused")
	private ID() {}
	
	protected ID(UUID uuid) {
		this(uuid.getMostSignificantBits(), uuid.getLeastSignificantBits());
	}

	protected ID(long mostSigBits, long leastSigBits) {
		this.mostSigBits = mostSigBits;
		this.leastSigBits = leastSigBits;
	}
	
	/** Returns val represented by the specified number of hex digits. */
    private static String digits(long val, int digits) {
        long hi = 1L << (digits * 4);
        return Long.toHexString(hi | (val & (hi - 1))).substring(1);
    }
	
	public String toString() {
		return (digits(mostSigBits >> 32, 8) + "-" +
                digits(mostSigBits >> 16, 4) + "-" +
                digits(mostSigBits, 4) + "-" +
                digits(leastSigBits >> 48, 4) + "-" +
                digits(leastSigBits, 12));
	}
	
	/**
     * Compares this ID with the specified ID.
     *
     * <p> The first of two IDs is greater than the second if the most
     * significant field in which the IDs differ is greater for the first
     * ID.
     *
     * @param  val
     *         {@code ID} to which this {@code ID} is to be compared
     *
     * @return  -1, 0 or 1 as this {@code ID} is less than, equal to, or
     *          greater than {@code val}
     *
     */
    public int compareTo(ID val) {
        // The ordering is intentionally set up so that the IDs
        // can simply be numerically compared as two numbers
        return (this.mostSigBits < val.mostSigBits ? -1 :
                (this.mostSigBits > val.mostSigBits ? 1 :
                 (this.leastSigBits < val.leastSigBits ? -1 :
                  (this.leastSigBits > val.leastSigBits ? 1 :
                   0))));
    }
    
    /**
     * Returns a hash code for this {@code ID}.
     *
     * @return  A hash code value for this {@code ID}
     */
    public int hashCode() {
        long hilo = mostSigBits ^ leastSigBits;
        return ((int)(hilo >> 32)) ^ (int) hilo;
    }

    /**
     * Compares this object to the specified object.  The result is {@code
     * true} if and only if the argument is not {@code null}, is a {@code ID}
     * object, has the same variant, and contains the same value, bit for bit,
     * as this {@code UUID}.
     *
     * @param  obj
     *         The object to be compared
     *
     * @return  {@code true} if the objects are the same; {@code false}
     *          otherwise
     */
    public boolean equals(Object obj) {
        if ((null == obj) || (obj.getClass() != ID.class))
            return false;
        ID id = (ID)obj;
        return (mostSigBits == id.mostSigBits &&
                leastSigBits == id.leastSigBits);
    }
}
