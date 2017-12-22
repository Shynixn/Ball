package com.github.shynixn.balls.bukkit.core.nms;

import com.github.shynixn.balls.api.bukkit.business.entity.BukkitBall;
import com.github.shynixn.balls.api.persistence.BallMeta;
import com.github.shynixn.balls.bukkit.core.logic.business.helper.ReflectionUtils;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.UUID;

/**
 * Registry for handling access to entities from different versions.
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
public class NMSRegistry {

    /**
     * Spawns a completely new ball entity at the given location with the ballMeta, persistent state and owner.
     *
     * @param location   location
     * @param ballMeta   ballMeta
     * @param persistent persistent
     * @param owner      nullable owner entity
     * @return ball
     */
    public static BukkitBall spawnNMSBall(Location location, BallMeta ballMeta, boolean persistent, LivingEntity owner) {
        if (location == null)
            throw new IllegalArgumentException("Location cannot be null!");
        if (ballMeta == null)
            throw new IllegalArgumentException("Ballmeta cannot be null!");
        try {
            final Class<?> clazz = ReflectionUtils.invokeClass("com.github.shynixn.balls.bukkit.core.nms.VERSION.CustomDesign".replace("VERSION", VersionSupport.getServerVersion().getVersionText()));
            return ReflectionUtils.invokeConstructor(clazz, new Class[]{Location.class, BallMeta.class, boolean.class, LivingEntity.class}, new Object[]{location, ballMeta, persistent, owner});
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Spawns a ball from a given uuid and a serialized Ball meta data.
     *
     * @param uuid uuid of the ball
     * @param data data serialized meta data.
     * @return ball
     */
    public static BukkitBall spawnNMSBall(UUID uuid, Map<String, Object> data) {
        if (uuid == null)
            throw new IllegalArgumentException("UUID cannot be null!");
        if (data == null)
            throw new IllegalArgumentException("Data cannot be null!");
        try {
            final Class<?> clazz = ReflectionUtils.invokeClass("com.github.shynixn.balls.bukkit.core.nms.VERSION.CustomDesign".replace("VERSION", VersionSupport.getServerVersion().getVersionText()));
            return ReflectionUtils.invokeConstructor(clazz, new Class[]{String.class, Map.class}, new Object[]{uuid.toString(), data});
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
