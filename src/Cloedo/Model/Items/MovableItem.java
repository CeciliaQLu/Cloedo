package Cluedo.Model.Items;

/**
 * This class defines the item that can be moved.
 * Its subclasses are Person and Weapon,
 * because persons and weapons can be moved, but rooms cannot be moved.
 */
public class MovableItem extends Item{
    /**
     * row and col defines the position of the item.
     */
    private int row, col;

    /**
     * Use the name of the item, and its position to construct the Movable item.
     *
     * @param name name of the item
     * @param row  index of the row that the item is in
     * @param col  index of the column that the item is in
     */
    public MovableItem(String name, int row, int col) {
        super(name);
        this.row = row;
        this.col = col;
    }

    /**
     * Get the index of the row
     */
    public int row(){ return row;}

    /**
     * Set the row of the item
     * @param row the row index
     */
    public void setRow(int row){this.row = row;}

    /**
     * Get the index of the column
     * @return column index
     */
    public int col(){ return col;}

    /**
     * Set the column of the item
     * @param col the column index
     */
    public void setCol(int col){this.col = col;}

}