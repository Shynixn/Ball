package com.github.shynixn.ball.bukkit.core.logic.business.listener;

import com.github.shynixn.ball.api.bukkit.business.controller.BukkitBallController;
import com.github.shynixn.ball.api.bukkit.business.entity.BukkitBall;
import com.github.shynixn.ball.api.bukkit.business.event.*;
import com.github.shynixn.ball.api.business.entity.Ball;
import com.github.shynixn.ball.api.persistence.effect.ParticleEffectMeta;
import com.github.shynixn.ball.api.persistence.effect.SoundEffectMeta;
import com.github.shynixn.ball.api.persistence.enumeration.ActionEffect;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.PlayerLeashEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.*;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.logging.Level;

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
    private final BukkitBallController ballController;
    private long spinDelay = 3L;

    /**
     * Initializes a new ball listener.
     *
     * @param ballController controller
     * @param plugin         plugin
     */
    public BallListener(BukkitBallController ballController, Plugin plugin) {
        super(plugin);
        if (ballController == null)
            throw new IllegalArgumentException("BallEntityController cannot be null!");
        this.ballController = ballController;
    }

    /**
     * Gets called when a player hits the ball and kicks the ball.
     *
     * @param event event
     */
    @EventHandler
    public void onPlayerInteractBallEvent(PlayerInteractEvent event) {
        for (final BukkitBall ball : this.ballController.getAll()) {
            if (ball.getLastInteractionEntity() != null && ball.getLastInteractionEntity().equals(event.getPlayer())) {
                ball.throwByEntity(event.getPlayer());
                event.setCancelled(true);
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
        if (!(event.getRightClicked() instanceof ArmorStand))
            return;
        this.dropBall(event.getPlayer());
        final Optional<BukkitBall> optBall = this.ballController.getBallFromEntity((LivingEntity) event.getRightClicked());
        if (optBall.isPresent()) {
            final BukkitBall ball = optBall.get();
            if (ball.getMeta().isCarryable() && !ball.isGrabbed()) {
                ball.grab(event.getPlayer());
            }
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
        if (event.getEntity() instanceof ArmorStand) {
            final Optional<BukkitBall> optBall = this.ballController.getBallFromEntity((LivingEntity) event.getEntity());
            if (optBall.isPresent()) {
                final BukkitBall ball = optBall.get();
                ball.kickByEntity((LivingEntity) event.getDamager());
            }
        }
    }

    /**
     * Gets called when the ball takes damage and cancels all of it.
     *
     * @param event event
     */
    @EventHandler
    public void entityDamageEvent(EntityDamageEvent event) {
        if (event.getEntity() instanceof ArmorStand) {
            final Optional<BukkitBall> optBall = this.ballController.getBallFromEntity((LivingEntity) event.getEntity());
            if (optBall.isPresent()) {
                event.setCancelled(true);
            }
        }
        if (event.getEntity() instanceof Player) {
            this.dropBall((Player) event.getEntity());
        }
    }

    /**
     * Drops the ball on command.
     *
     * @param event event
     */
    @EventHandler
    public void onPlayerCommandEvent(PlayerCommandPreprocessEvent event) {
        this.dropBall(event.getPlayer());
    }

    /**
     * Drops the ball on inventory open.
     *
     * @param event event
     */
    @EventHandler
    public void onInventoryOpenEvent(InventoryOpenEvent event) {
        this.dropBall((Player) event.getPlayer());
    }

    /**
     * Drops the ball on interact.
     *
     * @param event event
     */
    @EventHandler
    public void onPlayerEntityEvent(PlayerInteractEntityEvent event) {
        this.dropBall(event.getPlayer());
    }

    /**
     * Drops the ball on death.
     *
     * @param event event
     */
    @EventHandler
    public void onPlayerDeathEvent(PlayerDeathEvent event) {
        this.dropBall(event.getEntity());
    }

    /**
     * Drops the ball on left.
     *
     * @param event event
     */
    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent event) {
        this.dropBall(event.getPlayer());
    }

    /**
     * Drops the ball on inventory click.
     *
     * @param event event
     */
    @EventHandler
    public void onInventoryOpen(InventoryClickEvent event) {
        for (final Ball ball : this.ballController.getAll()) {
            if (ball.isGrabbed() && ball.getLastInteractionEntity() != null && ball.getLastInteractionEntity().equals(event.getWhoClicked())) {
                ball.deGrab();
                event.setCancelled(true);
                ((Player) event.getWhoClicked()).updateInventory();
                event.getWhoClicked().closeInventory();
            }
        }
    }

    /**
     * Drops the ball on teleport.
     *
     * @param event event
     */
    @EventHandler
    public void onTeleportEvent(PlayerTeleportEvent event) {
        this.dropBall(event.getPlayer());
    }

    /**
     * Drops the ball on item drop.
     *
     * @param event event
     */
    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        this.dropBall(event.getPlayer());
    }

    /**
     * Drops the ball on Slot change.
     *
     * @param event event
     */
    @EventHandler
    public void onSlotChange(PlayerItemHeldEvent event) {
        this.dropBall(event.getPlayer());
    }

    /**
     * Gets called when a player tries to leah a ball and cancels all of it.
     *
     * @param event event
     */
    @EventHandler
    public void entityLeashEvent(PlayerLeashEntityEvent event) {
        if (event instanceof LivingEntity) {
            final Optional<BukkitBall> optBall = this.ballController.getBallFromEntity((LivingEntity) event.getEntity());
            if (optBall.isPresent()) {
                event.setCancelled(true);
            }
        }
    }

    /**
     * Gets called when a player kicks a ball.
     *
     * @param event event
     */
    @EventHandler
    public void ballKickEvent(BallKickEvent event) {
        this.playEffectsForBall(event.getBall(), event.getBall().getLocation(), event.getEntity(), ActionEffect.ONKICK);
        
        if(event.getEntity() instanceof HumanEntity) {
            final HumanEntity entity = (HumanEntity) event.getEntity();
            Bukkit.getScheduler().runTaskLater(Bukkit.getPluginManager().getPlugin("Ball"), new Runnable() {
                @Override
                public void run() {
                    setMagnusForce(entity.getEyeLocation().getDirection(), event.getResult(), event.getBall());
                }
            }, this.spinDelay);
        }
    }

    /**
     * Gets called when a player interacts a ball.
     *
     * @param event event
     */
    @EventHandler
    public void ballInteractEvent(BallInteractEvent event) {
        this.playEffectsForBall(event.getBall(), event.getBall().getLocation(), event.getEntity(), ActionEffect.ONINTERACTION);
    }

    /**
     * Gets called when a player throws a ball.
     *
     * @param event event
     */
    @EventHandler
    public void ballThrowEvent(BallThrowEvent event) {
        this.playEffectsForBall(event.getBall(), event.getBall().getLocation(), event.getEntity(), ActionEffect.ONTHROW);
    }

    /**
     * Gets called when a player grabs a ball.
     *
     * @param event event
     */
    @EventHandler
    public void ballGrabEvent(BallGrabEvent event) {
        this.playEffectsForBall(event.getBall(), event.getBall().getLocation(), event.getEntity(), ActionEffect.ONGRAB);
    }

    /**
     * Gets called when the ball spawns.
     *
     * @param event event
     */
    @EventHandler
    public void ballSpawnEvent(BallSpawnEvent event) {
        this.playEffectsForBall(event.getBall(), event.getSpawnLocation(), null, ActionEffect.ONSPAWN);
    }

    /**
     * Gets called when a ball moves.
     *
     * @param event event
     */
    @EventHandler
    public void ballMoveEvent(BallMoveEvent event) {
        if (!event.getBall().isDead()) {
            this.playEffectsForBall(event.getBall(), event.getBall().getLocation(), null, ActionEffect.ONMOVE);
        }
    }
    
    @EventHandler
    public void ballPostMoveEvent(BallPostMoveEvent event) {
        BukkitBall ball = event.getBall();
        float force = ball.getMagnusForce();
        
        if(ball.isDead() || !event.isMoving() || force == 0F) {
            return;
        }
        
        BallSpinEvent spinEvent = new BallSpinEvent(ball, force);
        Bukkit.getPluginManager().callEvent(spinEvent);
        
        force = spinEvent.getMagnusForce();
        
        if(ball.getArmorstand().isOnGround()) {
            return;
        }
        
        if(force != 0F) {
            event.setVelocity(this.calculateMagnusForce(event.getVelocity(), force));
        }
    }
    
    private void setMagnusForce(Vector facing, Vector result, BukkitBall ball) {
        double angle = getAngle(result, facing);
        final float force;
        
        if (angle > 0.3F && angle < 10F) {
            force = 0.03F;
        }
        else if (angle < -0.3F && angle > -10F) {
            force = -0.03F;
        } else {
            return;
        }
        
        BallSpinEvent event = new BallSpinEvent(ball, force);
        Bukkit.getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            ball.setMagnusForce(event.getMagnusForce());
        }
    }
    
    private Vector calculateMagnusForce(Vector velocity, float force) {
        final Vector originUnit = velocity.normalize();
        double x = -originUnit.getZ();
        double z = originUnit.getX();
        
        Vector newVector = velocity.add(new Vector(x, 0, z).multiply(force));
        return newVector.multiply(velocity.length() / newVector.length());
    }
    
    /**
     * Calculates the angle between two vectors in two dimension (XZ Plane) <br>
     * If 'basis' vector is clock-wise to 'against' vector, the angle is negative.
     * @param basis The basis vector
     * @param against The vector which the angle is calculated against
     * @return The angle in the range of -180 to 180 degrees
     */
    private double getAngle(Vector basis, Vector against) {
        final Vector b = basis, a = against;
        double dot = b.getX() * a.getX() + b.getZ() * a.getZ();
        double det = b.getX() * a.getZ() - b.getZ() * a.getX();
        
        return Math.atan2(det, dot);
    }

    private void playEffectsForBall(BukkitBall ball, Location location, Entity cause, ActionEffect actionEffect) {
        try {
            final ParticleEffectMeta<Location, Player, Material> particleEffectMeta;
            if ((particleEffectMeta = ball.getMeta().getParticleEffectOf(actionEffect)) != null) {
                if (cause != null && cause instanceof Player) {
                    particleEffectMeta.apply(location, Collections.singletonList((Player) cause));
                } else {
                    particleEffectMeta.apply(location);
                }
            }
        } catch (final NullPointerException ex) {
        } catch (final Exception ex) {
            this.plugin.getServer().getLogger().log(Level.WARNING, "Failed to play ball particleEffect " + actionEffect.name() + ".", ex);
        }

        try {
            final SoundEffectMeta<Location, Player> soundEffectMeta;
            if ((soundEffectMeta = ball.getMeta().getSoundEffectOf(actionEffect)) != null) {
                if (cause != null && cause instanceof Player) {
                    soundEffectMeta.apply(location, Collections.singletonList((Player) cause));
                } else {
                    soundEffectMeta.apply(location);
                }
            }
        } catch (final NullPointerException ex) {
        } catch (final Exception ex) {
            this.plugin.getServer().getLogger().log(Level.WARNING, "Failed to play ball soundEffect " + actionEffect.name() + ".", ex);
        }
    }

    private void dropBall(Player player) {
        for (final Ball ball : this.ballController.getAll()) {
            if (ball.isGrabbed() && ball.getLastInteractionEntity() != null && ball.getLastInteractionEntity().equals(player)) {
                ball.deGrab();
            }
        }
    }
}
