package Cluedo.View;

import Cluedo.Model.Card;
import Cluedo.Model.Game;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * This class defines the canvas that updates the information of the current player.
 * It shows: the character the current player controls;
 *           how many steps remaining in this turn;
 *           cards in the current player's hand.
 */
public class UpdateCanvas extends CanvasView {
    /**
     * Name of the current player.
     */
    private String currentPlayer;
    /**
     * Name of the current person that the player controls.
     */
    private String currentPerson;
    /**
     * Number of steps remaining in this turn.
     */
    private int remainMove;
    /**
     * Number of players in this game.
     */
    private int numberOfPlayers;
    /**
     * Current game.
     */
    private Game game;
    /**
     * Index of current player in the players array.
     */
    private int currentPlayerIndex;

    /**
     * Reset the bound of the canvas.
     */
    public UpdateCanvas(){
        setBounds(0, 420, 400, 100);
    }

    /**
     * Set playerName as the current player
     * @param playerName the name of the current player
     */
    public void setCurrentPlayer(String playerName){
        this.currentPlayer = playerName;
    }

    /**
     * Set personName as the current person
     * @param personName the name of the person
     */
    public void setCurrentPerson(String personName){
        this.currentPerson = personName;
    }

    /**
     * Set the number of moves remaining
     * @param moveLeft number of moves remaining
     */
    public void setRemainMove(int moveLeft){
        this.remainMove = moveLeft;
    }

    /**
     * Set the player numbers
     * @param numOfPlayers number of players in the game
     */
    public void setNumberOfPlayers(int numOfPlayers){
        this.numberOfPlayers = numOfPlayers;
    }

    /**
     * Set the current game once a new game starts.
     * @param game current game object
     */
    public void setGame(Game game){
        this.game = game;
    }

    /**
     * Set the index of the current player.
     * @param currentIndex The index of the current player
     */
    public void setCurrentPlayerIndex(int currentIndex){
        this.currentPlayerIndex = currentIndex;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        if(numberOfPlayers > 0){
            // player controls person
            g.drawString(currentPlayer + "'s turn", 10, 20);
            g.drawString(currentPlayer + " controls " + currentPerson, 10, 40);
            // move remaining
            g.drawString("Move remaining: " + remainMove, 10, 60);
            // cards in hand
            g.drawString("Cards in hand: ", 10 , 80);
            g.drawString("Cards in hand: ", 10 , 80);
            ArrayList<Card> cardsCurrentPlayer = (ArrayList<Card>) game.players[currentPlayerIndex].getHand();
            int start_x = 10;
            int start_y = 100;
            int countX = 0;
            int countY = 0;
            for(int i = 0; i < cardsCurrentPlayer.size(); i++){
                String nm = cardsCurrentPlayer.get(i).getName();
                BufferedImage bi = null;
                try {
                    bi = ImageIO.read(new File("./assets/Envelop/" + nm + ".jpg"));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                g.drawImage(bi, start_x + countX * 90, start_y + countY * 100, 65, 85, null);
                nm = getShorterName(nm);
                g.drawString(nm, start_x + countX * 90, start_y + countY * 100 + 95);

                if(countX == 3){
                    countX = 0;
                    countY++;
                }else{
                    countX++;
                }
            }
        }
    }

    /**
     * Make the names of some characters and rooms shorter to fit the canvas and images
     * @param nm full name of a character or a room
     * @return shorter name of the character or room
     */
    private String getShorterName(String nm){
        if(nm.equals("Colonel Mustard")){
            nm = "Mustard";
        }
        if(nm.equals("Professor Plum")){
            nm = "Prof. Plum";
        }
        if(nm.equals("Miss Scarlett")){
            nm = "Scarlett";
        }
        if(nm.equals("Billiards Room")){
            nm = "Billiards";
        }if(nm.equals("Dining Room")){
            nm = "Dining";
        }
        return nm;
    }
}
