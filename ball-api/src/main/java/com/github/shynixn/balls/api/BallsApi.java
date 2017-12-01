package com.github.shynixn.balls.api;

import com.github.shynixn.balls.api.business.Ball;
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
public class BallsApi {


    public static BallMeta create(String skin) {

    }

    /**
     * Creates a new managed ball which spawns at the given location and respawns automatically
     * when the chunk is reloaded. Also, the ball is stored for restarts.
     *
     * @param location location
     * @return bal
     */
    public static Ball spawnBall(Object location, BallMeta ballMeta) {
        if (location == null)
            throw new IllegalArgumentException("Location cannot be null!");
        return null;
    }

    /**
     * Creates a new managed ball which spawns at the given location and despawns automatically
     * when the chunk is unloaded. Also, the ball gets removed on restarts.
     *
     * @param location location
     * @return ball
     */
    public static Ball spawnTemporaryBall(Object location, BallMeta ballMeta) {

    }

    /**
     * Creates a new managed ball which spawns at the given location and despawns automatically
     * when the owner of the ball is over 50 blocks away, left the server or changes worlds.
     *
     * @param location
     * @param player
     * @param ballMeta
     * @return
     */
    public static Ball spawnPlayerBall(Object location, Object player, BallMeta ballMeta) {

    }

    /**
     * Creates a new unManaged ball which leaves all spawn/despawn and event handling
     * of the ball entity to caller of this method.
     *
     * @param location location
     * @return ball
     */
    public static Ball spawnUnmanagedBall(Object location, BallMeta ballMeta) {

    }





    private static Ball spawn(Object location, BallMeta ballMeta, boolean permanent, boolean managed) {

    }
}
