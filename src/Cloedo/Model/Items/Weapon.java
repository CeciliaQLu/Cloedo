package Cluedo.Model.Items;

/**
 * This class defines the weapons in the game.
 */
public class Weapon extends MovableItem {
    /**
     * Constructing a Weapon using its name
     * @param name name of the weapon
     */
    public Weapon(String name, int row, int col) {
        super(name, row, col);
    }

    /**
     * Get the name of the weapon.
     * @return the name of the weapon.
     */
    public String getName(){ return super.getName(); }

    /**
     * Get the letter representing the name of the weapon.
     * @return the letter representing the name of the weapon
     */
    public String toString() {
        if(getName().equals("Candlestick")){
            return "C";
        }else if(getName().equals("Dagger")){
            return "D";
        }else if(getName().equals("Lead Pipe")){
            return "L";
        }else if(getName().equals("Revolver")){
            return "R";
        }else if(getName().equals("Rope")){
            return "O";
        }else if(getName().equals("Spanner")){
            return "A";
        }else{
            throw new Error("Invalid Weapon Name");
        }
    }
}