import bagel.Image;
import bagel.Input;
import bagel.Keys;
import bagel.Window;
import bagel.util.Point;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

public class Lv0 {
    private final Image BACKGROUND_IMAGE = new Image("res/background0.png");
    public final static int LEVEL_0 = 0;
    private final static int ENTITY_INFO_LENGTH = 3;
    private Player fae;
    private Point tlBorder;
    private Point brBorder;
    private ArrayList<Obstacle> walls = new ArrayList<Obstacle>();
    private ArrayList<Sinkhole> lv0sinkholes = new ArrayList<Sinkhole>();

    public Lv0(){
        lv0readCSV();
    }

    public Player getFae(){return fae;}

    /**
     * Method used to read file and create objects for level0
     */
    private void lv0readCSV(){
        try (BufferedReader br = new BufferedReader(new FileReader("res/level0.csv"))) {
            String startInfo;
            while ((startInfo = br.readLine()) != null) {
                String[] entityInfo= new String[ENTITY_INFO_LENGTH];
                entityInfo = startInfo.split(",");
                String type = entityInfo[0];
                int tlx = Integer.parseInt(entityInfo[1]);
                int tly = Integer.parseInt(entityInfo[2]);

                // read in and create Wall objects
                if (type.equals("Wall")){
                    walls.add(new Obstacle(new Point(tlx, tly), Obstacle.WALL_IMAGE));
                }
                // read in and create Sinkhole objects
                else if (type.equals("Sinkhole")){
                    lv0sinkholes.add(new Sinkhole(new Point(tlx, tly), Sinkhole.SINKHOLE_IMAGE));
                }
                // read in and create Player object(fae)
                else if (type.equals("Fae")){
                    fae = new Player(new Point(tlx, tly), Player.PLAYER_RIGHT_IMAGE);
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
     * Performs a state update.
     * @param input the input read from user pressing a certain key on the keyboard
     */
    public Player update(Input input){
        boolean collision = false;
        Point newtl;
        BACKGROUND_IMAGE.draw(Window.getWidth()/2.0, Window.getHeight()/2.0);
        fae.getHealthBar().printHealthBar();

        // allow player to be able to formally attack on level0
        if(input.wasPressed(Keys.A)){
            if (!fae.getStatus().contains("ATTACK") && !fae.getStatus().contains("COOLDOWN")) {
                fae.getStatus().add("ATTACK");
            }
        }
        fae.checkAndMakeAttack(null, null);

        // know where Fae is supposed to go using the input, then check if she will collide with anything or not
        newtl = fae.newPosition(input);
        Player newFae = new Player(newtl, Player.PLAYER_RIGHT_IMAGE);
        if(newFae.CheckCollisionAndDraw(LEVEL_0, walls) || newFae.CheckBorderCollision(tlBorder, brBorder))
            collision = true;
        if(newFae.checkHarmfulCollision(lv0sinkholes)){
            fae.setHealth(fae.getHealth() - Sinkhole.DAMAGE);
            fae.getHealthBar().setHealthPercentage(fae.calculateHealthPercentage());
            fae.printSinkholeLog();
        }

        /*
        if a collision has occurred, fae would not be moved to the new position, she would stay where she
        previously was till she stops colliding(changes direction)
        */
        if (!collision)
            fae.moveTo(newtl);
        fae.drawImage(fae.getStatus());

        return fae;
    }
}
