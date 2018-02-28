package com.github.shynixn.ball.bukkit.core.logic.business.controller;

import com.github.shynixn.ball.api.bukkit.business.controller.BukkitBallController;
import com.github.shynixn.ball.api.bukkit.business.entity.BukkitBall;
import com.github.shynixn.ball.api.business.entity.Ball;
import com.github.shynixn.ball.api.persistence.BallMeta;
import com.github.shynixn.ball.bukkit.core.nms.NMSRegistry;
import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.LivingEntity;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.*;
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
public class BallEntityController implements BukkitBallController {

    private final Set<BukkitBall> balls = new HashSet<>();
    private final Plugin plugin;
    private final String fileName;

    public BallEntityController(Plugin plugin, String fileName) {
        super();
        if(plugin == null)
            throw new IllegalArgumentException("Plugin cannot be null!");
        this.plugin = plugin;
        this.fileName = fileName;
    }

    /**
     * Creates a new ball from the given parameters.
     *
     * @param location   location
     * @param ballMeta   ballMeta
     * @param persistent persistent for restarts
     * @param owner      entityOwner
     * @return ball
     */
    @Override
    public BukkitBall create(Location location, BallMeta ballMeta, boolean persistent, LivingEntity owner) {
        final BukkitBall ball = NMSRegistry.spawnNMSBall(location, ballMeta, persistent, owner);
        ball.respawn();
        return ball;
    }

    /**
     * Creates a new ball from an uuid and the serialized Ball data.
     *
     * @param uuid uuid
     * @param data data
     * @return ball
     */
    @Override
    public BukkitBall create(UUID uuid, Map<String, Object> data) {
        final BukkitBall ball = NMSRegistry.spawnNMSBall(uuid, data);
        ball.respawn();
        return ball;
    }

    /**
     * Returns a ball if the given entity is part of a ball.
     *
     * @param entity entity
     * @return ball
     */
    @Override
    public Optional<BukkitBall> getBallFromEntity(LivingEntity entity) {
        if(entity == null)
            throw new IllegalArgumentException("Entity cannot be null!");
        for (final BukkitBall ball : this.balls) {
            if (ball.getArmorstand().equals(entity) || ball.getHitBox().equals(entity)) {
                return Optional.of(ball);
            }
        }
        return Optional.empty();
    }

    /**
     * Saves the current ball and destroys the entity from the server.
     *
     * @param destroy should ball be destroyed after saving
     * @param ball ball
     */
    public void saveAndDestroy(Ball ball, boolean destroy) {
        this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> {
            try {
                synchronized (this.fileName) {
                    final File storageFile = new File(this.plugin.getDataFolder(), this.fileName);
                    final FileConfiguration configuration = new YamlConfiguration();
                    configuration.load(storageFile);
                    final ConfigurationSerializable serializable = (ConfigurationSerializable) ball;
                    configuration.set("balls." + ball.getUUID().toString(), serializable.serialize());
                    configuration.save(storageFile);
                    this.plugin.getLogger().log(Level.INFO, "Saved ball with id " + ball.getUUID().toString() + ".");
                    if (destroy) {
                        this.plugin.getServer().getScheduler().runTask(this.plugin, ball::remove);
                    }
                }
            } catch (IOException | InvalidConfigurationException ex) {
                this.plugin.getLogger().log(Level.WARNING, "Failed to save ball.", ex);
            }
        });
    }

    public void loadAndSpawn(UUID uuid) {
        this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> {
            try {
                synchronized (this.fileName) {
                    final File storageFile = new File(this.plugin.getDataFolder(), this.fileName);
                    if (!storageFile.exists())
                        return;
                    final FileConfiguration configuration = new YamlConfiguration();
                    configuration.load(storageFile);
                    final String dataUUID = uuid.toString();
                    final Map<String, Object> balls = ((MemorySection) configuration.get("balls")).getValues(false);
                    if (balls.containsKey(dataUUID)) {
                        final Map<String, Object> data = ((MemorySection) balls.get(dataUUID)).getValues(true);
                        this.plugin.getServer().getScheduler().runTask(this.plugin, () -> {
                            final BukkitBall ball = this.create(uuid, data);
                            this.store(ball);
                            this.plugin.getLogger().log(Level.INFO, "Loaded ball with id " + ball.getUUID().toString() + ".");
                        });
                    }
                }
            } catch (IOException | InvalidConfigurationException ex) {
                this.plugin.getLogger().log(Level.WARNING, "Failed to load ball.", ex);
            }
        });
    }

    /**
     * Stores a new a item in the repository.
     *
     * @param item item
     */
    @Override
    public void store(BukkitBall item) {
        if (item == null)
            throw new IllegalArgumentException("Ball cannot be null!");
        if (item.getOwner().isPresent()) {
            for (final Ball ball : this.getAll()) {
                if (ball.getOwner().isPresent() && ball.getOwner().get().equals(item.getOwner().get())) {
                    ball.remove();
                }
            }
        }
        this.balls.add(item);
        this.plugin.getLogger().log(Level.INFO, "Added managed ball with id " + item.getUUID() + ".");
    }

    /**
     * Removes an item from the repository.
     *
     * @param item item
     */
    @Override
    public void remove(BukkitBall item) {
        if (item == null)
            throw new IllegalArgumentException("Ball cannot be null!");
        if (this.balls.contains(item)) {
            this.balls.remove(item);
            this.plugin.getLogger().log(Level.INFO, "Removed managed ball with id " + item.getUUID() + ".");
        }
    }

    /**
     * Returns the amount of items in the repository.
     *
     * @return size
     */
    @Override
    public int size() {
        return this.balls.size();
    }

    /**
     * Clears all items in the repository.
     */
    @Override
    public void clear() {
        this.balls.clear();
    }

    /**
     * Returns all items from the repository as unmodifiableList.
     *
     * @return items
     */
    @Override
    public List<BukkitBall> getAll() {
        return new ArrayList<>(this.balls);
    }

    /**
     * Closes this resource, relinquishing any underlying resources.
     * This method is invoked automatically on objects managed by the
     * {@code try}-with-resources statement.
     * <p>
     * @throws Exception if this resource cannot be closed
     */
    @Override
    public void close() throws Exception {
        this.balls.clear();
    }
}
