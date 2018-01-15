package com.github.shynixn.ball.test;

import com.github.shynixn.ball.api.persistence.BounceObject;
import com.github.shynixn.ball.api.persistence.effect.ParticleEffectMeta;
import com.github.shynixn.ball.api.persistence.effect.SoundEffectMeta;
import com.github.shynixn.ball.api.persistence.enumeration.ActionEffect;
import com.github.shynixn.ball.api.persistence.enumeration.BallSize;
import com.github.shynixn.ball.bukkit.core.logic.persistence.entity.BallData;
import com.github.shynixn.ball.bukkit.logic.persistence.entity.ItemContainer;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Created by Shynixn 2018.
 * <p>
 * Version 1.2
 * <p>
 * MIT License
 * <p>
 * Copyright (c) 2018 by Shynixn
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
public class MappingIT {

    @Test
    public void ballMappingTest() throws ClassNotFoundException, IOException, InvalidConfigurationException {

        final BallData ballData = new BallData("Shynixn");
        ballData.setCarryable(false);
        ballData.setRotatingEnabled(false);
        ballData.setHitBoxSize(50.2);
        ballData.setAlwaysBounceBack(true);
        ballData.setSize(BallSize.SMALL);

        ballData.getModifiers()
                .setRollingDistanceModifier(42.2)
                .setGravityModifier(20.4)
                .setVerticalThrowStrengthModifier(10.2)
                .setHorizontalThrowStrengthModifier(5.23)
                .setVerticalTouchModifier(4921.23)
                .setHorizontalTouchModifier(213.2)
                .setHorizontalKickStrengthModifier(123)
                .setVerticalKickStrengthModifier(12323.14);

        final FileConfiguration configuration = new YamlConfiguration();
        configuration.set("meta", ballData.serialize());
        final String data = configuration.saveToString();

        final FileConfiguration resultconf = new YamlConfiguration();
        resultconf.loadFromString(data);
        final BallData result = new BallData(((MemorySection) resultconf.get("meta")).getValues(true));

        assertEquals("Shynixn", result.getSkin());
        assertEquals(false, result.isCarryable());
        assertEquals(false, result.isRotatingEnabled());
        assertEquals(true, result.isAlwaysBounceBack());
        assertEquals(50.2, result.getHitBoxSize());
        assertEquals(BallSize.SMALL, result.getSize());

        assertEquals(42.2, ballData.getModifiers().getRollingDistanceModifier());
        assertEquals(20.4, ballData.getModifiers().getGravityModifier());
        assertEquals(10.2, ballData.getModifiers().getVerticalThrowStrengthModifier());
        assertEquals(5.23, ballData.getModifiers().getHorizontalThrowStrengthModifier());
        assertEquals(4921.23, ballData.getModifiers().getVerticalTouchModifier());
        assertEquals(213.2, ballData.getModifiers().getHorizontalTouchModifier());
        assertEquals(123, ballData.getModifiers().getHorizontalKickStrengthModifier());
        assertEquals(12323.14, ballData.getModifiers().getVerticalKickStrengthModifier());
    }

    @Test
    public void ballMappingParticleActionTest() throws ClassNotFoundException, IOException, InvalidConfigurationException {

        final BallData ballData = new BallData("Shynixn2");
        ballData.setCarryable(false);
        ballData.setRotatingEnabled(true);
        ballData.setHitBoxSize(50.2);
        ballData.setAlwaysBounceBack(true);
        ballData.setSize(BallSize.NORMAL);

        for (final ActionEffect actionEffect : ActionEffect.values()) {
            final ParticleEffectMeta particleEffectMeta = ballData.getParticleEffectOf(actionEffect);
            particleEffectMeta.setEffectType(ParticleEffectMeta.ParticleEffectType.EXPLOSION_HUGE)
                    .setAmount(200)
                    .setOffset(5, 23, 213)
                    .setSpeed(0.233);
        }

        final FileConfiguration configuration = new YamlConfiguration();
        configuration.set("meta.1.ball", ballData.serialize());



        final String data = configuration.saveToString();

        System.out.println(data);
        final FileConfiguration resultconf = new YamlConfiguration();
        resultconf.loadFromString(data);
        final BallData result = new BallData(((MemorySection) resultconf.get("meta.1.ball")).getValues(true));

        assertEquals("Shynixn2", result.getSkin());
        assertEquals(false, result.isCarryable());
        assertEquals(true, result.isRotatingEnabled());
        assertEquals(true, result.isAlwaysBounceBack());
        assertEquals(50.2, result.getHitBoxSize());
        assertEquals(BallSize.NORMAL, result.getSize());

        for (final ActionEffect actionEffect : ActionEffect.values()) {
            final ParticleEffectMeta particleEffectMeta = result.getParticleEffectOf(actionEffect);

            assertEquals(ParticleEffectMeta.ParticleEffectType.EXPLOSION_HUGE, particleEffectMeta.getEffectType());
            assertEquals(200, particleEffectMeta.getAmount());
            assertEquals(5, particleEffectMeta.getOffsetX());
            assertEquals(23, particleEffectMeta.getOffsetY());
            assertEquals(213, particleEffectMeta.getOffsetZ());
            assertEquals(0.233, particleEffectMeta.getSpeed());
        }

    }

