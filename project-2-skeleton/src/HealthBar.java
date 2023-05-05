import bagel.DrawOptions;
import bagel.Font;
import bagel.util.Point;
import bagel.util.Colour;

/**
 * healthbar class for characters
 */
public class HealthBar{
    private Colour colour;
    private Point bl;
    private double healthPercentage;
    private String message;
    public static final String FONT_PATH = "res/frostbite.ttf";
    private Font font;
    private static final double LOW_HEALTH_PERCENTAGE = 35;
    private static final double MID_HEALTH_PERCENTAGE = 65;

    public HealthBar(Point bl, String message, int fontSize){
        this.bl = bl;
        this.healthPercentage = Double.valueOf(message);
        this.message = message;
        font = new Font(FONT_PATH, fontSize);
    }

    /**
     * setter for variable healthPercentage
     * @param healthPercentage we pass to the variable
     */
    public void setHealthPercentage(double healthPercentage) {
        this.healthPercentage = healthPercentage;
    }

    /**
     * print out health point onto the screen
     */
    public void printHealthBar(){
        if (healthPercentage < LOW_HEALTH_PERCENTAGE) {
            colour = new Colour(1,0,0);
        }
        else if (healthPercentage < MID_HEALTH_PERCENTAGE){
            colour = new Colour(0.9,0.6,0);
        }
        else
            colour = new Colour(0,0.8,0.2);
        font.drawString(String.format("%.0f",healthPercentage) + "%", bl.x, bl.y,
                                  new DrawOptions().setBlendColour(colour));
    }
}
