package Cluedo.Model.Items;

/**
 * The Person class defines the characters in the game.
 * Because "Character" cannot be used here (class Character existsalready),
 * we call the characters in the game "Person".
 */
public class Person extends MovableItem {

    /**
     * Construct a Person object by its name, and original position (row and column).
     *
     * @param name name of the character/person
     * @param row  original row (at the start of the game)
     * @param col  original column (at the start of the game)
     */
    public Person(String name, int row, int col) {
        super(name, row, col);
    }

    /**
     * Get the name of the character.
     * @return the name of the character
     */
    public String getName(){ return super.getName(); }

    /**
     * A letter to represent the name of a character for short.
     * @return A letter representing the name of a character.
     */
    public String toString() {
        if(getName().equals("Miss Scarlett")){
            return "S";
        }else if(getName().equals("Colonel Mustard")){
            return "M";
        }else if(getName().equals("Mrs. White")){
            return "W";
        }else if(getName().equals("Mr. Green")){
            return "G";
        }else if(getName().equals("Mrs. Peacock")){
            return "E";
        }else if(getName().equals("Professor Plum")){
            return "P";
        }else{
            throw new Error("Invalid Player Name.");
        }
    }
}