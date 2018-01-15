package com.github.shynixn.ball.api.bukkit.business.event;

import com.github.shynixn.ball.api.bukkit.business.entity.BukkitBall;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

/**
 * Event which gets called when a ball collides with a wall.
 * <p>
 * Version 1.1
 * <p>
 * MIT License
 * <p>
 * Copyright (c) 2017 by Shynixn
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
public class BallWallCollideEvent extends BallCancelableEvent {
    private final Block block;
    private final BlockFace blockFace;
    private final Vector resultVector;
    private final Vector incomingVector;

    /**
     * Initializes a new ball collide event.
     * @param ball ball
     * @param block block
     * @param blockFace blockFace hitting the ball
     * @param resultVector vector of the ball after he hit the wall
     * @param incomingVector vector of the ball before he hit the wall
     */
    public BallWallCollideEvent(BukkitBall ball, Block block, BlockFace blockFace, Vector resultVector, Vector incomingVector) {
        super(ball);
        this.block = block;
        this.blockFace = blockFace;
        this.resultVector = resultVector;
        this.incomingVector = incomingVector;
    }

    /**
     * Returns the blockFace hitting the ball is hitting.
     * @return blockFace.
     */
    public BlockFace getBlockFace() {
        return this.blockFace;
    }

    /**
     * Returns the outgoing Vector which the ball has after he hit the wall.
     * @return vector
     */
    public Vector getResultVector() {
        return this.resultVector;
    }

    /**
     * Returns the incoming Vector which the ball had before he hit the wall-
     * @return vector
     */
    public Vector getIncomingVector() {
        return this.incomingVector;
    }

    /**
     * Returns the block the ball collided with.
     *
     * @return block
     */
    public Block getBlock() {
        return this.block;
    }
}
