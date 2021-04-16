package Cluedo.Model;

import Cluedo.Model.Items.Person;
import Cluedo.Model.Items.Room;
import Cluedo.Model.Items.Weapon;
import Cluedo.Model.Tiles.DoorTile;
import Cluedo.Model.Tiles.HallTile;
import Cluedo.Model.Tiles.RoomTile;
import Cluedo.Model.Tiles.Tile;
import Cluedo.View.GUI;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Game class defines the board of the game, and the cards in the game.
 * The number of players is between 3 and 6.
 * Players can move their cards and make accusation.
 */
public class Game {

    public GUI gui;

    /**
     * The board of the game: @ is the partition (wall) (cannot go across it);
     *                        | is the separator of two horizontally adjacent tiles (can go across it);
     *                        --- is the separator of two vertically adjacent tiles (can go across it).
     */
    public Board board;
    /**
     * Six persons on the board.
     */
    public Person[] people;
    /**
     * Nine rooms on the board.
     */
    private Room[] rooms;
    /**
     * Six weapons on the board.
     */
    private Weapon[] weapons;
    /**
     * Three murder cards.
     */
    public Card[] murderCards;
    /**
     * Players in the game (from 3 to 6).
     */
    public Player[] players;
    /**
     * A collection of all cards in the game:
     *    key is the name of the card,
     *    value is the card object.
     */
    public Map<String, Card> cards;
    /**
     * Names of the six characters.
     */
    public static final String[] CHARACTER_NAMES = {"Miss Scarlett", "Colonel Mustard", "Mrs. White", "Mr. Green",
            "Mrs. Peacock", "Professor Plum"};
    /**
     * Names of the nine rooms.
     */
    private static final String[] ROOM_NAMES = {"Kitchen", "Ballroom", "Conservatory", "Billiards Room",
            "Library", "Study", "Hall", "Lounge", "Dining Room"};
    /**
     * Names of the weapons.
     */
    public static final String[] WEAPON_NAMES = {"Candlestick", "Revolver", "Spanner", "Rope", "Dagger", "Lead Pipe"};
    /**
     * Starting rows of the corresponding characters in CHARACTER_NAMES (same order).
     */
    private static final int[] STARTING_ROWS = {24, 17, 0,  0, 6, 19};
    /**
     * Starting columns of the corresponding characters in CHARACTER_NAMES (same order).
     */
    private static final int[] STARTING_COLS = {7, 0, 9, 14,  23, 23};

    private int playerNum;
    private String currentPlayer = "_";
    private int remainMove = 0;
    public boolean canMakeSuggestion = false;
    boolean hasMadeSuggestion = false;
    private boolean canRollDice = true;
    private boolean hasRollDice = false;
    private boolean currentPlayerCanMove = false;
    private List<String> Player_Names;
    private int currentPlayerIndex = 0;
    private ArrayList<Integer[]> currentPlayerSquares = new ArrayList<>();
    private boolean canGetOut = false;


    public Game(int num, List<String> nms, GUI gui){
        this.playerNum = num;
        this.Player_Names = nms;
        this.gui = gui;
    }

