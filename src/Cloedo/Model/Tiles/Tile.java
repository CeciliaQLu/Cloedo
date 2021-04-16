package Cluedo.Model.Tiles;
import Cluedo.Model.Items.Item;

/**
 * Superclass of the different types of tiles.
 */
public class Tile {
    /**
     * Item on the tile
     */
    private Item item;
    /**
     * Position of the Tile: row index and column index.
     */
    private int row, col;

    /**
     * Construct a Tile object using its position
     * @param row row index
     * @param col column index
     */
    public Tile(int row, int col) {
        this.row = row;
        this.col = col;
        this.item = null;
    }

    /**
     * Get the row index of the tile
     *
     * @return row index
     */
    public int getRow() {
        return row;
    }

    /**
     * Get the column index of the tile
     *
     * @return column index
     */
    public int getCol() {
        return col;
    }

    /**
     * Set item on the tile
     * @param item the item on the tile
     */
    public void setItem(Item item) {
        this.item = item;
    }

    /**
     * Get the item on the tile
     * @return the item object on the tile
     */
    public Item getItem(){
        return this.item;
    }

    /**
     * Get the name of the tile.
     * If there is an item on the tile,
     * return the name of the item.
     * If there is nothing on the tile,
     * return a space.
     * @return the name of the item if there is an item on the tile;
     *         otherwise return space " ".
     */
    public String toString(){
        if(item != null) return item.toString();
        else return " ";
    }
}