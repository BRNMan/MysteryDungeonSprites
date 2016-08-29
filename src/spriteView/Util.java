package spriteView;


public class Util {
	
	/**
	 * Changes little endian pointers to an index the program could use.
	 **/
	public static int toIndex(byte[] pointer) {
		int ans = (int)(pointer[0]&0xFF)
				+ ((int)(pointer[1]&0xFF)<<1*8) //BIT SHIFT NOT BYTE SHIFT SILLY
				+ ((int)(pointer[2]&0xFF)<<2*8)
				+ ((int)(pointer[3]&0xFF)<<3*8)
				- (0x08<<3*8);
		return ans;
	}
	
	/**
	 * 	Changes a little endian byte array to a big endian byte array.
	 * @param littleE Little endian byte array to feed in.
	 * @return The big endian integer representation of the number.
	 */
	public static int toBigEndian(byte[] littleE) {
		int ans = 0;
		for(int i = 0; i < littleE.length; i++) {
			ans+=(int)(littleE[i]&0xFF)<<i*8;
		}
		return ans;
	}
	
	public static void printArray(byte[]lol) {
		for(byte l:lol)
			System.out.print(String.format("%02x", l) + " ");
	}
	
	public static void printArray(long lol) {
		System.out.print(String.format("%02x", lol) + " ");
	}
	
	/**
	 *  Gets bits in a byte[] from start to end inclusive. Can handle a byte array of up to length 4.
	 * @param myData The byte array you want bits out of.
	 * @param start The starting index, inclusive.
	 * @param end The ending index, inclusive. From 0 to byte[].length*8-1
	 * @return An integer containing only the bits specified by start and end. Integer.MAX_VALUE if the end is greater than the start.
	 */
	public static int getBitsFrom(byte[] myData, int start, int end) {
		int result = 0;
		if(end < start)
			return Integer.MAX_VALUE;
		
		int startByte = start/8;
		int endByte = end/8;
		int currentByte = startByte;
		int currentBit = start%8; 
		do {
			while(currentBit<8) {  //read in all bits of the byte or as many are needed.
				result += getBit(myData[currentByte], currentBit) << (currentByte*8 + currentBit); //Adds correct bit in correct position.
				if(currentByte==endByte && currentBit == end%8) //We're at the last byte in the sequence so stop reading bits in.
					break;
				currentBit++;
			}
			currentBit = 0;  //reset bit counter and go to next byte
			currentByte++;
		} while(currentByte<=endByte);
		
		return result;
	}
	
	private static int getBit(byte myByte, int position) {
		return (myByte >> position) & 1;
	}
}
