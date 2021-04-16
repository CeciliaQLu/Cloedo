package Cluedo.Model.Items;

/**
 * Superclass of Person, Room, and Weapon.
 */
public class Item {
    // Name of the Item
    private String name;

    /**
     * Constructor
     *
     * @param name the name of the Item
     */
    public Item(String name) {
        this.name = name;
    }

    /**
     * Get the name of the Item (Person, Room, or Weapon)
     */
    public String getName() {
        return name;
    }

}