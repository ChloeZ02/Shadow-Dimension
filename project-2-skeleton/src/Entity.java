import bagel.Image;
import bagel.util.Point;
import bagel.util.Rectangle;

/**
 * abstract parent class for fixed entities in the game
 */
public abstract class Entity extends Rectangle implements Drawable{

    public Entity(Point tl, Image image){
        super(tl, image.getWidth(), image.getHeight());
    }

    /**
     * draw the entities onto the screen using the image
     * @param image of the entity
     */
    public void drawImage(Image image){
        image.drawFromTopLeft(this.topLeft().x, this.topLeft().y);
    }
}
