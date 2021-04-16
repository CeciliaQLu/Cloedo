package Cluedo;
import Cluedo.Items.MovableItem;
import Cluedo.Items.Person;
import Cluedo.Items.Room;
import Cluedo.Items.Weapon;
import Cluedo.Tiles.DoorTile;
import Cluedo.Tiles.Tile;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Game class defines the board of the game, and the cards in the game.
 * The number of players is between 3 and 6.
 * Players can move their cards and make accusation.
 */
public class Game {
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
     * Extra cards after assigning cards to players at first.
     */
    private List<Card> extraCards;

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

    private ArrayList<String> Player_Names = new ArrayList<>();

    public Game(int num, ArrayList<String> nms){
        this.playerNum = num;
        this.Player_Names = nms;

    }



    /**
     * Initialize the people, rooms, weapons, cards, and assigning murder cards.
     * Also, it asks the number of players, and assigns cards to the players.
     * After all the extra cards have been assigned, initialize the board and add people onto the board.
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
            for (int j = 0; j < cardNum; j++) {
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

        extraCards = handCards;

        // initialize a new board
        board = new Board(rooms, weapons);
        board.addPeople(people);
    }

    /**
     * Run the game: Roll the two dice and sum up the two integers.
     * Implement a move and then update the board.
     * Print the number of remaining moves, and ask for a move if there is any move remaining;
     * or turn to the next player.
     * When a player makes a correct accusation, stop running and return.
     */



    public void run(){
        while(true){
            for(int i = 0; i < players.length; i++) {
                if(players[i].isHasMadeWrongAccusation()) continue; // cannot move if the player made a wrong accusation

                // System.out.println("\n=================================================================================================================================\n");
                // board.drawBoard();
                System.out.println(players[i].getName() + "'s turn");

                int diceOne = (int) (Math.random() * 6) + 1;
                int diceTwo = (int) (Math.random() * 6) + 1;
                int turnsRemaining = diceOne + diceTwo;

                boolean allowMoves = true;

                if(board.inRoom(players[i])) {
                    // ask: if make a suggestion
                    if(askSuggestion(i)){
                        if(makeSuggestion(i)){ // win
                            return;
                        }else{
                            allowMoves = false;
                        }

                    }else {
                        Room room = board.getRoom(players[i]);
                        Map<Integer, DoorTile> doorTiles = room.getDoorTiles();
                        boolean blocked = true;
                        for (Tile tile : doorTiles.values())
                            if (tile.getItem() == null) blocked = false;
                        if (blocked) {
                            System.out.println("You can't leave the room because all the exits are blocked");
                            allowMoves = false;
                            enterLetter();
                        } else {
                            if (doorTiles.size() > 1) {
                                System.out.print("What door would you like to exit from ");
                                int doorNum = getNumber(1, doorTiles.size());
                                while (!board.teleportPersonToDoor(players[i], doorTiles.get(doorNum))) {
                                    System.out.print("What door would you like to exit from ");
                                    doorNum = getNumber(1, doorTiles.size());
                                }
                                board.drawBoard();
                            } else {
                                board.teleportPersonToDoor(players[i], doorTiles.get(1));
                                board.drawBoard();
                                System.out.println("You have left the room");
                                enterLetter();
                            }
                            turnsRemaining--;
                        }
                    }
                }


                if(allowMoves) { // one turn
                    System.out.println("You rolled a " + diceOne + " and a " + diceTwo);
                    // record the squares that the player has been on
                    ArrayList<Integer[]> squaresOn = new ArrayList<>();
                    Integer[] initialPair = {players[i].getPerson().row(), players[i].getPerson().col()};
                    squaresOn.add(initialPair);

                    outerLoop:
                    for (int j = turnsRemaining; j >= 1; j--) {
                        System.out.println(players[i].getName() + ", you have " + j + " moves remaining");
                        String move = getMove();
                        if(move.contentEquals("x")) {
                            System.out.println(true);
                            break;
                        }


                        while (!board.movePerson(players[i], move, squaresOn)) {
                            move = getMove();
                            if(move.contentEquals("x")) {
                                break outerLoop;
                            }
                        }
                        board.drawBoard();
                        if (board.inRoom(players[i])) {
                            if(askSuggestion(i)){
                                if(makeSuggestion(i)) return;
                            }
                            break;

                        }
                    }
                }
            }
        }
    }

    /**
     * Ask the player if he wants to make an accusation
     * @param playerNum the index of the player in the player list
     * @return true if the player wants to make an accusation; otherwise return false.
     */
    public boolean askSuggestion(int playerNum){
        System.out.println("You are in the " + board.getRoom(players[playerNum]).getName());
        System.out.print("Would you like to make a suggestion (yes or no)? "); // change accusation to suggestion here
        return getAnswer();
    }



    public boolean askAccusation(int playerNum){
        System.out.println("You are in the " + board.getRoom(players[playerNum]).getName());
        System.out.print("Would you like to make an accusation (yes or no)? ");
        return getAnswer();
    }


    public Player getPlayer(String s){
        for(int i = 0; i < players.length; i++){
            if(players[i].getName().equals(s)){
                return players[i];
            }
        }
        return null;
    }

