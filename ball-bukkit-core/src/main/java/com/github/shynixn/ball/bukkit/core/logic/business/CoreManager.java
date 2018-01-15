package com.github.shynixn.ball.bukkit.core.logic.business;

import com.github.shynixn.ball.bukkit.core.logic.business.controller.BallEntityController;
import com.github.shynixn.ball.bukkit.core.logic.business.listener.BallListener;
import com.github.shynixn.ball.bukkit.core.logic.persistence.controller.BallDataRepository;
import com.github.shynixn.ball.api.BallsApi;
import com.github.shynixn.ball.api.bukkit.business.controller.BukkitBallController;
import com.github.shynixn.ball.api.business.controller.BallController;
import com.github.shynixn.ball.api.persistence.controller.BallMetaController;
import com.github.shynixn.ball.bukkit.core.logic.business.listener.StorageListener;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Field;
import java.util.logging.Level;
import java.util.logging.Logger;

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
public class CoreManager {

    private static Logger logger = Logger.getLogger("BallPlugin");

    private final BallMetaController metaController;
    private final BukkitBallController ballController;
    private final Plugin plugin;
    private String entityStorageFileName;

    /**
     * Initializes a new core manager.
     *
     * @param plugin plugin
     */
    public CoreManager(Plugin plugin, String metaDataFileName, String entityStorageFileName) {
        super();
        if (plugin == null)
            throw new IllegalArgumentException("Plugin cannot be null!");
        this.plugin = plugin;
        this.metaController = new BallDataRepository(plugin, metaDataFileName);
        this.ballController = new BallEntityController(plugin, entityStorageFileName);
        this.entityStorageFileName = entityStorageFileName;

        new BallListener(this.ballController, plugin);
        new StorageListener(plugin, (BallEntityController) this.ballController);

        final Field field;
        try {
            field = BallsApi.class.getDeclaredField("ballController");
            field.setAccessible(true);
            field.set(null, this.getBallController());

            final Field field2 = BallsApi.class.getDeclaredField("ballMetaController");
            field2.setAccessible(true);
            field2.set(null, this.getMetaController());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            plugin.getLogger().log(Level.WARNING, "Failed to initialize api.", e);
        }
    }

    public static Logger getLogger() {
        return logger;
    }

    /**
     * Returns the plugin which has initialized the core.
     *
     * @return plugin
     */
    public Plugin getPlugin() {
        return this.plugin;
    }

    /**
     * Returns the default meta controller.
     *
     * @return metaController
     */
    public BallMetaController getMetaController() {
        return this.metaController;
    }

    /**
     * Returns the ball controller.
     *
     * @return controller
     */
    public BallController getBallController() {
        return this.ballController;
    }
}
