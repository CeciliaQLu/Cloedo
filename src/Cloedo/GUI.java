package Cluedo;

import Cluedo.Items.Person;
import Cluedo.Items.Room;
import Cluedo.Items.Weapon;
import Cluedo.Tiles.DoorTile;
import Cluedo.Tiles.HallTile;
import Cluedo.Tiles.RoomTile;
import Cluedo.Tiles.Tile;
import javafx.scene.control.ScrollBar;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.AttributedCharacterIterator;
import java.util.*;
import java.util.List;
import java.util.function.Function;

/** Author: CeciliaLu */

public class GUI extends JFrame implements ActionListener, KeyListener {

    private static String[] preferredFonts = {"Arial","Times New Roman"};

    // menu part:
    private JMenu menu;

    private JMenuItem i1, i2, i3, i4;

    private JMenuBar mb;


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

    private int numberOfPlayers;

    private int currentPlayerIndex = 0;

    private Game game;

    private boolean gameStarted = false;


    private String currentPlayer = "_";

    private String currentPerson = "_";

    private int remainMove = 0;


    private int textCanvasStartY = 0;

    // private boolean hasRolledDice = false;
    private int clickX = 0;
    private int clickY = 0;
    private int moveLeftOrRight = 0;
    private int moveUpOrDown = 0;

    private boolean canRollDice = false;
    private boolean hasRollDice = false;
    private boolean currentPlayerCanMove = false;

    private ArrayList<Integer[]> currentPlayerSquares = new ArrayList<>();


    private boolean currentPlayerCanMakeSuggestion = false;

    private boolean canGetOut = false;