    @Test
    public void ballMappingSoundActionTest() throws ClassNotFoundException, IOException, InvalidConfigurationException {

        final BallData ballData = new BallData("Shynixn2");
        ballData.setCarryable(false);
        ballData.setRotatingEnabled(true);
        ballData.setHitBoxSize(50.2);
        ballData.setAlwaysBounceBack(true);
        ballData.setSize(BallSize.NORMAL);

        for (final ActionEffect actionEffect : ActionEffect.values()) {
            final SoundEffectMeta soundEffectMeta = ballData.getSoundEffectOf(actionEffect);
            soundEffectMeta.setName("custom").setPitch(14324.1).setVolume(212.3);
        }

        final FileConfiguration configuration = new YamlConfiguration();
        configuration.set("meta", ballData.serialize());
        final String data = configuration.saveToString();

        final FileConfiguration resultconf = new YamlConfiguration();
        resultconf.loadFromString(data);
        final BallData result = new BallData(((MemorySection) resultconf.get("meta")).getValues(true));

        assertEquals("Shynixn2", result.getSkin());
        assertEquals(false, result.isCarryable());
        assertEquals(true, result.isRotatingEnabled());
        assertEquals(true, result.isAlwaysBounceBack());
        assertEquals(50.2, result.getHitBoxSize());
        assertEquals(BallSize.NORMAL, result.getSize());

        for (final ActionEffect actionEffect : ActionEffect.values()) {
            final SoundEffectMeta soundEffectMeta = result.getSoundEffectOf(actionEffect);
            assertEquals("custom", soundEffectMeta.getName());
            assertEquals(14324.1, soundEffectMeta.getPitch());
            assertEquals(212.3, soundEffectMeta.getVolume());
        }



        for(int i = 1; i < 100; i++)
        {
            String custom = "  'SIEGDAMN':\n" +
                    "    ball:\n" +
                    "      skin: 'http://textures.minecraft.net/texture/f45c9acea8da71b4f252cd4deb5943f49e7dbc0764274b25a6a6f5875baea3'\n" +
                    "      size: NORMAL\n" +
                    "      hitbox-size: 2.0\n" +
                    "      carry-able: false\n" +
                    "      always-bounce: true\n" +
                    "      rotating: true\n" +
                    "      modifiers:\n" +
                    "        horizontal-touch: 1.0\n" +
                    "        vertical-touch: 1.0\n" +
                    "        horizontal-kick: 1.0\n" +
                    "        vertical-kick: 1.0\n" +
                    "        horizontal-throw: 1.0\n" +
                    "        vertical-throw: 1.0\n" +
                    "        rolling-distance: 1.0\n" +
                    "        gravity: 1.0\n" +
                    "      particle-effects:\n" +
                    "        onkick:\n" +
                    "          effecting: EVERYONE\n" +
                    "          name: none\n" +
                    "          amount: 0\n" +
                    "          speed: 0.0\n" +
                    "          offset:\n" +
                    "            x: 0.0\n" +
                    "            y: 0.0\n" +
                    "            z: 0.0\n" +
                    "        onmove:\n" +
                    "          effecting: EVERYONE\n" +
                    "          name: none\n" +
                    "          amount: 0\n" +
                    "          speed: 0.0\n" +
                    "          offset:\n" +
                    "            x: 0.0\n" +
                    "            y: 0.0\n" +
                    "            z: 0.0\n" +
                    "        oninteraction:\n" +
                    "          effecting: EVERYONE\n" +
                    "          name: none\n" +
                    "          amount: 0\n" +
                    "          speed: 0.0\n" +
                    "          offset:\n" +
                    "            x: 0.0\n" +
                    "            y: 0.0\n" +
                    "            z: 0.0\n" +
                    "        ongrab:\n" +
                    "          effecting: EVERYONE\n" +
                    "          name: none\n" +
                    "          amount: 0\n" +
                    "          speed: 0.0\n" +
                    "          offset:\n" +
                    "            x: 0.0\n" +
                    "            y: 0.0\n" +
                    "            z: 0.0\n" +
                    "        onspawn:\n" +
                    "          effecting: EVERYONE\n" +
                    "          name: none\n" +
                    "          amount: 0\n" +
                    "          speed: 0.0\n" +
                    "          offset:\n" +
                    "            x: 0.0\n" +
                    "            y: 0.0\n" +
                    "            z: 0.0\n" +
                    "        onthrow:\n" +
                    "          effecting: EVERYONE\n" +
                    "          name: none\n" +
                    "          amount: 0\n" +
                    "          speed: 0.0\n" +
                    "          offset:\n" +
                    "            x: 0.0\n" +
                    "            y: 0.0\n" +
                    "            z: 0.0\n" +
                    "      sound-effects:\n" +
                    "        onkick:\n" +
                    "          effecting: EVERYONE\n" +
                    "          name: none\n" +
                    "          volume: 0.0\n" +
                    "          pitch: 0.0\n" +
                    "        onmove:\n" +
                    "          effecting: EVERYONE\n" +
                    "          name: none\n" +
                    "          volume: 0.0\n" +
                    "          pitch: 0.0\n" +
                    "        oninteraction:\n" +
                    "          effecting: EVERYONE\n" +
                    "          name: none\n" +
                    "          volume: 0.0\n" +
                    "          pitch: 0.0\n" +
                    "        ongrab:\n" +
                    "          effecting: EVERYONE\n" +
                    "          name: none\n" +
                    "          volume: 0.0\n" +
                    "          pitch: 0.0\n" +
                    "        onspawn:\n" +
                    "          effecting: EVERYONE\n" +
                    "          name: none\n" +
                    "          volume: 0.0\n" +
                    "          pitch: 0.0\n" +
                    "        onthrow:\n" +
                    "          effecting: EVERYONE\n" +
                    "          name: none\n" +
                    "          volume: 0.0\n" +
                    "          pitch: 0.0\n" +
                    "      wall-bouncing: {}\n" +
                    "    gui:    \n" +
                    "      enabled: true\n" +
                    "      id: 397\n" +
                    "      damage: 3\n" +
                    "      skin: 'http://textures.minecraft.net/texture/f45c9acea8da71b4f252cd4deb5943f49e7dbc0764274b25a6a6f5875baea3'\n" +
                    "      name: '&a&lBall 1'\n" +
                    "      unbreakable: false\n" +
                    "      lore:\n" +
                    "        - 'Waterball.' ";
            custom = custom.replace("SIEGDAMN", String.valueOf(i));
            System.out.println(custom);
        }









    }

