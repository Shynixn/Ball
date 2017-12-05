package com.github.shynixn.balls.bukkit.core.logic.business.listener;

import com.github.shynixn.balls.api.business.controller.BallController;
import com.github.shynixn.balls.api.business.entity.Ball;
import com.github.shynixn.balls.bukkit.core.logic.business.controller.BallEntityController;
import org.bukkit.Chunk;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.WorldSaveEvent;
import org.bukkit.plugin.Plugin;

import java.util.Optional;
import java.util.logging.Level;

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
public class StorageListener extends SimpleListener {

    private final BallEntityController ballController;
    private Chunk saved;

    /**
     * Initializes a new listener by plugin
     *
     * @param plugin plugin
     */
    public StorageListener(Plugin plugin, BallEntityController ballController) {
        super(plugin);
        this.ballController = ballController;
    }

    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent event) {
        final Optional<Ball> optBall = this.getBallFromPlayer(event.getPlayer());
        optBall.ifPresent(Ball::remove);
    }

    @EventHandler
    public void onPlayerTeleportEvent(PlayerTeleportEvent event) {
        if (!event.getTo().getWorld().equals(event.getFrom().getWorld())) {
            final Optional<Ball> optBall = this.getBallFromPlayer(event.getPlayer());
            optBall.ifPresent(Ball::remove);
        }
    }

    @EventHandler
    public void onChunkSaveEvent(ChunkUnloadEvent event) {
        if(saved != null && event.getChunk().equals(saved))
        {
            plugin.getLogger().log(Level.INFO, "Saved chunk got unloaded.");
        }

        for (final Entity entity : event.getChunk().getEntities()) {
            final Optional<Ball> ball;
            if ((ball = this.ballController.getBallFromEntity(entity)).isPresent()) {
                if (ball.get().isPersistent()) {
                    this.ballController.saveAndDestroy(ball.get(), true);
                    saved = event.getChunk();
                } else {
                    ball.get().remove();
                }
            }
        }
    }

    @EventHandler
    public void onWorldSaveEvent(WorldSaveEvent event) {
        for (final Entity entity : event.getWorld().getEntities()) {
            final Optional<Ball> ball;
            if ((ball = this.ballController.getBallFromEntity(entity)).isPresent()) {
                if (ball.get().isPersistent()) {
                    this.ballController.saveAndDestroy(ball.get(), false);
                }
            }
        }
    }

    @EventHandler
    public void onChunkLoadEvent(ChunkLoadEvent event) {
        if(saved != null && event.getChunk().equals(saved))
        {
            plugin.getLogger().log(Level.INFO, "Saved chunk got loaded.");
        }
        for (final Entity entity : event.getChunk().getEntities()) {
            if (entity.getCustomName() != null && entity.getCustomName().equals("ResourceBallsPlugin")) {
                this.plugin.getLogger().log(Level.INFO, "Removed unknown ball.");
                entity.remove();
            }
        }
        this.ballController.loadAndSpawn(event.getChunk());
    }

    private Optional<Ball> getBallFromPlayer(Player player) {
        for (final Ball ball : this.ballController.getAll()) {
            if (ball.getOwner().isPresent() && ball.getOwner().get().equals(player)) {
                return Optional.of(ball);
            }
        }
        return Optional.empty();
    }
}
