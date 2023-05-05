import bagel.Keys;
import bagel.Image;
import bagel.Input;
import bagel.util.Point;
import java.util.ArrayList;
import bagel.util.Rectangle;

/**
 * child class of the Character class
 */
public class Player extends Character implements Drawable{

    public static final int MAX_HEALTH = 100;
    public static final int DAMAGE = 20;
    private final static int STEP_SIZE = 2;
    private static final double HEALTH_BAR_X = 20;
    private static final double HEALTH_BAR_Y = 25;
    private static final int HEALTH_BAR_FONT_SIZE = 30;
    private String direction = "Right";

    public final static Image PLAYER_LEFT_IMAGE = new Image("res/fae/faeLeft.png");
    public final static Image PLAYER_RIGHT_IMAGE = new Image("res/fae/faeRight.png");
    public final static Image PLAYER_ATTACK_LEFT_IMAGE = new Image("res/fae/faeAttackLeft.png");
    public final static Image PLAYER_ATTACK_RIGHT_IMAGE = new Image("res/fae/faeAttackRight.png");

    private boolean hasCollision = false;
    private Timer attackTimer = new Timer(Timer.ATTACK_PERIOD);
    private Timer coolDownTimer = new Timer(Timer.COOLDOWN_PERIOD);
    private Timer lvChangeTimer = new Timer(Timer.LV_CHANGE_PERIOD);
    private HealthBar healthBar = new HealthBar(new Point(HEALTH_BAR_X, HEALTH_BAR_Y),
                                                String.valueOf(MAX_HEALTH), HEALTH_BAR_FONT_SIZE);

    public Player(Point tl, Image image) {
        super(tl, image);
        this.setHealth(MAX_HEALTH);
    }

    public void setDirection(String direction) {
        if (!this.direction.equals(direction)) {
            this.direction = direction;
        }
    }

    public HealthBar getHealthBar(){
        return healthBar;
    }

    public Timer getAttackTimer(){
        return attackTimer;
    }

    public Timer getCoolDownTimer(){
        return coolDownTimer;
    }

    public Timer getLvChangeTimer(){
        return lvChangeTimer;
    }

    /**
     * calculate player's health as a percentage
     * @return player's health as a percentage
     */
    public double calculateHealthPercentage(){
        return (Double.valueOf(getHealth())/MAX_HEALTH) * 100;
    }

    /**
     * record the position fae should move to according to the keyboard input,and also the direction she should
     * be facing at the moment sp tht the right image for her is selected
     * @param input input from user of where the player should move
     * @return the new coordinates of the top-left corner of the player according to the input
     */
    public Point newPosition(Input input){
        Point newtl;
        if (input.isDown(Keys.LEFT)) {
            this.setDirection("Left");
            newtl = new Point(this.topLeft().x-STEP_SIZE, this.topLeft().y);
        } else if (input.isDown(Keys.RIGHT)) {
            this.setDirection("Right");
            newtl = new Point(this.topLeft().x+STEP_SIZE, this.topLeft().y);
        } else if (input.isDown(Keys.UP)) {
            newtl = new Point(this.topLeft().x, this.topLeft().y-STEP_SIZE);
        } else if (input.isDown(Keys.DOWN)) {
            newtl = new Point(this.topLeft().x, this.topLeft().y+STEP_SIZE);
        }
        else{
            newtl = new Point(this.topLeft().x, this.topLeft().y);
        }
        return newtl;
    }

    /**
     * check if the player will collide with an entity or not and draw out the entities at the same time
     * @param lv indicate which level the player is in
     * @param obstacles a list of the entities the player might run into in the current level
     * @return boolean value indicating if the player will collide with any entity or not
     */
    public boolean CheckCollisionAndDraw(int lv, ArrayList<Obstacle> obstacles){
        boolean collision = false;
        for (Obstacle o: obstacles){
            // draw walls in level0 and trees in level1
            if (lv == ShadowDimension.LEVEL_0)
                o.drawImage(Obstacle.WALL_IMAGE);
            else if (lv == ShadowDimension.LEVEL_1)
                o.drawImage(Obstacle.TREE_IMAGE);
            if (new Rectangle(this.topLeft(), PLAYER_RIGHT_IMAGE.getWidth(),
                    PLAYER_RIGHT_IMAGE.getHeight()).intersects(o)) {
                collision = true;
            }
        }
        return collision;
    }

    /**
     * check if the player encounters a sinkhole or not
     * @param sinkholes the lsit of sinkholes present on the current level
     * @return boolean value indicating if the player will step onto a sinkhole
     */
    public boolean checkHarmfulCollision(ArrayList<Sinkhole> sinkholes){
        boolean collision = false;
        int collisionIndex = sinkholes.size();
        for (Sinkhole s:sinkholes){
            s.drawImage(Sinkhole.SINKHOLE_IMAGE);
            if (new Rectangle(this.topLeft(), Player.PLAYER_RIGHT_IMAGE.getWidth(),
                    Player.PLAYER_RIGHT_IMAGE.getHeight()).intersects(s)) {
                collisionIndex = sinkholes.indexOf(s);
                collision = true;
            }
        }
        // if player steps onto a sinkhole, we will need to remove the sinkhole from now on so it will appear
        // from the screen
        if(collision) {
            sinkholes.remove(collisionIndex);
            sinkholes.trimToSize();
        }
        return collision;
    }

