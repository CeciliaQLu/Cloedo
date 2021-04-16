package Cluedo.View;

import Cluedo.Model.Game;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * This class defines the canvas that shows the characters and weapons the pictures represent.
 * Text canvas supports scrolling.
 */
public class TextCanvas extends CanvasView {
    /**
     * Current game
     */
    private Game game;
    /**
     * Y value of the canvas on the main frame
     */
    private int textCanvasStartY;

    /**
     * Reset the size of the text canvas.
     */
    public TextCanvas(){
        setBounds(0, 420, 400, 100);
    }

    /**
     * Set the current game when a new game starts.
     * @param g current game
     */
    public void setGame(Game g){
        this.game = g;
    }

    /**
     * Set the start y value on y axis
     * @param y the y value of the canvas
     */
    public void setTextCanvasStartY(int y){
        this.textCanvasStartY = y;
    }

    /**
     * Get the y value on y axis
     * @return y value of the canvas on the whole frame
     */
    public int getTextCanvasStartY(){
        return this.textCanvasStartY;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        g.drawString("Player Colour Key: ",  10, textCanvasStartY + 20);

        // draw images
        try{
            for(int i = 0; i < 6; i++){
                String characterName = game.CHARACTER_NAMES[i];
                String weaponName = game.WEAPON_NAMES[i];
                BufferedImage bi_character = ImageIO.read(new File("./assets/Characters/" + characterName + ".png"));
                BufferedImage bi_weapon = ImageIO.read(new File("./assets/Weapons/" + weaponName + ".jpg"));
                g.drawImage(bi_character, 10, textCanvasStartY + 40 + 30 * i, 20, 20, null);
                g.drawImage(bi_weapon, 10, textCanvasStartY + 240 + 30 * i, 20, 20, null);
            }
        }catch (IOException e){
            throw new Error("Text canvas draw error");
        }

        // draw strings: names corresponding to the images.
        for(int i = 0; i < 6; i++){
            g.drawString(game.CHARACTER_NAMES[i], 40, textCanvasStartY + 55 + 30 * i);
            g.drawString(game.WEAPON_NAMES[i], 40, textCanvasStartY + 255 + 30 * i);
        }
    }
}
