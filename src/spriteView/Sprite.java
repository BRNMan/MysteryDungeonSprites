package spriteView;

import java.util.ArrayList;
import java.util.Arrays;

public class Sprite {
	ArrayList<int[]> imgData;
	Sprite(int[] pixelData) {
		imgData = new ArrayList<int[]>();
		imgData.add(pixelData);
	}
	
	public void addSubsprite(int[] pixelData) {
		imgData.add(pixelData);
	}
	
	public int[] getSubsprite(int index) {
		if(index >= imgData.size())
			return imgData.get(0);
		else
			return imgData.get(index);
	}
	
	public int getNumSubsprites() {
		return imgData.size();
	}
}
