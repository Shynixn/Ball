package com.github.shynixn.ball.api.bukkit.event;

import com.github.shynixn.ball.api.business.proxy.BallProxy;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * SubEvent of every Ball event.
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
public class BallEvent extends Event {
    private final static HandlerList handlers = new HandlerList();
    private final BallProxy ball;

    /**
     * Initializes a new ball event.
     *
     * @param ball ball
     */
    public BallEvent(BallProxy ball) {
        super();
        if (ball == null)
            throw new IllegalArgumentException("Ball cannot be null!");
        this.ball = ball;
    }

    /**
     * Returns the ball of the event.
     *
     * @return ball
     */
    public BallProxy getBall() {
        return this.ball;
    }

    /**
     * Bukkit implementation of the handlerList.
     *
     * @return handler
     */
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    /**
     * Bukkit implementation of the handlerList.
     *
     * @return handlerList
     */
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
