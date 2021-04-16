package Cluedo.Model.Tiles;

/**
 * BlockedTile defines the tiles where any character cannot move to.
 */
public class BlockedTile extends Tile {

    /**
     * Construct a BlockedTile by its position (defined by row and column)
     * @param row index of row
     * @param col index of column
     */
    public BlockedTile(int row, int col) {
        super(row, col);
    }
}