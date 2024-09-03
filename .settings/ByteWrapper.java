//Final Project - Nir Hazan

package LZW;


import java.util.Arrays;

/**
 * Helper class to handle byte arrays as dictionary keys.
 *
 * This class ensures that byte arrays can be used as keys in a HashMap by correctly implementing
 * equals() and hashCode(). This is necessary because Java's native byte array does not override these
 * methods, which can lead to incorrect behavior when using byte arrays as map keys.
 * ByteWrapper is used to ensure that byte arrays are compared based on their content rather than their memory reference.
 * 
 * 
 * we used help from "https://docs.oracle.com/javase/8/docs/api/java/util/Arrays.html"
 * also from : "https://docs.oracle.com/javase/8/docs/api/java/lang/System.html" , "https://docs.oracle.com/javase/8/docs/api/java/lang/Byte.html"
 */
public class ByteWrapper {
    
	private final byte[] data;

    /**
     * Concatenates a byte array with a single byte.
     *
     * This utility function helps build new sequences during compression and decompression by adding a
     * single byte to the end of an existing sequence. This operation is critical for reconstructing data
     * accurately during the decompression process.
     *
     * @param prefix The byte array to concatenate.
     * @param b The byte to add to the end of the byte array.
     * @return A new byte array containing the combined data.
     */
    public static byte[] concat(byte[] prefix, byte b) {
        byte[] result = new byte[prefix.length + 1];
        System.arraycopy(prefix, 0, result, 0, prefix.length);
        result[result.length - 1] = b;
        return result;
    }
    
    /**
     * Constructs a ByteArrayWrapper from the given byte array.
     *
     * @param data The byte array to wrap.
     */
    public ByteWrapper(byte[] data) {
        this.data = data;
    }

    /**
     * Constructs a ByteArrayWrapper from a sub array of the given byte array.
     *
     * @param data   The byte array to wrap.
     * @param offset The starting offset of the sub array.
     * @param length The length of the sub array.
     */
    public ByteWrapper(byte[] data, int offset, int length) {
        this.data = new byte[length];
        System.arraycopy(data, offset, this.data, 0, length);
        
    }

    /**
     * Compares this ByteWrapper to another object. Two ByteWrapper objects are equal
     * if they have the same byte content.
     *
     * This override ensures that the dictionary correctly recognizes sequences that are the
     * same and avoids adding duplicates.
     *
     * @param obj The object to compare this ByteArrayWrapper against.
     * @return true if the given object represents a ByteArrayWrapper with the same byte content, false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ByteWrapper)) 
        	return false;
        return Arrays.equals(data, ((ByteWrapper) obj).data);
    }

    /**
     * Returns a hash code for this ByteWrapper. The hash code is computed based on the
     * contents of the byte array.
     *
     * This override ensures that the hash code is consistent with the equals() method,
     * preventing hash collisions in the dictionary.
     *
     * @return A hash code value for this ByteArrayWrapper.
     */
    @Override
    public int hashCode() {
        return Arrays.hashCode(data);
    }
}
