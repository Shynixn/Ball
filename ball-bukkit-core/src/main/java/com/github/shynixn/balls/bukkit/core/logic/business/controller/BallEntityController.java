package com.github.shynixn.balls.bukkit.core.logic.business.controller;

import com.github.shynixn.balls.api.bukkit.entity.BukkitBall;
import com.github.shynixn.balls.api.business.controller.BallController;
import com.github.shynixn.balls.api.business.entity.Ball;
import com.github.shynixn.balls.api.persistence.BallMeta;
import com.github.shynixn.balls.bukkit.core.nms.NMSRegistry;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;

import java.io.Closeable;
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
public class BallEntityController implements BallController {

    private final Set<Ball> balls = new HashSet<>();
    private final Plugin plugin;

    public BallEntityController(Plugin plugin) {
        this.plugin = plugin;
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
    public Ball create(Object location, BallMeta ballMeta, boolean persistent, Object owner) {
        final Ball ball = NMSRegistry.spawnNMSBall(location, ballMeta, persistent, (Entity) owner);
        ball.respawn();
        return ball;
    }

    public Ball create(String uuid, Map<String, Object> data) {
        final Ball ball = NMSRegistry.spawnNMSBall(uuid, data);
        ball.respawn();
        return ball;
    }

    /**
     * Saves the current ball and destroys the entity from the server.
     *
     * @param ball ball
     */
    public void saveAndDestroy(Ball ball, boolean destroy) {
        this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> {
            try {
                final File file = new File(this.plugin.getDataFolder(), "storage.yml");
                if (!file.exists())
                    file.createNewFile();
                final FileConfiguration configuration = new YamlConfiguration();
                configuration.load(file);
                final ConfigurationSerializable serializable = (ConfigurationSerializable) ball;
                configuration.set("balls." + ball.getUUID().toString(), serializable.serialize());
                configuration.save(file);
                this.plugin.getLogger().log(Level.INFO, "Saved ball with id " + ball.getUUID().toString() + ".");
                if (destroy) {
                    this.plugin.getServer().getScheduler().runTask(this.plugin, ball::remove);
                }
            } catch (IOException | InvalidConfigurationException ex) {
                this.plugin.getLogger().log(Level.WARNING, "Failed to save ball.", ex);
            }
        });
    }

    public void loadAndSpawn(Chunk chunk) {
        this.plugin.getServer().getScheduler().runTaskAsynchronously(this.plugin, () -> {
            try {
                final File file = new File(this.plugin.getDataFolder(), "storage.yml");
                if (!file.exists()) {
                    return;
                }
                final FileConfiguration configuration = new YamlConfiguration();
                configuration.load(file);
                final Map<String, Object> balls = ((MemorySection) configuration.get("balls")).getValues(false);
                for (final String key : balls.keySet()) {
                    final Map<String, Object> data = ((MemorySection) balls.get(key)).getValues(true);
                    final Location location = Location.deserialize(((MemorySection) data.get("location")).getValues(true));
                    this.plugin.getServer().getScheduler().runTask(this.plugin, () -> {
                        final Chunk dataChunk = chunk.getWorld().getChunkAt(location);

                        if (dataChunk != null && chunk.equals(dataChunk)) {
                            final Ball ball = this.create(key, data);
                            this.store(ball);
                            this.plugin.getLogger().log(Level.INFO, "Loaded ball with id " + ball.getUUID().toString() + ".");
                        }
                    });
                }
            } catch (IOException | InvalidConfigurationException ex) {
                this.plugin.getLogger().log(Level.WARNING, "Failed to load ball.", ex);
            }
        });
    }

    /**
     * Returns a ball if the given entity is part of a ball.
     *
     * @param entity entity
     * @return ball
     */
    @Override
    public Optional<Ball> getBallFromEntity(Object entity) {
        for (final Ball ball : this.balls) {
            if (ball.getArmorstand().equals(entity) || ball.getHitBox().equals(entity)) {
                return Optional.of(ball);
            }
        }
        return Optional.empty();
    }

    /**
     * Stores a new a item in the repository.
     *
     * @param item item
     */
    @Override
    public void store(Ball item) {
        if (item == null)
            throw new IllegalArgumentException("Ball cannot be null!");
        this.balls.add(item);
        this.plugin.getLogger().log(Level.INFO, "Added managed ball with id " + item.getUUID() + ".");
    }

    /**
     * Removes an item from the repository.
     *
     * @param item item
     */
    @Override
    public void remove(Ball item) {
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
    public List<Ball> getAll() {
        return new ArrayList<>(this.balls);
    }

    /**
     * Closes this resource, relinquishing any underlying resources.
     * This method is invoked automatically on objects managed by the
     * {@code try}-with-resources statement.
     * <p>
     * <p>While this interface method is declared to throw {@code
     * Exception}, implementers are <em>strongly</em> encouraged to
     * declare concrete implementations of the {@code close} method to
     * throw more specific exceptions, or to throw no exception at all
     * if the close operation cannot fail.
     * <p>
     * <p> Cases where the close operation may fail require careful
     * attention by implementers. It is strongly advised to relinquish
     * the underlying resources and to internally <em>mark</em> the
     * resource as closed, prior to throwing the exception. The {@code
     * close} method is unlikely to be invoked more than once and so
     * this ensures that the resources are released in a timely manner.
     * Furthermore it reduces problems that could arise when the resource
     * wraps, or is wrapped, by another resource.
     * <p>
     * <p><em>Implementers of this interface are also strongly advised
     * to not have the {@code close} method throw {@link
     * InterruptedException}.</em>
     * <p>
     * This exception interacts with a thread's interrupted status,
     * and runtime misbehavior is likely to occur if an {@code
     * InterruptedException} is {@linkplain Throwable#addSuppressed
     * suppressed}.
     * <p>
     * More generally, if it would cause problems for an
     * exception to be suppressed, the {@code AutoCloseable.close}
     * method should not throw it.
     * <p>
     * <p>Note that unlike the {@link Closeable#close close}
     * method of {@link Closeable}, this {@code close} method
     * is <em>not</em> required to be idempotent.  In other words,
     * calling this {@code close} method more than once may have some
     * visible side effect, unlike {@code Closeable.close} which is
     * required to have no effect if called more than once.
     * <p>
     * However, implementers of this interface are strongly encouraged
     * to make their {@code close} methods idempotent.
     *
     * @throws Exception if this resource cannot be closed
     */
    @Override
    public void close() throws Exception {
        this.balls.clear();
    }
}
