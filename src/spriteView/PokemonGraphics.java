package spriteView;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;

public class PokemonGraphics {
	int startIndex, pA, pB, pCII, pCIII, endIndex, spriteStart;
	static RandomAccessFile ROM;
	static byte[] siro = new byte[]{0x53,0x49,0x52,0x4F};
	private ArrayList<int[]> sprites;
	
	PokemonGraphics(int StartIndex, RandomAccessFile inROM) throws IOException {
		startIndex = StartIndex;
		ROM = inROM;
		sprites = new ArrayList<int[]>();
		byte[] myPointer = new byte[4];
		ROM.seek(startIndex);
		ROM.read(myPointer);
		
		if(!Arrays.equals(myPointer, siro)) {
			System.out.println("Bad Header"); 
			return;
		}
		
		//Reads pointers at bottom of file to data structures.
		ROM.read(myPointer);
		endIndex = Util.toIndex(myPointer);
		ROM.seek(endIndex);
		ROM.read(myPointer);
		pA = Util.toIndex(myPointer);
		ROM.read(myPointer);
		pCII = Util.toIndex(myPointer);
		ROM.read(myPointer);
		int pUnknown = Util.toBigEndian(myPointer);
		ROM.read(myPointer);
		pCIII = Util.toIndex(myPointer); //Pointer to sprite table
		ROM.read(myPointer);
		pB = Util.toIndex(myPointer);
	}
	
	public ArrayList<int[]> processSprites() throws IOException {
		byte[] spritePointer = new byte[4];
		byte[] tileSize = new byte[2];
		int numBytesRead;
		
		//For some reason, the bulbasaur has sprite data that makes 6 FF bytes refer to the top of the sprite.
		ROM.seek(pCII - 4);
		ROM.read(spritePointer);
		spriteStart = Util.toIndex(spritePointer) + 28;
		
		int currentSpriteData = pCIII; //The pointer to the data we're on
		int currentSprite; //The index of the pixels
		Util.printArray(currentSpriteData);
		
		//Iterate through sprites
		while(currentSpriteData<endIndex) {
			
			ROM.seek(currentSpriteData);//Go back to iterating through the list
			ROM.read(spritePointer); //Goes to the sprite data.
			int actualData = Util.toIndex(spritePointer); //Where the sprite foot is.
			ROM.read(spritePointer);
			int nextData = Util.toIndex(spritePointer);//Next data to find out where we have to stop.
			ROM.seek(actualData);//Points us to sprite information
			
			ROM.read(spritePointer);//Points us to the location of the sprite's pixels
			if(Arrays.equals(spritePointer, new byte[] {(byte)0xFF,(byte)0xFF,(byte)0xFF,(byte)0xFF})) {
				currentSprite = spriteStart; 					//Weird case with Bulbasaur. To beginning of all sprites
				numBytesRead = 0x200;
				int[] mySprite = spriteToPicture(currentSprite, numBytesRead);
				sprites.add(Arrays.copyOf(mySprite, mySprite.length));
			}
			else if(Arrays.equals(spritePointer, new byte[] {(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00})) {
				
				int stopPoint = getStopPoint(actualData, nextData, spritePointer);
				
				int endFlag;
				ROM.read(spritePointer); //This means that the pointer is after the size.
				ROM.read(spritePointer);
				do {
					currentSprite = Util.toIndex(spritePointer);
					ROM.read(spritePointer);	
					numBytesRead = Util.toBigEndian(spritePointer);	//actually reading tileSize
					long fp = ROM.getFilePointer();
					int[] mySprite = spriteToPicture(currentSprite, numBytesRead);
					sprites.add(Arrays.copyOf(mySprite, mySprite.length));
					ROM.seek(fp);
					ROM.read(spritePointer);
					ROM.read(spritePointer);
					endFlag = Util.toBigEndian(spritePointer);
					ROM.read(spritePointer);
				} while(ROM.getFilePointer() < stopPoint - 0x10);
				
			}
			else {
				/*currentSprite = Util.toIndex(spritePointer); //Pixel location
				ROM.read(tileSize); //How many bytes to read from each sprite
				numBytesRead = Util.toBigEndian(tileSize);
				int[] mySprite = spriteToPicture(currentSprite, numBytesRead);
				sprites.add(Arrays.copyOf(mySprite, mySprite.length));*/
				int stopPoint = getStopPoint(actualData, nextData, spritePointer);
				int endFlag;
				do {	
					
					currentSprite = Util.toIndex(spritePointer);
					ROM.read(spritePointer);	
					numBytesRead = Util.toBigEndian(spritePointer);	//actually tileSize
					long fp = ROM.getFilePointer();
					int[] mySprite = spriteToPicture(currentSprite, numBytesRead);
					sprites.add(Arrays.copyOf(mySprite, mySprite.length));
					ROM.seek(fp);
					ROM.read(spritePointer);
					ROM.read(spritePointer);
					endFlag = Util.toBigEndian(spritePointer);
					ROM.read(spritePointer);
				} while(ROM.getFilePointer() < stopPoint - 0x10);
			}
			currentSpriteData+=4;
		}
		return sprites;
	}
	
	private int getStopPoint(int actualData, int nextData, byte[] spritePointer) throws IOException {
		//Go to next pointer to find out where we have to end
		int endOfSpriteData = 0;
		ROM.seek(nextData);
		ROM.read(spritePointer);
		if(Arrays.equals(spritePointer, new byte[] {(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00})) {
			ROM.read(spritePointer);
			ROM.read(spritePointer);
			endOfSpriteData = Util.toIndex(spritePointer);
		} else {
			endOfSpriteData = Util.toIndex(spritePointer);
		}
		ROM.seek(actualData);
		ROM.read(spritePointer);
		return endOfSpriteData;
	}
	
	private int[] spriteToPicture(int curSprite, int numBytes) throws IOException {
		ROM.seek(curSprite);
		byte buffer;
		int[] ans = new int[numBytes*2];
		for(int i = 0; i < numBytes; i++) {
			buffer = ROM.readByte();
			ans[i*2+1] = (buffer>>4) & 0xF; //high nibble
			ans[i*2] = buffer & 0xF;	//low nibble
		}
		return ans;
	}
}
