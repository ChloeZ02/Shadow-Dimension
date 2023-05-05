import bagel.Image;
import bagel.Input;
import bagel.util.Point;
import bagel.util.Rectangle;
import bagel.util.Vector2;
import java.util.ArrayList;

/**
 * child class of parent class Enemy
 */
public class Navec extends Enemy implements Drawable{
    public static final int MAX_HEALTH = 80;
    public final static int DAMAGE = 20;
    private boolean isAttacked = false;
    private static final int ATTACK_RANGE = 200;
    private static final int HEALTHBAR_HEIGHT_DIFF = 6;
    public final static Image FIRE_IMAGE = new Image("res/navec/navecFire.png");
    public final static Image LEFT_IMAGE = new Image("res/navec/navecLeft.png");
    public final static Image RIGHT_IMAGE = new Image("res/navec/navecRight.png");
    public final static Image INVINCIBLE_LEFT_IMAGE = new Image("res/navec/navecInvincibleLeft.PNG");
    public final static Image INVINCIBLE_RIGHT_IMAGE = new Image("res/navec/navecInvincibleRight.PNG");

    public Navec(Point tl, Image image){
        super(tl, image, FIRE_IMAGE, ATTACK_RANGE, MAX_HEALTH);
        this.setIsAggressive(true);
        assignDirection();
        assignSpeed();
    }

    /**
     * Performs a state update.
     */
    public void update(ArrayList<Obstacle> trees, ArrayList<Sinkhole> sinkholes,
                       Player fae, Point tl, Point br, Input input){
        Point newtl;
        newtl = new Point(this.topLeft().x + this.getDirection().x * getSpeed(),
                this.topLeft().y + this.getDirection().y * getSpeed());
        Navec newNavec = copyNavec(this);
        newNavec.moveTo(newtl);

        // checking if navec has collided or not and update the new position & direction of navec accordingly
        if (newNavec.CheckCollision(trees) || newNavec.CheckCollision(sinkholes) ||
                newNavec.CheckBorderCollision(tl, br)) {
            newtl = this.changeDirection();
        }
        this.moveTo(newtl);

        // update the health bar of navec
        this.setHealthBar(new HealthBar(new Point(this.topLeft().x, this.topLeft().y-HEALTHBAR_HEIGHT_DIFF),
                String.valueOf(this.calculateHealthPercentage()), Enemy.HEALTH_BAR_FONT_SIZE));
        this.getHealthBar().printHealthBar();

        // check if navec has cause damage to player or not and let player respond to the damage if it occurred
        Point fireTl = checkAndMakeAttack(fae.centre());
        if (fireTl != null && !fae.getStatus().contains("INVINCIBLE")) {
            isAttacked = fae.checkEnemyAttacked(fireTl, this.toString());
            if (isAttacked){
                fae.setHealth(fae.getHealth() - Navec.DAMAGE);
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
     * make an exact copy of a navec by copying over all the correct information of the template navec
     * @param navec the template navec we want to copy
     * @return the copy of the template navec, a new navec object
     */
    public Navec copyNavec(Navec navec){
        Navec newNavec = new Navec(navec.topLeft(), RIGHT_IMAGE);
        newNavec.setIsAggressive(navec.getIsAggressive());
        newNavec.setDirection(navec.getDirection());
        newNavec.setSpeed(navec.getSpeed());
        newNavec.setStatus(navec.getStatus());
        return newNavec;
    }

    /**
     * check if navec collides with any entities
     * @param obstacles a list of entities that navec might collide with
     * @return whether or not navec has collided
     * @param <T> generic type for the entities/obstacles navec might encounter
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
     * draw navec on the screen depending on its state and current direction
     * @param status to keep track of which state navec is in so correct image is rendered
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
     * allow navec to print out it's class name when it's asked to be printed
     * @return class name
     */
    public String toString(){
        return "Navec";
    }
}
