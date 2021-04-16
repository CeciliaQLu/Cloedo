package Cluedo.Model.Items;

import Cluedo.Model.Tiles.DoorTile;
import Cluedo.Model.Tiles.RoomTile;
import Cluedo.Model.Tiles.Tile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The Room class defines the 9 rooms in the game.
 * Each room has a list of RoomTiles,
 * and at least one DoorTile.
 */
public class Room extends Item{
    // RoomTiles in the room.
    private List<RoomTile> roomTiles = new ArrayList<RoomTile>();
    // DoorTile(s) for the room:
    //             key is the door number, value is the DoorTile
    private Map<Integer, DoorTile> doorTiles;

    /**
     * Constructor
     *
     * @param name Name of the room
     */
    public Room(String name) {
        super(name);
        doorTiles = new HashMap<>();
    }

    /**
     * Add RoomTile into this room.
     * @param t the RoomTile that will be added into the room
     */
    public void addRoomTile(RoomTile t){
        roomTiles.add(t);
    }

    /**
     * Add DoorTile into this room.
     *
     * @param num the number of the door (starting from 1 for the doors of the room)
     * @param d the DoorTile added to the room
     */
    public void addDoorTile(int num, DoorTile d){
        doorTiles.put(num, d);
    }

    /**
     * Get all the DoorTiles in the room.
     *
     * @return a map of the DoorTiles
     *         (key is the index of the door tile, value is the DoorTile object)
     */
    public Map<Integer, DoorTile> getDoorTiles() {
        return doorTiles;
    }

    /**
     * Remove a Tile from the tiles in the room.
     * @param t the rile that is going to be removed.
     */
    public void removeTile(Tile t) {
        roomTiles.remove(t);
    }

    /**
     * Place the item at a random position in the room
     * @param i the movable item that will be placed in the room
     */
    public void placeRandom(MovableItem i) {
        while(true) {
            int place = (int)Math.floor(Math.random()*roomTiles.size());
            RoomTile tile = roomTiles.get(place);
            if(tile.getItem() == null) {
                tile.setItem(i);
                i.setCol(tile.getCol());
                i.setRow(tile.getRow());
                break;
            }
        }
    }
}

