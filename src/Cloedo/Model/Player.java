package Cluedo.Model;

import Cluedo.Model.Items.Person;

import java.util.List;

/**
 * The Player class defines the player in the game.
 * A player has his name, a list of cards in his hand, and the character he is playing.
 */
public class Player {
    /**
     * name of the player
     */
    private String name;

    /**
     * the cards that are assigned to the player
     */
    private List<Card> hand;

    /**
     * the character the player is playing
     */
    private Person person;

    private boolean hasMadeWrongAccusation = false;

    private int row;
    private int col;

    /**
     * Constructing a player using his name, the cards in his hand, and the character he is playing.
     *
     * @param name name of the player
     * @param hand cards the player has
     * @param person the character the player is playing
     */
    public Player(String name, List<Card> hand, Person person) {
        this.name = name;
        this.hand = hand;
        this.person = person;
    }

    public int getRow(){
        return this.row;
    }
    public void setRow(int r){
        this.row = r;
    }
    public int getCol(){
        return this.col;
    }
    public void setCol(int c){
        this.col = c;
    }

    /**
     * Get the cards the player has.
     *
     * @return the cards in the player's hand
     */
    public List<Card> getHand(){
        return hand;
    }

    /**
     * Get the character of the player.
     * @return the character of the player
     */
    public Person getPerson(){ return person;}

    /**
     * Get the name of the player
     * @return the name of the player
     */
    public String getName(){ return person.getName(); }

    public boolean isHasMadeWrongAccusation(){
        return this.hasMadeWrongAccusation;
    }

    public void setHasMadeWrongAccusation(boolean b){
        this.hasMadeWrongAccusation = b;
    }

    public String toString(){
        return this.name;
    }



}