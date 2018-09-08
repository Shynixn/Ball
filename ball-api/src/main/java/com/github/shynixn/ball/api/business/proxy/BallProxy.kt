package com.github.shynixn.ball.api.business.proxy

import com.github.shynixn.ball.api.persistence.BallMeta
import java.util.*

/**
 * Created by Shynixn 2018.
 * <p>
 * Version 1.2
 * <p>
 * MIT License
 * <p>
 * Copyright (c) 2018 by Shynixn
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
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS Oo89R
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
interface BallProxy {
    /**
     * Gets the meta data.
     */
    val meta: BallMeta

    /**
     * Is the ball currently grabbed by some entity?
     */
    val isGrabbed: Boolean

    /**
     * Is the entity dead?
     */
    val isDead: Boolean

    /**
     * Unique id.
     */
    val uuid: UUID

    /**
     * Is the entity persistent and can be stored.
     */
    var persistent: Boolean

    /**
     * Current spinning force value.
     */
    var spinningForce: Double

    /**
     * Returns the armorstand for the design.
     */
    fun <A> getDesignArmorstand(): A

    /**
     * Returns the armorstand for the hitbox.
     */
    fun <A> getHitboxArmorstand(): A

    /**
     * Gets the optional living entity owner of the ball.
     */
    fun <L> getOwner(): Optional<L>

    /**
     * Gets the last interaction entity.
     */
    fun <L> getLastInteractionEntity(): Optional<L>

    /**
     * Teleports the ball to the given [location].
     */
    fun <L> teleport(location: L)

    /**
     * Gets the location of the ball.
     */
    fun <L> getLocation(): L

    /**
     * Sets the velocity of the ball.
     */
    fun <V> setVelocity(vector: V)

    /**
     * Gets the velocity of the ball.
     */
    fun <V> getVelocity(): V

    /**
     * Kicks the ball by the given entity.
     * The calculated velocity can be manipulated by the BallKickEvent.
     *
     * @param entity entity
     */
    fun <E> kickByEntity(entity: E)

    /**
     * Throws the ball by the given entity.
     * The calculated velocity can be manipulated by the BallThrowEvent.
     *
     * @param entity entity
     */
    fun <E> throwByEntity(entity: E)

    /**
     * Lets the given living entity grab the ball.
     */
    fun <L> grab(entity: L)

    /**
     * DeGrabs the ball.
     */
    fun deGrab()

    /**
     * Removes the ball.
     */
    fun remove()

    /**
     * Calculates the movement vectors.
     */
    fun <V> calculateMoveSourceVectors(movementVector: V, motionVector: V, onGround: Boolean): Optional<V>

    /**
     * Calculates the knockback for the given [sourceVector] and [sourceBlock]. Uses the motion values to correctly adjust the
     * wall.
     */
    fun <V, B> calculateKnockBack(sourceVector: V, sourceBlock: B, mot0: Double, mot2: Double, mot6: Double, mot8: Double)
}