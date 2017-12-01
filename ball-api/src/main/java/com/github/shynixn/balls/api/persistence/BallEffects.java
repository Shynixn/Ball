package com.github.shynixn.balls.api.persistence;

import com.github.shynixn.balls.api.persistence.effect.ParticleEffectMeta;
import com.github.shynixn.balls.api.persistence.effect.SoundEffectMeta;

/**
 * All particle and sound effects for the ball.
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
public interface BallEffects {

    /**
     * Returns the sound being played when a player kicks the ball with his hands.
     *
     * @return sound
     */
    SoundEffectMeta getKickSoundEffect();

    /**
     * Returns the sound being played when a player touches the ball in any way.
     *
     * @return sound
     */
    SoundEffectMeta getInteractionSoundEffect();

    /**
     * Returns the sound being played when a player grabs the ball.
     *
     * @return sound
     */
    SoundEffectMeta getGrabbingSoundEffect();

    /**
     * Returns the sound being played when a player throws the ball.
     *
     * @return sound
     */
    SoundEffectMeta getThrowingSoundEffect();

    /**
     * Returns the sound being played when the ball spawns.
     *
     * @return sound
     */
    SoundEffectMeta getSpawnSoundEffect();

    /**
     * Returns the sound being played when the ball moves.
     *
     * @return sound
     */
    SoundEffectMeta getMovingSoundEffect();

    /**
     * Returns the particle being played when a player kicks the ball with his hands.
     *
     * @return particle
     */
    ParticleEffectMeta getKickParticleEffect();

    /**
     * Returns the particle being played when a player touches the ball in any way.
     *
     * @return particle
     */
    ParticleEffectMeta getInteractionParticleEffect();

    /**
     * Returns the particle being played when a player grabs the ball.
     *
     * @return particle
     */
    ParticleEffectMeta getGrabbingParticleEffect();

    /**
     * Returns the particle being played when a player throws the ball.
     *
     * @return particle
     */
    ParticleEffectMeta getThrowingParticleEffect();

    /**
     * Returns the particle being played when the ball spawns.
     *
     * @return particle
     */
    ParticleEffectMeta getSpawnParticleEffect();

    /**
     * Returns the particle being played when the ball moves.
     *
     * @return particle
     */
    ParticleEffectMeta getMovingParticleEffect();
}
