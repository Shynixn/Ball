package com.github.shynixn.balls.bukkit.core.logic.persistence.entity;

import com.github.shynixn.balls.api.bukkit.persistence.entity.BukkitSoundEffectMeta;
import com.github.shynixn.balls.api.persistence.effect.SoundEffectMeta;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Map;

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
public class SoundBuilder extends EffectData<BukkitSoundEffectMeta> implements BukkitSoundEffectMeta{

    private String text;
    private float volume;
    private float pitch;

    /**
     * Initializes a new soundBuilder
     */
    public SoundBuilder() {
        super();
    }

    /**
     * Initializes a new soundBuilder
     *
     * @param text text
     */
    public SoundBuilder(String text) {
        super();
        this.text = text;
        this.volume = 1.0F;
        this.pitch = 1.0F;
    }

    /**
     * Initializes a new soundBuilder
     *
     * @param text   text
     * @param volume volume
     * @param pitch  pitch
     */
    public SoundBuilder(String text, double volume, double pitch) {
        super();
        this.text = text;
        this.volume = (float) volume;
        this.pitch = (float) pitch;
    }

    /**
     * Initializes a new soundBuilder from serialized Content
     *
     * @param items items
     * @throws Exception exception
     */
    public SoundBuilder(Map<String, Object> items) throws Exception {
        super(items);
        this.text = (String) items.get("name");
        this.volume = (float) (double) items.get("volume");
        this.pitch = (float) (double) items.get("pitch");
    }

    /**
     * Plays the effect at the given location to the given players.
     *
     * @param location location
     * @param players  players
     */
    @Override
    public void apply(Location location, Collection<Player> players) {
        for (final Player player : players) {
            player.playSound(location, Sound.valueOf(this.text), this.volume, this.pitch);
        }
    }

    /**
     * Plays the effect to the given players.
     *
     * @param players players
     */
    @Override
    public void apply(Collection<Player> players) {
        for (final Player player : players) {
            player.playSound(player.getLocation(), Sound.valueOf(this.text), this.volume, this.pitch);
        }
    }

    /**
     * Plays the effect at the given location to the given players.
     *
     * @param location location
     */
    @Override
    public void apply(Location location) {
        this.apply(location,location.getWorld().getPlayers());
    }

    /**
     * Returns the name of the sound
     *
     * @return name
     */
    @Override
    public String getName() {
        return this.text;
    }

    /**
     * Sets the name of the sound
     *
     * @param name name
     * @return builder
     */
    @Override
    public SoundBuilder setName(String name) {
        this.text = name;
        return this;
    }

    /**
     * Returns the sound and throws exception if the sound does not exist
     *
     * @return sound
     * @throws Exception exception
     */
    public Sound getSound() throws Exception {
        return Sound.valueOf(this.text);
    }

    /**
     * Sets the bukkit sound of the sound.
     *
     * @param sound sound
     * @return builder
     */
    public SoundBuilder setSound(Sound sound) {
        this.text = sound.name();
        return this;
    }

    /**
     * Returns the volume of the sound
     *
     * @return volume
     */
    @Override
    public double getVolume() {
        return this.volume;
    }

    /**
     * Sets the volume of the sound
     *
     * @param volume volume
     * @return builder
     */
    @Override
    public SoundBuilder setVolume(double volume) {
        this.volume = (float) volume;
        return this;
    }

    /**
     * Returns the pitch of the sound
     *
     * @return pitch
     */
    @Override
    public double getPitch() {
        return this.pitch;
    }

    /**
     * Sets the pitch of the sound
     *
     * @param pitch pitch
     * @return builder
     */
    @Override
    public SoundBuilder setPitch(double pitch) {
        this.pitch = (float) pitch;
        return this;
    }

    /**
     * Clones the current object.
     *
     * @return clone
     */
    @Override
    public SoundEffectMeta copy() {
        final SoundBuilder soundBuilder = new SoundBuilder();
        soundBuilder.text = this.text;
        soundBuilder.pitch = this.pitch;
        soundBuilder.volume = this.volume;
        return soundBuilder;
    }

    /**
     * Serializes the builder
     *
     * @return serializedContent
     */
    public Map<String, Object> serialize() {
        final Map<String, Object> items = super.serialize();
        items.put("name", this.text);
        items.put("volume", this.volume);
        items.put("pitch", this.pitch);
        return items;
    }

    /**
     * Returns the sound from the given name
     *
     * @param name name
     * @return sounds
     */
    public static Sound getSoundFromName(String name) {
        for (final Sound sound : Sound.values()) {
            if (sound.name().equalsIgnoreCase(name))
                return sound;
        }
        return null;
    }

    /**
     * Returns all available sound names
     *
     * @return text
     */
    public static String getAvailableSounds() {
        final StringBuilder s = new StringBuilder();
        for (final Sound sound : Sound.values()) {
            if (s.length() != 0) {
                s.append(", ");
            }
            s.append(sound.name().toLowerCase());
        }
        return s.toString();
    }
}