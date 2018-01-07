package com.github.shynixn.balls.bukkit.core.logic.persistence.entity;

import com.github.shynixn.balls.api.bukkit.persistence.entity.BukkitParticleEffectMeta;
import com.github.shynixn.balls.api.bukkit.persistence.entity.BukkitSoundEffectMeta;
import com.github.shynixn.balls.api.persistence.BallMeta;
import com.github.shynixn.balls.api.persistence.BallModifiers;
import com.github.shynixn.balls.api.persistence.controller.BounceController;
import com.github.shynixn.balls.api.persistence.enumeration.ActionEffect;
import com.github.shynixn.balls.api.persistence.enumeration.BallSize;
import com.github.shynixn.balls.bukkit.core.logic.persistence.controller.BounceObjectController;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

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
public class BallData implements BallMeta, ConfigurationSerializable {

    private String skin;

    private boolean carryAble = true;
    private boolean rotating = true;
    private double hitbox = 2.0;
    private boolean alwaysBounce;
    private BallSize size = BallSize.NORMAL;

    private final BallModifications modifications;
    private final BounceController bounceObjectIController;

    private final Map<ActionEffect, BukkitSoundEffectMeta> soundeffects = new HashMap<>();
    private final Map<ActionEffect, BukkitParticleEffectMeta> particleEffectMetaMap = new HashMap<>();

    /**
     * Deserializes a ballData.
     *
     * @param data data
     */
    public BallData(Map<String, Object> data) {
        if (data == null)
            throw new IllegalArgumentException("Data cannot be null!");
        this.skin = (String) data.get("skin");
        this.size = BallSize.valueOf((String) data.get("size"));
        this.hitbox = (double) data.get("hitbox-size");
        this.carryAble = (boolean) data.get("carry-able");
        this.rotating = (boolean) data.get("rotating");
        this.alwaysBounce = (boolean) data.get("always-bounce");
        this.bounceObjectIController = new BounceObjectController(((MemorySection) data.get("wall-bouncing")).getValues(false));
        this.modifications = new BallModifications(((MemorySection) data.get("modifiers")).getValues(false));

        final Map<String, Object> particleMap = ((MemorySection)data.get("particle-effects")).getValues(false);
        final Map<String, Object> soundMap = ((MemorySection)data.get("sound-effects")).getValues(false);
        for (final ActionEffect actionEffect : ActionEffect.values()) {
            this.particleEffectMetaMap.put(actionEffect, new ParticleEffectData(((MemorySection)particleMap.get(actionEffect.name().toLowerCase())).getValues(true)));
            this.soundeffects.put(actionEffect, new SoundBuilder(((MemorySection) soundMap.get(actionEffect.name().toLowerCase())).getValues(true)));
        }
    }

    /**
     * Initializes the ball data with a new skin.
     *
     * @param skin skin
     */
    public BallData(String skin) {
        if (skin == null)
            throw new IllegalArgumentException("Skin cannot be null!");
        this.skin = skin;
        this.modifications = new BallModifications();
        this.bounceObjectIController = new BounceObjectController();

        for (final ActionEffect actionEffect : ActionEffect.values()) {
            this.soundeffects.put(actionEffect, new SoundBuilder().setName("none"));
            this.particleEffectMetaMap.put(actionEffect, new ParticleEffectData().setEffectName("none"));
        }
    }

    /**
     * Returns the particle effect for the given action.
     *
     * @param effect effect
     * @return particleEffect
     */
    @Override
    public BukkitParticleEffectMeta getParticleEffectOf(ActionEffect effect) {
        if (effect == null)
            throw new IllegalArgumentException("Effect cannot be null!");
        return this.particleEffectMetaMap.get(effect);
    }

    /**
     * Returns the sound effect for the given action.
     *
     * @param effect effect
     * @return soundEffect
     */
    @Override
    public BukkitSoundEffectMeta getSoundEffectOf(ActionEffect effect) {
        if (effect == null)
            throw new IllegalArgumentException("Effect cannot be null!");
        return this.soundeffects.get(effect);
    }

