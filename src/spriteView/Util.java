package spriteView;


public class Util {
	
	/*
	 * Changes little endian pointers to an index the program could use.
	 */
	public static int toIndex(byte[] pointer) {
		int ans = (int)(pointer[0]&0xFF)
				+ ((int)(pointer[1]&0xFF)<<1*8) //BIT SHIFT NOT BYTE SHIFT SILLY
				+ ((int)(pointer[2]&0xFF)<<2*8)
				+ ((int)(pointer[3]&0xFF)<<3*8)
				- (0x08<<3*8);
		return ans;
	}
	
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
}