    /**
     * check if player is attacked by the enemies
     * @param fireTl the top left point of the enemy's fire
     * @return boolean value of whether the player is attacked by the enemy
     */
    public boolean checkEnemyAttacked(Point fireTl, String enemyType){
        if(enemyType.equals("Demon"))
            return this.intersects(new Rectangle(fireTl, Demon.FIRE_IMAGE.getWidth(), Demon.FIRE_IMAGE.getHeight()));
        else
            return this.intersects(new Rectangle(fireTl, Navec.FIRE_IMAGE.getWidth(), Navec.FIRE_IMAGE.getHeight()));
    }

    /**
     * update cooldown timer by either increment the frame count or reset timer and remove the state from player
     * to indicate the end of the cooldown period
     */
    public void updateCoolDownState(){
        if (!this.getCoolDownTimer().TimerEnded())
            this.getCoolDownTimer().incrementFrameCount();
        else if (this.getCoolDownTimer().TimerEnded()){
            this.getCoolDownTimer().resetTimer();
            this.removeStatus("COOLDOWN");
        }
    }

    /**
     * see if player in the attack state actually have caused damage to enemies, and make attack if it has
     * @param demons the list of demons that are present in the game
     * @param navec navec in the game
     */
    public void checkAndMakeAttack(ArrayList<Demon> demons, Navec navec){
        if (this.getStatus().contains("ATTACK")){
            // if the attack state hasn't ended, check if the player hits an enemy
            if (!this.getAttackTimer().TimerEnded()) {
                this.getAttackTimer().incrementFrameCount();
                // if in level1, update information of the demon that's attacked/navec
                if(demons != null && navec != null){
                    for (Demon d:demons){
                        if(this.intersects(d)) {
                            // player can only cause damage to the demon if it's not invincible
                            if (!d.getStatus().contains("INVINCIBLE")){
                                makeAttack(d);
                            }
                        }
                    }
                    if (this.intersects(navec)){
                        // player can only cause damage to the demon if it's not invincible
                        if (!navec.getStatus().contains("INVINCIBLE")){
                            makeAttack(navec);
                        }
                    }
                }
                // no matter which level we are in, if the player is in cooldown state, always update player's
                // cooldown timer
                if(this.getStatus().contains("COOLDOWN"))
                    this.updateCoolDownState();
            }
            // if player has finished it's attack period, player will start their cooldown period
            else if (this.getAttackTimer().TimerEnded()){
                this.getAttackTimer().resetTimer();
                this.removeStatus("ATTACK");
                this.addStatus("COOLDOWN");
            }
        }
        // if player is not in attack state but is in cooldown state, update player's cooldown timer
        else if (this.getStatus().contains("COOLDOWN"))
            this.updateCoolDownState();
    }

    /**
     * make attack to the enemy through updating relative information of the enemy and print out damage log
     * @param demon
     */
    public void makeAttack(Enemy demon){
        // update enemy's healthbar value
        demon.setHealth(demon.getHealth() - Player.DAMAGE);
        demon.getHealthBar().setHealthPercentage(demon.calculateHealthPercentage());
        // print damage log
        demon.printAttackLog(demon, Player.DAMAGE);
        // update the status of the demon by adding an invincible state
        demon.addStatus("INVINCIBLE");
        demon.getInvincibleTimer().incrementFrameCount();
    }

    /**
     * print out the damage log of an attack by the attacker on player
     * @param attacker the attacker
     * @param damage damage brought by the attacker
     * @param <T> generic type to allow different types of the attacker
     */
    public <T extends Enemy>void printAttackLog(T attacker, int damage){
        System.out.println(attacker + " inflicts " + String.valueOf(damage) +
                " damage points on Fae. Fae's current health: " + getHealth() +
                "/" + MAX_HEALTH);
    }

    /**
     * print out the damage log of an attack by the sinkhole on player
     */
    public void printSinkholeLog(){
        System.out.println( "Sinkhole inflicts " + Sinkhole.DAMAGE +
                " damage points on Fae. Fae's current health: " + getHealth() +
                "/" + MAX_HEALTH);
    }

    /**
     * draw the player with the correct image depending on the direction he/she is facing
     * @param status the status the player is currently in
     */
    public void drawImage(ArrayList<String> status){
        if (status.contains("ATTACK")){
            if (this.direction.equals("Left"))
                PLAYER_ATTACK_LEFT_IMAGE.drawFromTopLeft(this.topLeft().x, this.topLeft().y);
            else if (this.direction.equals("Right"))
                PLAYER_ATTACK_RIGHT_IMAGE.drawFromTopLeft(this.topLeft().x, this.topLeft().y);
        }
        else {
            if (this.direction.equals("Left"))
                PLAYER_LEFT_IMAGE.drawFromTopLeft(this.topLeft().x, this.topLeft().y);
            else if (this.direction.equals("Right"))
                PLAYER_RIGHT_IMAGE.drawFromTopLeft(this.topLeft().x, this.topLeft().y);
        }
    }
}
