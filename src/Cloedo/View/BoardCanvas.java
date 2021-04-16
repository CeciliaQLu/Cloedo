package Cluedo.View;

import Cluedo.Model.Game;
import Cluedo.Model.Items.Person;
import Cluedo.Model.Items.Weapon;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


/**
 * This class defines the board canvas of the game.
 * Players can move on yellow squares; green squares are blocked tiles.
 * Thick lines are walls.
 */
public class BoardCanvas extends CanvasView{
    /**
     * Current game running
     */
    private Game game;

    /**
     * Whether the game has started.
     */
    private boolean gameStarted;

    /**
     * Set the game status
     * @param b true if the game has started; false otherwise
     */
    public void setGameStarted(boolean b){
        this.gameStarted = b;
    }

    /**
     * Set a new game
     * @param g the game that is belonged to the board
     */
    public void setGame(Game g){
        this.game = g;
    }


    @Override
    public void paint(Graphics g) {
        super.paint(g);
        // width of a square
        int width = (getWidth()-4)/24;
        // height of a square
        int height = (getHeight()-4)/25;

        if(font != null) {
            g.setFont(font);
        }
        // draw background
        g.setColor(new Color(240, 255, 240));
        g.fillRect(0,0, getWidth(),getHeight());
        g.setColor(new Color(255, 250, 205));
        g.fillRect(5,5, width * 24, height * 25);
        drawGrid(g, 5, 5, width, height);
        drawBoardBackground(g, 5, 5, width, height);
        drawStrings(g, 5, 5, width, height);

        // draw characters
        if(gameStarted == true){
            try {
                drawPeopleAndWeapons(g, 5, 5, width, height);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Draw the names of the rooms
     * @param g Graphics of the board canvas
     * @param startX x value of the board area (on x axis)
     * @param startY y value of the board area (on y axis)
     * @param wid width of a square/tile
     * @param hei height of a square/tile
     */
    private void drawStrings(Graphics g, int startX, int startY, int wid, int hei){
        g.setColor(Color.gray);
        Font myFont = new Font("Serif", Font.BOLD, wid/2);
        g.setFont(myFont);
        g.drawString("Kitchen", startX + wid * 2, startY + hei * 3 + hei/4);
        g.drawString("Dining Room", startX + wid * 2, startY + hei * 12);
        g.drawString("Lounge", startX + 2 * wid + 15, startY + 21 * hei);
        g.drawString("Hall", startX + wid * 11, startY + hei * 21);
        g.drawString("Study", startX + wid * 19, startY + hei * 22);
        g.drawString("Library", startX + wid * 19, startY + hei * 15 + hei / 2);
        g.drawString("Billiard Room", startX + wid * 19, startY + hei * 10);
        g.drawString("Conservatory", startX + wid * 19, startY + hei * 2);
        g.drawString("Ball Room", startX + wid * 10, startY + hei * 4);

    }

    /**
     * Draw the horizontal and vertical lines on the board
     * @param g Graphics of the board canvas
     * @param startX x value of the board area (on x axis)
     * @param startY y value of the board area (on y axis)
     * @param wid width of a square/tile
     * @param hei height of a square/tile
     */
    private void drawGrid(Graphics g, int startX, int startY, int wid, int hei){
        g.setColor(Color.lightGray);
        // draw horizontal lines
        for(int i = 0; i < 26; i++){
            g.drawLine(startX, startY + hei * i, startX + wid * 24, startY + hei * i);
        }

        // draw vertical lines
        for(int i = 0; i < 25; i++){
            g.drawLine(startX + wid * i, startY, startX + wid * i, startY + hei * 25);
        }
    }

    /**
     * Draw people and weapons on the board. Weapons are randomly put into six rooms.
     * @param g Graphics of the board canvas
     * @param startx x value of the board area (on x axis)
     * @param starty y value of the board area (on y axis)
     * @param wid width of a square/tile
     * @param hei height of a square/tile
     * @throws IOException catch Exception for BufferedImage
     */
    public void drawPeopleAndWeapons(Graphics g, int startx, int starty, int wid, int hei) throws IOException {
        // draw weapons
        try{
            for(int i = 0; i < 6; i++){
                Weapon w = game.board.weapons[i];
                String nameOfWeapon = w.getName();
                BufferedImage bi = ImageIO.read(new File("./assets/Weapons/" + nameOfWeapon + ".jpg"));
                g.drawImage(bi, startx + wid * w.col(), starty + hei * w.row(), wid, hei, null);
            }
        }catch (IOException e){
            throw new Error("Draw weapon error.");
        }

        // draw people
        try{
            for(int i = 0; i < game.people.length; i++){
                Person p = game.people[i];
                BufferedImage bi = ImageIO.read(new File("./assets/Characters/" + game.CHARACTER_NAMES[i] + ".png"));
                g.drawImage(bi, startx + wid * p.col(), starty + hei * p.row() - 1, wid, hei, null);

            }
        }catch (IOException e){
            throw new Error("Draw people error.");
        }
    }

    /**
     * Color different types of tiles and draw the walls.
     * B: blocked tiles;
     * R: room tiles;
     * H: hall tiles;
     * Numbers: door tiles.
     *
     * @param g Graphics of the board canvas
     * @param startx x value of the board area (on x axis)
     * @param starty y value of the board area (on y axis)
     * @param wid width of a square/tile
     * @param hei height of a square/tile
     */
    private void drawBoardBackground(Graphics g, int startx, int starty, int wid, int hei){
        String boardString = "BBBBBBBBBHBBBBHBBBBBBBBB" +
                "RRRRRRBHHHRRRRHHHBRRRRRR" +
                "RRRRRRHHRRRRRRRRHHRRRRRR" +
                "RRRRRRHHRRRRRRRRHHRRRRRR" +
                "RRRRRRHHRRRRRRRRHHRRRRRR" +
                "RRRRRRH1RRRRRRRR4H1RRRRB" +
                "BRRRRRHHRRRRRRRRHHHHHHHH" +
                "HHHH1HHHRRRRRRRRHHHHHHHB" +
                "BHHHHHHHH2HHHH3HHHRRRRRR" +
                "RRRRRHHHHHHHHHHHH1RRRRRR" +
                "RRRRRRRRHHBBBBBHHHRRRRRR" +
                "RRRRRRRRHHBBBBBHHHRRRRRR" +
                "RRRRRRRR1HBBBBBHHHRRRRRR" +
                "RRRRRRRRHHBBBBBHHHHH1H2B" +
                "RRRRRRRRHHBBBBBHHHRRRRRB" +
                "RRRRRRRRHHBBBBBHHRRRRRRR" +
                "BHHHHH2HHHBBBBBH2RRRRRRR" +
                "HHHHHHHHHHH12HHHHRRRRRRR" +
                "BHHHHH1HHRRRRRRHHHRRRRRB" +
                "RRRRRRRHHRRRRRRHHHHHHHHH" +
                "RRRRRRRHHRRRRRR3H1HHHHHB" +
                "RRRRRRRHHRRRRRRHHRRRRRRR" +
                "RRRRRRRHHRRRRRRHHRRRRRRR" +
                "RRRRRRRHHRRRRRRHHRRRRRRR" +
                "RRRRRRBHBRRRRRRBHBRRRRRR";

        int index = 0;
        // fill colour of rooms and blocked tiles
        for(int i = 0; i < 25; i++){
            for(int j = 0; j < 24; j++){
                char c = boardString.charAt(index);
                if(c == 'B'){
                    g.setColor(new Color(240, 255, 240));
                    g.fillRect(startx + wid * j, starty + hei * i, wid, hei);

                }else if(c == 'R'){
                    g.setColor(new Color(248, 248, 255));
                    g.fillRect(startx + wid * j, starty + hei * i, wid, hei);
                }
                index++;
            }
        }

        // draw walls
        index = 0;
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(4));
        g.setColor(new Color(105, 105, 105));
        for(int i = 0; i < 25; i++){
            for(int j = 0; j < 24; j++){
                char c = boardString.charAt(index);
                if(c == 'B'){
                    // draw vertical walls
                    if(j != 23){
                        char rightChar = boardString.charAt(index + 1);
                        if(rightChar == 'R'){
                            g.drawLine(startx + wid * (j + 1) + 2, starty + hei * i + 2, startx + wid * (j + 1) + 2, starty + hei * (i + 1) - 2);
                        }else if(rightChar == 'H'){
                            g.drawLine(startx + wid * (j + 1) - 2, starty + hei * i + 2,startx + wid * (j + 1) - 2, starty + hei * (i + 1) - 2);
                        }
                    }
                    // draw vertical walls
                    if(i != 24){
                        char underChar = boardString.charAt(index + 24);
                        if(underChar == 'R'){
                            g.drawLine(startx + wid * j + 2, starty + hei * (i + 1) + 2, startx + wid * (j + 1) - 2, starty + hei * (i + 1) + 2);
                        }else if(underChar == 'H'){
                            if(j != 0){
                                char leftChar = boardString.charAt(index - 1);
                                char underLeftChar = boardString.charAt(index + 23);
                                if(leftChar == 'B' && underLeftChar == 'B'){
                                    g.drawLine(startx + wid * j - 2, starty + hei * (i + 1) - 2, startx + wid * (j + 1) - 2, starty + hei * (i + 1) - 2);
                                }
                            }

                            if(j != 23){
                                char rightChar = boardString.charAt(index + 1);
                                char underRightChar = boardString.charAt(index + 25);
                                if(rightChar == 'B' && underRightChar == 'B'){
                                    g.drawLine(startx + wid * j + 2, starty + hei * (i + 1) - 2, startx + wid * (j + 1) + 2, starty + hei * (i + 1) - 2);
                                }
                            }
                            g.drawLine(startx + wid * j + 2, starty + hei * (i + 1) - 2, startx + wid * (j + 1) - 2, starty + hei * (i + 1) - 2);
                        }
                    }
                }else if(c == 'R'){
                    if(j != 23){
                        char rightChar = boardString.charAt(index + 1);
                        if(rightChar == 'B' || rightChar == 'H'){
                            if(i != 24){
                                char underChar = boardString.charAt(index + 24);
                                char rightUnderChar = boardString.charAt(index + 25);
                                if(underChar == 'R' && rightUnderChar == 'R'){
                                    g.drawLine(startx + wid * (j + 1) - 2, starty + hei * i + 2, startx + wid * (j + 1) - 2, starty + hei * (i + 1) + 2);
                                }
                            }
                            g.drawLine(startx + wid * (j + 1) - 2, starty + hei * i + 2, startx + wid * (j + 1) - 2, starty + hei * (i + 1) - 2);
                        }
                    }
                    // draw horizontal walls
                    if(i != 24){
                        char underChar = boardString.charAt(index + 24);
                        if(underChar == 'B' || underChar == 'H'){
                            if(j != 0){
                                char leftChar = boardString.charAt(index - 1);
                                char underLeftChar = boardString.charAt(index + 23);
                                if(leftChar == 'R' && underLeftChar == 'R'){
                                    g.drawLine(startx + wid * j - 2, starty + hei * (i + 1) - 2, startx + wid * (j + 1) - 2, starty + hei * (i + 1) - 2);
                                }
                            }

                            if(j != 23){
                                char rightChar = boardString.charAt(index + 1);
                                char underRightChar = boardString.charAt(index + 25);
                                if(rightChar == 'R' && underRightChar == 'R'){
                                    g.drawLine(startx + wid * j + 2, starty + hei * (i + 1) - 2, startx + wid * (j + 1) + 2, starty + hei * (i + 1) - 2);
                                }
                            }

                            g.drawLine(startx + wid * j + 2, starty + hei * (i + 1) - 2, startx + wid * (j + 1) - 2, starty + hei * (i + 1) - 2);
                        }
                    }
                }else if(c == 'H'){
                    if(j != 23){
                        char rightChar = boardString.charAt(index + 1);
                        if(rightChar == 'B' || rightChar == 'R'){
                            if(i != 24){
                                char underChar = boardString.charAt(index + 24);
                                char rightUnderChar = boardString.charAt(index + 25);
                                if(underChar == 'R' && rightUnderChar == 'R'){
                                    g.drawLine(startx + wid * (j + 1) + 2, starty + hei * i + 2, startx + wid * (j + 1) + 2, starty + hei * (i + 1) + 2);
                                }
                            }
                            g.drawLine(startx + wid * (j + 1) + 2, starty + hei * i + 2, startx + wid * (j + 1) + 2, starty + hei * (i + 1) - 2);
                        }
                    }
                    if(i != 24){
                        char underChar = boardString.charAt(index + 24);
                        if(underChar == 'B' || underChar == 'R'){
                            g.drawLine(startx + wid * j + 2, starty + hei * (i + 1) + 2, startx + wid * (j + 1) - 2, starty + hei * (i + 1) + 2);
                        }
                    }
                }
                index++;
            }
        }
        g.drawLine(startx + wid * 11, starty + hei * 17 - 2, startx + wid * 13, starty + hei * 17 - 2);
        g.drawLine(startx + wid * 23 - 2, starty + hei * 13 - 2, startx + wid * 23 - 2, starty + hei * 14);
        g.drawLine(startx + wid * 20, starty + hei* 13 -2, startx + wid * 21, starty + hei* 13 -2);
        g.drawLine(startx + wid * 22, starty + hei* 14 + 2, startx + wid * 23 - 2, starty + hei* 14 + 2);
        g.drawLine(startx + wid * 18 + 2, starty + hei* 5 + 2, startx + wid * 19 - 2, starty + hei* 5 + 2);
        g2.setStroke(new BasicStroke(1));
    }

}
