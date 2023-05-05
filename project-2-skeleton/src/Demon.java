import bagel.Image;
import bagel.Input;
import bagel.util.Point;
import bagel.util.Rectangle;
import bagel.util.Vector2;
import java.util.ArrayList;
import java.util.Random;

/**
 * Demon class, a child class of the Enemy class
 */
public class Demon extends Enemy implements Drawable{
    public static final int MAX_HEALTH = 40;
    private static final int TOTAL_TYPES = 2;
    private static final int AGGRESIVE = 1;
    private static final int PASSIVE = 0;
    public final static int DAMAGE = 10;
    private static final int HEALTHBAR_HEIGHT_DIFF = 6;
    private boolean isAttacked = false;
    private static final int ATTACK_RANGE = 150;
    public final static Image FIRE_IMAGE = new Image("res/demon/demonFire.png");
    public final static Image LEFT_IMAGE = new Image("res/demon/demonLeft.png");
    public final static Image RIGHT_IMAGE = new Image("res/demon/demonRight.png");
    public final static Image INVINCIBLE_LEFT_IMAGE = new Image("res/demon/demonInvincibleLeft.PNG");
    public final static Image INVINCIBLE_RIGHT_IMAGE = new Image("res/demon/demonInvincibleRight.PNG");

    public Demon(Point tl, Image image){
        super(tl, image, FIRE_IMAGE, ATTACK_RANGE, MAX_HEALTH);
        assignType();
        assignDirection();
        if (this.getIsAggressive())
            assignSpeed();
    }

    /**
     * randomly assign the type of the demon
     */
    public void assignType(){
        int type = new Random().nextInt(TOTAL_TYPES);
        if (type == AGGRESIVE) {
            this.setIsAggressive(true);
        }
        else if (type == PASSIVE)
            this.setIsAggressive(false);
    }

    /**
     * Performs a state update.
     */
    public void update(ArrayList<Obstacle> trees, ArrayList<Sinkhole> sinkholes,
                       Player fae, Point tl, Point br, Input input){
        Point newtl;
        newtl = new Point(this.topLeft().x + this.getDirection().x * getSpeed(),
                          this.topLeft().y + this.getDirection().y * getSpeed());
        Demon newDemon = copyDemon(this);
        newDemon.moveTo(newtl);

        // checking if the demon has collided or not and update the new position&direction of the demon accordingly
        if (newDemon.CheckCollision(trees) || newDemon.CheckCollision(sinkholes) ||
                newDemon.CheckBorderCollision(tl, br)) {
            newtl = this.changeDirection();
        }
        this.moveTo(newtl);

        // update the health bar of the demon
        this.setHealthBar(new HealthBar(new Point(this.topLeft().x, this.topLeft().y-HEALTHBAR_HEIGHT_DIFF),
                String.valueOf(this.calculateHealthPercentage()), Enemy.HEALTH_BAR_FONT_SIZE));
        this.getHealthBar().printHealthBar();

        // check if the demon has cause damage to player or not and let player respond to the damage if it occurred
        Point fireTl = checkAndMakeAttack(fae.centre());
        if (fireTl != null && !fae.getStatus().contains("INVINCIBLE")) {
            isAttacked = fae.checkEnemyAttacked(fireTl, this.toString());
            if (isAttacked){
                fae.setHealth(fae.getHealth() - Demon.DAMAGE);
                fae.getHealthBar().setHealthPercentage(fae.calculateHealthPercentage());
                fae.printAttackLog(this, this.DAMAGE);
                fae.addStatus("INVINCIBLE");
            }
        }

        // keep track of its invincible status
        this.updateInvincibleState();
        drawImage(this.getStatus());
    }

    /**
     * make an exact copy of a demon by copying over all the correct information of the template demon
     * @param demon the template demon we want to copy
     * @return the copy of the template demon, a new demon object
     */
    public Demon copyDemon(Demon demon){
        Demon newDemon = new Demon(demon.topLeft(), RIGHT_IMAGE);
        newDemon.setIsAggressive(demon.getIsAggressive());
        newDemon.setDirection(demon.getDirection());
        newDemon.setSpeed(demon.getSpeed());
        newDemon.setStatus(demon.getStatus());
        return newDemon;
    }

    /**
     * check if demon collides with any entities
     * @param obstacles a list of entities that demons might collide with
     * @return whether or not the demon collided
     * @param <T> generic type for the entities/obstacles demons might encounter
     */
    public <T extends Entity & Drawable > boolean CheckCollision(ArrayList<T> obstacles){
        boolean collision = false;
        for (T o: obstacles){
            if (new Rectangle(this.topLeft(), RIGHT_IMAGE.getWidth(),
                    RIGHT_IMAGE.getHeight()).intersects(o)) {
                collision = true;
            }
        }
        return collision;
    }

    /**
     * draw the demon on the screen depending on its state and current direction
     * @param status to keep track of which state the demon is in so correct image is rendered
     */
    public void drawImage(ArrayList<String> status){
        if(status.contains("INVINCIBLE")){
            if (this.getDirection() == Vector2.left)
                INVINCIBLE_LEFT_IMAGE.drawFromTopLeft(this.topLeft().x, this.topLeft().y);
            else
                INVINCIBLE_RIGHT_IMAGE.drawFromTopLeft(this.topLeft().x, this.topLeft().y);
        }
        else if (status.contains("Attack")){
            if (this.getDirection() == Vector2.left)
                LEFT_IMAGE.drawFromTopLeft(this.topLeft().x, this.topLeft().y);
            else
                RIGHT_IMAGE.drawFromTopLeft(this.topLeft().x, this.topLeft().y);
        }
    }

    /**
     * allow every demon to print out it's class name when it's asked to be printed
     * @return class name
     */
    public String toString(){
        return "Demon";
    }
}