    @Test
    public void ballMappingBounceTest() throws ClassNotFoundException, IOException, InvalidConfigurationException {

        final BallData ballData = new BallData("Shynixn2");
        ballData.setCarryable(false);
        ballData.setRotatingEnabled(true);
        ballData.setHitBoxSize(50.2);
        ballData.setAlwaysBounceBack(true);
        ballData.setSize(BallSize.NORMAL);

        final BounceObject bounceObject = ballData.getBounceObjectController().create(Material.SKULL, 123);
        bounceObject.setBounceModifier(20.2);
        ballData.getBounceObjectController().store(bounceObject);

        final FileConfiguration configuration = new YamlConfiguration();
        configuration.set("meta", ballData.serialize());
        final String data = configuration.saveToString();

        final FileConfiguration resultconf = new YamlConfiguration();
        resultconf.loadFromString(data);
        final BallData result = new BallData(((MemorySection) resultconf.get("meta")).getValues(true));

        assertEquals("Shynixn2", result.getSkin());
        assertEquals(false, result.isCarryable());
        assertEquals(true, result.isRotatingEnabled());
        assertEquals(true, result.isAlwaysBounceBack());
        assertEquals(50.2, result.getHitBoxSize());
        assertEquals(BallSize.NORMAL, result.getSize());

        assertEquals(1, result.getBounceObjectController().size());

        BounceObject bounceObject1 = result.getBounceObjectController().getAll().get(0);
        assertEquals(Material.SKULL.getId(), bounceObject1.getMaterialId());
        assertEquals(123, bounceObject1.getMaterialDamageValue());
        assertEquals(20.2, bounceObject1.getBounceModifier());
    }
}
