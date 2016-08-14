package spriteView;

public class AnimationTiming {
	private byte[] timingData; //Size: 0x0C; This one's nicer since they're all ints.
	int numFrames;
	int animationIndex;
	int hDisplacement;
	int vDisplacement;
	int shadowX;
	int shadowY;
	
	AnimationTiming(byte[] TimingData) {
		timingData = TimingData;
		numFrames = Util.toBigEndian(new byte[]{timingData[0], timingData[1]});
		animationIndex = Util.toBigEndian(new byte[]{timingData[2], timingData[3]});
		hDisplacement = Util.toBigEndian(new byte[]{timingData[4], timingData[5]});
		vDisplacement = Util.toBigEndian(new byte[]{timingData[5], timingData[6]});
		shadowX = Util.toBigEndian(new byte[]{timingData[7], timingData[8]});
		shadowY = Util.toBigEndian(new byte[]{timingData[9], timingData[10]});
	}
}
