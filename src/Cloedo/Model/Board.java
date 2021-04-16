package Cluedo.Model;

import Cluedo.Model.Items.Person;
import Cluedo.Model.Items.Room;
import Cluedo.Model.Items.Weapon;
import Cluedo.Model.Tiles.*;

import java.util.ArrayList;
import java.util.List;

/**
 * This is the underlying data structure for a 24 x 25 board.
 * Each square for each position,
 * null if this square is not available.
 *
 * "@" is the partition (wall) (cannot go across it);
 * "|" is the separator of two horizontally adjacent tiles (can go across it);
 * "---" is the separator of two vertically adjacent tiles (can go across it).
 */
public class Board {
    /** Tiles on each square. */
    public Tile[][] tiles;

    /**
     *  Using different letters to represent different types of tiles:
     *    numbers: DoorTile
     *    B: BlockedTile
     *    H: HallTile
     *    K: RoomTile - Kitchen
     *    A: RoomTile - Ballroom
     *    C: RoomTile - Conservatory
     *    O: RoomTile - Billiards Room
     *    L: RoomTile - Library
     *    S: RoomTile - Study
     *    X: RoomTile - Hall
     *    U: RoomTile - Lounge
     *    I: RoomTile - Dining Room
     */
    public String initialTiles;

    /**
     * "Kitchen", "Ballroom", "Conservatory", "Billiards Room",
     * "Library", "Study", "Hall", "Lounge", "Dining Room"
     */
    private Room[] rooms;

    /**
     * "Candlestick", "Revolver", "Spanner", "Rope", "Dagger", "Lead Pipe"
     */
    public Weapon[] weapons;

    /**
     * Constructor
     * @param rooms 9 rooms in the game
     */
    public Board(Room[] rooms, Weapon[] weapons) {
        this.rooms = rooms;
        this.weapons = weapons;
        this.tiles = new Tile[25][24];
        generateTiles();
        assignRoomsToDoors();
        placeWeapons();
    }

    /**
     * Check if a player is in a room or not.
     *
     * @param player a player
     * @return true if the player is in a room;
     *         false otherwise.
     */
    public boolean inRoom(Player player){
        Person person = player.getPerson();
        int row = person.row();
        int col = person.col();
        return tiles[row][col] instanceof RoomTile;
    }

    /**
     * Add people onto the tiles when initialising a new board.
     *
     * @param people 6 people (represented by the name of the character, starting row and column) in the game.
     */
    public void addPeople(Person[] people){
        for(int i = 0; i < people.length; i++){
            Person person = people[i];
            int row = person.row();
            int col = person.col();
            tiles[row][col].setItem(person);
        }
    }

    /**
     * Place 6 weapons into 6 different rooms randomly.
     */
    public void placeWeapons() {
        List<Integer> unusedRooms = new ArrayList<Integer>();
        for(int i = 0; i< 9; i++) {
            unusedRooms.add(i);
        }
        int roomsLeft = 9;
        for(int i = 0; i < 6; i++) {
            int newRoom = (int)(Math.random() * roomsLeft);
            rooms[unusedRooms.get(newRoom)].placeRandom(weapons[i]);
            unusedRooms.remove(newRoom);
            roomsLeft = roomsLeft - 1;
        }
    }

    /**
     * Get the room that a specific player is in.
     * @param player a specific player
     * @return the room the player is in
     */
    public Room getRoom(Player player){
        Person person = player.getPerson();
        int row = person.row();
        int col = person.col();
        return ((RoomTile) tiles[row][col]).getRoom();
    }

