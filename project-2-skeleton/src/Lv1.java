import bagel.*;
import bagel.util.Point;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

/**
 * a class created for level1, package all unique methods and attribute into the class
 */
public class Lv1 {
    private final static int LEVEL_1 = 1;
    private final static double LV1_INSTRUCTION1_X = 350;
    private final static double LV1_INSTRUCTION1_Y = 350;
    private final static int INSTRUCTION_FONT_SIZE = 40;
    private final static int MESSAGE_FONT_SIZE = 75;
    private final static double LINE_SPACING = 50; // set the spacing between lines to allow readability
    private final static int ENTITY_INFO_LENGTH = 3;
    private static final int INCREASE_TIME_SCALE = 1;
    private static final int DECREASE_TIME_SCALE = -1;
    private static final int ZERO_HEALTH = 0;
    private final static String LV1_INSTRUCTION1 = "PRESS SPACE TO START";
    private final static String LV1_INSTRUCTION2 = "  PRESS A TO ATTACK";
    private final static String LV1_INSTRUCTION3 = "DEFEAT NAVEC TO WIN";
    private final static String WIN_MESSAGE = "CONGRATULATIONS";
    private final static String LOSE_MESSAGE = "GAME OVER!";
    private final Image BACKGROUND_IMAGE = new Image("res/background1.png");
    private final Font MESSAGE_FONT = new Font("res/frostbite.ttf", MESSAGE_FONT_SIZE);
    private final Font INSTRUCTION_FONT = new Font("res/frostbite.ttf", INSTRUCTION_FONT_SIZE);

    private boolean lv1Started = false;

    private Player fae;
    private Navec navec;
    private Point tlBorder;
    private Point brBorder;

    private ArrayList<Obstacle> trees = new ArrayList<Obstacle>();
    private ArrayList<Sinkhole> sinkholes = new ArrayList<Sinkhole>();
    private ArrayList<Demon> demons = new ArrayList<Demon>();

    public Lv1(){
        readCSV();
    }

