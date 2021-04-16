package Cluedo.View;

import java.awt.*;
import java.util.HashSet;

/**
 * In this game, there are four canvases:
 * board canvas, text canvas (showing keys of characters and weapons),
 * update canvas (update information once the player has done something),
 * and envelop canvas (show the cards in envelop and if the player has win).
 *
 */
public class CanvasView extends Canvas {
    /**
     * Fonts that might be used in the canvas
     */
    public static String[] preferredFonts = {"Arial","Times New Roman"};
    public Font font;

    /**
     * Constructor: set bounds and fonts of canvas.
     */
    public CanvasView(){
        setBounds(0, 0, 800, 800);
        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        HashSet<String> availableNames = new HashSet();

        for(String name : env.getAvailableFontFamilyNames()) {
            availableNames.add(name);
        }

        for(String pf : preferredFonts) {
            if(availableNames.contains(pf)) {
                font = new Font("Comic Sans MS", Font.BOLD, 24);
                break;
            }
        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
    }
}
