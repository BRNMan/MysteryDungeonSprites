package spriteView;

import java.io.IOException;
import java.io.RandomAccessFile;

public class PokemonEntry {
	int speciesName; // See http://datacrystal.romhacking.net/wiki/Pok%C3%A9mon_Mystery_Dungeon:_Red_Rescue_Team:Miscellaneous_Data:Main_Pok%C3%A9mon_Data
	int speciesCategory;
	byte palette;
	byte size;
	int movementSpeed;
	byte firstType, secondType;
	byte walkableTiles;
	byte friendArea;
	byte firstAbility, secondAbility;
	byte shadowSize;
	byte baseExp;
	byte[] recruitChance;
	
	RandomAccessFile myROM;
	PokemonEntry(int startIndex, RandomAccessFile ROM) throws IOException {
		myROM = ROM;
		byte[] buffer = new byte[4];
		myROM.seek(startIndex);
		myROM.read(buffer);
		speciesName = Util.toIndex(buffer);
		myROM.read(buffer);
		speciesCategory = Util.toIndex(buffer);
		palette = myROM.readByte();
		size = myROM.readByte();
		myROM.read(buffer);
		movementSpeed = Util.toIndex(buffer);
	}
	
	public String getSpecies() throws IOException {
		StringBuilder name = new StringBuilder();
		char buffer = 0x01;
		myROM.seek(speciesName);
		while((int)buffer != 0x00) {
			buffer = (char) myROM.readByte();
			name.append(buffer);
		}
		return name.toString();
	}
	
	public String getCategory() throws IOException {
		StringBuilder name = new StringBuilder();
		char buffer = 0x01;
		myROM.seek(speciesCategory);
		while((int)buffer != 0x00) {
			buffer = (char) myROM.readByte();
			name.append(buffer);
		}
		return name.toString();
	}
	
	public int getPalleteIndex() {
		return palette;
	}
}
