package com.github.shynixn.balls.bukkit.logic.persistence.controller;

import com.github.shynixn.balls.api.persistence.BounceObject;
import com.github.shynixn.balls.api.persistence.controller.BounceController;
import com.github.shynixn.balls.api.persistence.controller.IController;
import com.github.shynixn.balls.api.persistence.controller.IFileController;
import com.github.shynixn.balls.bukkit.BallsPlugin;
import com.github.shynixn.balls.bukkit.core.logic.persistence.entity.BounceInfo;
import com.github.shynixn.balls.bukkit.logic.persistence.configuration.Config;
import org.bukkit.configuration.MemorySection;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.Closeable;
import java.util.List;
import java.util.Map;
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
public class BounceInfoFileRepository implements IFileController<BounceObject>, BounceController {

    private final BounceController controller;
    private final String key;

    public BounceInfoFileRepository(String key, BounceController controller) {
        this.controller = controller;
        this.key = key;
    }

    /**
     * Reloads the content from the fileSystem.
     */
    @Override
    public void reload() {
        final Plugin plugin = JavaPlugin.getPlugin(BallsPlugin.class);
        this.controller.clear();
        Config.getInstance().reload();
        System.out.println(this.key);
        final Map<String, Object> data = ((MemorySection) plugin.getConfig().get(this.key)).getValues(false);
        System.out.println(this.key);
        for (final String key : data.keySet()) {
            System.out.println("RELOAD " + this.key + "." + key);
            final Map<String, Object> content = ((MemorySection) plugin.getConfig().get(this.key + "." + key)).getValues(true);
            try {
                this.store(new BounceInfo(content));
            } catch (final Exception e) {
                BallsPlugin.logger().log(Level.WARNING, "Failed to add content " + key + '.', e);
            }
        }
    }

    /**
     * Stores a new a item in the repository.
     *
     * @param item item
     */
    @Override
    public void store(BounceObject item) {
        this.controller.store(item);
    }

    /**
     * Removes an item from the repository.
     *
     * @param item item
     */
    @Override
    public void remove(BounceObject item) {
        this.controller.remove(item);
    }

    /**
     * Returns the amount of items in the repository.
     *
     * @return size
     */
    @Override
    public int size() {
        return this.controller.size();
    }

    /**
     * Clears all items in the repository.
     */
    @Override
    public void clear() {
        this.controller.clear();
    }

    /**
     * Returns all items from the repository as unmodifiableList.
     *
     * @return items
     */
    @Override
    public List<BounceObject> getAll() {
        return this.controller.getAll();
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
        this.controller.close();
    }

    /**
     * Creates a new bounceObject from the given parameters.
     *
     * @param type   type
     * @param damage damage
     * @return bounceObject
     */
    @Override
    public BounceObject create(int type, int damage) {
        return controller.create(type, damage);
    }

    /**
     * Returns the bounceObject from the given block.
     *
     * @param block block
     * @return optBounceObject
     */
    @Override
    public Optional<BounceObject> getBounceObjectFromBlock(Object block) {
        return this.controller.getBounceObjectFromBlock(block);
    }
}
