import bagel.Image;
import bagel.util.Point;

/**
 * class for treating tree and wall as entities of the same category hence put them into one class rather than
 * having them separate
 */
public class Obstacle extends Entity{
    public final static Image TREE_IMAGE = new Image("res/tree.png");
    public static final Image WALL_IMAGE = new Image("res/wall.png");

    public Obstacle(Point tl, Image image){
        super(tl, image);
    }

}
