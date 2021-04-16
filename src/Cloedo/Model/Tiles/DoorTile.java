package Cluedo.Model.Tiles;

import Cluedo.Model.Items.Item;
import Cluedo.Model.Items.Room;

/**
 * This DoorTile class represents the door tiles.
 * A room might have more than one doors,
 * which means more than one door tiles for the room.
 */
public class DoorTile extends Tile {
    // the room that has the DoorTile
    private Room room;
    // the index of the door
    private int doorNumber;

    /**
     * Constructor
     * @param x the index of the door amoung all the doors of the room
     */
    public DoorTile(int x, int row, int col) {
        super(row, col);
        this.doorNumber = x;
    }

    /**
     * Get the number/index of the door
     *
     * @return integer value of the door index
     */
    public int getDoorNumber() {
        return doorNumber;
    }

    /**
     * Set the room for the door tile
     * @param r the room of the door tile
     */
    public void setRoom(Room r) {
        this.room = r;
        this.room.addDoorTile(doorNumber, this);
    }

    public Room getAssociatedRoom(){
        return room;
    }

    /**
     * Get the Item of the door tile.
     * @return the Item
     */
    public Item getItem() {
        return super.getItem();
    }

    /**
     * Get the String of the door tile
     * @return the name of the item if there is an item on the door tile;
     *         return the door tile number otherwise.
     */
    public String toString() {
        if(getItem() != null) return getItem().toString();
        return "" + this.doorNumber;
    }

}