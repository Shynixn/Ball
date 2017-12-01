package com.github.shynixn.balls.bukkit.core.logic.persistence.entity;

import com.github.shynixn.balls.api.persistence.BallEffects;
import com.github.shynixn.balls.api.persistence.BallMeta;
import com.github.shynixn.balls.api.persistence.BallModifiers;
import com.github.shynixn.balls.api.persistence.BounceObject;
import com.github.shynixn.balls.api.persistence.controller.IController;
import com.github.shynixn.balls.bukkit.core.logic.persistence.controller.BounceObjectController;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.io.Serializable;
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

    private boolean carryAble;
    private boolean rotating = true;

    private IController<BounceObject> bounceObjectIController;

    public BallData(Map<String, Object> data, IController<BounceObject> bounceObjectIController) {
        this.skin = (String) data.get("skin");
        this.carryAble = (boolean) data.get("carry-able");
        this.rotating = (boolean) data.get("rotating");
        this.bounceObjectIController = bounceObjectIController;
    }

    public BallData(String skin) {
        this.skin = skin;
        this.bounceObjectIController = new BounceObjectController();
    }

    /**
     * Returns the effect meta of the ball.
     *
     * @return effects
     */
    @Override
    public BallEffects getEffects() {
        return null;
    }

    /**
     * Returns the modifiers of the ball.
     *
     * @return modifiers
     */
    @Override
    public BallModifiers getModifiers() {
        return null;
    }

    /**
     * Returns a controller for all bounce Objects.
     *
     * @return list
     */
    @Override
    public IController<BounceObject> getBounceObjectController() {
        return null;
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

    @Override
    public Map<String, Object> serialize() {
        final Map<String, Object> data = new LinkedHashMap<>();
        data.put("skin", this.getSkin());
        data.put("carry-able", this.isCarryable());
        data.put("rotating", this.isRotatingEnabled());
        data.put("wall-bouncing", ((BounceObjectController) this.bounceObjectIController).serialize());
        return data;
    }
}
