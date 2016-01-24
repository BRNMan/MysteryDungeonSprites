package spriteView;

import java.awt.Color;
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

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class MainFrame extends JFrame {
	
	JPanel spritePanel, dataPanel, controlPanel;
	JButton loadButton;
	JLabel label;
	JFileChooser jfc;
	File romFile;
	RandomAccessFile inROM;
	GraphicsDecoder gd;
	PokemonData pd;
	
	public static void main(String[] args) {
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
						BufferedImage pkmn = gd.displayPoke(100);
						JLabel hello = new JLabel(new ImageIcon(pkmn));
						spritePanel.add(hello);
//						for(int i = 0; i < 423; i++) {
//							pd = new PokemonData(inROM);
//							PokemonEntry pe = pd.getPokemon(i);
//							System.out.println(pe.getSpecies() + " the " + pe.getCategory() + " Pokemon.");
//							System.out.println("Palette #: " + pe.palette + " Pokemon #: " + i);
//						}
						
						inROM.close();
					} catch (IOException fe) {
						fe.printStackTrace();
					}	
			}
			
			private BufferedImage makePalImage() {
				ArrayList<Color[]> listPal = gd.getPalettes();
				BufferedImage biff = new BufferedImage(16,listPal.size(),BufferedImage.TYPE_INT_RGB);
				for (int y = 0; y < listPal.size(); y++) {
					for(int x = 0; x < listPal.get(y).length; x++) { 
						biff.setRGB(x, y, listPal.get(y)[x].getRGB());
					}
				}
				System.out.println("Good stuff!");
				return biff;
			}			
		});
		
		controlPanel.add(loadButton);
		add(controlPanel);
	}

}
