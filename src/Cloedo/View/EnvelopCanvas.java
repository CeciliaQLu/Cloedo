package Cluedo.View;

import Cluedo.Model.Card;
import Cluedo.Model.Game;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * This class defines the envelop canvas.
 * When a player makes an accusation, a window will pop up and show the murder cards in the envelop
 * and whether the player wins or not.
 */
public class EnvelopCanvas extends CanvasView {
    /**
     * Name of the person in envelop
     */
    private String person;
    /**
     * Name of the weapon in envelop
     */
    private String weapon;
    /**
     * Name of the room in envelop
     */
    private String room;
    /**
     * Whether the player wins
     */
    public boolean win = false;
    /**
     * Current game.
     */
    public Game game;

    /**
     * Reset the size of the canvas.
     */
    public EnvelopCanvas(){
        setBounds(0, 0, 400, 100);
    }

    /**
     * Set a current game.
     * @param g a game object to be the current game
     */
    public void setGame(Game g){
        this.game = g;
    }

    /**
     * Set accusation cards.
     * @param p name of person
     * @param w name of weapon
     * @param r name of room
     */
    public void setAccusationCards(String p, String w, String r){
        this.person = p;
        this.weapon = w;
        this.room = r;
    }

    public boolean getWin(){
        Card[] murderCards = game.murderCards;
        String personName = murderCards[0].getName();
        String weaponName = murderCards[1].getName();
        String roomName = murderCards[2].getName();
        win = personName.equals(this.person) && weaponName.equals(this.weapon) && roomName.equals(this.room);
        return win;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        g.setColor(new Color(255, 250, 250));
        g.fillRect(0, 0, this.getWidth(), this.getHeight());
        int wid = this.getWidth()/7;
        int hei = this.getHeight()/4;

        try{
            // draw an envelop image
            BufferedImage bi = ImageIO.read(new File("./assets/Envelop/Envelop.jpg"));
            g.drawImage(bi, this.getWidth()/12, this.getWidth()/12, this.getWidth()/8, this.getWidth()/8, null);

            // draw murder cards
            Card[] murderCards = game.murderCards;
            String personName = murderCards[0].getName();
            String weaponName = murderCards[1].getName();
            String roomName = murderCards[2].getName();

            BufferedImage bi_person = ImageIO.read(new File("./assets/Envelop/" + personName + ".jpg"));
            g.drawImage(bi_person, wid, hei, wid, wid*3/2, null);

            BufferedImage bi_weapon = ImageIO.read(new File("./assets/Envelop/" + weaponName + ".jpg"));
            g.drawImage(bi_weapon, wid*3, hei, wid, wid*3/2, null);

            BufferedImage bi_room = ImageIO.read(new File("./assets/Envelop/" + roomName + ".jpg"));
            g.drawImage(bi_room, wid*5, hei, wid, wid*3/2, null);


            // draw the information about winning or lossing
            g.setColor(Color.BLACK);
            if(getWin()){
                this.win = true;
                g.drawString("You Win!", wid * 3, hei*3);
            }else{
                g.drawString("Oops! Better luck next time!", wid * 3, hei*3);
            }

        }catch (IOException e){
            throw new Error("Error: something wrong with Envelop Canvas");
        }
    }
}