    /**
     * Initialize the people, rooms, weapons, cards, and assigning murder cards.
     * Also, it asks the number of players, and assigns cards to the players.
     * After all the cards have been assigned, initialize the board and add people onto the board.
     */
    public void initialise(){
        // objects and cards
        people = new Person[6];
        for(int i = 0; i < 6; i++)
            people[i] = new Person(CHARACTER_NAMES[i], STARTING_ROWS[i], STARTING_COLS[i]);
        rooms = new Room[9];
        for(int i = 0; i < 9; i++)
            rooms[i] = new Room(ROOM_NAMES[i]);
        weapons = new Weapon[6];
        for(int i = 0; i < 6; i++)
            weapons[i] = new Weapon(WEAPON_NAMES[i], 0, 0);

        List<Card> characterCards = new ArrayList<>();
        List<Card> weaponCards = new ArrayList<>();
        List<Card> roomCards = new ArrayList<>();

        cards = new HashMap<>();
        for(Person c: people) {
            Card card = new Card(c);
            characterCards.add(card);
            cards.put(c.getName(), card);
        }
        for(Weapon w: weapons) {
            Card c = new Card(w);
            weaponCards.add(c);
            cards.put(w.getName(), c);
        }
        for(Room r: rooms) {
            Card c = new Card(r);
            roomCards.add(c);
            cards.put(r.getName(), c);
        }

        // assigning murder cards
        murderCards = new Card[3];
        Card murderCharacter = characterCards.get((int) (Math.random() * characterCards.size()));
        characterCards.remove(murderCharacter);
        Card murderWeapon = weaponCards.get((int) (Math.random() * weaponCards.size()));
        weaponCards.remove(murderWeapon);
        Card murderRoom = roomCards.get((int) (Math.random() * roomCards.size()));
        roomCards.remove(murderRoom);
        murderCards[0] = murderCharacter;
        murderCards[1] = murderWeapon;
        murderCards[2] = murderRoom;
        List<Card> handCards = new ArrayList<>();
        handCards.addAll(characterCards);
        handCards.addAll(weaponCards);
        handCards.addAll(roomCards);

        players = new Player[playerNum];
        int cardNum = 18 / playerNum;
        for(int i = 0; i < playerNum; i++) {
            List<Card> hand = new ArrayList<>();
            int extra = 0;
            if(handCards.size() > (playerNum - i)*cardNum){
                extra = 1;
            }
            for (int j = 0; j < cardNum + extra; j++) {
                int index = (int) (Math.random() * handCards.size());
                hand.add(handCards.get(index));
                handCards.remove(index);
            }
            players[i] = new Player(Player_Names.get(i), hand, people[i]);

            if(i == 0){
                players[i].setRow(24);
                players[i].setCol(7);
            }else if(i == 1){
                players[i].setRow(17);
                players[i].setCol(0);
            }else if(i == 2){
                players[i].setRow(0);
                players[i].setCol(9);
            }else if(i == 3){
                players[i].setRow(0);
                players[i].setCol(14);
            }else if(i == 4){
                players[i].setRow(6);
                players[i].setCol(23);
            }else if(i == 5){
                players[i].setRow(19);
                players[i].setCol(23);
            }
        }

        // initialize a new board
        board = new Board(rooms, weapons);
        board.addPeople(people);
    }

    /**
     * Method called when "Roll Dice" button is pressed, rolls the dice
     */
    public void rollDice(){
        if(!canRollDice) return;

        // get random numbers
        int diceOne = (int) (Math.random() * 6) + 1;
        int diceTwo = (int) (Math.random() * 6) + 1;
        remainMove = diceOne + diceTwo;
        canRollDice = false;
        hasRollDice = true;
        run();
        gui.diceAnimation(diceOne, diceTwo);
    }

    public void nextPlayer(){
        if(hasRollDice == false){
            return;
        }
        JOptionPane.showMessageDialog(null, "Go to next player now.", "Message", JOptionPane.INFORMATION_MESSAGE);

        nextIndex();
        if(players[currentPlayerIndex].isHasMadeWrongAccusation()){
            while(players[currentPlayerIndex].isHasMadeWrongAccusation()){
                nextIndex();
            }
        }

        remainMove = 0;
        gui.setRemainingMoves(remainMove);
        gui.updatePlayer(players[currentPlayerIndex]);
        currentPlayerSquares = new ArrayList<>();
        Integer[] initialisPair = {players[currentPlayerIndex].getPerson().row(), players[currentPlayerIndex].getPerson().col()};
        currentPlayerSquares.add(initialisPair);
        canRollDice = true;
        hasRollDice = false;
        canMakeSuggestion = false;

        // if the currentPlayer is in a room, can go out by clicking the door
        int r = players[currentPlayerIndex].getRow();
        int c = players[currentPlayerIndex].getCol();
        Tile t = board.tiles[r][c];
        boolean inRoom = false;
        if(t instanceof RoomTile){
            canGetOut = true;
            canMakeSuggestion = true;
            inRoom = true;
        }else{
            canGetOut = false;
        }
        gui.newTurn(inRoom);
    }

