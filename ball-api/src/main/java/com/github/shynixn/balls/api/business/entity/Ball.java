package com.github.shynixn.balls.api.business.entity;

import com.github.shynixn.balls.api.persistence.BallMeta;

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
public interface Ball {

    /**
     * Teleports the ball to the given location.
     *
     * @param location location
     */
    void teleport(Object location);

    /**
     * Kicks the ball by the given entity. Returns the calculated velocity for the ball.
     * The calculated velocity can be manipulated before actually applying it to the ball
     * in next tick.
     *
     * @param entity entity
     * @return velocity
     */
    Object kickByEntity(Object entity);

    /**
     * Throws the ball by the given entity. Returns the calculated velocity for the ball.
     * The calculated velocity can be manipulated before actually applying it to the ball
     * in next tick.
     *
     * @param entity entity
     * @return velocity
     */
    Object throwByEntity(Object entity);

    /**
     * Returns the last entity the ball interacted with. If it is contact, kicking or grabbing.
     *
     * @return entity.
     */
    Object getLastInteractionEntity();

    /**
     * Sets the ball in the hands of the entity.
     *
     * @param entity entity
     */
    void grab(Object entity);

    /**
     * Removes the ball from the hands of an entity.
     */
    void deGrab();

    /**
     * Returns if the ball is currently hold by any entity.
     *
     * @return isGrabbed
     */
    boolean isGrabbed();

    /**
     * Returns the meta data of the ball. Ball has to be respawned for applying changes from the ballMeta.
     *
     * @return ball
     */
    BallMeta getMeta();

    /**
     * Respawns the ball at the current location.
     */
    void respawn();

    /**
     * Removes the ball.
     */
    void remove();

    /**
     * Returns if the ball is dead.
     *
     * @return dead
     */
    boolean isDead();

    /**
     * Lets the ball roll or fly by the given values.
     *
     * @param x x
     * @param y y
     * @param z z
     */
    void move(double x, double y, double z);

    /**
     * Returns the armorstand of the ball.
     *
     * @return armorstand
     */
    Object getArmorstand();

    /**
     * Returns the hitbox of the ball.
     *
     * @return armorstand
     */
    Object getHitBox();
}