    /**
     * Tiles on the board.
     */
    public void generateTiles() {
        initialTiles = "BBBBBBBBBHBBBBHBBBBBBBBB" +
                "KKKKKKBHHHAAAAHHHBCCCCCC" +
                "KKKKKKHHAAAAAAAAHHCCCCCC" +
                "KKKKKKHHAAAAAAAAHHCCCCCC" +
                "KKKKKKHHAAAAAAAAHHCCCCCC" +
                "KKKKKKH1AAAAAAAA4H1CCCCB" +
                "BKKKKKHHAAAAAAAAHHHHHHHH" +
                "HHHH1HHHAAAAAAAAHHHHHHHB" +
                "BHHHHHHHH2HHHH3HHHOOOOOO" +
                "IIIIIHHHHHHHHHHHH1OOOOOO" +
                "IIIIIIIIHHBBBBBHHHOOOOOO" +
                "IIIIIIIIHHBBBBBHHHOOOOOO" +
                "IIIIIIII1HBBBBBHHHOOOOOO" +
                "IIIIIIIIHHBBBBBHHHHH1H2B" +
                "IIIIIIIIHHBBBBBHHHLLLLLB" +
                "IIIIIIIIHHBBBBBHHLLLLLLL" +
                "BHHHHH2HHHBBBBBH2LLLLLLL" +
                "HHHHHHHHHHH12HHHHLLLLLLL" +
                "BHHHHH1HHXXXXXXHHHLLLLLB" +
                "UUUUUUUHHXXXXXXHHHHHHHHH" +
                "UUUUUUUHHXXXXXX3H1HHHHHB" +
                "UUUUUUUHHXXXXXXHHSSSSSSS" +
                "UUUUUUUHHXXXXXXHHSSSSSSS" +
                "UUUUUUUHHXXXXXXHHSSSSSSS" +
                "UUUUUUBHBXXXXXXBHBSSSSSS";
        int index = 0;
        char currentChar;
        for(int i = 0; i< 25; i++) {
            for(int j = 0; j<24; j++) {
                currentChar = initialTiles.charAt(index);
                if(currentChar == 'B') {
                    tiles[i][j] = new BlockedTile(i, j);
                }
                else if(currentChar == 'H') {
                    tiles[i][j] = new HallTile(i, j);
                }
                else if(currentChar == '1' || currentChar == '2' || currentChar == '3' || currentChar == '4') {
                    tiles[i][j] = new DoorTile(Integer.parseInt(String.valueOf(currentChar)), i, j);
                }
                else{
                    if(currentChar == 'K') {
                        tiles[i][j] = new RoomTile(rooms[0], i, j);
                        rooms[0].addRoomTile((RoomTile)tiles[i][j]);
                    }
                    if(currentChar == 'A') {
                        tiles[i][j] = new RoomTile(rooms[1], i, j);
                        rooms[1].addRoomTile((RoomTile)tiles[i][j]);
                    }
                    if(currentChar == 'C') {
                        tiles[i][j] = new RoomTile(rooms[2], i, j);
                        rooms[2].addRoomTile((RoomTile)tiles[i][j]);
                    }
                    if(currentChar == 'O') {
                        tiles[i][j] = new RoomTile(rooms[3], i, j);
                        rooms[3].addRoomTile((RoomTile)tiles[i][j]);
                    }
                    if(currentChar == 'L') {
                        tiles[i][j] = new RoomTile(rooms[4], i, j);
                        rooms[4].addRoomTile((RoomTile)tiles[i][j]);
                    }
                    if(currentChar == 'S') {
                        tiles[i][j] = new RoomTile(rooms[5], i, j);
                        rooms[5].addRoomTile((RoomTile)tiles[i][j]);
                    }
                    if(currentChar == 'X') {
                        tiles[i][j] = new RoomTile(rooms[6], i, j);
                        rooms[6].addRoomTile((RoomTile)tiles[i][j]);
                    }
                    if(currentChar == 'U') {
                        tiles[i][j] = new RoomTile(rooms[7], i, j);
                        rooms[7].addRoomTile((RoomTile)tiles[i][j]);
                    }
                    if(currentChar == 'I') {
                        tiles[i][j] = new RoomTile(rooms[8], i, j);
                        rooms[8].addRoomTile((RoomTile)tiles[i][j]);
                    }
                }
                index = index + 1;
            }
        }
    }

    /**
     * Assign each door tile to a specific room
     */
    public void assignRoomsToDoors() {
        for(int i = 0; i<25; i++) {
            for(int j = 0; j <24; j++) {
                if(tiles[i][j] instanceof DoorTile) {
                    if(tiles[i][j+1] instanceof RoomTile) {
                        ((DoorTile) tiles[i][j]).setRoom(((RoomTile) tiles[i][j+1]).getRoom());
                        ((RoomTile)(tiles[i][j+1])).getRoom().removeTile(tiles[i][j+1]);
                    }
                    else if(tiles[i][j-1] instanceof RoomTile) {
                        ((DoorTile) tiles[i][j]).setRoom(((RoomTile) tiles[i][j-1]).getRoom());
                        ((RoomTile)tiles[i][j-1]).getRoom().removeTile(tiles[i][j+1]);
                    }
                    else if(tiles[i+1][j] instanceof RoomTile) {
                        ((DoorTile) tiles[i][j]).setRoom(((RoomTile) tiles[i+1][j]).getRoom());
                        ((RoomTile) tiles[i+1][j]).getRoom().removeTile(tiles[i][j+1]);
                    }
                    else if(tiles[i-1][j] instanceof RoomTile) {
                        ((DoorTile) tiles[i][j]).setRoom(((RoomTile) tiles[i-1][j]).getRoom());
                        ((RoomTile) tiles[i-1][j]).getRoom().removeTile(tiles[i][j+1]);
                    }
                }
            }
        }
        ((DoorTile) tiles[13][20]).setRoom(((RoomTile) tiles[14][20]).getRoom());
        ((DoorTile) tiles[13][22]).setRoom(((RoomTile) tiles[12][22]).getRoom());
    }
}