    public void movePlayer(int dx, int dy){
        if(currentPlayerCanMove && remainMove > 0){
            int currentRow = players[currentPlayerIndex].getRow();
            int currentCol = players[currentPlayerIndex].getCol();
            Integer[] oldPair = {currentRow, currentCol};
            currentPlayerSquares.add(oldPair);
            Tile oldTile = board.tiles[currentRow][currentCol];
            int newRow = currentRow + dy;
            int newCol = currentCol + dx;
            if(newRow < 0 || newRow >= 25 || newCol < 0 || newCol >= 24){
                return;
            }
            for(Integer[] p : currentPlayerSquares){
                if(newRow == p[0] && newCol == p[1]){
                    JOptionPane.showMessageDialog(null, "You can't go back to this tile!", "Warning", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
            }
            Tile newTile = board.tiles[currentRow + dy][currentCol + dx];

            if(newTile instanceof DoorTile || newTile instanceof HallTile) {
                if(newTile.getItem() != null) {
                    return;
                }else{
                    board.tiles[currentRow][currentCol].setItem(null);
                    newTile.setItem(players[currentPlayerIndex].getPerson());
                    players[currentPlayerIndex].getPerson().setRow(newRow);
                    players[currentPlayerIndex].getPerson().setCol(newCol);
                    players[currentPlayerIndex].setRow(newRow);
                    players[currentPlayerIndex].setCol(newCol);
                    // add pos to the list
                    Integer[] posPair = {newRow, newCol};
                    currentPlayerSquares.add(posPair);
                    remainMove--;
                    gui.updateCanvasAfterMove(remainMove);
                }
            }else if(oldTile instanceof DoorTile && newTile instanceof RoomTile){
                board.tiles[currentRow][currentCol].setItem(null);
                ((RoomTile) newTile).getRoom().placeRandom(players[currentPlayerIndex].getPerson());
                players[currentPlayerIndex].setRow(players[currentPlayerIndex].getPerson().row());
                players[currentPlayerIndex].setCol(players[currentPlayerIndex].getPerson().col());
                gui.updateCanvasAfterMove(remainMove);
                // ask suggestion
                gui.updateCanvasAfterMove(remainMove - 1);
                canMakeSuggestion = true;
                gui.setSuggestionColour(true);
                JOptionPane.showMessageDialog(null, "You can make suggestion now.", "Ask for suggestion", JOptionPane.INFORMATION_MESSAGE);
            }
        }
        if(remainMove == 0){
            nextPlayer();
        }
    }

    public void run(){
        Player playerNow = players[currentPlayerIndex];
        if(playerNow.isHasMadeWrongAccusation()){
            nextPlayer();
            return;
        }

        boolean allowMoves = true;

        if(board.inRoom(players[currentPlayerIndex])){
            canMakeSuggestion = true;
            if(!hasMadeSuggestion){
                Room room = board.getRoom(players[currentPlayerIndex]);
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
                }
            }
        }
        // when the player is not in any room
        if(allowMoves && remainMove > 0){
            currentPlayerCanMove = true;
        }
    }

    public void doMouse(int row, int col){
        int currentRow = players[currentPlayerIndex].getRow();
        int currentCol = players[currentPlayerIndex].getCol();

        if(hasRollDice == false){
            return;
        }

        Tile currentTile = board.tiles[currentRow][currentCol];
        Tile newTile = board.tiles[row][col]; // new Tile(newRow, newCol);

        if(currentTile instanceof RoomTile && newTile instanceof DoorTile && canGetOut){
            Room room = ((RoomTile)currentTile).getRoom();

            Map<Integer, DoorTile> map = room.getDoorTiles();
            for(DoorTile dt : map.values()){
                if(dt.getRow() == row && dt.getCol() == col){
                    if(dt.getItem() == null){
                        currentTile.setItem(null);
                        dt.setItem(players[currentPlayerIndex].getPerson());
                        players[currentPlayerIndex].setRow(dt.getRow());
                        players[currentPlayerIndex].setCol(dt.getCol());
                        players[currentPlayerIndex].getPerson().setRow(dt.getRow());
                        players[currentPlayerIndex].getPerson().setCol(dt.getCol());
                        remainMove--;
                        gui.updateCanvasAfterMove(remainMove);
                        canGetOut = false;
                        canMakeSuggestion = false;
                        gui.setSuggestionColour(false);
                    }
                    break;
                }
            }
        }

        int dy = row - currentRow;
        int dx = col - currentCol;
        int absDx = Math.abs(dx);
        int absDy = Math.abs(dy);
        if ((absDx == 1 && absDy == 0) || (absDx == 0 && absDy == 1)) {
            movePlayer(dx, dy);
        }
    }

    public void nextIndex(){
        if(currentPlayerIndex < playerNum - 1){
            currentPlayerIndex = currentPlayerIndex + 1;
        }else if(currentPlayerIndex == playerNum - 1){
            currentPlayerIndex = 0;
        }
        gui.changePlayer(currentPlayerIndex);
    }

    /**
     * Method called when "Make Suggestion" button is pressed
     * Gives user the option to make a suggestion by opening a popup
     */
    public void makeSuggestion(String character, String weapon) {
        if (canMakeSuggestion == false) {
            return;
        }
        Room room = board.getRoom(players[currentPlayerIndex]);
        Person personTeleported = getPerson(character);
        Player playerTeleported = getPlayer(character);
        Weapon weaponTeleported = getWeapon(weapon);
        if(personTeleported != null){
            room.placeRandom(personTeleported);
            int pr = personTeleported.row();
            int pc = personTeleported.col();
            if(playerTeleported != null){
                playerTeleported.setRow(pr);
                playerTeleported.setCol(pc);
            }
        }
        room.placeRandom(weaponTeleported);
        gui.updateCanvasAfterMove(remainMove);
        Card weaponCard = cards.get(weapon);
        Card roomCard = cards.get(room.getName());
        Card personCard = cards.get(character);

        for(int i = currentPlayerIndex + 1; i != currentPlayerIndex; i++){
            if(i == players.length){
                if(currentPlayerIndex == 0) break;
                i = 0;
            }
            List<Card> hand = players[i].getHand();
            Card card = null;
            if(hand.contains(weaponCard)) card = weaponCard;
            else if(hand.contains(roomCard)) card = roomCard;
            else if(hand.contains(personCard)) card = personCard;
            if(card != null){
                String disputedInfo = "Your suggestion has been disputed: " + players[i].getName() + " has the " + card.getName() + " card";
                JOptionPane.showMessageDialog(null, disputedInfo, "Suggestion Disputed", JOptionPane.INFORMATION_MESSAGE);
                hasRollDice = true;
                nextPlayer();
                return;
            }
        }
        String noDisputedInfo = "Nobody can dispute this guess. \nWould you like to make an accusation?";
        int yesOrNo = JOptionPane.showConfirmDialog(null, noDisputedInfo, "Accusation", JOptionPane.YES_NO_OPTION); // yes: 0; no: 1
        if(yesOrNo == 0){// Make an accusation -- open the envelop
            // make correct accusation: win game
            boolean win = gui.envelopePopUp(character, weapon, room.getName());
            if(win){
                setGameOver();
                return;
            }else{ // make wrong accusation, set isHasmadewrongaccusation for the currentplayer, and check if all players have made wrong accusation
                players[currentPlayerIndex].setHasMadeWrongAccusation(true);
                boolean isGameOver = true;
                for(int i = 0; i < players.length; i++){
                    if(players[i].isHasMadeWrongAccusation() == false){
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
        canMakeSuggestion = false;
        gui.setSuggestionColour(false);
    }


    public void setGameOver(){
        canRollDice = false;
        gui.setRollDiceColour(false);
        hasRollDice = true;
        gui.setEndTurnColour(true);
        currentPlayerCanMove = false;
        canGetOut = false;
        canMakeSuggestion = false;
        gui.setSuggestionColour(false);
    }

    /**
     * Method called when "End Turn" button is pressed
     * Ends the turn and passes control over to the next player
     */
    public void endTurn(){
        if(currentPlayer == null || !hasRollDice){
            return;
        }
        nextPlayer();
    }

    /**
     * Returns the person object associated with the name
     * @param s
     * @return
     */
    public Person getPerson(String s){
        for(int i = 0; i < people.length; i++){
            if(people[i].getName().equals(s)){
                return people[i];
            }
        }
        return null;
    }

    /**
     * Returns the player object associated with this name
     * @param s
     * @return
     */
    public Player getPlayer(String s){
        for(int i = 0; i < players.length; i++){
            if(players[i].getName().equals(s)){
                return players[i];
            }
        }
        return null;
    }

    /**
     * Returns the weapon object associated with this name
     * @param s
     * @return
     */
    public Weapon getWeapon(String s){
        for(int i = 0; i < weapons.length; i++){
            if(weapons[i].getName().equals(s)){
                return weapons[i];
            }
        }
        throw new Error("Error: no weapon has this name. ");
    }


}