package com.github.shynixn.balls.bukkit.core.logic.business;

import com.github.shynixn.balls.api.BallsApi;
import com.github.shynixn.balls.api.business.controller.BallController;
import com.github.shynixn.balls.api.persistence.controller.BallMetaController;
import com.github.shynixn.balls.bukkit.core.logic.business.controller.BallEntityController;
import com.github.shynixn.balls.bukkit.core.logic.business.listener.BallListener;
import com.github.shynixn.balls.bukkit.core.logic.persistence.controller.BallDataRepository;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Field;

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

    private final BallMetaController metaController;
    private final BallController ballController;

    public CoreManager(Plugin plugin) {

        this.metaController = new BallDataRepository();
        this.ballController = new BallEntityController();

        new BallListener(this.ballController, plugin);

        Field field;
        try {
            field = BallsApi.class.getDeclaredField("ballController");
            field.setAccessible(true);
            field.set(null, this.getBallController());

            final Field field2 = BallsApi.class.getDeclaredField("ballMetaController");
            field2.setAccessible(true);
            field2.set(null, this.getMetaController());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

    }

    public BallMetaController getMetaController() {
        return this.metaController;
    }

    public BallController getBallController() {
        return this.ballController;
    }
}
