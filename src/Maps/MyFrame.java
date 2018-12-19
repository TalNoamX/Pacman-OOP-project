package Maps;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import javax.imageio.ImageIO;
import javax.swing.*;
import GameData.Fruit;
import GameData.Game;
import GameData.Pacman;
import Geom.Point3D;

public class MyFrame extends JFrame implements MouseListener,ComponentListener {

	private static final long serialVersionUID = 1L;

	private BufferedImage myImage;
	private Map map;
	private BufferedImage pacmanImg;
	private BufferedImage FruitImg;
	private MenuItem loadCSV;
	private MenuItem run;
	private MenuItem saveKml;
	private MenuItem pacman;
	private MenuItem fruit;
	//will tell us whether to add a pacman or a fruit when pressed
	private boolean fruitButton = false;
	private boolean pacmanButton = false;
	private int fruitID;
	private int pacmanID;
	private Game game;
	Dimension fSize; //The frame size

	/**
	 * constructor 
	 * @param path The Images Path
	 * @param top The
	 * @param bottom
	 */
	public MyFrame(String path, Point3D leftTop, Point3D rightBottom, Point3D rightTop) {
		map = new Map(path, leftTop, rightBottom, rightTop);//Setting a new map
		myImage = map.getmyImage();// setting the map image
		setMenu();	//setting the menu bar with all the options
		//adding the image to the frame
		JLabel label1 = new JLabel(new ImageIcon(myImage));
		add(label1);
		game = new Game();
		setActionListeners();//adding action listeners to the menu Items
		setPacFruImg();//setting the image of the pacman and the fruit
		this.addMouseListener(this); //adding mouselisteners
		this.addComponentListener(this);
		fSize = this.getSize();

	}

	/**
	 * Setting the Menu Items - 
	 * first will be "file" the contain run, load CSV, and save as kml.
	 * Second will be "draw images" that will make it possible to make game by drawing pacmans and fruits on the map
	 */
	private void setMenu() {
		MenuBar menuBar = new MenuBar();
		Menu menu1 = new Menu("File");  
		loadCSV = new MenuItem("Load csv");
		run = new MenuItem("Run");
		saveKml = new MenuItem("Save as kml");
		menu1.add(loadCSV);
		menu1.add(run);
		menu1.add(saveKml);
		Menu menu2 = new Menu("Draw Images");  
		pacman = new MenuItem("Pacman");
		fruit = new MenuItem("Fruit");
		menu2.add(pacman);
		menu2.add(fruit);
		menuBar.add(menu1);
		menuBar.add(menu2);
		this.setMenuBar(menuBar);
	}

