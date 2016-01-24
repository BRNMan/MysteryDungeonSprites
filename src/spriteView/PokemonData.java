package spriteView;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

public class PokemonData {
	int startIndex = 0x357b88;
	ArrayList<PokemonEntry> pokes;
	ArrayList<PokemonGraphics> pokeSprites;
	//TODO:Combine this and GraphicsDecoder.
	PokemonData(RandomAccessFile ROM) throws IOException {
		pokes = new ArrayList<PokemonEntry>();
		for(int i = 0; i < 423; i++) {
			pokes.add(new PokemonEntry(startIndex + 0x10 + 72*i, ROM));
		}
	}
	
	public PokemonEntry getPokemon(int index) {
		return pokes.get(index);
	}
}