    public GUI(){
        super("Cluedo");
        // menu bar:
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mb = new JMenuBar();
        menu = new JMenu("Game");
        i1 = new JMenuItem("Instruction"); // instruction

        i1.setToolTipText("Basic instruction for the game");

        i1.addActionListener(new MenuActionListener1());
        i2 = new JMenuItem("New Game");     // start a new game

        i2.setToolTipText("Start a new game of Cluedo");

        i2.addActionListener(new MenuActionListener2());
        i3 = new JMenuItem("End Current Game");
        i3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                for(Frame f : getFrames()){
                    f.dispose();
                }
                // getFrames();

                new GUI();
                return;
            }
        });
        i4 = new JMenuItem("Exit");
        i4.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                System.exit(0);
            }
        });

        menu.add(i1);
        menu.add(i2);
        menu.add(i3);
        menu.add(i4);
        mb.add(menu);
        setJMenuBar(mb);




        // board canvas on the left
        boardCanvas = new BoardCanvas();
        // boardCanvas.setBounds(0, 0, 500, 500);


        leftInnerPanel = new JPanel();
        leftInnerPanel.setLayout(new BorderLayout());
        Border cb = BorderFactory.createCompoundBorder(BorderFactory
                .createEmptyBorder(3, 3, 3, 3), BorderFactory
                .createLineBorder(Color.gray));
        leftInnerPanel.setBorder(cb);
        leftInnerPanel.add(boardCanvas, BorderLayout.CENTER);

        // add mouselistener to send the player out of the room
        boardCanvas.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(gameStarted == false){
                    return;
                }

                clickX = e.getX();
                clickY = e.getY();

                int wid = (boardCanvas.getWidth()-4)/24;
                int hei = (boardCanvas.getHeight()-4)/25;

                int currentRow = game.players[currentPlayerIndex].getRow();
                int currentCol = game.players[currentPlayerIndex].getCol();

                int newCol = (clickX - 5)/wid;
                int newRol = (clickY - 5)/hei;



                if(canGetOut == false){
                    return;
                }
                if(hasRollDice == false){
                    return;
                }


                Tile currentTile = game.board.tiles[currentRow][currentCol];
                Tile newTile = game.board.tiles[newRol][newCol]; // new Tile(newRol, newCol);


                if(!(currentTile instanceof RoomTile)){
                    return;
                }
                if(!(newTile instanceof DoorTile)){
                    return;
                }


                Room room = ((RoomTile)currentTile).getRoom();

                Map<Integer, DoorTile> map = room.getDoorTiles();
                for(DoorTile dt : map.values()){
                    if(dt.getRow() == newRol && dt.getCol() == newCol){
                        if(dt.getItem() == null){
                            currentTile.setItem(null);
                            dt.setItem(game.players[currentPlayerIndex].getPerson());
                            game.players[currentPlayerIndex].setRow(dt.getRow());
                            game.players[currentPlayerIndex].setCol(dt.getCol());
                            game.players[currentPlayerIndex].getPerson().setRow(dt.getRow());
                            game.players[currentPlayerIndex].getPerson().setCol(dt.getCol());
                            remainMove--;
                            updateCanvas.repaint();
                            boardCanvas.repaint();

                            canGetOut = false;
                            currentPlayerCanMakeSuggestion = false;
                        }
                        break;
                    }
                }

            }

            @Override
            public void mousePressed(MouseEvent e) {


            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });

        boardCanvas.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                char keyPressed = e.getKeyChar();
                if(keyPressed == 'w' || keyPressed == 'W'){ // || e.getKeyCode() == KeyEvent.VK_UP
                    moveUpOrDown = -1;
                    moveLeftOrRight = 0;
                }else if(keyPressed == 's' || keyPressed == 'S'){ // || e.getKeyCode() == KeyEvent.VK_DOWN
                    moveUpOrDown = 1;
                    moveLeftOrRight = 0;
                }else if(keyPressed == 'a' || keyPressed == 'A'){ // || e.getKeyCode() == KeyEvent.VK_LEFT
                    moveLeftOrRight = -1;
                    moveUpOrDown = 0;
                }else if(keyPressed == 'd' || keyPressed == 'D' ){ //|| e.getKeyCode() == KeyEvent.VK_RIGHT
                    moveLeftOrRight = 1;
                    moveUpOrDown = 0;
                }else if(keyPressed == 'x' || keyPressed == 'X'){
                    // next player
                    nextPlayer();
                }


                if(currentPlayerCanMove && remainMove > 0){
                    int currentRow = game.players[currentPlayerIndex].getRow();
                    int currentCol = game.players[currentPlayerIndex].getCol();
                    Tile oldTile = game.board.tiles[currentRow][currentCol];
                    int newRow = currentRow + moveUpOrDown;
                    int newCol = currentCol + moveLeftOrRight;
                    //System.out.println(currentRow + "  " + () + "  " + currentCol + "  " + ());
                    if(newRow < 0 || newRow >= 25 || newCol < 0 || newCol >= 24){
                        return;
                    }
                    for(Integer[] p : currentPlayerSquares){
                        if(newRow == p[0] && newCol == p[1]){
                            return;
                        }
                    }

                    Tile newTile = game.board.tiles[currentRow + moveUpOrDown ][currentCol + moveLeftOrRight];

                    if(newTile instanceof DoorTile || newTile instanceof HallTile) {
                        if(newTile.getItem() != null) {
                            // System.out.println("Someone is already on that square");
                            return;
                        }else{
                            game.board.tiles[currentRow][currentCol].setItem(null);
                            newTile.setItem(game.players[currentPlayerIndex].getPerson());
                            game.players[currentPlayerIndex].getPerson().setRow(newRow);
                            game.players[currentPlayerIndex].getPerson().setCol(newCol);
                            game.players[currentPlayerIndex].setRow(newRow);
                            game.players[currentPlayerIndex].setCol(newCol);
                            // add pos to the list
                            Integer[] posPair = {newRow, newCol};
                            currentPlayerSquares.add(posPair);
                            boardCanvas.repaint();
                            remainMove--;
                            updateCanvas.repaint();
                        }
                    }else if(oldTile instanceof DoorTile && newTile instanceof RoomTile){
                        game.board.tiles[currentRow][currentCol].setItem(null);
                        ((RoomTile) newTile).getRoom().placeRandom(game.players[currentPlayerIndex].getPerson());
                        game.players[currentPlayerIndex].setRow(game.players[currentPlayerIndex].getPerson().row());
                        game.players[currentPlayerIndex].setCol(game.players[currentPlayerIndex].getPerson().col());
                        boardCanvas.repaint();
                        // remainMove--;
                        updateCanvas.repaint();
                        // ask suggestion
                        JOptionPane.showMessageDialog(null, "You can make suggestion now.", "Ask for suggestion", JOptionPane.INFORMATION_MESSAGE);

                        currentPlayerCanMakeSuggestion = true;



                    }else{
                        System.out.println("?");
                    }
                }
                if(remainMove == 0){
                    nextPlayer();

                }


            }

            @Override
            public void keyPressed(KeyEvent keyEvent) {

            }

            @Override
            public void keyReleased(KeyEvent keyEvent) {

            }
        });



        // buttons on the right
        rightInnerPanel = new JPanel(); //new GridLayout(0, 1, 50, 380)
        rightInnerPanel.setLayout(new BorderLayout());
        cb = BorderFactory.createCompoundBorder(BorderFactory
                .createEmptyBorder(3, 3, 3, 3), BorderFactory
                .createLineBorder(Color.gray));
        rightInnerPanel.setBorder(cb);



        textCanvas = new TextCanvas();
        JPanel p1 = new JPanel();
        p1.setLayout(new BorderLayout());
        cb = BorderFactory.createCompoundBorder(BorderFactory
                .createEmptyBorder(3, 3, 3, 3), BorderFactory
                .createLineBorder(Color.gray));
         p1.setBorder(cb);


         p1.add(textCanvas, BorderLayout.WEST);
         p1.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                int wheelRotation = e.getWheelRotation();
                if(wheelRotation > 0){
                    for(int i = 0; i < wheelRotation; i++){
                        if(textCanvas.getHeight() - textCanvasStartY > 430) {
                            return;
                        }else{
                            textCanvasStartY = textCanvasStartY - 20;
                            textCanvas.repaint();
                        }
                    }
                }else if(wheelRotation < 0){
                    for(int i = 0; i < -wheelRotation; i++){
                        if(textCanvasStartY == 0){
                            return;
                        }
                        textCanvasStartY = textCanvasStartY + 20;
                        textCanvas.repaint();
                    }
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
        p2.add(updateCanvas);


        rightInnerPanel.setLayout(new GridLayout(2, 1));
        rightInnerPanel.add(p1);
        rightInnerPanel.add(p2);


        // Text on the bottom
        JButton rollDice = new JButton("Roll Dice");
        JButton suggestion = new JButton("Suggestion");
        JButton endTurn = new JButton("End Turn");


        // rollDice.addActionListener(this);
        rollDice.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {

                if(canRollDice == false){
                    return;
                }

                JFrame diceFrame = new JFrame("Rolling Dice");
                diceFrame.setSize(new Dimension(900, 600));
                diceFrame.setLocation(50, 100);


                JPanel contentPanel = (JPanel) diceFrame.getContentPane();
                contentPanel.setSize(new Dimension(300, 150));


                // get random numbers
                int diceOne = (int) (Math.random() * 6) + 1;
                int diceTwo = (int) (Math.random() * 6) + 1;

                remainMove = diceOne + diceTwo;

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

                updateCanvas.repaint();

                canRollDice = false;
                hasRollDice = true;
                run();


            }
        });


        suggestion.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if(currentPlayerCanMakeSuggestion == false){
                    return;
                }
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
                jp1.setBorder(BorderFactory.createTitledBorder(
                        BorderFactory.createEtchedBorder(), "Character"));




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
                jb1.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {
                        String suggestCharacter = getSelectedButtonText(bg1);
                        String suggestWeapon = getSelectedButtonText(bg2);
                        Room room = game.board.getRoom(game.players[currentPlayerIndex]);


                        Player playerTeleported = game.getPlayer(suggestCharacter);
                        Weapon weaponTeleported = game.getWeapon(suggestWeapon);

                        if(playerTeleported != null){
                            room.placeRandom(playerTeleported.getPerson());
                            int pr = playerTeleported.getPerson().row();
                            int pc = playerTeleported.getPerson().col();
                            playerTeleported.setRow(pr);
                            playerTeleported.setCol(pc);
                        }
                        room.placeRandom(weaponTeleported);
                        boardCanvas.repaint();

                        Card weaponCard = game.cards.get(suggestWeapon);
                        Card roomCard = game.cards.get(room.getName());
                        Card personCard = game.cards.get(suggestCharacter);

                        for(int i = currentPlayerIndex + 1; i != currentPlayerIndex; i++){
                            if(i == game.players.length){
                                if(currentPlayerIndex == 0) break;
                                i = 0;
                            }
                            List<Card> hand = game.players[i].getHand();
                            Card card = null;
                            if(hand.contains(weaponCard)) card = weaponCard;
                            else if(hand.contains(roomCard)) card = roomCard;
                            else if(hand.contains(personCard)) card = personCard;
                            if(card != null){
                                String disputedInfo = "Your suggestion has been disputed: " + game.players[i].getName() + " has the " + card.getName() + " card";
                                JOptionPane.showMessageDialog(null, disputedInfo, "Suggestion Disputed", JOptionPane.INFORMATION_MESSAGE);
                                suggestionFrame.dispose();

                                hasRollDice = true;
                                nextPlayer();
                                return;
                            }
                        }

                        String noDisputedInfo = "Nobody can dispute this guess. \nWould you like to make an accusation?";
                        int yesOrNo = JOptionPane.showConfirmDialog(null, noDisputedInfo, "Accusation", JOptionPane.YES_NO_OPTION); // yes: 0; no: 1


                        if(yesOrNo == 0){ // Make an accusation -- open the envelop
                            // make correct accusation: win game
                            JOptionPane.showMessageDialog(null, "Nice! Open Envelop!", "Open Envelop", JOptionPane.INFORMATION_MESSAGE);
                            JFrame envelopFrame = new JFrame("Envelop");
                            envelopFrame.setSize(new Dimension(400, 400));
                            envelopFrame.setLayout(new BorderLayout());

                            JPanel envelopPanel = new JPanel();
                            envelopPanel.setLayout(new BorderLayout());
                            envelopCanvas = new EnvelopCanvas();
                            envelopCanvas.setAccusationCards(suggestCharacter, suggestWeapon, room.getName());
                            envelopPanel.add(envelopCanvas);

                            JButton okButton = new JButton("Ok");
                            okButton.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent actionEvent) {
                                    envelopFrame.dispose();
                                }
                            });

                            JPanel buttonPanel = new JPanel();
                            buttonPanel.add(okButton);

                            envelopFrame.add(envelopPanel, BorderLayout.CENTER);
                            envelopFrame.add(buttonPanel, BorderLayout.SOUTH);
                            envelopFrame.setVisible(true);
                            envelopFrame.setFocusable(true);
                            pack();

                            if(envelopCanvas.win == true){
                                setGameOver();
                            }else{ // make wrong accusation, set isHasmadewrongaccusation for the currentplayer, and check if all players have made wrong accusation
                                game.players[currentPlayerIndex].setHasMadeWrongAccusation(true);
                                boolean isGameOver = true;
                                for(int i = 0; i < game.players.length; i++){
                                    if(game.players[i].isHasMadeWrongAccusation() == false){
                                        isGameOver = false;
                                        nextPlayer();
                                        break;
                                    }
                                }
                                if(isGameOver == true){
                                    setGameOver();
                                    JOptionPane.showMessageDialog(null, "Game Over.", "Game Over", JOptionPane.INFORMATION_MESSAGE);
                                }
                            }




                        }else if(yesOrNo == 1){ // do not make accusation, go to next player
                            nextPlayer();
                        }


                suggestionFrame.dispose();
                    }
                });
                JButton jb2 = new JButton("Cancel");
                jb2.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {
                        suggestionFrame.dispose();
                    }
                });
                jpBottom.add(jb1);
                jpBottom.add(jb2);

                suggestionFrame.add(jpUp, BorderLayout.CENTER);
                suggestionFrame.add(jpBottom, BorderLayout.SOUTH);

                suggestionFrame.setTitle("Make Suggestion");
                suggestionFrame.setVisible(true);
                currentPlayerCanMakeSuggestion = false;

            }
        });


        endTurn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if(gameStarted == false || currentPlayer == null){
                    return;
                }
                nextPlayer();
            }
        });

        bottomInnerPanel = new JPanel();
        bottomInnerPanel.add(rollDice);
        bottomInnerPanel.add(suggestion);
        bottomInnerPanel.add(endTurn);


        add(leftInnerPanel,BorderLayout.CENTER);
        add(rightInnerPanel,BorderLayout.EAST);
        add(bottomInnerPanel, BorderLayout.SOUTH);


        setFocusable(true);
        addKeyListener(this);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setVisible(true);

    }

    public void setGameOver(){
        canRollDice = false;
        hasRollDice = true;
        currentPlayerCanMove = false;
        currentPlayerCanMakeSuggestion = false;
        canGetOut = false;
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


    public String readAllLines(){
        String ans = "";
        try{
            BufferedReader br = new BufferedReader(new FileReader("./assets/Introduction.txt"));
            String str;
            while((str = br.readLine()) != null){
                ans += (str + "\n");
            }

        }  catch (IOException e) {
            e.printStackTrace();
        }
        return ans;
    }

    class MenuActionListener1 implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String rules = readAllLines();
            JOptionPane.showMessageDialog(null, readAllLines(), "Game Introduction", JOptionPane.PLAIN_MESSAGE);

        }
    }

    class MenuActionListener2 implements ActionListener {
        public void actionPerformed(ActionEvent e) {

            if(gameStarted == true){

                JFrame jfm = new JFrame("End Game?");
                jfm.setSize(260, 150);
                jfm.setLocation(200, 200);

                JLabel jlb = new JLabel("     Game ongoing. End current game?");

                JButton b1 = new JButton("End");
                b1.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {
                        for(Frame f : getFrames()){
                            f.dispose();
                        }
                        new GUI();
                        jfm.dispose();
                        return;
                    }
                });
                JButton b2 = new JButton("Continue");
                b2.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {
                        jfm.dispose();
                        return;
                    }
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


            String[] options = {"6", "5", "4", "3"};
            int num = JOptionPane.showOptionDialog(null, "Number of Players", "Number of Players", JOptionPane.PLAIN_MESSAGE, JOptionPane.PLAIN_MESSAGE, null, options, 3);
            num = 6 - num;
            numberOfPlayers = num;


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


            okbt.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent actionEvent) {
                    ArrayList<String> playerNames = new ArrayList<>();
                    int defaultCount = 1;
                    for(JTextField t : fields){
                        String s = t.getText();
                        if(s.length() >= 1){
                            playerNames.add(s);
                        }else{
                            playerNames.add("Default Name " + defaultCount);
                            defaultCount++;
                        }
                    }
                    game = new Game(numberOfPlayers, playerNames);
                    game.initialise();

                    game.board.placeWeapons();

                    gameStarted = true;
                    inputNameFrame.dispose();
                    boardCanvas.repaint();
                    currentPlayer = playerNames.get(0);
                    currentPerson = "Miss Scarlett";
                    updateCanvas.repaint();
                    canRollDice = true;

                }
            });


        }
    }



    boolean canMakeSuggestion = false;
    boolean hasMadeSuggestion = false;


    public void nextIndex(){
        if(currentPlayerIndex < numberOfPlayers - 1){
            currentPlayerIndex = currentPlayerIndex + 1;
        }else if(currentPlayerIndex == numberOfPlayers - 1){
            currentPlayerIndex = 0;
        }
    }

    public void nextPlayer(){
        if(hasRollDice == false){
            return;
        }

        nextIndex();
        if(game.players[currentPlayerIndex].isHasMadeWrongAccusation()){
            while(game.players[currentPlayerIndex].isHasMadeWrongAccusation()){
                nextIndex();
            }
        }

        remainMove = 0;

            currentPlayer = game.players[currentPlayerIndex].toString();
            currentPerson = game.players[currentPlayerIndex].getPerson().getName();
            currentPlayerSquares = new ArrayList<>();
            Integer[] initialisPair = {game.players[currentPlayerIndex].getPerson().row(), game.players[currentPlayerIndex].getPerson().col()};
            currentPlayerSquares.add(initialisPair);
            remainMove = 0;
            canRollDice = true;
            hasRollDice = false;



            currentPlayerCanMakeSuggestion = false;
            updateCanvas.repaint();


            // if the currentPlayer is in a room, can go out by clicking the door
        int r = game.players[currentPlayerIndex].getRow();
        int c = game.players[currentPlayerIndex].getCol();
        Tile t = game.board.tiles[r][c];
        if(t instanceof RoomTile){
            canGetOut = true;
            currentPlayerCanMakeSuggestion = true;
        }else{
            canGetOut = false;
        }


    }


    public void run(){
        Player playerNow = game.players[currentPlayerIndex];
        if(playerNow.isHasMadeWrongAccusation()){
            nextPlayer();
            return;
        }

        boolean allowMoves = true;

        if(game.board.inRoom(game.players[currentPlayerIndex])){
            canMakeSuggestion = true;
            if(hasMadeSuggestion){
                // see if the suggestion is correct
                // if it is, return;
                // otherwise, allowMoves = false;
            }else{
                Room room = game.board.getRoom(game.players[currentPlayerIndex]);
                Map<Integer, DoorTile> doorTiles = room.getDoorTiles();
                boolean blocked = true;
                for(Tile tile : doorTiles.values()){
                    if(tile.getItem() == null){
                        blocked = false;
                    }
                }
                if(blocked){
                    int dialog = JOptionPane.INFORMATION_MESSAGE;
                    JOptionPane.showMessageDialog(null, "You can't leave the room because all the exits are blocked", "Message", dialog);
                    allowMoves = false;
                }else{
//                    boolean leftRoom = false;
//                    while(leftRoom == false){
//                        System.out.println(1);
//                        for(Tile tile : doorTiles.values()){
//                            boolean clickOnDoorTile = checkClickOnDoorTile(tile);
//                            if(clickOnDoorTile){
//                                game.board.teleportPersonToDoor(game.players[currentPlayerIndex], tile);
//                                boardCanvas.repaint();
//                                remainMove--;
//                                currentPlayer = game.players[currentPlayerIndex].toString();
//                                currentPerson = game.players[currentPlayerIndex].getPerson().toString();
//                                updateCanvas.repaint();
//                                leftRoom = true;
//                            }
//                        }
//                    }
                }
            }
        }
        // when the player is not in any room
        if(allowMoves && remainMove > 0){

            currentPlayerCanMove = true;




        }


            }




    private String getMove(){
        if(moveUpOrDown == -1){
            return "w";
        }else if(moveUpOrDown == 1){
            return "s";
        }else if(moveLeftOrRight == -1){
            return "a";
        }else if(moveLeftOrRight == 1){
            return "d";
        }else if(moveLeftOrRight == 100){
            return "x";
        }else{
            return "invalid";
        }
    }

    private boolean checkClickOnDoorTile(Tile tile){
        int tileRow = tile.getRow();
        int tileCol = tile.getCol();
        int wid = (getWidth()-4)/24;
        int hei = (getHeight()-4)/25;
        int minX = 5 + tileCol * wid;
        int maxX = 5 + (tileCol + 1) * wid;
        int minY = 5 + tileRow * hei;
        int maxY = 5 + (tileRow + 1) * hei;
        if(clickX >= minX && clickX <= maxX && clickY >= minY && clickY <= maxY){
            return true;
        }
        return false;
    }



    private class EnvelopCanvas extends Canvas{
        private Font font;
        private String person;
        private String weapon;
        private String room;
        public boolean win = false;
        public EnvelopCanvas(){
            setBounds(0, 0, 400, 100);
            GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
            HashSet<String> availableNames = new HashSet();

            for(String name : env.getAvailableFontFamilyNames()) {
                availableNames.add(name);
            }

            for(String pf : preferredFonts) {
                if(availableNames.contains(pf)) {
                    font = new Font("Comic Sans MS", Font.BOLD, 24);
                    break;
                }
            }
        }

        public void setAccusationCards(String p, String w, String r){
            this.person = p;
            this.weapon = w;
            this.room = r;
        }


        @Override
        public void paint(Graphics g) {
            super.paint(g);
            g.setColor(new Color(255, 250, 250));
            g.fillRect(0, 0, this.getWidth(), this.getHeight());
            int wid = this.getWidth()/7;
            int hei = this.getHeight()/4;

            try{

                BufferedImage bi = ImageIO.read(new File("./assets/Envelop/Envelop.jpg"));
                g.drawImage(bi, this.getWidth()/12, this.getWidth()/12, this.getWidth()/8, this.getWidth()/8, null);



                Card[] murderCards = game.murderCards;
                String personName = murderCards[0].getName();
                String weaponName = murderCards[1].getName();
                String roomName = murderCards[2].getName();

                BufferedImage bi_person = ImageIO.read(new File("./assets/Envelop/" + personName + ".jpg"));
                g.drawImage(bi_person, wid, hei, wid, wid*3/2, null);

                BufferedImage bi_weapon = ImageIO.read(new File("./assets/Envelop/" + weaponName + ".jpg"));
                g.drawImage(bi_weapon, wid*3, hei, wid, wid*3/2, null);

                BufferedImage bi_room = ImageIO.read(new File("./assets/Envelop/" + roomName + ".jpg"));
                g.drawImage(bi_room, wid*5, hei, wid, wid*3/2, null);


                g.setColor(Color.BLACK);
                if(personName.equals(this.person) && weaponName.equals(this.weapon) && roomName.equals(this.room)){
                    this.win = true;
                    g.drawString("You Win!", wid * 3, hei*3);
                }else{
                    g.drawString("Oops! Better luck next time!", wid * 3, hei*3);
                }


            }catch (IOException e){
                throw new Error("Error: something wrong with Envelop Canvas");
            }

        }
    }


    private class UpdateCanvas extends Canvas {

        private Font font;

        public UpdateCanvas(){

            setBounds(0, 420, 400, 100);
            GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
            HashSet<String> availableNames = new HashSet();

            for(String name : env.getAvailableFontFamilyNames()) {
                availableNames.add(name);
            }

            for(String pf : preferredFonts) {
                if(availableNames.contains(pf)) {
                    font = new Font(pf,Font.PLAIN,20);
                    break;
                }
            }
        }

        @Override
        public void paint(Graphics g) {
            super.paint(g);
            g.drawString(currentPlayer + "'s turn", 10, 20);
            g.drawString(currentPlayer + " controls " + currentPerson, 10, 40);
            g.drawString("Move remaining: " + remainMove, 10, 60);
            g.drawString("Cards in hand: ", 10 , 80);

            if(numberOfPlayers > 0){
                List<Card> cardsCurrentPlayer = game.players[currentPlayerIndex].getHand();
                int start_x = 10;
                int start_y = 100;
                int countX = 0;
                int countY = 0;
                for(int i = 0; i < cardsCurrentPlayer.size(); i++){
                    String nm = cardsCurrentPlayer.get(i).getName();
//                    List<String> characterNames = Arrays.asList(game.CHARACTER_NAMES);
//                    List<String> weaponNames = Arrays.asList(game.WEAPON_NAMES);
                    BufferedImage bi = null;
                    try {
                        bi = ImageIO.read(new File("./assets/Envelop/" + nm + ".jpg"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    g.drawImage(bi, start_x + countX * 90, start_y + countY * 100, 65, 85, null);

                    nm = getShorterName(nm);
                    g.drawString(nm, start_x + countX * 90, start_y + countY * 100 + 95);

                    if(countX == 3){
                        countX = 0;
                        countY++;
                    }else{
                        countX++;
                    }




                }
            }
        }
    }

    private String getShorterName(String nm){
        if(nm.equals("Colonel Mustard")){
            nm = "Mustard";
        }
        if(nm.equals("Professor Plum")){
            nm = "Prof. Plum";
        }
        if(nm.equals("Miss Scarlett")){
            nm = "Scarlett";
        }
        if(nm.equals("Billiards Room")){
            nm = "Billiards";
        }if(nm.equals("Dining Room")){
            nm = "Dining";
        }
        return nm;
    }



    private class BoardCanvas extends Canvas{
        /**
         * font
         */
        private Font font;

        public BoardCanvas(){

           // this.board = b;

            setBounds(0, 0, 800, 800);
            GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
            HashSet<String> availableNames = new HashSet();

            for(String name : env.getAvailableFontFamilyNames()) {
                availableNames.add(name);
            }

            for(String pf : preferredFonts) {
                if(availableNames.contains(pf)) {
                    font = new Font(pf,Font.PLAIN,20);
                    break;
                }
            }
        }


        @Override
        public void paint(Graphics g){
            super.paint(g);
            int width = (getWidth()-4)/24;
            int height = (getHeight()-4)/25;
            if(font != null) {
                g.setFont(font);
            }
            // draw background



            g.setColor(new Color(240, 255, 240));
            g.fillRect(0,0, getWidth(),getHeight());
            g.setColor(new Color(255, 250, 205));
            g.fillRect(5,5, width * 24, height * 25);
            drawGrid(g, 5, 5, width, height);
            drawBoardBackground(g, 5, 5, width, height);


            drawStrings(g, 5, 5, width, height);

             // draw characters/people
            if(gameStarted == true){
                try {
                    drawPeopleAndWeapons(g, 5, 5, width, height);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

        private void drawBoardBackground(Graphics g, int startx, int starty, int wid, int hei){
            String boardString = "BBBBBBBBBHBBBBHBBBBBBBBB" +
                    "RRRRRRBHHHRRRRHHHBRRRRRR" +
                    "RRRRRRHHRRRRRRRRHHRRRRRR" +
                    "RRRRRRHHRRRRRRRRHHRRRRRR" +
                    "RRRRRRHHRRRRRRRRHHRRRRRR" +
                    "RRRRRRH1RRRRRRRR4H1RRRRB" +
                    "BRRRRRHHRRRRRRRRHHHHHHHH" +
                    "HHHH1HHHRRRRRRRRHHHHHHHB" +
                    "BHHHHHHHH2HHHH3HHHRRRRRR" +
                    "RRRRRHHHHHHHHHHHH1RRRRRR" +
                    "RRRRRRRRHHBBBBBHHHRRRRRR" +
                    "RRRRRRRRHHBBBBBHHHRRRRRR" +
                    "RRRRRRRR1HBBBBBHHHRRRRRR" +
                    "RRRRRRRRHHBBBBBHHHHH1H2B" +
                    "RRRRRRRRHHBBBBBHHHRRRRRB" +
                    "RRRRRRRRHHBBBBBHHRRRRRRR" +
                    "BHHHHH2HHHBBBBBH2RRRRRRR" +
                    "HHHHHHHHHHH12HHHHRRRRRRR" +
                    "BHHHHH1HHRRRRRRHHHRRRRRB" +
                    "RRRRRRRHHRRRRRRHHHHHHHHH" +
                    "RRRRRRRHHRRRRRR3H1HHHHHB" +
                    "RRRRRRRHHRRRRRRHHRRRRRRR" +
                    "RRRRRRRHHRRRRRRHHRRRRRRR" +
                    "RRRRRRRHHRRRRRRHHRRRRRRR" +
                    "RRRRRRBHBRRRRRRBHBRRRRRR";

            int index = 0;
            // fill colour of rooms and blocked tiles
            for(int i = 0; i < 25; i++){
                for(int j = 0; j < 24; j++){
                    char c = boardString.charAt(index);
                    if(c == 'B'){
                        g.setColor(new Color(240, 255, 240));
                        g.fillRect(startx + wid * j, starty + hei * i, wid, hei);

                    }else if(c == 'R'){
                        g.setColor(new Color(248, 248, 255));
                        g.fillRect(startx + wid * j, starty + hei * i, wid, hei);
                    }

                    index++;
                }
            }

            // draw walls
            index = 0;
            Graphics2D g2 = (Graphics2D) g;
            g2.setStroke(new BasicStroke(4));
            g.setColor(new Color(105, 105, 105));
            for(int i = 0; i < 25; i++){
                for(int j = 0; j < 24; j++){
                    char c = boardString.charAt(index);
                    if(c == 'B'){
                        // draw vertical walls
                        if(j != 23){
                            char rightChar = boardString.charAt(index + 1);
                            if(rightChar == 'R'){
                                g.drawLine(startx + wid * (j + 1) + 2, starty + hei * i + 2, startx + wid * (j + 1) + 2, starty + hei * (i + 1) - 2);
                            }else if(rightChar == 'H'){
                                g.drawLine(startx + wid * (j + 1) - 2, starty + hei * i + 2,startx + wid * (j + 1) - 2, starty + hei * (i + 1) - 2);
                            }
                        }
                        // draw horizontal walls
                        if(i != 24){
                            char underChar = boardString.charAt(index + 24);

                            if(underChar == 'R'){

                                g.drawLine(startx + wid * j + 2, starty + hei * (i + 1) + 2, startx + wid * (j + 1) - 2, starty + hei * (i + 1) + 2);
                            }else if(underChar == 'H'){



                                if(j != 0){
                                    char leftChar = boardString.charAt(index - 1);
                                    char underLeftChar = boardString.charAt(index + 23);
                                    if(leftChar == 'B' && underLeftChar == 'B'){
                                        g.drawLine(startx + wid * j - 2, starty + hei * (i + 1) - 2, startx + wid * (j + 1) - 2, starty + hei * (i + 1) - 2);
                                    }
                                }

                                if(j != 23){
                                    char rightChar = boardString.charAt(index + 1);
                                    char underRightChar = boardString.charAt(index + 25);
                                    if(rightChar == 'B' && underRightChar == 'B'){
                                        g.drawLine(startx + wid * j + 2, starty + hei * (i + 1) - 2, startx + wid * (j + 1) + 2, starty + hei * (i + 1) - 2);
                                    }
                                }
                                g.drawLine(startx + wid * j + 2, starty + hei * (i + 1) - 2, startx + wid * (j + 1) - 2, starty + hei * (i + 1) - 2);
                            }
                        }
                    }else if(c == 'R'){
                        if(j != 23){
                            char rightChar = boardString.charAt(index + 1);
                            if(rightChar == 'B' || rightChar == 'H'){

                                if(i != 24){
                                    char underChar = boardString.charAt(index + 24);
                                    char rightUnderChar = boardString.charAt(index + 25);
                                    if(underChar == 'R' && rightUnderChar == 'R'){
                                        g.drawLine(startx + wid * (j + 1) - 2, starty + hei * i + 2, startx + wid * (j + 1) - 2, starty + hei * (i + 1) + 2);
                                    }
                                }

                                g.drawLine(startx + wid * (j + 1) - 2, starty + hei * i + 2, startx + wid * (j + 1) - 2, starty + hei * (i + 1) - 2);
                            }
                        }
                        // draw horizontal walls
                        if(i != 24){
                            char underChar = boardString.charAt(index + 24);
                            if(underChar == 'B' || underChar == 'H'){

                                if(j != 0){
                                    char leftChar = boardString.charAt(index - 1);
                                    char underLeftChar = boardString.charAt(index + 23);
                                    if(leftChar == 'R' && underLeftChar == 'R'){
                                        g.drawLine(startx + wid * j - 2, starty + hei * (i + 1) - 2, startx + wid * (j + 1) - 2, starty + hei * (i + 1) - 2);
                                    }
                                }

                                if(j != 23){
                                    char rightChar = boardString.charAt(index + 1);
                                    char underRightChar = boardString.charAt(index + 25);
                                    if(rightChar == 'R' && underRightChar == 'R'){
                                        g.drawLine(startx + wid * j + 2, starty + hei * (i + 1) - 2, startx + wid * (j + 1) + 2, starty + hei * (i + 1) - 2);
                                    }
                                }

                                g.drawLine(startx + wid * j + 2, starty + hei * (i + 1) - 2, startx + wid * (j + 1) - 2, starty + hei * (i + 1) - 2);
                            }
                        }
                    }else if(c == 'H'){
                        if(j != 23){
                            char rightChar = boardString.charAt(index + 1);
                            if(rightChar == 'B' || rightChar == 'R'){
                                if(i != 24){
                                    char underChar = boardString.charAt(index + 24);
                                    char rightUnderChar = boardString.charAt(index + 25);
                                    if(underChar == 'R' && rightUnderChar == 'R'){
                                        g.drawLine(startx + wid * (j + 1) + 2, starty + hei * i + 2, startx + wid * (j + 1) + 2, starty + hei * (i + 1) + 2);
                                    }
                                }
                                g.drawLine(startx + wid * (j + 1) + 2, starty + hei * i + 2, startx + wid * (j + 1) + 2, starty + hei * (i + 1) - 2);
                            }
                        }
                        if(i != 24){
                            char underChar = boardString.charAt(index + 24);
                            if(underChar == 'B' || underChar == 'R'){
                                g.drawLine(startx + wid * j + 2, starty + hei * (i + 1) + 2, startx + wid * (j + 1) - 2, starty + hei * (i + 1) + 2);
                            }
                        }
                    }
                    index++;
                }
            }
            g.drawLine(startx + wid * 11, starty + hei * 17 - 2, startx + wid * 13, starty + hei * 17 - 2);
            g.drawLine(startx + wid * 23 - 2, starty + hei * 13 - 2, startx + wid * 23 - 2, starty + hei * 14);
            g2.setStroke(new BasicStroke(1));
        }


        public void drawPeopleAndWeapons(Graphics g, int startx, int starty, int wid, int hei) throws IOException {
            // draw weapons
            try{
                for(int i = 0; i < 6; i++){
                    Weapon w = game.board.weapons[i];
                    String nameOfWeapon = w.getName();
                    BufferedImage bi = ImageIO.read(new File("./assets/Weapons/" + nameOfWeapon + ".jpg"));
                    g.drawImage(bi, startx + wid * w.col(), starty + hei * w.row(), wid, hei, null);
                }
            }catch (IOException e){
                throw new Error("Draw weapon error.");
            }

            // draw people
            try{
                for(int i = 0; i < numberOfPlayers; i++){
                    Player p = game.players[i];

                    BufferedImage bi = ImageIO.read(new File("./assets/Characters/" + game.CHARACTER_NAMES[i] + ".png"));
                    g.drawImage(bi, startx + wid * p.getCol(), starty + hei * p.getRow() - 1, wid, hei, null);

                }
            }catch (IOException e){
                throw new Error("Draw people error.");
            }
        }


        private void drawGrid(Graphics g, int startX, int startY, int wid, int hei){
            g.setColor(Color.lightGray);
            // draw horizontal lines
            for(int i = 0; i < 26; i++){
                g.drawLine(startX, startY + hei * i, startX + wid * 24, startY + hei * i);
            }

            // draw vertical lines
            for(int i = 0; i < 25; i++){
                g.drawLine(startX + wid * i, startY, startX + wid * i, startY + hei * 25);
            }
        }

        private void drawStrings(Graphics g, int startX, int startY, int wid, int hei){
            g.setColor(Color.gray);
            Font myFont = new Font("Serif", Font.BOLD, wid/2);
            g.setFont(myFont);
            g.drawString("Kitchen", startX + wid * 2, startY + hei * 3 + hei/4);
            g.drawString("Dining Room", startX + wid * 2, startY + hei * 12);
            g.drawString("Lounge", startX + 2 * wid + 15, startY + 21 * hei);
            g.drawString("Hall", startX + wid * 11, startY + hei * 21);
            g.drawString("Study", startX + wid * 19, startY + hei * 22);
            g.drawString("Library", startX + wid * 19, startY + hei * 15 + hei / 2);
            g.drawString("Billiard Room", startX + wid * 19, startY + hei * 10);
            g.drawString("Conservatory", startX + wid * 19, startY + hei * 2);
            g.drawString("Ball Room", startX + wid * 10, startY + hei * 4);

        }

    }



    private class TextCanvas extends Canvas{
        private Font font;
        public TextCanvas(){
            setBounds(0, 420, 400, 100);
            GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
            HashSet<String> availableNames = new HashSet();

            for(String name : env.getAvailableFontFamilyNames()) {
                availableNames.add(name);
            }


            for(String pf : preferredFonts) {
                if(availableNames.contains(pf)) {
                    font = new Font(pf,Font.PLAIN,20);
                    break;
                }
            }
        }

        @Override
        public void paint(Graphics g) {
            super.paint(g);
            g.drawString("Player Colour Key: ",  10, textCanvasStartY + 20);

            // draw images
            try{
                for(int i = 0; i < 6; i++){
                    String characterName = game.CHARACTER_NAMES[i];
                    String weaponName = game.WEAPON_NAMES[i];
                    BufferedImage bi_character = ImageIO.read(new File("./assets/Characters/" + characterName + ".png"));
                    BufferedImage bi_weapon = ImageIO.read(new File("./assets/Weapons/" + weaponName + ".jpg"));
                    g.drawImage(bi_character, 10, textCanvasStartY + 40 + 30 * i, 20, 20, null);
                    // g.drawString((AttributedCharacterIterator) bi_character, 40, textCanvasStartY + 40 + 30 * i);
                    g.drawImage(bi_weapon, 10, textCanvasStartY + 240 + 30 * i, 20, 20, null);
                    // g.drawString((AttributedCharacterIterator) bi_weapon, 40, textCanvasStartY + 240 + 30 * i);
                }
            }catch (IOException e){
                throw new Error("Text canvas draw error");
            }

            for(int i = 0; i < 6; i++){
                g.drawString(game.CHARACTER_NAMES[i], 40, textCanvasStartY + 55 + 30 * i);
                g.drawString(game.WEAPON_NAMES[i], 40, textCanvasStartY + 255 + 30 * i);
            }
        }
    }









    @Override
    public void actionPerformed(ActionEvent actionEvent) {
//        String cmd = actionEvent.getActionCommand();
//
//        if(cmd.equals("Roll Dice")) {
//            //JOptionPane.showMessageDialog(null, "roll dice", "Game Introduction", JOptionPane.PLAIN_MESSAGE);
//        } else if(cmd.equals("Suggestion")){
//            String[] options = {"s", "w", "d", "g", "d"};
//            //JOptionPane.showOptionDialog(null, "Suggestion", "Suggestion", JOptionPane.PLAIN_MESSAGE, JOptionPane.PLAIN_MESSAGE, null, options, 3);
//
//        }

    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {

    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {

    }



    public static void main(String[] args) {
        new GUI();
    }
}
