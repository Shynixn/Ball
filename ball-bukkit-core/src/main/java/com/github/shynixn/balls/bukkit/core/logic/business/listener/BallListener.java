package com.github.shynixn.balls.bukkit.core.logic.business.listener;

import com.github.shynixn.balls.api.bukkit.event.BallDeathEvent;
import com.github.shynixn.balls.api.business.controller.BallController;
import com.github.shynixn.balls.api.business.entity.Ball;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Rabbit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerLeashEntityEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.Plugin;

import java.util.Optional;

/**
 * Copyright 2017 Shynixn
 * <p>
 * Do not remove this header!
 * <p>
 * Version 1.0
 * <p>
 * MIT License
 * <p>
 * Copyright (c) 2017
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
public class BallListener extends SimpleListener {
    private final BallController ballController;

    /**
     * Initializes a new ball listener.
     *
     * @param ballController controller
     * @param plugin         plugin
     */
    public BallListener(BallController ballController, Plugin plugin) {
        super(plugin);
        this.ballController = ballController;
        this.plugin.getServer().getScheduler().runTaskTimer(this.plugin, () -> {
            for (final World world : Bukkit.getWorlds()) {
                for (final Entity entity : world.getEntities()) {
                    if (BallListener.this.isDeadBall(entity)) {
                        entity.remove();
                    }
                }
            }
        }, 0L, 20L * 10);
    }

    /**
     * Gets called when a player hits the ball and kicks the ball.
     *
     * @param event event
     */
    @EventHandler
    public void onPlayerInteractBallEvent(PlayerInteractEvent event) {
        for (final Ball ball : this.ballController.getAll()) {
            if (ball.getLastInteractionEntity() != null && ball.getLastInteractionEntity().equals(event.getPlayer())) {
                ball.throwByEntity(event.getPlayer());
            }
        }
    }

    /**
     * Gets called when a player rightClicks on a ball.
     *
     * @param event event
     */
    @EventHandler
    public void entityRightClickBallEvent(PlayerInteractAtEntityEvent event) {
        final Optional<Ball> optBall = this.ballController.getBallFromEntity(event.getRightClicked());
        if (optBall.isPresent()) {
            final Ball ball = optBall.get();
            if (ball.getMeta().isCarryable() && !ball.isGrabbed()) {
                ball.grab(event.getPlayer());
            }
            event.setCancelled(true);
        } else if (this.isDeadBall(event.getRightClicked())) {
            event.setCancelled(true);
        }
    }

    /**
     * Gets called when a ball entity dies.
     *
     * @param event event
     */
    @EventHandler
    public void onBallDeathEvent(BallDeathEvent event) {
        this.ballController.remove(event.getBall());
    }

    /**
     * Gets called when a player hits the ball and kicks the ball.
     *
     * @param event event
     */
    @EventHandler
    public void onPlayerDamageBallEvent(EntityDamageByEntityEvent event) {
        final Optional<Ball> optBall = this.ballController.getBallFromEntity(event.getEntity());
        if (optBall.isPresent()) {
            final Ball ball = optBall.get();
            ball.kickByEntity(event.getDamager());
        }
    }

    /**
     * Gets called when the ball takes damage and cancels all of it.
     *
     * @param event event
     */
    @EventHandler
    public void entityDamageEvent(EntityDamageEvent event) {
        final Optional<Ball> optBall = this.ballController.getBallFromEntity(event.getEntity());
        if (optBall.isPresent()) {
            event.setCancelled(true);
        }
    }

    /**
     * Gets called when a player tries to leah a ball and cancels all of it.
     *
     * @param event event
     */
    @EventHandler
    public void entityLeashEvent(PlayerLeashEntityEvent event) {
        final Optional<Ball> optBall = this.ballController.getBallFromEntity(event.getEntity());
        if (optBall.isPresent()) {
            event.setCancelled(true);
        }
    }

    /**
     * Checks if the ball is a dead ball.
     *
     * @param entity entity
     * @return isDead
     */
    private boolean isDeadBall(Entity entity) {
        final Optional<Ball> optBall = this.ballController.getBallFromEntity(entity);
        if (!optBall.isPresent()) {
            if (entity instanceof ArmorStand) {
                final ArmorStand stand = (ArmorStand) entity;
                final int xidentifier = (int) stand.getBodyPose().getZ();
                final int identifier = (int) stand.getRightArmPose().getX();
                if (xidentifier == 2777 && identifier == 2777) {
                    return true;
                }
            } else if (entity instanceof Rabbit && entity.getCustomName() != null && entity.getCustomName().equals("MyBallsIdentifier")) {
                return true;
            }
        }
        return false;
    }
}
