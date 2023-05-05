import bagel.Image;
import bagel.util.Point;

/**
 * class for Sinkhole entity
 */
public class Sinkhole extends Entity{
    public final static Image SINKHOLE_IMAGE = new Image("res/sinkhole.png");

    public final static int DAMAGE = 30;
    public Sinkhole(Point tl, Image image){
        super(tl, image);
    }

    /**
     * allow sinkholes to print out it's class name when it's asked to be printed
     * @return class name
     */
    public String toString(){
        return "Sinkhole";
    }
}