    public Weapon getWeapon(String s){
        for(int i = 0; i < weapons.length; i++){
            if(weapons[i].getName().equals(s)){
                return weapons[i];
            }
        }
        throw new Error("Error: no weapon has this name. ");
    }









    public boolean makeSuggestion(int playerNum){
        System.out.println("Here is your hand: " + players[playerNum].getHandString());
        if(!extraCards.isEmpty()) {
            System.out.print("Here are the extra cards: " + extraCards.get(0).getName());
            for(int i = 1; i < extraCards.size(); i++)
                System.out.print(", " +extraCards.get(i).getName());
            System.out.println();
        }
        Room room = board.getRoom(players[playerNum]);
        Card roomCard = cards.get(room.getName());
        Card weaponCard = getCard("weapon", WEAPON_NAMES);
        Card personCard = getCard("character", CHARACTER_NAMES);
        board.teleportItemToRoom((MovableItem) weaponCard.getItem(), room);
        board.teleportItemToRoom((MovableItem) personCard.getItem(), room);
        board.drawBoard();

        
        for(int i = playerNum + 1; i != playerNum; i++){
            if(i == players.length){
                if(playerNum == 0) break;
                i = 0;
            }
            List<Card> hand = players[i].getHand();
            Card card = null;
            if(hand.contains(weaponCard)) card = weaponCard;
            else if(hand.contains(roomCard)) card = roomCard;
            else if(hand.contains(personCard)) card = personCard;
            if(card != null){
                // if the player made a wrong accusation
                System.out.println("Your suggestion has been disputed: " +
                            players[i].getName() + " has the " + card.getName() + " card");
                enterLetter();
                return false;
            }
            System.out.println(players[i].getName() + " can't dispute your accusation");
        }
        System.out.println("Nobody can dispute this guess.");
        if(!askAccusation(playerNum)){
            return false;
        }

        // accusation successful, win
        if(murderCards[0] == personCard && murderCards[1] == weaponCard && murderCards[2] == roomCard){
            System.out.println(players[playerNum].getName() + " wins");
            return true;
        }else{

            players[playerNum].setHasMadeWrongAccusation(true);

            System.out.println("These were not the correct cards");
            enterLetter();
            return false;
        }
    }




    /**
     * Press w a s d to move the card.
     * w: up;
     * a: left;
     * s: down;
     * d: right.
     * x to finish moving
     *
     * @return w, a, s, or d.
     */
    public String getMove(){
        System.out.print("Please enter in your move (w a s d or x to finish): ");
        Scanner scan = new Scanner(System.in);
        String move = scan.next();
        while (!(move.equalsIgnoreCase("w") || move.equalsIgnoreCase("a") ||
                move.equalsIgnoreCase("s") || move.equalsIgnoreCase("d") || move.equalsIgnoreCase("x"))) {
            System.out.print("Please enter a valid character (w, a, s, d or x):");
            move = scan.next();
        }
        return move;
    }


    /**
     * Get the answer entered by the user.
     *
     * @return true if the user inputs "yes";
     *         false if the user input "no".
     */
    public boolean getAnswer(){
        Scanner sc = new Scanner(System.in);
        String answer = sc.next();
        while(!(answer.equalsIgnoreCase("yes") || answer.equalsIgnoreCase("no"))){
            System.out.print("Please input yes or no: ");
            answer = sc.next();
        }
        if(answer.equalsIgnoreCase("yes")) return true;
        else return false;
    }

    /**
     * Get a integer from the user.
     *
     * @return int between 1 and upper bound (inclusive).
     */
    public int getNumber(int lowerBound, int upperBound){
        int number = -1;
        boolean validInput = false;
        System.out.print("(integer between " + lowerBound + " and " + upperBound + "): ");
        while(!validInput) {
            try {
                Scanner scan = new Scanner(System.in);
                number = scan.nextInt();
                if(number >= lowerBound && number <= upperBound) {
                    validInput = true;
                }else{
                    System.out.print("Please input an integer between " + lowerBound + " and " + upperBound + ": ");
                }
            }catch(java.util.InputMismatchException e) {
                System.out.print("Please input an integer between " + lowerBound + " and " + upperBound + ": ");
            }
        }
        return number;
    }

    /**
     * Allow the user to enter any letter to continue the game.
     */
    public void enterLetter(){
        Scanner scan = new Scanner(System.in);
        System.out.print("Enter any letter to continue: ");
        scan.next();
    }

    /**
     * Get the card based on the type or the corresponding array.
     *
     * @param type weapon, room, or character
     * @param names WEAPON_NAMES, ROOM_NAMES, or CHARACTER_NAMES
     * @return the card that matches the type and name (input by the user)
     */
    public Card getCard(String type, String[] names){
        for(int i = 0; i < names.length; i++){
            System.out.println((i + 1) + " " + names[i]);
        }
        System.out.print("Enter a number to choose a "  + type + " ");
        int num = getNumber(1, names.length);
        return cards.get(names[num - 1]);
    }

    /**
     * Initialise game and run it.
     * @param args
     */
//    public static void main(String args[]){
//        Game g = new Game();
//        g.initialise();
//        g.run();
//    }
}