    /**
     * Method used to read file and create objects for level1
     */
    private void readCSV(){
        try (BufferedReader br = new BufferedReader(new FileReader("res/level1.csv"))) {
            String startInfo;
            while ((startInfo = br.readLine()) != null) {
                String[] entityInfo= new String[ENTITY_INFO_LENGTH];
                entityInfo = startInfo.split(",");
                String type = entityInfo[0];
                int tlx = Integer.parseInt(entityInfo[1]);
                int tly = Integer.parseInt(entityInfo[2]);

                // read in and create Tree objects
                if (type.equals("Tree")){
                    trees.add(new Obstacle(new Point(tlx, tly), Obstacle.TREE_IMAGE));
                }
                // read in and create Sinkhole objects
                else if (type.equals("Sinkhole")){
                    sinkholes.add(new Sinkhole(new Point(tlx, tly), Sinkhole.SINKHOLE_IMAGE));
                }
                // read in and create Player object(fae)
                else if (type.equals("Fae")){
                    fae = new Player(new Point(tlx, tly), Player.PLAYER_RIGHT_IMAGE);
                }
                else if (type.equals("Demon")){
                    demons.add(new Demon(new Point(tlx, tly), Demon.RIGHT_IMAGE));
                }
                else if (type.equals("Navec")){
                    navec = new Navec(new Point(tlx, tly), Navec.RIGHT_IMAGE);
                }
                // read in and create a Point object specifying the top-left border
                else if (type.equals("TopLeft")){
                    tlBorder = new Point(tlx, tly);
                }
                // read in and create a Point object specifying the bottom-right border
                else if (type.equals("BottomRight")){
                    brBorder = new Point(tlx, tly);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
    public void update(Input input){
        boolean collision = false;
        Demon deadDemon = null;

        // display instructions when level1 has not started yet
        if (!lv1Started) {
            INSTRUCTION_FONT.drawString(LV1_INSTRUCTION1, LV1_INSTRUCTION1_X, LV1_INSTRUCTION1_Y);
            INSTRUCTION_FONT.drawString(LV1_INSTRUCTION2, LV1_INSTRUCTION1_X, LV1_INSTRUCTION1_Y + LINE_SPACING);
            INSTRUCTION_FONT.drawString(LV1_INSTRUCTION3, LV1_INSTRUCTION1_X, LV1_INSTRUCTION1_Y + LINE_SPACING + LINE_SPACING);
            // identify the time when the start screen of level1 should appear(level1 starts)
            if (input.wasPressed(Keys.SPACE)){
                lv1Started = true;
            }
        }
        // deal with the situation where player loses the game
        else if (fae.calculateHealthPercentage() == (double)ZERO_HEALTH){
            MESSAGE_FONT.drawString(LOSE_MESSAGE, calculateCentre().x - MESSAGE_FONT.getWidth(LOSE_MESSAGE)/2,
                    calculateCentre().y + ((double)MESSAGE_FONT_SIZE)/2);
        }
        // the player wins the game
        else if (navec.getHealth() <= ZERO_HEALTH){
            MESSAGE_FONT.drawString(WIN_MESSAGE, calculateCentre().x - MESSAGE_FONT.getWidth(WIN_MESSAGE)/2,
                    calculateCentre().y + ((double)MESSAGE_FONT_SIZE)/2);
        }
        // when level1 is started, update player and entities same as level0, and enemies will be updated as well
        else if (lv1Started) {
            Point newtl;
            BACKGROUND_IMAGE.draw(Window.getWidth() / 2.0, Window.getHeight() / 2.0);
            fae.getHealthBar().printHealthBar();

            newtl = fae.newPosition(input);
            Player newFae = new Player(newtl, Player.PLAYER_RIGHT_IMAGE);
            fae.updateInvincibleState();


            // fae attack first and then update the demons
            if (input.wasPressed(Keys.A)){
                if (!fae.getStatus().contains("ATTACK") && !fae.getStatus().contains("COOLDOWN")){
                    fae.addStatus("ATTACK");
                }
            }
            fae.checkAndMakeAttack(demons, navec);

            // check for collisions for the player same as level0
            if (newFae.CheckCollisionAndDraw(LEVEL_1, trees) || newFae.CheckBorderCollision(tlBorder, brBorder))
                collision = true;
            if (newFae.checkHarmfulCollision(sinkholes)) {
                fae.setHealth(fae.getHealth() - Sinkhole.DAMAGE);
                fae.getHealthBar().setHealthPercentage(fae.calculateHealthPercentage());
                fae.printSinkholeLog();
            }

            // update status of the enemies
            for (Demon d:demons)
                d.update(trees, sinkholes, fae, tlBorder, brBorder, input);
            navec.update(trees, sinkholes, fae, tlBorder, brBorder, input);

            // will get rid of the dead demon from the screen
            for (Demon d:demons) {
                if (d.getHealth() <= ZERO_HEALTH)
                    deadDemon = d;
            }
            demons.remove(deadDemon);
            demons.trimToSize();

            // speed up enemies
            if (input.wasPressed(Keys.L)) {
                if(Enemy.changeTimeScale(INCREASE_TIME_SCALE)){
                    System.out.println("Sped up, Speed: " + Enemy.getTimeScale());
                    for(Demon d:demons)
                        d.speedUp();
                    navec.speedUp();
                }
            }

            // speed down enemies
            else if (input.wasPressed(Keys.K)) {
                if (Enemy.changeTimeScale(DECREASE_TIME_SCALE)) {
                    System.out.println("Slowed down, Speed: " + Enemy.getTimeScale());
                    for(Demon d:demons)
                        d.speedDown();
                    navec.speedDown();
                }
            }

            // deal with collision same as level0
            if (!collision)
                fae.moveTo(newtl);
            fae.drawImage(fae.getStatus());
        }
    }
}
