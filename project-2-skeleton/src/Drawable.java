import bagel.Image;

/**
 * Drawable interface that will be useful for Entity class and Character class
 */
public interface Drawable {
    /**
     * draw an object using the image that's passed in
     * @param image of the image to be drawn
     */
    void drawImage(Image image);
}
