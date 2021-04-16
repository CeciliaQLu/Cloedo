package Cluedo.View;

import Cluedo.Controller.Controller;
import Cluedo.Model.Game;
import Cluedo.Model.Player;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GUI extends JFrame{

    private static String[] preferredFonts = {"Arial","Times New Roman"};

    // menu part:
    private JMenu menu;

    private JMenuItem i1, i2, i3, i4;

    private JMenuBar mb;

    private Controller controller;

    /**
     * left Inner Panel: board
     */
    private JPanel leftInnerPanel;
    /**
     * right Inner Panel: dice, buttons and cards
     */
    private JPanel rightInnerPanel;
    /**
     * bottom Inner Panel: text to show
     */
    private JPanel bottomInnerPanel;
    /**
     * board Canvas
     */
    private BoardCanvas boardCanvas;

    private TextCanvas textCanvas;

    private UpdateCanvas updateCanvas;

    private EnvelopCanvas envelopCanvas;

    private Game game;

    private boolean gameStarted = false;

    /**JButtons for the GUI to allow the player to roll dice and make suggestions*/
    JButton rollDice;
    JButton suggestion;
    JButton endTurn;

    public GUI(boolean ongoingGame){
        super("Cluedo");
        // menu bar:
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mb = new JMenuBar();
        menu = new JMenu("Game");
        i1 = new JMenuItem("Instructions"); // instructions
        i1.setToolTipText("Basic instructions for the game");
        i2 = new JMenuItem("New Game");     // start a new game
        i2.setToolTipText("Start a new game of Cluedo");
        i2.addActionListener(new MenuActionListener2());
        i3 = new JMenuItem("End Current Game");
        i3.addActionListener(actionEvent -> endCurrentGame());
        i4 = new JMenuItem("Exit");

        menu.add(i1);
        menu.add(i2);
        menu.add(i3);
        menu.add(i4);
        Map<String, JMenuItem> menuItems = new HashMap<>();
        menuItems.put("Instructions", i1);
        menuItems.put("New Game", i2);
        menuItems.put("End Current Game", i3);
        menuItems.put("Exit", i4);
        mb.add(menu);
        setJMenuBar(mb);

        // board canvas on the left
        boardCanvas = new BoardCanvas();

        leftInnerPanel = new JPanel();
        leftInnerPanel.setLayout(new BorderLayout());
        Border cb = BorderFactory.createCompoundBorder(BorderFactory
                .createEmptyBorder(3, 3, 3, 3), BorderFactory
                .createLineBorder(Color.gray));
        leftInnerPanel.setBorder(cb);
        leftInnerPanel.add(boardCanvas, BorderLayout.CENTER);

        // buttons on the right
        rightInnerPanel = new JPanel(); //new GridLayout(0, 1, 50, 380)
        rightInnerPanel.setLayout(new BorderLayout());
        cb = BorderFactory.createCompoundBorder(BorderFactory
                .createEmptyBorder(3, 3, 3, 3), BorderFactory
                .createLineBorder(Color.gray));
        rightInnerPanel.setBorder(cb);

        textCanvas = new TextCanvas();
        textCanvas.setTextCanvasStartY(0);
        JPanel p1 = new JPanel();
        p1.setLayout(new BorderLayout());
        cb = BorderFactory.createCompoundBorder(BorderFactory
                .createEmptyBorder(3, 3, 3, 3), BorderFactory
                .createLineBorder(Color.gray));
         p1.setBorder(cb);

         p1.add(textCanvas, BorderLayout.WEST);
         p1.addMouseWheelListener(e -> {
             int wheelRotation = e.getWheelRotation();
             if(wheelRotation > 0){
                 for(int i = 0; i < wheelRotation; i++){
                     if(textCanvas.getHeight() - textCanvas.getTextCanvasStartY() > 430) {
                         return;
                     }else{

                         textCanvas.setTextCanvasStartY(textCanvas.getTextCanvasStartY() - 20);
                         textCanvas.repaint();
                     }
                 }
             }else if(wheelRotation < 0){
                 for(int i = 0; i < -wheelRotation; i++){
                     if(textCanvas.getTextCanvasStartY() == 0){
                         return;
                     }
                     textCanvas.setTextCanvasStartY(textCanvas.getTextCanvasStartY() + 20);
                     textCanvas.repaint();
                 }
             }
         });

        JPanel p2 =new JPanel();
        p2.setLayout(new BorderLayout());
        cb = BorderFactory.createCompoundBorder(BorderFactory
                .createEmptyBorder(3, 3, 3, 3), BorderFactory
                .createLineBorder(Color.gray));
        p2.setBorder(cb);

        updateCanvas = new UpdateCanvas();
        updateCanvas.setCurrentPerson("_");
        updateCanvas.setCurrentPlayer("_");
        updateCanvas.setRemainMove(0);
        p2.add(updateCanvas);

        rightInnerPanel.setLayout(new GridLayout(2, 1));
        rightInnerPanel.add(p1);
        rightInnerPanel.add(p2);

        // Text on the bottom
        rollDice = new JButton("Roll Dice");
        rollDice.setForeground(Color.gray);
        suggestion = new JButton("Suggestion");
        suggestion.setForeground(Color.gray);
        endTurn = new JButton("End Turn");
        endTurn.setForeground(Color.gray);

        Map<String, JButton> buttons = new HashMap<>();
        buttons.put("Roll Dice", rollDice);
        buttons.put("Suggestions", suggestion);
        buttons.put("End turn", endTurn);

        bottomInnerPanel = new JPanel();
        bottomInnerPanel.add(rollDice);
        bottomInnerPanel.add(suggestion);
        bottomInnerPanel.add(endTurn);

        add(leftInnerPanel,BorderLayout.CENTER);
        add(rightInnerPanel,BorderLayout.EAST);
        add(bottomInnerPanel, BorderLayout.SOUTH);

        setFocusable(true);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setVisible(true);
        if(ongoingGame){
            MenuActionListener2 listener = new MenuActionListener2();
            listener.actionPerformed(new ActionEvent(new JMenuItem(),1736452436,null));
        }

        controller = new Controller(boardCanvas, menuItems, buttons);
    }

    public void endCurrentGame(){
        for(Frame f : getFrames()){
            f.dispose();
        }
        new GUI(false);
    }

    public void diceAnimation(int diceOne, int diceTwo){
        int remainMove = diceOne + diceTwo;
        updateCanvas.setRemainMove(remainMove);

        JFrame diceFrame = new JFrame("Rolling Dice");
        diceFrame.setSize(new Dimension(900, 600));
        diceFrame.setLocation(50, 100);

        JPanel contentPanel = (JPanel) diceFrame.getContentPane();
        contentPanel.setSize(new Dimension(300, 150));
        String ii1Name = "./assets/Animations/0" + diceOne + ".gif";
        String ii2Name = "./assets/Animations/0" + diceTwo + ".gif";

        ImageIcon ii1 = new ImageIcon(ii1Name);
        ImageIcon ii2 = new ImageIcon(ii2Name);

        // add two dice to the panel
        JLabel imageLabel1 = new JLabel();
        imageLabel1.setSize(new Dimension(100, 100));
        JLabel imageLabel2 = new JLabel();
        imageLabel2.setSize(new Dimension(100, 100));

        imageLabel1.setIcon(ii1);
        imageLabel2.setIcon(ii2);
        contentPanel.add(imageLabel1);
        contentPanel.add(imageLabel2);

        // add panel to frame
        contentPanel.setLayout(new GridLayout(1, 2));
        diceFrame.setLocationRelativeTo(null);
        diceFrame.setVisible(true);

        ii1.getImage().flush();
        ii2.getImage().flush();

        rollDice.setForeground(Color.gray);
        endTurn.setForeground(Color.black);
        updateCanvas.repaint();
    }

    public void updateCanvasAfterMove(int remainingMoves){
        boardCanvas.repaint();
        setRemainingMoves(remainingMoves);
        updateCanvas.repaint();
    }

    public void setRemainingMoves(int remainingMoves){
        updateCanvas.setRemainMove(remainingMoves);
    }

    public void newTurn(boolean inRoom){
        if(inRoom)setSuggestionColour(true);
        else setSuggestionColour(false);
        setEndTurnColour(false);
        setRollDiceColour(true);
        updateCanvas.repaint();
    }

    public void setRollDiceColour(boolean black){
        if(black) rollDice.setForeground(Color.black);
        else rollDice.setForeground(Color.gray);
    }

    public void setSuggestionColour(boolean black){
        if(black) suggestion.setForeground(Color.black);
        else suggestion.setForeground(Color.gray);
    }

    public void setEndTurnColour(boolean black){
        if(black) endTurn.setForeground(Color.black);
        else endTurn.setForeground(Color.gray);
    }

    public void updatePlayer(Player player){
        updateCanvas.setCurrentPlayer(player.toString());
        updateCanvas.setCurrentPerson(player.getPerson().getName());
    }

    class MenuActionListener2 implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            if(gameStarted == true){

                JFrame jfm = new JFrame("End Game?");
                jfm.setSize(260, 150);
                jfm.setLocation(200, 200);

                JLabel jlb = new JLabel("     Game ongoing. End current game?");

                JButton b1 = new JButton("End");
                b1.addActionListener(actionEvent -> {
                    for(Frame f : getFrames()){
                        f.dispose();
                    }
                    new GUI(true);
                    jfm.dispose();
                    return;
                });
                JButton b2 = new JButton("Continue");
                b2.addActionListener(actionEvent -> {
                    jfm.dispose();
                    return;
                });

                JPanel jpnl = new JPanel();
                jpnl.add(b1);
                jpnl.add(b2);

                jfm.setLayout(new GridLayout(2, 1));
                jfm.add(jlb);
                jfm.add(jpnl);
                jfm.setResizable(false);
                jfm.setVisible(true);

                return;
            }

            String[] options = {"3", "4", "5", "6"};
            int num = JOptionPane.showOptionDialog(null, "Number of Players", "Number of Players", JOptionPane.PLAIN_MESSAGE, JOptionPane.PLAIN_MESSAGE, null, options, 3);
            num = 3 + num;
            int numberOfPlayers = num;
            updateCanvas.setNumberOfPlayers(numberOfPlayers);

            JFrame inputNameFrame = new JFrame("Input Name");
            inputNameFrame.setSize(500, 500);
            inputNameFrame.setLocation(200, 200);

            inputNameFrame.setLayout(new GridLayout(num + 1, 1));
            ArrayList<JTextField> fields = new ArrayList<>();

            // text
            for(int i = 0; i < num; i++){
                JPanel pl = new JPanel();
                pl.add(new JLabel("Name of player " + (i + 1) + ": "));
                JTextField txtField = new JTextField(20);
                fields.add(txtField);
                pl.add(txtField);
                inputNameFrame.add(pl);
            }

            // button
            JPanel bp = new JPanel();
            JButton okbt = new JButton("OK");

            bp.add(okbt);
            inputNameFrame.add(bp);

            inputNameFrame.setVisible(true);

            okbt.addActionListener(actionEvent -> {
                ArrayList<String> playerNames = new ArrayList<>();
                int defaultCount = 1;
                for(JTextField t : fields){
                    String s = t.getText();
                    if(s.length() >= 1){
                        playerNames.add(s);
                    }else{
                        playerNames.add("Player " + defaultCount);
                        defaultCount++;
                    }
                }
                game = new Game(numberOfPlayers, playerNames, GUI.this);
                game.initialise();

                game.board.placeWeapons();

                gameStarted = true;

                inputNameFrame.dispose();

                boardCanvas.setGameStarted(gameStarted);
                boardCanvas.setGame(game);

                textCanvas.setGame(game);
                updateCanvas.setGame(game);
                boardCanvas.repaint();

                updateCanvas.setCurrentPlayer(playerNames.get(0));
                updateCanvas.setCurrentPerson("Miss Scarlett");
                updateCanvas.setRemainMove(0);
                updateCanvas.setNumberOfPlayers(numberOfPlayers);
                updateCanvas.repaint();

                rollDice.setForeground(Color.BLACK);

                controller.addGame(game);
            });
        }
    }

    public void changePlayer(int index){ updateCanvas.setCurrentPlayerIndex(index); }

    public boolean envelopePopUp(String character, String weapon, String room){
        JOptionPane.showMessageDialog(null, "Nice! Open Envelop!", "Open Envelop", JOptionPane.INFORMATION_MESSAGE);
        JFrame envelopFrame = new JFrame("Envelop");
        envelopFrame.setSize(new Dimension(400, 400));
        envelopFrame.setLayout(new BorderLayout());

        JPanel envelopPanel = new JPanel();
        envelopPanel.setLayout(new BorderLayout());
        envelopCanvas = new EnvelopCanvas();
        envelopCanvas.setGame(game);
        envelopCanvas.setAccusationCards(character, weapon, room);
        envelopPanel.add(envelopCanvas);

        JButton okButton = new JButton("Ok");
        okButton.addActionListener(actionEvent11 -> {
            envelopFrame.dispose();
            if(envelopCanvas.getWin()) endCurrentGame();
        });


        JPanel buttonPanel = new JPanel();
        buttonPanel.add(okButton);

        envelopFrame.add(envelopPanel, BorderLayout.CENTER);
        envelopFrame.add(buttonPanel, BorderLayout.SOUTH);
        envelopFrame.setVisible(true);
        envelopFrame.setFocusable(true);
        pack();
        return envelopCanvas.getWin();
    }

    public static void main(String[] args) {
        new GUI(false);
    }
}
