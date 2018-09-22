package com.github.shynixn.ball.api.bukkit.event;

import com.github.shynixn.ball.api.business.proxy.BallProxy;
import org.bukkit.util.Vector;

public class BallPostMoveEvent extends BallEvent{
    
    private Vector velocity;
    private final boolean moving;
    
    /**
     * Initializes a new ball event.
     *
     * @param ball ball
     */
    public BallPostMoveEvent(BallProxy ball, Vector velocity, int times) {
        super(ball);
        this.velocity = velocity;
        this.moving = (times > 0);
    }
    
    public Vector getVelocity() {
        return this.velocity;
    }
    
    public void setVelocity(Vector velocity) {
        this.velocity = velocity;
    }
    
    public boolean isMoving() {
        return this.moving;
    }
}