	/**
	 * setting the Icons for pacmans and fruits.
	 */
	public void setPacFruImg() {
		try {
			File file = new File("Pacman.jpg");
			pacmanImg = ImageIO.read(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			File file = new File("strawberry-icon.jpg");
			FruitImg = ImageIO.read(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Setting all the needed ACtionListeners For the menuItem.
	 */
	public void setActionListeners() {
		loadCSV.addActionListener(new ActionListener() {	//Loading CSV game files usinf FileChooser 
			public void actionPerformed(ActionEvent e) {
				JButton open = new JButton();
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setCurrentDirectory(new java.io.File("■■Documents"));
				fileChooser.setDialogTitle("Load CSV");
				if(fileChooser.showOpenDialog(open)==JFileChooser.APPROVE_OPTION) {

				}
				if(fileChooser.getSelectedFile().getAbsolutePath().endsWith(".csv")) {
					game.csvToGame(fileChooser.getSelectedFile().getAbsolutePath());
					setPoints();//Changes all the points coords to mach the map.
					repaint();
				}
				else JOptionPane.showMessageDialog(null, "Not a CSV file, Please try again");
			}
	
		});
		run.addActionListener(new ActionListener() {	//Starting The Game.
			public void actionPerformed(ActionEvent e) {
				
			}
		});
		saveKml.addActionListener(new ActionListener() {	//Saving The game Data as kml.
			public void actionPerformed(ActionEvent e) {
				
			}
		});
		pacman.addActionListener(new ActionListener() {	//Drawing pacmans.
			public void actionPerformed(ActionEvent e) {
				if(fruitButton) fruitButton = false;//In case we drew fruits before
				if(pacmanButton) pacmanButton = false;//pressing twice on the button will cancel the drawing
				pacmanButton=true;
			}
		});
		
		fruit.addActionListener(new ActionListener() {	//Drawing fruits
			public void actionPerformed(ActionEvent e) {
				if (pacmanButton) pacmanButton = false;//in case we drew pacmans before
				if(fruitButton) fruitButton = false; //pressing twice on the button will cancel the drawing
				fruitButton=true;
			}
		});
	}

	
	private void setPoints() {
		for(Pacman p: game.pList()) {
			p.setPoint(map.coords2pixels(p.getPoint()));
		}
		for(Fruit f: game.fList()) {
			f.setPoint(new Point3D(map.coords2pixels(f.getPoint())));
		}
	}
	/**
	 * Drawing the maps image, and painting the pacman and the fruits.
	 */
	public void paint(Graphics g) {
		g.drawImage(myImage, 8,53, this.getWidth()-16,this.getHeight()-61,this);//Drawing the map image
			Iterator<Pacman> itP=game.pList().iterator();
			Iterator<Fruit> itF=game.fList().iterator();
			while(itP.hasNext()) {
				Pacman temp=itP.next();
				int x = (int)temp.getPoint().x();
				int y = (int)temp.getPoint().y();
				g.drawImage(pacmanImg, x, y, pacmanImg.getWidth(), pacmanImg.getHeight(), this);
			}
			while(itF.hasNext()) {
				Fruit temp = itF.next();
				int x = (int)temp.getPoint().x();
				int y = (int)temp.getPoint().y();
				g.drawImage(FruitImg, x, y, FruitImg.getWidth(), FruitImg.getHeight(), this);
			}
		}

	@Override
	public void mouseClicked(MouseEvent e) {
		int x=e.getX();
		int y=e.getY();
		Point3D p=new Point3D(x, y);
		if(fruitButton) {
			game.fList().add(new Fruit(++fruitID, p.x(), p.y(), p.z(), 1));//The default fruit weight is 1
		}
		if(pacmanButton) {
			game.pList().add(new Pacman(++pacmanID, p.x(), p.y(), p.x(), 1, 1));//The default radius and speed is 1
		}
		repaint();
	
	}
	public void componentResized(ComponentEvent e) {
		double xRat =  (e.getComponent().getWidth()/this.getWidth()); 
		double yRat =  (e.getComponent().getHeight()/this.getHeight());
		Iterator<Pacman> itP=game.pList().iterator();
		Iterator<Fruit> itF=game.fList().iterator();
		while(itP.hasNext()) {
			Pacman p = itP.next();
			p.setPoint(new Point3D(p.getPoint().x()*xRat, p.getPoint().y()*yRat));
			System.out.println(p.getPoint());
		}
		
		while(itF.hasNext()) {
			Fruit f = itF.next();
			f.setPoint(new Point3D(f.getPoint().x()*xRat, f.getPoint().y()));
		}
//			for(Pacman p: game.pList()) {
//				p.setPoint(new Point3D(p.getPoint().x()*xRat, p.getPoint().y()*yRat));
//			}
//			for(Fruit f: game.fList()) {
//				f.setPoint(new Point3D(f.getPoint().y()*yRat, f.getPoint().y()));
//			}
	}

	//Unneeded functions:
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
	public void componentHidden(ComponentEvent arg0) {}
	public void componentMoved(ComponentEvent arg0) {}
	public void componentShown(ComponentEvent arg0) {}

	public static void main(String[] args) {
		MyFrame window = new MyFrame("Ariel1.png", new Point3D(32.10566,35.20238), new Point3D(32.10191,35.21237),new Point3D(32.10566,35.21241));
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setVisible(true);
		window.pack();
	}
}













