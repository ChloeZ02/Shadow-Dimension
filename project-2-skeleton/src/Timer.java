/**
 * Class Timer used to keep track of characters' current status
 */
public class Timer {
    private static final int REFRESH_RATE = 60;
    public static final int INVINCIBLE_PERIOD = 3;
    private static final int INITIAL_FRAME_COUNT = 0;
    public static final int ATTACK_PERIOD = 1;
    public static final int COOLDOWN_PERIOD = 2;
    public static final int LV_CHANGE_PERIOD =3;
    private int requiredFrames;
    private int frameCount=0;

    public Timer(int timeLength){
        this.requiredFrames = timeLength * REFRESH_RATE;
    }

    /**
     * keep track of whether the timer has ended its session or not
     * @return boolean value of whether the required time period is over
     */
    public boolean TimerEnded(){
        return this.frameCount == requiredFrames;
    }

    /**
     * increment the frame count that acts as the progression of the timer
     */
    public void incrementFrameCount(){
        this.frameCount++;
    }

    /**
     * reset frame count to zero for next use
     */
    public void resetTimer(){
        this.frameCount = INITIAL_FRAME_COUNT;
    }
}
