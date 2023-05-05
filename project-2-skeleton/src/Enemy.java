import bagel.Image;
import bagel.util.Point;
import bagel.util.Vector2;
import java.util.ArrayList;
import java.util.Random;

/**
 * abstract parent class for Demon and Navec classes
 */
public abstract class Enemy extends Character implements Drawable{

    private static final double MAX_SPEED = 0.7;
    private static final double MIN_SPEED = 0.2;
    private Vector2 direction;
    private Fire fire = new Fire();
    private boolean isAggressive;
    private HealthBar healthBar;
    private static int timeScale = 0;
    private double speed = 0;
    private static final int MAX_TIME_SCALE = 3;
    private static final int MIN_TIME_SCALE = -3;
    private static final int POSSIBLE_DIRECTIONS = 4;
    private static final int LEFT = 0;
    private static final int RIGHT = 1;
    private static final int UP = 2;
    private static final int DOWN = 3;
    public static final int HEALTH_BAR_FONT_SIZE = 15;
    private final Image FIRE_IMAGE;
    private final int ATTACK_RANGE;
    private final int MAX_HEALTH;

    public Enemy (Point tl, Image image, Image fireImage, int attackRange, int maxHealth){
        super(tl, image);
        this.FIRE_IMAGE = fireImage;
        this.ATTACK_RANGE = attackRange;
        this.MAX_HEALTH = maxHealth;
        this.setHealth(maxHealth);
        this.setStatus(new ArrayList<>());
        this.getStatus().add("Attack");
    }

    public Vector2 getDirection(){
        return direction;
    }

    public void setDirection(Vector2 direction){
        this.direction = direction;
    }

    public double getSpeed(){
        return speed;
    }

    public void setSpeed(double speed){
        this.speed = speed;
    }

    public boolean getIsAggressive(){return this.isAggressive;}
    public void setIsAggressive(boolean isAggressive){this.isAggressive = isAggressive;}
    public HealthBar getHealthBar(){return this.healthBar;}
    public void setHealthBar(HealthBar healthBar){this.healthBar = healthBar;}
    public static int getTimeScale(){
        return timeScale;
    }


    /**
     * randomly assign direction to the demon
     */
    public void assignDirection(){
        int direction = new Random().nextInt(POSSIBLE_DIRECTIONS);
        if (direction == LEFT)
            this.setDirection(Vector2.left);
        else if (direction == RIGHT)
            this.setDirection(Vector2.right);
        else if (direction == UP)
            this.setDirection(Vector2.up);
        else if (direction == DOWN)
            this.setDirection(Vector2.down);
    }

    /**
     * change the enemy's direction of moving by setting its direction to the opppsite direction
     * @return the top left point of the enemy after moving in the new direction
     */
    public Point changeDirection(){
        if(this.getDirection() == Vector2.right)
            this.setDirection(Vector2.left);
        else if (this.getDirection() == Vector2.left)
            this.setDirection(Vector2.right);
        else
            this.setDirection(new Vector2(-this.getDirection().x, -this.getDirection().y));
        return new Point(this.topLeft().x + this.getDirection().x * getSpeed(),
                this.topLeft().y + this.getDirection().y * getSpeed());
    }

    /**
     * assign a random speed to the enemy within the range
     */
    public void assignSpeed(){
        this.speed = Math.random()*(MAX_SPEED - MIN_SPEED) + MIN_SPEED;
    }

    /**
     * calculate enemy's health as a percentage
     * @return enemy's health as a percentage
     */
    public double calculateHealthPercentage(){
        return (Double.valueOf(getHealth())/MAX_HEALTH) * 100;
    }

    /**
     * allow enemies to be able to detect player when it comes into the attack range, and display the fire in the
     * right position
     * @param faeC the centre point of the player
     */
    public Point checkAndMakeAttack(Point faeC){
        Point enemyC = new Point(this.centre().x, this.centre().y);
        Point enemyTl = new Point(this.topLeft().x, this.topLeft().y);
        Point enemyTr = new Point(this.topRight().x, this.topRight().y);
        Point fireTl = null;
        // the player needs to be within the attack range to allow the attack to happen
        if (enemyC.distanceTo(faeC) <= ATTACK_RANGE){
            // player is in the top-left corner relative to the enemy
            if (faeC.x <= enemyC.x && faeC.y <= enemyC.y){
                fireTl = new Point(enemyTl.x-FIRE_IMAGE.getWidth(), enemyTl.y-FIRE_IMAGE.getHeight());
                fire.drawImage(fireTl, FIRE_IMAGE, "TopLeft");
            }
            // player is in the bottom-left corner relative to the enemy
            else if (faeC.x <= enemyC.x && faeC.y > enemyC.y){
                fireTl = new Point(enemyTl.x-FIRE_IMAGE.getWidth(), enemyTl.y+FIRE_IMAGE.getHeight());
                fire.drawImage(fireTl, FIRE_IMAGE, "BottomLeft");
            }
            // player is in the top-right corner relative to the enemy
            else if (faeC.x > enemyC.x && faeC.y <= enemyC.y){
                fireTl = new Point(enemyTr.x, enemyTl.y-FIRE_IMAGE.getHeight());
                fire.drawImage(fireTl, FIRE_IMAGE, "TopRight");
            }
            // player is in the bottom-right corner relative to the enemy
            else if (faeC.x > enemyC.x && faeC.y > enemyC.y){
                fireTl = new Point(enemyTr.x, enemyTl.y+FIRE_IMAGE.getHeight());
                fire.drawImage(fireTl, FIRE_IMAGE, "BottomRight");
            }
        }
        return fireTl;
    }

    /**
     * print out the damage log of an attack by the enemy to the attacked(player)
     * @param attacked the attacker
     * @param damage damage brought by the attacker
     * @param <T> generic type to allow different types of those attacked
     */
    public <T extends Enemy>void printAttackLog(T attacked, int damage){
        System.out.println("Fae inflicts " + String.valueOf(damage) +
                " damage points on " + attacked + ". " + attacked + "'s current health: " + getHealth() +
                "/" + MAX_HEALTH);
    }

    /**
     * change timescale of the game hence to allow speed changes
     * @param change to take the order of whether user wants speed up or speed down
     * @return boolena value indicating whether the timescale is changed or has reached the bounds hence not changed
     */
    public static boolean changeTimeScale(int change){
        int newTimeScale = timeScale + change;
        boolean hasChanged = false;
        if ( MIN_TIME_SCALE <= newTimeScale && newTimeScale <= MAX_TIME_SCALE) {
            timeScale = newTimeScale;
            hasChanged = true;
        }
        return hasChanged;
    }

    /**
     * speed up all enemies
     */
    public void speedUp(){
        this.setSpeed(this.getSpeed() * 2);
    }

    /**
     * speed down all enemies
     */
    public void speedDown(){
        this.setSpeed(this.getSpeed() / 2);
    }

}
