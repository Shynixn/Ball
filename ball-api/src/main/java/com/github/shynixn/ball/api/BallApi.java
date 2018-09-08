package com.github.shynixn.ball.api;

import com.github.shynixn.ball.api.business.controller.BallController;
import com.github.shynixn.ball.api.business.proxy.BallProxy;
import com.github.shynixn.ball.api.persistence.BallMeta;
import com.github.shynixn.ball.api.persistence.controller.BallMetaController;

/**
 * Ball Api.
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
public class BallApi {

    private static BallController ballController;
    private static BallMetaController ballMetaController;

    /**
     * Returns the controller for meta managing.
     *
     * @return controller
     */
    public static BallMetaController getBallMetaController() {
        return ballMetaController;
    }

    /**
     * Creates a new managed ball which spawns at the given location and respawns automatically
     * when the chunk is reloaded. Also, the ball is stored for restarts.
     *
     * @param location location
     * @param ballMeta ballMeta
     * @return bal
     */
    public static BallProxy spawnBall(Object location, BallMeta ballMeta) {
        if (location == null)
            throw new IllegalArgumentException("Location cannot be null!");
        if (ballMeta == null)
            throw new IllegalArgumentException("BallMeta cannot be null!");
        final BallProxy ball = ballController.create(location, ballMeta, true, null);
        ballController.store(ball);
        return ball;
    }

    /**
     * Creates a new managed ball which spawns at the given location and despawns automatically
     * when the chunk is unloaded. Also, the ball gets removed on restarts.
     *
     * @param location location
     * @param ballMeta ballMeta
     * @return ball
     */
    public static BallProxy spawnTemporaryBall(Object location, BallMeta ballMeta) {
        if (location == null)
            throw new IllegalArgumentException("Location cannot be null!");
        if (ballMeta == null)
            throw new IllegalArgumentException("BallMeta cannot be null!");
        final BallProxy ball = ballController.create(location, ballMeta, false, null);
        ballController.store(ball);
        return ball;
    }

    /**
     * Creates a new managed ball which spawns at the given location and despawns automatically
     * when the owner of the ball left the server, the chunk gets unloaded or changed worlds.
     *
     * @param location location
     * @param entity   entity
     * @param ballMeta meta
     * @return ball
     */
    public static BallProxy spawnPlayerBall(Object location, Object entity, BallMeta ballMeta) {
        if (location == null)
            throw new IllegalArgumentException("Location cannot be null!");
        if (ballMeta == null)
            throw new IllegalArgumentException("BallMeta cannot be null!");
        if (entity == null)
            throw new IllegalArgumentException("Owner cannot be null!");
        final BallProxy ball = ballController.create(location, ballMeta, false, entity);
        ballController.store(ball);
        return ball;
    }

    /**
     * Creates a new unManaged ball which leaves all spawn/despawn and event handling
     * of the ball entity to caller of this method.
     *
     * @param ballMeta ballMeta
     * @param location location
     * @return ball
     */
    public static BallProxy spawnUnmanagedBall(Object location, BallMeta ballMeta) {
        if (location == null)
            throw new IllegalArgumentException("Location cannot be null!");
        if (ballMeta == null)
            throw new IllegalArgumentException("BallMeta cannot be null!");
        return ballController.create(location, ballMeta, false, null);
    }
}
