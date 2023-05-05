import bagel.Image;
import bagel.util.Point;
import bagel.util.Rectangle;

import java.util.ArrayList;

/**
 * abstract parent class for Enemy class and Player class
 */
public abstract class Character extends Rectangle implements Drawable{

    private int health;
    private ArrayList<String> status = new ArrayList<>();
    private Timer invincibleTimer = new Timer(Timer.INVINCIBLE_PERIOD);

    public Character(Point tl, Image image){
        super(tl, image.getWidth(), image.getHeight());
    }

    public ArrayList<String> getStatus(){
        return status;
    }

    public void setStatus(ArrayList<String> status){
        this.status = status;
    }

    public int getHealth(){
        return this.health;
    }
    public void setHealth(int health){
        if (health >=0)
            this.health = health;
        else
            this.health = 0;
    }

    public Timer getInvincibleTimer(){
        return invincibleTimer;
    }

    /**
     * add a status to the player
     * @param status the status we want to add to the player
     */
    public void addStatus(String status){
        this.status.add(status);
    }

    /**
     * remove a status from the player
     * @param status the status we want to remove from the player
     */
    public void removeStatus(String status){
        this.status.remove(status);
        this.status.trimToSize();
    }

    /**
     * check if the player is going out of the border
     * @param tl top left point indicating the upper and left hand side border
     * @param br bottom right point indicating the lower and right hand side border
     * @return boolean value indicating if the player will hit the border
     */
    public boolean CheckBorderCollision(Point tl, Point br){
        boolean collision = false;
        if(this.topLeft().x < tl.x || this.topLeft().y < tl.y ||
                this.topLeft().x > br.x || this.topLeft().y > br.y)
            collision = true;
        return collision;
    }

    /**
     * update invincible timer by either increment the frame count or reset timer and remove the state from player
     * to indicate the end of the invincible period
     */
    public void updateInvincibleState(){
        if (this.getStatus().contains("INVINCIBLE")){
            if (!this.getInvincibleTimer().TimerEnded()) {
                this.getInvincibleTimer().incrementFrameCount();
            }
            else {
                this.getInvincibleTimer().resetTimer();
                this.removeStatus("INVINCIBLE");
            }
        }
    }
    /**
     * draw the character on the screen
     * @param image the required image of the character based on its status and direction of moving
     */
    public void drawImage(Image image){
        image.drawFromTopLeft(this.topLeft().x, this.topLeft().y);
    }
}
