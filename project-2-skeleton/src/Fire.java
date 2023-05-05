import bagel.Image;
import bagel.util.Point;
import bagel.DrawOptions;
import java.lang.Math;

/**
 * fire class for enemies
 */
public class Fire {
    /**
     * draw the fire in the correct location with the right rotation according to player's location
     * @param tl coordinate of the top left point of the image
     * @param image the image we use for the fire
     * @param position of the player that's detected
     */
    public void drawImage(Point tl, Image image, String position){
        if (position.equals("TopLeft"))
            image.drawFromTopLeft(tl.x, tl.y);
        else if (position.equals("BottomLeft"))
            image.drawFromTopLeft(tl.x, tl.y, new DrawOptions().setRotation(-Math.PI/2));
        else if (position.equals("TopRight"))
            image.drawFromTopLeft(tl.x, tl.y, new DrawOptions().setRotation(Math.PI/2));
        else
            image.drawFromTopLeft(tl.x, tl.y, new DrawOptions().setRotation(Math.PI));
    }
}
