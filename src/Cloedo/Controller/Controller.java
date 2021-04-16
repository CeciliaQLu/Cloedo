package Cluedo.Controller;

import Cluedo.Model.Game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;

public class Controller {
    Canvas boardCanvas;
    Map<String, JMenuItem> menuItems;
    Map<String, JButton> buttons;
    Game game;

    public Controller(Canvas boardCanvas, Map<String, JMenuItem> menuItems, Map<String, JButton> buttons) {
        this.boardCanvas = boardCanvas;
        addCanvasListeners();
        this.menuItems = menuItems;
        addMenuListeners();
        this.buttons = buttons;
        addButtonListeners();
    }

    public void addGame(Game game){
        this.game = game;
    }

    public void addCanvasListeners(){
        boardCanvas.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(game == null) return;
                int clickX = e.getX();
                int clickY = e.getY();
                int wid = (boardCanvas.getWidth()-4)/24;
                int hei = (boardCanvas.getHeight()-4)/25;
                int col = (clickX - 5)/wid;
                int row = (clickY - 5)/hei;
                game.doMouse(row, col);
            }

            @Override
            public void mousePressed(MouseEvent e) { }

            @Override
            public void mouseReleased(MouseEvent e) { }

            @Override
            public void mouseEntered(MouseEvent e) { }

            @Override
            public void mouseExited(MouseEvent e) { }
        });

        boardCanvas.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                if(game == null) return;
                char keyPressed = e.getKeyChar();
                if(keyPressed == 'w' || keyPressed == 'W'){
                    game.movePlayer(0, -1);
                }else if(keyPressed == 's' || keyPressed == 'S'){
                    game.movePlayer(0, 1);
                }else if(keyPressed == 'a' || keyPressed == 'A'){
                    game.movePlayer(-1, 0);
                }else if(keyPressed == 'd' || keyPressed == 'D' ){
                    game.movePlayer(1, 0);
                }else if(keyPressed == 'x' || keyPressed == 'X'){
                    // next player
                    game.nextPlayer();
                }

            }

            @Override
            public void keyPressed(KeyEvent e) { }

            @Override
            public void keyReleased(KeyEvent e) { }
        });
    }

    public void addMenuListeners(){
        menuItems.get("Instructions").addActionListener(e -> {
            String ans = "";
            try{
                BufferedReader br = new BufferedReader(new FileReader("./assets/Introduction.txt"));
                String str;
                while((str = br.readLine()) != null){
                    ans += (str + "\n");
                }
            }  catch (IOException a) {
                a.printStackTrace();
            }

            JOptionPane.showMessageDialog(null, ans, "Game Introduction", JOptionPane.PLAIN_MESSAGE);
        });

        menuItems.get("Exit").addActionListener(actionEvent -> System.exit(0));

    }

    public void addButtonListeners(){
        buttons.get("Roll Dice").addActionListener(actionEvent -> {
            if(game == null) return;
            game.rollDice();
        });

        buttons.get("Suggestions").addActionListener(actionEvent -> {
            if(game == null || !game.canMakeSuggestion) return;
            JFrame suggestionFrame = new JFrame("Make Suggestion");
            suggestionFrame.setLocation(200,200);
            suggestionFrame.setSize(500,500);

            // JPanel that contains characters and weapons
            JPanel jpUp = new JPanel();

            // JPanel for the six characters
            JPanel jp1 = new JPanel();
            JRadioButton jrb1 = new JRadioButton("Miss Scarlett");
            JRadioButton jrb2 = new JRadioButton("Colonel Mustard");
            JRadioButton jrb3 = new JRadioButton("Mrs. White");
            JRadioButton jrb4 = new JRadioButton("Mr. Green");
            JRadioButton jrb5 = new JRadioButton("Mrs. Peacock");
            JRadioButton jrb6 = new JRadioButton("Professor Plum");
            ButtonGroup bg1 = new ButtonGroup();
            bg1.add(jrb1);
            bg1.add(jrb2);
            bg1.add(jrb3);
            bg1.add(jrb4);
            bg1.add(jrb5);
            bg1.add(jrb6);

            jp1.add(jrb1);
            jp1.add(jrb2);
            jp1.add(jrb3);
            jp1.add(jrb4);
            jp1.add(jrb5);
            jp1.add(jrb6);
            jp1.setLayout(new GridLayout(6, 1));
            jp1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Character"));

            // JPanel for the six weapons
            JPanel jp2 = new JPanel();
            JRadioButton jrb7 = new JRadioButton("Candlestick");
            JRadioButton jrb8 = new JRadioButton("Dagger");
            JRadioButton jrb9 = new JRadioButton("Lead Pipe");
            JRadioButton jrb10 = new JRadioButton("Revolver");
            JRadioButton jrb11 = new JRadioButton("Rope");
            JRadioButton jrb12 = new JRadioButton("Spanner");
            ButtonGroup bg2 = new ButtonGroup();
            bg2.add(jrb7);
            bg2.add(jrb8);
            bg2.add(jrb9);
            bg2.add(jrb10);
            bg2.add(jrb11);
            bg2.add(jrb12);

            jp2.add(jrb7);
            jp2.add(jrb8);
            jp2.add(jrb9);
            jp2.add(jrb10);
            jp2.add(jrb11);
            jp2.add(jrb12);

            jp2.setLayout(new GridLayout(6, 1));
            jp2.setBorder(BorderFactory.createTitledBorder(
                    BorderFactory.createTitledBorder("Weapon"), "Weapon"));

            // add character panel and weapon panel to the top panel
            jpUp.setLayout(new GridLayout(1, 2));
            jpUp.add(jp1);
            jpUp.add(jp2);

            // bottom panel has "OK" and "Cancel" button
            JPanel jpBottom = new JPanel();
            JButton jb1 = new JButton("OK");
            jb1.addActionListener(actionEvent1 -> {
                String character = getSelectedButtonText(bg1);
                String weapon = getSelectedButtonText(bg2);
                suggestionFrame.dispose();
                game.makeSuggestion(character, weapon);
                boardCanvas.repaint();
            });
            JButton jb2 = new JButton("Cancel");
            jb2.addActionListener(actionEvent12 -> suggestionFrame.dispose());
            jpBottom.add(jb1);
            jpBottom.add(jb2);

            suggestionFrame.add(jpUp, BorderLayout.CENTER);
            suggestionFrame.add(jpBottom, BorderLayout.SOUTH);
            suggestionFrame.setTitle("Make Suggestion");
            suggestionFrame.setVisible(true);
        });

        buttons.get("End turn").addActionListener(actionEvent -> {
            if(game == null) return;
            game.endTurn();
        });
    }

    public String getSelectedButtonText(ButtonGroup buttonGroup) {
        for (Enumeration<AbstractButton> buttons = buttonGroup.getElements(); buttons.hasMoreElements();) {
            AbstractButton button = buttons.nextElement();

            if (button.isSelected()) {
                return button.getText();
            }
        }
        return null;
    }



}
