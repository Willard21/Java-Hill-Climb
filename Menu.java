import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

public class Menu implements ActionListener {
	public static JMenuItem pauseButton, saveButton, loadButton, controls, newGame, exit;
	
	public static void init() {
		Menu m = new Menu();
		
		//Menu Bar
		JMenuBar mb = new JMenuBar();

		// create a menu
		JMenu x = new JMenu("Menu");
		
		// create menu items
		pauseButton = new JMenuItem("Pause");
		saveButton = new JMenuItem("Save");
		loadButton = new JMenuItem("Load");
		controls = new JMenuItem("Controls");
		newGame = new JMenuItem("New Game");
		exit = new JMenuItem("Exit");

		// add ActionListener to menuItems 
		pauseButton.addActionListener(m);
		saveButton.addActionListener(m);
		loadButton.addActionListener(m);
		controls.addActionListener(m);
		newGame.addActionListener(m);
		exit.addActionListener(m);

		// add menu items to menu 
		x.add(pauseButton);
		x.add(saveButton);
		x.add(loadButton);
		x.add(controls);
		x.add(newGame);
		x.add(exit);

		// add menu to menu bar
		mb.add(x);

		// add menu bar to frame
		Game.game.setJMenuBar(mb);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String s = e.getActionCommand();
		
		if (s.equals("Pause") || s.equals("Unpause") || s.equals("Controls")) {
			Board.paused ^= true;
			pauseButton.setText(Board.paused ? "Unpause" : "Pause");
		}
		else if (s.equals("Save")) {
			Board.board.save();
		}
		else if (s.equals("Load")) {
			Board.board.load();
		} else if (s.equals("New Game")) {
			Board.board.newGame();
		} else if (s.equals("Exit")) {
			Game.close();
		}
	}
}
