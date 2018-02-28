package com.github.shynixn.ball.bukkit

import com.github.shynixn.ball.bukkit.core.logic.business.CoreManager
import com.github.shynixn.ball.bukkit.core.logic.persistence.Factory
import com.github.shynixn.ball.bukkit.core.nms.VersionSupport
import com.github.shynixn.ball.bukkit.logic.persistence.BallsManager
import com.github.shynixn.ball.bukkit.logic.persistence.configuration.Config
import org.bstats.bukkit.Metrics
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.plugin.java.JavaPlugin
import java.util.logging.Level
import java.util.logging.Logger

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
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
class BallPlugin : JavaPlugin() {

    companion object {
        val PREFIX_CONSOLE: String = ChatColor.YELLOW.toString() + "[Balls] "
        private val PLUGIN_NAME = "Ball"
        fun logger(): Logger = Logger.getLogger("PetBlocks")
    }

    private var disabled: Boolean = false
    private var ballsManager: BallsManager? = null

    /**
     * Enables the Ball plugin.
     */
    override fun onEnable() {
        this.saveDefaultConfig()
        Bukkit.getServer().consoleSender.sendMessage(PREFIX_CONSOLE + ChatColor.GREEN + "Loading Ball ...")
        if (!VersionSupport.isServerVersionSupported(PLUGIN_NAME, PREFIX_CONSOLE)) {
            this.disabled = true
            Bukkit.getPluginManager().disablePlugin(this)
        } else {
            Config.reload()
            if (Config.metrics!!) {
           //     Metrics(this)
            }
            try {
                this.ballsManager = BallsManager(this)
                Factory.initialize(this, "ball-meta.yml", "ball-storage.yml");
                Bukkit.getServer().consoleSender.sendMessage(PREFIX_CONSOLE + ChatColor.GREEN + "Enabled Ball " + this.description.version + " by Shynixn")
            } catch (e: Exception) {
                logger.log(Level.WARNING, "Failed to enable plugin.", e)
            }

        }
    }


    /**
     *  Disables the plugin.
     */
    override fun onDisable() {
        if (this.disabled)
            return
        try {
        } catch (e: Exception) {
            logger.log(Level.WARNING, "Failed to close resources.", e)
        }
    }
}