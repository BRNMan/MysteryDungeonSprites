package spriteView;

public class AnimationFrame {
	private byte[] frameData; // 0x14 Bytes Total
	
	private int spriteIndex;
	private byte[] unknownValue;
	private byte[] objAttrib0;
	private byte[] objAttrib1;
	private byte[] objAttrib2;
	byte[] clearBytes;
	
	AnimationFrame(byte[] FrameData) {
		frameData = FrameData;
		spriteIndex = Util.toBigEndian(new byte[] {frameData[0], frameData[1]});
		unknownValue = new byte[] {frameData[2], frameData[3]};
		objAttrib0 = new byte[] {frameData[4], frameData[5]};
		objAttrib1 = new byte[] {frameData[6], frameData[7]};
		objAttrib2 = new byte[] {frameData[8], frameData[9]};
		clearBytes = new byte[] {frameData[10],frameData[11],frameData[12],frameData[13],frameData[14],frameData[15],frameData[16],frameData[17],frameData[18], frameData[19]};
		
	}
	
	public int getVDisplacement() {
		return 0;
	}
	
	public int getHDisplacement() {
		return 0;
	}
	
	public int getOBJSize() {
		return 0;
	}
}
