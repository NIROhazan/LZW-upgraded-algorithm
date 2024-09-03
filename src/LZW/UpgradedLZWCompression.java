	//Final Project - Nir Hazan 316009489 & May Setter 312123037
	package LZW;
	
	import java.io.ByteArrayOutputStream;
	import java.io.IOException;
	import java.util.HashMap;
	import java.util.Map;
	
	/**
	 * Upgraded LZW Compression Algorithm with Dynamic Dictionary Management.
	 *
	 * This implementation of the LZW compression algorithm includes several improvements over the original LZW
	 * algorithm. It limits the maximum size of the dictionary to prevent excessive memory usage and dynamically manages
	 * the dictionary size to ensure consistent performance.
	 *
	 * Features:
	 *
	 *   1. Dictionary Size Limitation: Limits the dictionary to selected entries to prevent excessive memory usage.
	 *
	 *   2. Dynamic Dictionary Management: Resets the dictionary when it reaches its maximum size or over minimum compression ratio to maintain consistent
	 *      performance and prevent uncontrolled growth.
	 *      Default parameters are best way to compress files with repetitive data.
	 */
	public class UpgradedLZWCompression {
	
	    /** Maximum dictionary size to limit memory usage - default value 16384. */
	    private static int maxDictionarySize =16384;
	
	    /** Initial dictionary size - default value 256*/
	    private static  int initialDictionarySize = 256;
	    
	
	    /** Minimum compression ratio before resetting the dictionary - default 0.7 */
	    // Data Compression Ratio - measurement of the relative reduction in size of data representation produced by a data compression algorithm.
	    //help us to control the dictionary reset and help the compress.
	    private static double minCompressionRatio = 0.7;
	
	    /** Marker indicates a dictionary reset during compression */
	    private static final int RESET_MARKER = -1;
	
	    /*Variable to calculate the progress of decompress/compress*/
	    public static int PROGRESS;
	    /**
	     * Sets the initial dictionary size for the upgraded LZW compression algorithm.
	     *
	     * @param size The size of the initial dictionary to be set. 
	     *             Common values are  256 and 512.
	     */
	    public static void setInitialDictionarySize(int size) {
	        initialDictionarySize = size;
	    }
	
	    /**
	     * Sets the maximum dictionary size for the LZW upgraded compression algorithm.
	     *
	     * @param size The maximum size of the dictionary to be set. 
	     *             Common values are 4096, 8192, and 16384.
	     */
	    public static void setMaxDictionarySize(int size) {
	        maxDictionarySize = size;
	    }
	    
	    /**
	     * Sets the minimum compression ratio to be used by the compression algorithm.
	     * 
	     * @param ratio The minimum compression ratio (0 < ratio < 1)
	     * @throws IllegalArgumentException if the ratio is not within the valid range
	     */
	    public static void setMinCompressionRatio(double ratio) {
	        if (ratio <= 0 || ratio >= 1) {
	            throw new IllegalArgumentException("Compression ratio must be between 0 and 1.");
	        }
	        minCompressionRatio = ratio;
	    }
	
	    /**
	     * Compresses a byte array using the upgraded LZW algorithm with dynamic dictionary management.
	     *
	     * @param input The byte array to compress.
	     * @return A compressed byte array.
	     * @throws IOException if an I/O error occurs during compression.
	     */
	    public static byte[] compress(byte[] input) throws IOException {
	        // Initialize the dictionary with single-byte sequences
	        Map<ByteWrapper, Integer> dictionary = new HashMap<>();
	        for (int i = 0; i < initialDictionarySize; i++) {
	            dictionary.put(new ByteWrapper(new byte[]{(byte) i}), i);
	        }
	
	        int dictSize = initialDictionarySize; // Current dictionary size
	        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(); // Output stream to store compressed data
	        ByteArrayOutputStream currentBytes = new ByteArrayOutputStream(); // Buffer for current sequence of bytes
	
	        int inputSize = input.length; // Size of the input data
	        int compressedSize = 0; // Size of the compressed data
	        int checkCompressedSize=0;  //variables to show the progress to the user ...
	        
	        // Loop through each byte in the input
	        for (byte b : input) {
	            currentBytes.write(b); // Append current byte to the sequence
	            
	         // Calculate the progress percentage
	            PROGRESS = (int) ((checkCompressedSize++/ (double) inputSize) * 100);
	           GUI.progressBar.setValue(PROGRESS);
	           
	            ByteWrapper currentBytesWrapper = new ByteWrapper(currentBytes.toByteArray());
	
	            // Check if the current sequence is in the dictionary
	            if (dictionary.containsKey(currentBytesWrapper)) {
	                continue; // Continue building the sequence if it's in the dictionary
	            } else {
	                // Sequence not in dictionary: output the code for the previous sequence
	                ByteWrapper previousBytesWrapper = new ByteWrapper(currentBytes.toByteArray(), 0, currentBytes.size() - 1);
	                int code = dictionary.get(previousBytesWrapper); // Get the numerical code from the dictionary
	                outputStream.write((byte) (code / 256)); // Write the high byte by dividing by 256
	                outputStream.write((byte) (code % 256)); // Write the low byte using modulus 256
	
	                compressedSize += 2; // Increment the compressed size
	                // Add new sequence to the dictionary if there is space
	                if (dictSize < maxDictionarySize) {
	                    dictionary.put(currentBytesWrapper, dictSize++);
	                }
	                 // Calculate the compression ratio
	                 double compressionRatio = (double) compressedSize / inputSize;
	
	                 if (compressionRatio > minCompressionRatio && dictSize >= maxDictionarySize) {
	                     outputStream.write((byte) (RESET_MARKER / 256)); // Write the reset marker's high byte
	                     outputStream.write((byte) (RESET_MARKER % 256)); // Write the reset marker's low byte
	
	                     // Reset the dictionary and dictionary size
	                     dictionary.clear();
	                     for (int i = 0; i < initialDictionarySize; i++) {
	                         dictionary.put(new ByteWrapper(new byte[]{(byte) i}), i);
	                     }
	                     dictSize = initialDictionarySize;
	                     compressedSize = 0; // Reset compressed size to calculate compression ratio
	                 }
	                
	
	                // Reset the buffer for the next sequence
	                currentBytes.reset();
	                currentBytes.write(b);
	                   
	            }
	        }
	
	        // Output the code for the last sequence if any
	        if (currentBytes.size() > 0) {
	            ByteWrapper currentBytesWrapper = new ByteWrapper(currentBytes.toByteArray());
	            int code = dictionary.get(currentBytesWrapper); // Get the numerical code for the last sequence
	            outputStream.write((byte) (code / 256)); // Write the high byte by dividing by 256
	            outputStream.write((byte) (code % 256)); // Write the low byte using modulus 256
	        }
	
	        return outputStream.toByteArray(); // Return compressed data
	    }
	
	    /**
	     * Decompresses a byte array that has been compressed using the LZW algorithm with adaptive dictionary resetting.
	     *
	     * @param compressed The compressed byte array.
	     * @return A decompressed byte array.
	     * @throws IOException if an I/O error occurs during decompression.
	     */
	    public static byte[] decompress(byte[] compressed) throws IOException {
	        // Initialize the dictionary
	        Map<Integer, byte[]> inverseDictionary = new HashMap<>();
	        int dictSize = initialDictionarySize; // Current initial dictionary size
	        //variables to show the progress to the user ...
	        int checkCompressedSize=0;
	        int inputSize=compressed.length;
	
	        for (int i = 0; i < dictSize; i++) {
	            inverseDictionary.put(i, new byte[]{(byte) i});
	        }
	
	        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(); // Output stream to store decompressed data
	
	        // Read the first code and output its corresponding byte sequence
	        int code = ((compressed[0] % 256 + 256) % 256) * 256 + ((compressed[1] % 256 + 256) % 256);
	        byte[] currentStr = inverseDictionary.get(code);
	        outputStream.write(currentStr);
	
	        // Process each subsequent code
	        for (int i = 2; i < compressed.length; i += 2) {
	        	
	           
	            int prevCode = code; // Store the previous code
	            code = ((compressed[i] % 256 + 256) % 256) * 256 + ((compressed[i + 1] % 256 + 256) % 256);
	
	            // If the code is the reset marker, reset the dictionary
	            if (code == RESET_MARKER) {
	                inverseDictionary.clear();
	                for (int j = 0; j < initialDictionarySize; j++) {
	                    inverseDictionary.put(j, new byte[]{(byte) j});
	                }
	                dictSize = initialDictionarySize;
	
	                // Move to the next code after the reset marker
	                if (i + 2 < compressed.length) {
	                    i += 2;
	                    code = ((compressed[i] % 256 + 256) % 256) * 256 + ((compressed[i + 1] % 256 + 256) % 256);
	                } else {
	                    break; // End of the compressed data
	                }
	            }
	
	            byte[] entry;
	
	            // If the code is in the dictionary, retrieve its byte sequence
	            if (inverseDictionary.containsKey(code)) {
	                entry = inverseDictionary.get(code);
	            } else if (code == dictSize) {
	                // Special case where the new code equals the current dictionary size
	                entry = ByteWrapper.concat(currentStr, currentStr[0]);
	            } else {
	                throw new IllegalArgumentException("Invalid compressed code: " + code);
	            }
	
	            outputStream.write(entry); // Write the decoded sequence to the output
	
	            // Add the new sequence to the dictionary if there is space
	            if (dictSize < maxDictionarySize) {
	                inverseDictionary.put(dictSize++, ByteWrapper.concat(inverseDictionary.get(prevCode), entry[0]));
	            }
	
	            currentStr = entry; // Update the current string to the last entry decoded
	            
				// Calculate the progress percentage
	            PROGRESS = (int) ((checkCompressedSize+=2/ (double) inputSize) * 100);
	           GUI.progressBar.setValue(PROGRESS);
	           
	        }
	
	        return outputStream.toByteArray(); // Return the decompressed data
	    }
	}