import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class GUI {
	public final static int PANEL_WIDTH = 1000;
	public final static int PANEL_HEIGHT = 600;
	JMenuBar menuBar;
	JMenu fileMenu;
	
	public GUI() {
		
	}
	
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		
		

	}

	public static void main(final String args[]) {
		GUI gui = new GUI();
		JFrame frame = new JFrame("Books GUI");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		gui.menuBar = new JMenuBar();

		gui.fileMenu = new JMenu("File");
		gui.menuBar.add(gui.fileMenu);

		JMenuItem newMenuItem = new JMenuItem("Open");
		gui.fileMenu.add(newMenuItem);

		frame.setJMenuBar(gui.menuBar);
		frame.setSize(PANEL_WIDTH, PANEL_HEIGHT);
		frame.setVisible(true);
	}
}