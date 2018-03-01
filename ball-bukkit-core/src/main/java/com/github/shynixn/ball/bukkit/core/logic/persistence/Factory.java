package com.github.shynixn.ball.bukkit.core.logic.persistence;

import com.github.shynixn.ball.api.bukkit.business.controller.BukkitBallController;
import com.github.shynixn.ball.api.persistence.controller.BallMetaController;
import com.github.shynixn.ball.bukkit.core.logic.business.CoreManager;
import com.github.shynixn.ball.bukkit.core.logic.business.controller.BallEntityController;
import com.github.shynixn.ball.bukkit.core.logic.persistence.controller.BallDataRepository;
import org.bukkit.plugin.Plugin;

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
public final class Factory {

    private static CoreManager coreManager;

    /**
     * Initializes the api with the given plugin. Throws exception if already initialized.
     *
     * @param entityDataFileName file
     * @param metaDataFilename   file
     * @param plugin             plugin
     */
    public static synchronized void initialize(Plugin plugin, String metaDataFilename, String entityDataFileName) throws IllegalArgumentException {
        if (plugin == null)
            throw new IllegalArgumentException("Plugin cannot be null!");
        if (coreManager != null)
            throw new IllegalArgumentException("Ball core is already initialized. Api can be used!");
        coreManager = new CoreManager(plugin, metaDataFilename, entityDataFileName);
    }

    /**
     * Disables the api of the given plugin.
     *
     * @param plugin plugin
     */
    public static synchronized void disable(Plugin plugin) {
        try {
            coreManager.close();
        } catch (final Exception e) {
            throw new RuntimeException("Ball core could not be disabled!");
        }
    }

    /**
     * Creates a new ball managing controller for the given plugin. Does not include a listener so the ball does
     * not react to the default events when you add the ball to the default ballcontroller.
     *
     * @param fileName fileName
     * @param plugin   plugin
     * @return bukkitBallController
     */
    public static BukkitBallController createBallController(Plugin plugin, String fileName) {
        if (plugin == null)
            throw new IllegalArgumentException("Plugin cannot be null!");
        return new BallEntityController(plugin, fileName);
    }

    /**
     * Creates a new ball meta managing controller for the given plugin. Can be used to store and retrieve multiple ball meta from files.
     *
     * @param plugin   plugin
     * @param fileName fileName
     * @return ballMetaController
     */
    public static BallMetaController createBallMetaController(Plugin plugin, String fileName) {
        if (plugin == null)
            throw new IllegalArgumentException("Plugin cannot be null!");
        if (fileName == null)
            throw new IllegalArgumentException("Filename cannot be null!");
        return new BallDataRepository(plugin, fileName);
    }
}
