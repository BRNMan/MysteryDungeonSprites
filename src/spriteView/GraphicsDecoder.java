package spriteView;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;

public class GraphicsDecoder {
	int startAddr = 0x510000;
	RandomAccessFile inROM;
	int palettePointer;
	int[] pokemonPointers, pokemonIDs;
	
	public GraphicsDecoder(File romFile, RandomAccessFile InROM) {
		try {
			inROM = InROM;
			inROM.seek(startAddr);
			byte[] myPointer = new byte[8];
			inROM.read(myPointer);
			byte[] other = new byte[]{0x70,0x6B,0x73,0x64, 0x69, 0x72, 0x30, 0x00};
			if(Arrays.equals(myPointer,other))
				System.out.println("Found File!");
			myPointer = new byte[4];
			inROM.read(myPointer);  //0x1f1 - how many loops
			int loopNum = Util.toBigEndian(myPointer);
			inROM.read(myPointer);  //Where the file starts
			
			pokemonIDs = new int[loopNum];
			pokemonPointers = new int[loopNum];
			inROM.seek(Util.toIndex(myPointer));
			for (int i = 0; i < pokemonPointers.length; i++) {
				inROM.read(myPointer);
				pokemonIDs[i] = Util.toIndex(myPointer);
				inROM.read(myPointer);
				pokemonPointers[i] = Util.toIndex(myPointer);
			}
			
			palettePointer = 0;
			int numPKMN = 0;
			for (int i = 0; i < pokemonPointers.length; i++) {
				inROM.seek(pokemonIDs[i]);
				inROM.read(myPointer);
				if(myPointer[0] == 0x61 && myPointer[1] == 0x78) //ax, for pokemon overworld sprite data
					numPKMN++;
				else if(Arrays.equals(myPointer, new byte[]{0x70,0x61,0x6c,0x65}))//palet, for palletes
					palettePointer = pokemonPointers[i];
					
			}
			
			System.out.println("The number of pokemon is: " + numPKMN);
			
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public ArrayList<int[]> getSprites(int index) throws IOException {
		PokemonGraphics bulbasaur = new PokemonGraphics(pokemonPointers[index], inROM);
		return bulbasaur.processSprites();
	}
	
	
	//Neatly organizes colors into a list of palettes of 16 Colors
	public ArrayList<Color[]> getPalettes() {
		ArrayList<Color[]> listPal = new ArrayList<Color[]>();
		try {
			int i = 0;
			inROM.seek(palettePointer);
			byte[] currentPalette = new byte[4];
			inROM.read(currentPalette);
			Color[] curPal = new Color[16];
			while((int)(currentPalette[3]&0xFF) == 0x80) {
				Color curColors = new Color((int)(currentPalette[0]&0xFF), (int)(currentPalette[1]&0xFF), (int)(currentPalette[2]&0xFF));
				curPal[i%16] = curColors;
				i++;
				if(i%16 == 0) {
					listPal.add(Arrays.copyOf(curPal, curPal.length));
				}
				inROM.read(currentPalette);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return listPal;
	}
	
	public BufferedImage displayPoke(int pokemonIndex) throws IOException {
		BufferedImage ans = new BufferedImage(256,2400,BufferedImage.TYPE_INT_RGB);
		Graphics2D g = ans.createGraphics();
		ArrayList<int[]> imgData = getSprites(pokemonIndex);
		ArrayList<Color[]> listPal = getPalettes();
		Color[] myPal = listPal.get(3);
		for(int strip = 0; strip < imgData.size(); strip++) {
			for(int j = 0; j < imgData.get(strip).length; j++) {
				ans.setRGB(j%8+8*(j/64),
						j/8-8*(j/64) + 8*strip,
						myPal[imgData.get(strip)[j]].getRGB());
				//g.setColor(myPal[imgData.get(strip)[j]]);
				//g.drawRect(j%8+8*(j/64), j/8-8*(j/64) + 8*strip, 1, 1);
			}
		}
		return ans;
	}
}