    /**
     * Returns the modifiers of the ball.
     *
     * @return modifiers
     */
    @Override
    public BallModifiers getModifiers() {
        return this.modifications;
    }

    /**
     * Returns a controller for all bounce Objects.
     *
     * @return list
     */
    @Override
    public BounceController getBounceObjectController() {
        return this.bounceObjectIController;
    }

    /**
     * Sets always bouncing back from blocks regardless of bounceController.
     *
     * @param enabled enabled
     */
    @Override
    public void setAlwaysBounceBack(boolean enabled) {
        this.alwaysBounce = enabled;
    }

    /**
     * Returns if always bouncing back from blocks regardless of bounceController.
     *
     * @return enabled
     */
    @Override
    public boolean isAlwaysBounceBack() {
        return this.alwaysBounce;
    }

    /**
     * Sets if the ball is carry able.
     *
     * @param enabled enabled
     */
    @Override
    public void setCarryable(boolean enabled) {
        this.carryAble = enabled;
    }

    /**
     * Returns if the ball is carry able.
     *
     * @return carryAble
     */
    @Override
    public boolean isCarryable() {
        return this.carryAble;
    }

    /**
     * Sets if the ball should display a rotation animation when being kicked or thrown.
     *
     * @param enabled enabled
     */
    @Override
    public void setRotatingEnabled(boolean enabled) {
        this.rotating = enabled;
    }

    /**
     * Sets the size of the hitbox of the ball. Default 2.
     *
     * @param size size
     */
    @Override
    public void setHitBoxSize(double size) {
        this.hitbox = size;
    }

    /**
     * Returns the size of the hitbox of the ball.
     *
     * @return size
     */
    @Override
    public double getHitBoxSize() {
        return this.hitbox;
    }

    /**
     * Returns if the ball displays a rotation animation when being kicked or thrown.
     *
     * @return enabled
     */
    @Override
    public boolean isRotatingEnabled() {
        return this.rotating;
    }

    /**
     * Changes the skin of the ball. Has to be a skin-URL or name of a player.
     *
     * @param skin skin
     */
    @Override
    public void setSkin(String skin) {
        if (skin == null)
            throw new IllegalArgumentException("Skin cannot be null!");
        this.skin = skin;
    }

    /**
     * Returns the skin of the ball.
     *
     * @return skin
     */
    @Override
    public String getSkin() {
        return this.skin;
    }

    /**
     * Returns the size of the ball.
     *
     * @return size
     */
    @Override
    public BallSize getSize() {
        return this.size;
    }

    /**
     * Sets the size of the ball.
     *
     * @param size size
     */
    @Override
    public void setSize(BallSize size) {
        this.size = size;
    }

    /**
     * Serializes the given content.
     *
     * @return serializedContent
     */
    @Override
    public Map<String, Object> serialize() {
        final Map<String, Object> data = new LinkedHashMap<>();
        data.put("skin", this.getSkin());
        data.put("size", this.getSize().name().toUpperCase());
        data.put("hitbox-size", this.getHitBoxSize());
        data.put("carry-able", this.isCarryable());
        data.put("always-bounce", this.alwaysBounce);
        data.put("rotating", this.isRotatingEnabled());
        data.put("modifiers", this.modifications.serialize());
        data.put("particle-effects", this.serializeEffects(this.particleEffectMetaMap));
        data.put("sound-effects", this.serializeEffects(this.soundeffects));
        data.put("wall-bouncing", ((BounceObjectController) this.bounceObjectIController).serialize());
        return data;
    }

    private Map<String, Object> serializeEffects(Map container) {
        final Map<String, Object> data = new LinkedHashMap<>();
        int i = 0;
        for (final Object f : container.keySet()) {
            final ActionEffect actionEffect = (ActionEffect) f;
            data.put(actionEffect.name().toLowerCase(), ((ConfigurationSerializable) container.get(actionEffect)).serialize());
            i++;
        }
        return data;
    }
}
