package com.github.shynixn.balls.api.persistence;

/**
 * Created by Shynixn 2017.
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
public interface BallModifiers {

    /**
     * Returns the modifier value for the horizontal velocity a player kicks the ball.
     *
     * @return strength
     */
    double getHorizontalKickStrengthModifier();

    /**
     * Sets the modifier value for the horizontal velocity a player kicks the ball.
     *
     * @param strength strength
     */
    void setHorizontalKickStrengthModifier(double strength);

    /**
     * Returns the modifier value for the vertical velocity a player kicks the ball.
     *
     * @return strength
     */
    double getVerticalKickStrengthModifier();

    /**
     * Sets the modifier value for the vertical velocity a player kicksthe ball.
     *
     * @param strength strength
     */
    void setVerticalKickStrengthModifier(double strength);

    /**
     * Returns the modifier value for the horizontal velocity a player throws the ball.
     *
     * @return strength
     */
    double getHorizontalThrowStrengthModifier();

    /**
     * Sets the modifier value for the horizontal velocity a player throws the ball.
     *
     * @param strength strength
     */
    void setHorizontalThrowStrengthModifier(double strength);

    /**
     * Returns the modifier value for the vertical velocity a player throws the ball.
     *
     * @return strength
     */
    double getVerticalThrowStrengthModifier();

    /**
     * Sets the modifier value for the vertical velocity a player throws the ball.
     *
     * @param strength strength
     */
    void setVerticalThrowStrengthModifier(double strength);
}
