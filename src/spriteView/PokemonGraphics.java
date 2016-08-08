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
	static byte[] SIRO = new byte[]{0x53,0x49,0x52,0x4F};
	private ArrayList<Sprite> newSprites;
	
	PokemonGraphics(int StartIndex, RandomAccessFile inROM) throws IOException {
		startIndex = StartIndex;
		ROM = inROM;
		newSprites = new ArrayList<Sprite>();
		byte[] myPointer = new byte[4];
		ROM.seek(startIndex);
		ROM.read(myPointer);
		
		if(!Arrays.equals(myPointer, SIRO)) {
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
	
	public ArrayList<Sprite> processSprites() throws IOException {
		byte[] spritePointer = new byte[4]; //Will be used as a general 4 byte buffer.
		byte[] tileSize = new byte[2];
		int numBytesRead;		
		int currentSpriteData = pCIII; //The pointer to the data we're on
		int currentSprite;						 //The index of the pixels
		
		//Iterate through sprites for a given pokemon
		while(currentSpriteData<endIndex) {
			Sprite current = null;
			
			ROM.seek(currentSpriteData);//Go back to iterating through the list
			ROM.read(spritePointer); //Goes to the sprite data.
			int actualData = Util.toIndex(spritePointer); //Where the sprite foot is.
			ROM.read(spritePointer);
			int nextData = Util.toIndex(spritePointer);//Next data to find out where we have to stop.
			ROM.seek(actualData);//Points us to sprite information
			ROM.read(spritePointer);//Points us to the location of the sprite's pixels
			if(Arrays.equals(spritePointer, new byte[] {(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00})) { //Starts with the offset. Sprite pointer  after offset.
				int stopPoint = getStopPoint(actualData, nextData, spritePointer);
				
				int offset;
				ROM.read(spritePointer); 
				offset = Util.toBigEndian(spritePointer); //This is the first offset, pre-sprite.
				ROM.read(spritePointer);
				do {
					currentSprite = Util.toIndex(spritePointer);
					ROM.read(spritePointer);	
					numBytesRead = Util.toBigEndian(spritePointer);	//actually reading tileSize
					long fp = ROM.getFilePointer();
					int[] mySprite = spriteToPicture(currentSprite, numBytesRead);					
					ROM.seek(fp);
					ROM.read(spritePointer);

					if(current != null) //Will add a subsprite or a sprite depending on which is needed.
						current.addSubsprite(mySprite,offset);
					else
						current = new Sprite(mySprite,offset);
					
					ROM.read(spritePointer);
					offset = Util.toBigEndian(spritePointer);
					ROM.read(spritePointer);
				} while(ROM.getFilePointer() < stopPoint - 0x10);
				current.addOffset(offset); //Should add the post-sprite offset if necessary.
				newSprites.add(current);
			}
			else { //Simpler condition, start with the sprite.
				int stopPoint = getStopPoint(actualData, nextData, spritePointer);
				int offset = 0;
				do {	
					currentSprite = Util.toIndex(spritePointer); //Gets our graphics index
					ROM.read(spritePointer);
					numBytesRead = Util.toBigEndian(spritePointer);	//Gets tileSize
					long fp = ROM.getFilePointer();
					int[] mySprite = spriteToPicture(currentSprite, numBytesRead);//reads graphics with new information
					ROM.seek(fp);
					ROM.read(spritePointer);//returns back to sprite data
					
					
					if(current != null) 
						current.addSubsprite(mySprite,offset);
					else
						current = new Sprite(mySprite,offset);
					
					ROM.read(spritePointer);
					offset = Util.toBigEndian(spritePointer);
					ROM.read(spritePointer);
				} while(ROM.getFilePointer() < stopPoint - 0x10);
				current.addOffset(offset);
				newSprites.add(current);
			}
			currentSpriteData+=4;
		}
		return newSprites;
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
