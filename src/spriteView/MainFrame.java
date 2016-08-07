package spriteView;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class MainFrame extends JFrame {
	
	JPanel spritePanel, dataPanel, controlPanel;
	JButton loadButton;
	JLabel label, spriteLabel;
	JComboBox<String> pokeChooser;
	JComboBox<Integer> spriteChooser;
	JFileChooser jfc;
	File romFile;
	RandomAccessFile inROM;
	GraphicsDecoder gd;
	PokemonData pd;
	
	PokemonEntry selectedPokemon;
	
	public static void main(String[] args) {
		//Initial Initialization
		MainFrame mf = new MainFrame();
		mf.setSize(800, 600);
		mf.setDefaultCloseOperation(EXIT_ON_CLOSE);
		mf.setVisible(true);
		mf.setLocationRelativeTo(null);
	}
	
	MainFrame() {		
		GridLayout gl = new GridLayout(1,3);
		gl.setHgap(10);
		setLayout(gl);
		
		spritePanel = new JPanel();
		label = new JLabel("Palletes: ");
		spritePanel.add(label);
		add(spritePanel);
		
		dataPanel = new JPanel();
		add(dataPanel);
		
		controlPanel = new JPanel();
		controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
		pokeChooser = new JComboBox<String>();
		pokeChooser.setEnabled(false);
		pokeChooser.setMaximumSize(new Dimension(200, 50));
		spriteChooser = new JComboBox<Integer>();
		spriteChooser.setEnabled(false);
		spriteChooser.setMaximumSize(new Dimension(200,50));
		
		pokeChooser.addActionListener( new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					
					updateIndicies(pd.getPokemon(pokeChooser.getSelectedIndex()));
					spriteChooser.setSelectedIndex(0); //To change the pokemon, we want to make sure this is 0, the least amount of sprites possible.
					updatePokemon(pd.getPokemon(pokeChooser.getSelectedIndex()), 0);
				} catch (IOException e1) {
					System.out.println("whatev");
				}
			} 
		});
		
		spriteChooser.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					if(spriteChooser.isEnabled()) {
						if(spriteChooser.getSelectedIndex() != -1) //Checks if you've selected something yet.
							updatePokemon(pd.getPokemon(pokeChooser.getSelectedIndex()), spriteChooser.getSelectedIndex());
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		
		loadButton = new JButton();
		loadButton.setText("Load File");
		loadButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try{
					jfc = new JFileChooser();
					jfc.setCurrentDirectory(new File("C:\\Users\\Michael\\Desktop\\GBADEV\\GenRomHack"));
					if(jfc.showOpenDialog(null)==JFileChooser.APPROVE_OPTION) 
						romFile = jfc.getSelectedFile();
						inROM = new RandomAccessFile(romFile, "rw");
						gd = new GraphicsDecoder(romFile, inROM);
						pd = new PokemonData(inROM);
						updatePokemon(pd.getPokemon(1), 0); //Initial arguments.
						String speciesName = "";
						int sameName = 0;
						for(int i = 1; i < 423; i++) {
								PokemonEntry pe = pd.getPokemon(i);
								if(speciesName.equals(pe.getSpecies()))
									sameName++;
								else
									sameName = 0;
								
								speciesName = pe.getSpecies();
								if(sameName != 0)
									pokeChooser.addItem(speciesName + (sameName));
								else
									pokeChooser.addItem(speciesName);
						}
						pokeChooser.setEnabled(true);
						controlPanel.add(pokeChooser, BorderLayout.NORTH);
						updateIndicies(pd.getPokemon(1));
						spriteChooser.setSelectedIndex(0);
						spriteChooser.setEnabled(true);
						controlPanel.add(spriteChooser, BorderLayout.NORTH);
						
					} catch (IOException fe) {
						fe.printStackTrace();
					}	
			}
		});
		
		controlPanel.add(loadButton);
		add(controlPanel);
	}
	
	private void updateIndicies(PokemonEntry selectedPokemon) throws IOException {
		int max = gd.getSprites(selectedPokemon).size();
		spriteChooser.removeAllItems();
		for(int i = 0; i < max; i++) {
			spriteChooser.addItem(i);
		}
	}
	
	/**
	 * Changes the sprites displayed on the screen.
	 * @param selectedPokemon The PokemonEntry selected
	 * @param selectedIndex The Sprite Index of the Pokemon Selected
	 * @throws IOException
	 */
	private void updatePokemon(PokemonEntry selectedPokemon, int selectedIndex) throws IOException {
		if(spriteLabel != null) 
			spritePanel.remove(spriteLabel);
		BufferedImage pkmn = gd.displayPoke(selectedPokemon, selectedIndex); //Change for a different pokemon: Index starts at 0
		spriteLabel = new JLabel(new ImageIcon(pkmn));
		spritePanel.add(spriteLabel);
		pack();
	}

}
