package Cluedo.Model.Tiles;
import Cluedo.Model.Items.Room;

/**
 * The RoomTile class defines the tiles of the rooms.
 */
public class RoomTile extends Tile {
    // the room that a RoomTile in
    private Room room;

    /**
     * Constructing a room tile by its room
     * @param room
     */
    public RoomTile(Room room, int row, int col) {
        super(row, col);
        this.room = room;
    }

    /**
     * Get the room of the RoomTile
     * @return the room object of the room tile
     */
    public Room getRoom() {
        return this.room;
    }

}