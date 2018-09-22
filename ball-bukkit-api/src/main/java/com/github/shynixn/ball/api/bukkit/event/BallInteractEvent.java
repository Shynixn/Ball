package com.github.shynixn.ball.api.bukkit.event;

import com.github.shynixn.ball.api.business.proxy.BallProxy;
import org.bukkit.entity.Entity;

/**
 * Event which gets called when an entity interacts with the ball.
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
public class BallInteractEvent extends BallCancelableEvent {
    private final Entity entity;

    /**
     * Initializes a new ball kick event with the given entity interacting with the ball.
     *
     * @param ball   ball
     * @param entity entity
     */
    public BallInteractEvent(BallProxy ball, Entity entity) {
        super(ball);
        this.entity = entity;
    }

    /**
     * Returns the entity interacting with the ball.
     *
     * @return entity
     */
    public Entity getEntity() {
        return this.entity;
    }
}
