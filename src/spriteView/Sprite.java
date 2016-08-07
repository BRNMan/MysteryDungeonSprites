package spriteView;

import java.util.ArrayList;
import java.util.Arrays;

public class Sprite {
	private ArrayList<int[]> imgData;
	private ArrayList<Integer> offsetData;
	
	
	Sprite(int[] pixelData, int offset) {
		imgData = new ArrayList<int[]>();
		imgData.add(pixelData);
		offsetData = new ArrayList<Integer>();
		offsetData.add(offset);
	}
	
	//TODO: Add constructor for offset only.
	
	public void addSubsprite(int[] pixelData) {
		imgData.add(pixelData);
	}
	
	public void addSubsprite(int[] pixelData, int offset) {
		imgData.add(pixelData);
		offsetData.add(offset);
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
	
	public int getOffset(int index) {
		return offsetData.get(index);
	}
}
