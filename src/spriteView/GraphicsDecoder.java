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
	private int pixelX, tileIndex; //The pixel and tile indexes.
	
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
	
	/**
	 * Now you can run a pokemonEntry through and get sprites back.
	 * @param pe The pokemon you want sprites from.
	 * @return An ArrayList of its sprites.
	 * @throws IOException
	 */
	public ArrayList<Sprite> getSprites(PokemonEntry pe) throws IOException {
		return new PokemonGraphics(pokemonPointers[pe.getNumber()], inROM).processSprites();
	}
	
	//Old method.
	public BufferedImage displayPoke(PokemonEntry myPoke) throws IOException {
		BufferedImage ans = new BufferedImage(500,2000,BufferedImage.TYPE_INT_RGB);
		Graphics2D g = ans.createGraphics();
		
		//Convert Sprites to Pixels.
		//This line takes the pokemon selected, and gets an ArrayList of sprites from it.
		ArrayList<Sprite> pokemon = new PokemonGraphics(pokemonPointers[myPoke.getNumber()], inROM).processSprites();
		ArrayList<int[]> imgData = new ArrayList<int[]>();
		ArrayList<Integer> offsetData = new ArrayList<Integer>();
		for(int i = 0; i < pokemon.size(); i++) {
			for(int j = 0; j < pokemon.get(i).getNumSubsprites(); j++) {
				imgData.add(pokemon.get(i).getSubsprite(j));
				offsetData.add(pokemon.get(i).getOffset(j));
			}
		}
		
		PokemonData pd = new PokemonData(inROM);
		ArrayList<Color[]> listPal = getPalettes();
		Color[] myPal = listPal.get(pd.getPokemon(myPoke.getNumber()+1).getPalleteIndex()); //Starts with mystery who has no sprites.
		
		int pokeSize = myPoke.getSize();
		
		
		
		//Arranges pixels decently.
		for(int strip = 0; strip < imgData.size(); strip++) {
			for(int j = 0; j < imgData.get(strip).length; j++) {
				try{
				ans.setRGB(j%8 + 8*(j/64),
						j/8-8*(j/64) + 8*strip,
						myPal[imgData.get(strip)[j]].getRGB());
				}
				catch(IndexOutOfBoundsException e) {    //The whole sprite delivery method will eventually be changed.
					System.out.println(strip + " woop " + j);//But a temporary fix is needed for the big boys.
				}
			}
		}
		return ans;
	}
	
	//New method. Displays individual sprites instead.
	//TODO: Make offsets independent of sprites
	public BufferedImage displayPoke(PokemonEntry myPoke, int spriteIndex) throws IOException {
		BufferedImage temp = new BufferedImage(128,128,BufferedImage.TYPE_INT_RGB);
		Graphics2D g = temp.createGraphics();
		//Convert Sprites to Pixels.
		//This line takes the pokemon selected, and gets an ArrayList of sprites from it.
		ArrayList<Sprite> pokemon = new PokemonGraphics(pokemonPointers[myPoke.getNumber()], inROM).processSprites();
		Sprite selectedSprite = pokemon.get(spriteIndex);
		ArrayList<int[]> imgData = new ArrayList<int[]>();
		ArrayList<Integer> offsetData = new ArrayList<Integer>();
		//Gets the pixels and offsets for the sprite.
		for(int j = 0; j < selectedSprite.getNumSubsprites(); j++) {
				imgData.add(selectedSprite.getSubsprite(j));
				offsetData.add(selectedSprite.getOffset(j));
		}
		boolean isTrailing = false;
		int trailingIndex = selectedSprite.getNumSubsprites(); //For trailing offset index.
		if(selectedSprite.getNumOffsets() == trailingIndex+1) {  //If we have the trailing offset add it in too.
			offsetData.add(selectedSprite.getOffset(trailingIndex));
			isTrailing = true;
		}
			
		//Gets palettes
		PokemonData pd = new PokemonData(inROM);
		ArrayList<Color[]> listPal = getPalettes();
		Color[] myPal = listPal.get(pd.getPokemon(myPoke.getNumber()+1).getPalleteIndex()); //Starts with mystery who has no sprites.
		
		int pokeSize = myPoke.getSize(); //The pokemon's actual size. For putting the image together.		
		
		pixelX = 0; tileIndex = 0; //reset pixel location
		for(int i = 0; i < imgData.size(); i++) {
			//If there is blank space.
			if(offsetData.get(i)!=0) {
				//Set pixels to transparent.
				for(int j = 0; j < offsetData.get(i)*2; j++) { //Each byte is 2 pixels, so multiply by 2.
					temp.setRGB(getRealX(pokeSize), getRealY(pokeSize), 
								myPal[0].getRGB());
					pixelX++;//Increase pixel index.
				}
			}
			//Then do the associated subsprite.
			for(int j = 0; j < imgData.get(i).length; j++) {
				temp.setRGB(getRealX(pokeSize), getRealY(pokeSize), 
						myPal[imgData.get(i)[j]].getRGB());
				pixelX++;
			}
		}
		if(isTrailing) {  //Add last offset if its there
			for(int j = 0; j < offsetData.get(trailingIndex)*2; j++) { //Each byte is 2 pixels, so multiply by 2.
				temp.setRGB(getRealX(pokeSize), getRealY(pokeSize), 
							myPal[0].getRGB());
				pixelX++;//Increase pixel index.
			}
		}
		return temp;
	}
	
	private int getRealX(int pokeSize) {
		int T = getNumTiles(pokeSize);
		//Loops around pixels according to how many bytes tiles are.
		int ans = pixelX%8 + 8*(pixelX/64)
				-(pixelX/(64*T))*(8*T); //However many rows.
		return ans;
	}
	
	private int getRealY(int pokeSize) {
		int T = getNumTiles(pokeSize);
		//I don't know why I wrote it like this. I could've just done some ifs.
		int ans = pixelX/8 - 8*(pixelX/64) + 8*(pixelX/(64*T));
		return ans;
	}
	
	private int getNumTiles(int pokeSize) {
		//This method is obviously wrong, but I don't know the size parameter yet so I'll keep it.
		int numTiles;
		switch(pokeSize) {
		case 1:
			numTiles = 4;
		case 2:
			numTiles = 4;
		case 4: 
			numTiles = 8;
		default:
			numTiles = 4;
		}
		return numTiles;
	}
}
