package com.github.shynixn.balls.bukkit;

import com.github.shynixn.balls.api.BallsApi;
import com.github.shynixn.balls.api.bukkit.persistence.controller.BukkitBounceController;
import com.github.shynixn.balls.api.business.entity.Ball;
import com.github.shynixn.balls.api.persistence.BallMeta;
import com.github.shynixn.balls.api.persistence.BounceObject;
import com.github.shynixn.balls.api.persistence.enumeration.BallSize;
import com.github.shynixn.balls.bukkit.core.logic.business.CoreManager;
import com.github.shynixn.balls.bukkit.core.nms.VersionSupport;
import com.github.shynixn.balls.bukkit.logic.persistence.BallsManager;
import com.github.shynixn.balls.bukkit.logic.persistence.configuration.Config;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

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
public class BallsPlugin extends JavaPlugin {
    public static final String PREFIX_CONSOLE = ChatColor.YELLOW + "[Balls] ";
    private static final String PLUGIN_NAME = "Balls";
    private static Logger logger;
    private boolean disabled;

    private BallsManager ballsManager;
    private CoreManager coreManager;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        logger = this.getLogger();
        if (!VersionSupport.isServerVersionSupported(PLUGIN_NAME, PREFIX_CONSOLE)) {
            this.disabled = true;
            Bukkit.getPluginManager().disablePlugin(this);
        } else {
            Bukkit.getServer().getConsoleSender().sendMessage(PREFIX_CONSOLE + ChatColor.GREEN + "Loading Balls ...");
            Config.getInstance().reload();
            try {


                this.ballsManager = new BallsManager(this);
                this.coreManager = new CoreManager(this);






                Player p = Bukkit.getPlayer("Shynixn");



                BallMeta meta = BallsApi.getBallMetaController().create("textures.minecraft.net/texture/f6c5ee57717f561fc12b9f8878fbe0d0d62c72facfad61c0d27cade54e818c14");
                meta.setSize(BallSize.SMALL);

                meta.setAlwaysBounceBack(true);

                meta.getModifiers().setGravityModifier(0.5);
                meta.getModifiers().setRollingDistanceModifier(5.0);

                BounceObject dirt = meta.<BukkitBounceController>getBounceObjectController().create(Material.DIRT, 0);
                dirt.setBounceModifier(1.2);
                meta.<BukkitBounceController>getBounceObjectController().store(dirt);

                BounceObject grass = meta.<BukkitBounceController>getBounceObjectController().create(Material.GRASS, 0);
               // grass.setBounceModifier(2.0);

                Ball bukkitBall = BallsApi.spawnBall(p.getLocation(), meta);


                Bukkit.getServer().getConsoleSender().sendMessage(PREFIX_CONSOLE + ChatColor.GREEN + "Enabled Balls " + this.getDescription().getVersion() + " by Shynixn");
            } catch (final Exception e) {
                logger().log(Level.WARNING, "Failed to enable plugin.", e);
            }
        }

    }

    @Override
    public void onDisable() {
        if (this.disabled)
            return;
        try {
            this.ballsManager.close();
        } catch (final Exception e) {
            logger().log(Level.WARNING, "Failed to close resources.", e);
        }
    }

    public static Logger logger() {
        return logger;
    }
}
