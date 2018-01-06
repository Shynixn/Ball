package com.github.shynixn.balls.bukkit.logic.persistence.configuration;

import com.github.shynixn.balls.bukkit.logic.persistence.controller.FixedItemConfiguration;

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
public class Config extends SimpleConfig {
    private static final Config instance = new Config();
    private FixedItemConfiguration fixedItemConfiguration;

    private Config() {
        super();
    }

    public static Config getInstance() {
        return instance;
    }

    /**
     * Reloads the config
     */
    @Override
    public void reload() {
        super.reload();
        this.getGUIItemsController().reload();
    }

    public FixedItemConfiguration getGUIItemsController() {
        if (this.fixedItemConfiguration == null) {
            this.fixedItemConfiguration = new FixedItemConfiguration(this.plugin);
        }
        return this.fixedItemConfiguration;
    }

    public String getGUITitle() {
        return this.getData("messages.gui-title");
    }

    public String getPrefix() {
        return this.getData("messages.prefix");
    }

    public String getPermissionIconYes() {
        return this.getData("messages.perms-ico-yes");
    }

    public String getPermissionIconNo() {
        return this.getData("messages.perms-ico-no");
    }

    public boolean isMetricsEnabled() {
        return this.getData("metrics");
    }
}
