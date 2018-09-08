package com.github.shynixn.ball.api.bukkit.business.event;

import com.github.shynixn.ball.api.bukkit.business.entity.BukkitBall;

/**
 * Gets called when the ball starts spinning clockwise or counter clockwise.
 * <p>
 * Version 1.2
 * <p>
 * MIT License
 * <p>
 * Copyright (c) 2018 by LazoYoung, Shynixn
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
public class BallSpinEvent extends BallCancelableEvent {
    private final float angle;
    private float modifier;

    /**
     * Initializes a new ball event.
     *
     * @param ball ball
     */
    public BallSpinEvent(BukkitBall ball, float angle, float modifier) {
        super(ball);
        this.angle = angle;
        this.modifier = modifier;
    }

    public float getAngle() {
        return this.angle;
    }

    public float getSpinModifier() {
        return this.modifier;
    }

    public void setSpinModifier(float modifier) {
        this.modifier = modifier;
    }
}
