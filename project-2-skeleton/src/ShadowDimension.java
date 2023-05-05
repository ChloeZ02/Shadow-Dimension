import bagel.*;
import bagel.util.Point;

import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileReader;

/**
 * Skeleton Code for SWEN20003 Project 2, Semester 2, 2022
 *
 * Please enter your name below
 * @Neifang(Chloe)Zhang
 */

/**
 * class for the game
 */
public class ShadowDimension extends AbstractGame {
    private final static int WINDOW_WIDTH = 1024;
    private final static int WINDOW_HEIGHT = 768;
    private final static double ENDPOINT_X = 950;
    private final static double ENDPOINT_Y = 670;
    private final static double TITLE_X = 260;
    private final static double TITLE_Y = 250;
    private final static double X_DIFF = 90; // difference in x-coordinate between the title and instructions
    private final static double Y_DIFF = 190; // difference in y-coordinate between the title and instructions
    private final static double LINE_SPACING = 50; // set the spacing between lines to allow readability
    public final static int LEVEL_0 = 0;
    public final static int LEVEL_1 = 1;
    private final static int MESSAGE_FONT_SIZE = 75;
    private final static int INSTRUCTION_FONT_SIZE = 40;
    private final static int ZERO_HEALTH = 0;
    private final static String GAME_TITLE = "SHADOW DIMENSION";
    private final String LV0_INSTRUCTION1 = "PRESS SPACE TO START";
    private final static String LV0_INSTRUCTION2 = "USE ARROW KEYS TO FIND GATE";

    private final static String LV0_END_MESSAGE = "LEVEL COMPLETE!";
    private final static String WIN_MESSAGE = "CONGRATULATIONS";
    private final static String LOSE_MESSAGE = "GAME OVER!";
    private final Font MESSAGE_FONT = new Font("res/frostbite.ttf", MESSAGE_FONT_SIZE);
    private final Font INSTRUCTION_FONT = new Font("res/frostbite.ttf", INSTRUCTION_FONT_SIZE);

    public ShadowDimension(){
        super(WINDOW_WIDTH, WINDOW_HEIGHT, GAME_TITLE);
    }

    private Player fae;
    private boolean lv0Started = false;
    private boolean lv1Started = false;
    private Lv1 lv1 = new Lv1();
    private Lv0 lv0 = new Lv0();

    /**
     * The entry point for the program.
     */
    public static void main(String[] args) {
        ShadowDimension game = new ShadowDimension();
        game.run();
    }

    /**
     * @return centre of the window
     */
    private Point calculateCentre(){
        return new Point(((double)Window.getWidth())/2,
                ((double)Window.getHeight())/2);
    }

    /**
     * Performs a state update.
     * @param input the input read from user pressing a certain key on the keyboard
     */
    @Override
    protected void update(Input input) {
        fae = lv0.getFae();

        // the window should be able to be closed at any time
        if (input.wasPressed(Keys.ESCAPE)){
            Window.close();
        }

        // performing transition between level0 and level1
        if (!lv0Started && lv1Started){
            if (!fae.getLvChangeTimer().TimerEnded()) {
                MESSAGE_FONT.drawString(LV0_END_MESSAGE, calculateCentre().x - MESSAGE_FONT.getWidth(WIN_MESSAGE)/2,
                        calculateCentre().y + ((double)MESSAGE_FONT_SIZE)/2);
                fae.getLvChangeTimer().incrementFrameCount();
            }
            else{
                lv1.update(input);
            }
        }

        // identify when the player lose/win the game and bring the game to the end screen
        else if (fae.calculateHealthPercentage() == ZERO_HEALTH){
            // make sure the message is centred using the window size and the dimensions of the message
            MESSAGE_FONT.drawString(LOSE_MESSAGE, calculateCentre().x - MESSAGE_FONT.getWidth(LOSE_MESSAGE)/2,
                    calculateCentre().y + ((double)MESSAGE_FONT_SIZE)/2);
        }
        // identify when level0 is finished
        else if (fae.topLeft().x >= ENDPOINT_X && fae.topLeft().y >= ENDPOINT_Y){
            // make sure the message is centred using the window size and the dimensions of the message
            MESSAGE_FONT.drawString(LV0_END_MESSAGE, calculateCentre().x - MESSAGE_FONT.getWidth(WIN_MESSAGE)/2,
                    calculateCentre().y + ((double)MESSAGE_FONT_SIZE)/2);
            if (lv1Started == false){
                fae.getLvChangeTimer().resetTimer();
                lv1Started = true;
                lv0Started = false;
            }
        }

        // if the game is not started by the user, show only the start screen with the title and instructions
        else if (!lv0Started){
            MESSAGE_FONT.drawString(GAME_TITLE, TITLE_X, TITLE_Y);
            INSTRUCTION_FONT.drawString(LV0_INSTRUCTION1, TITLE_X+X_DIFF, TITLE_Y+Y_DIFF);
            INSTRUCTION_FONT.drawString(LV0_INSTRUCTION2, TITLE_X+X_DIFF, TITLE_Y+Y_DIFF+LINE_SPACING);
            // capture the input required to start the game
            if (input.wasPressed(Keys.SPACE)) {
                lv0Started = true;
            }
        }
        // when the level0 is started, update the whole window by detecting the player's movement and the entities
        else if (lv0Started){
            fae = lv0.update(input);
        }
    }
}
