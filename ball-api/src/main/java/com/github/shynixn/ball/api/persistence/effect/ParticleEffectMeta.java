package com.github.shynixn.ball.api.persistence.effect;

import java.util.Collection;

/**
 * Handles particleEffects for players.
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
public interface ParticleEffectMeta<Location, Player, Material> extends EffectMeta {
    /**
     * Sets the RGB colors of the particleEffect.
     *
     * @param <T>   type of particleMeta
     * @param red   red
     * @param green green
     * @param blue  blue
     * @return builder
     */
    <T extends ParticleEffectMeta> T setColor(int red, int green, int blue);

    /**
     * Sets the color of the particleEffect.
     *
     * @param <T>           type of particleMeta
     * @param particleColor particleColor
     * @return builder
     */
    <T extends ParticleEffectMeta> T setColor(ParticleColor particleColor);

    /**
     * Sets the color for note particleEffect.
     *
     * @param <T>   type of particleMeta
     * @param color color
     * @return builder
     */
    <T extends ParticleEffectMeta> T setNoteColor(int color);

    /**
     * Sets the amount of particles of the particleEffect.
     *
     * @param <T>    type of particleMeta
     * @param amount amount
     * @return builder
     */
    <T extends ParticleEffectMeta> T setAmount(int amount);

    /**
     * Sets the speed of the particleEffect.
     *
     * @param <T>   type of particleMeta
     * @param speed speed
     * @return builder
     */
    <T extends ParticleEffectMeta> T setSpeed(double speed);

    /**
     * Sets the offsetX of the particleEffect.
     *
     * @param <T>     type of particleMeta
     * @param offsetX offsetX
     * @return builder
     */
    <T extends ParticleEffectMeta> T setOffsetX(double offsetX);

    /**
     * Sets the offsetY of the particleEffect.
     *
     * @param <T>     type of particleMeta
     * @param offsetY offsetY
     * @return builder
     */
    <T extends ParticleEffectMeta> T setOffsetY(double offsetY);

    /**
     * Sets the offsetZ of the particleEffect.
     *
     * @param <T>     type of particleMeta
     * @param offsetZ offsetZ
     * @return builder
     */
    <T extends ParticleEffectMeta> T setOffsetZ(double offsetZ);

    /**
     * Sets the offset of the particleEffect.
     *
     * @param <T>     type of particleMeta
     * @param offsetX offsetX
     * @param offsetY offsetY
     * @param offsetZ offsetZ
     * @return instance
     */
    <T extends ParticleEffectMeta> T setOffset(double offsetX, double offsetY, double offsetZ);

    /**
     * Sets the effectType of the particleEffect.
     *
     * @param <T>  type of particleMeta
     * @param name name
     * @return builder
     */
    <T extends ParticleEffectMeta> T setEffectName(String name);

    /**
     * Sets the effectType of the particlEffect.
     *
     * @param <T>  type of particleMeta
     * @param type type
     * @return builder
     */
    <T extends ParticleEffectMeta> T setEffectType(ParticleEffectType type);

    /**
     * Sets the blue of the RGB color.
     *
     * @param <T>  type of particleMeta
     * @param blue blue
     * @return builder
     */
    <T extends ParticleEffectMeta> T setBlue(int blue);

    /**
     * Sets the red of the RGB color.
     *
     * @param <T> type of particleMeta
     * @param red red
     * @return builder
     */
    <T extends ParticleEffectMeta> T setRed(int red);

    /**
     * Sets the green of the RGB color.
     *
     * @param <T>   type of particleMeta
     * @param green green
     * @return builder
     */
    <T extends ParticleEffectMeta> T setGreen(int green);

    /**
     * Sets the material of the particleEffect.
     *
     * @param <T>      type of particleMeta
     * @param material material
     * @return builder
     */
    <T extends ParticleEffectMeta> T setMaterial(Material material);

    /**
     * Sets the data of the material of the particleEffect.
     *
     * @param <T>  type of particleMeta
     * @param data data
     * @return builder
     */
    <T extends ParticleEffectMeta> T setData(Byte data);

    /**
     * Returns the effect of the particleEffect.
     *
     * @return effectName
     */
    String getEffectName();

    /**
     * Returns the particleEffectType of the particleEffect.
     *
     * @return effectType
     */
    ParticleEffectType getEffectType();

    /**
     * Returns the amount of particles of the particleEffect.
     *
     * @return amount
     */
    int getAmount();

    /**
     * Returns the speed of the particleEffect.
     *
     * @return speed
     */
    double getSpeed();

    /**
     * Returns the offsetX of the particleEffect.
     *
     * @return offsetX
     */
    double getOffsetX();

    /**
     * Returns the offsetY of the particleEffect.
     *
     * @return offsetY
     */
    double getOffsetY();

    /**
     * Returns the offsetZ of the particleEffect.
     *
     * @return offsetZ
     */
    double getOffsetZ();

    /**
     * Returns the RGB color blue of the particleEffect.
     *
     * @return blue
     */
    int getBlue();

    /**
     * Returns the RGB color red of the particleEffect.
     *
     * @return red
     */
    int getRed();

    /**
     * Returns the RGB color green of the particleEffect.
     *
     * @return green
     */
    int getGreen();

    /**
     * Returns the material of the particleEffect.
     *
     * @return material
     */
    Material getMaterial();

    /**
     * Returns the data of the particleEffect.
     *
     * @return data
     */
    Byte getData();

    /**
     * Copies the current builder.
     *
     * @return copy
     */
    ParticleEffectMeta copy();

    /**
     * Returns if the particleEffect is a color particleEffect.
     *
     * @return isColor
     */
    boolean isColorParticleEffect();

    /**
     * Returns if the particleEffect is a note particleEffect.
     *
     * @return isNote
     */
    boolean isNoteParticleEffect();

    /**
     * Returns if the particleEffect is a materialParticleEffect.
     *
     * @return isMaterial
     */
    boolean isMaterialParticleEffect();

    /**
     * Plays the effect at the given location to the given players.
     *
     * @param location location
     * @param players  players
     */
    void apply(Location location, Collection<Player> players);

    /**
     * Plays the effect at the given location to the given players.
     *
     * @param location location
     */
    void apply(Location location);

    /**
     * ParticleColors.
     */
    enum ParticleColor {
        BLACK(0, 0, 0),
        DARK_BLUE(0, 0, 170),
        DARK_GREEN(0, 170, 0),
        DARK_AQUA(0, 170, 170),
        DARK_RED(170, 0, 0),
        DARK_PURPLE(170, 0, 170),
        GOLD(255, 170, 0),
        GRAY(170, 170, 170),
        DARK_GRAY(85, 85, 85),
        BLUE(85, 85, 255),
        GREEN(85, 255, 85),
        AQUA(85, 255, 255),
        RED(255, 85, 85),
        LIGHT_PURPLE(255, 85, 255),
        YELLOW(255, 255, 85),
        WHITE(255, 255, 255);

        private final int red;
        private final int green;
        private final int blue;

        /**
         * Initializes a new particleColor.
         *
         * @param red   red
         * @param green green
         * @param blue  blue
         */
        ParticleColor(int red, int green, int blue) {
            this.red = red;
            this.green = green;
            this.blue = blue;
        }

        /**
         * Returns the RGB value red.
         *
         * @return red
         */
        public int getRed() {
            return this.red;
        }

        /**
         * Returns the RGB value green.
         *
         * @return green
         */
        public int getGreen() {
            return this.green;
        }

        /**
         * Returns the RGB value blue.
         *
         * @return blue
         */
        public int getBlue() {
            return this.blue;
        }
    }

    /**
     * ParticleEffectTypes
     */
    enum ParticleEffectType {
        /**
         * No Particle.
         */
        NONE("none", "none", "none"),
        /**
         * Explosion.
         */
        EXPLOSION_NORMAL("explode", "poof", "explosion"),
        /**
         * Large explosion.
         */
        EXPLOSION_LARGE("largeexplode", "explosion", "large_explosion"),
        /**
         * Huge explosion.
         */
        EXPLOSION_HUGE("hugeexplosion", "explosion_emitter", "huge_explosion"),
        /**
         * Firework.
         */
        FIREWORKS_SPARK("fireworksSpark", "firework", "fireworks_spark"),
        /**
         * Water Bubble simple.
         */
        WATER_BUBBLE("bubble", "bubble", "water_bubble"),
        /**
         * Water Bubble up.
         */
        WATER_BUBBLE_UP("bubble_column_up", "bubble_column_up", "bubble_column_up"),
        /**
         * Water Bubble pop.
         */
        WATER_BUBBLE_POP("bubble_pop", "bubble_pop", "bubble_pop"),
        /**
         * Water Splash.
         */
        WATER_SPLASH("splash", "splash", "water_splash"),
        /**
         * Fishing effect.
         */
        WATER_WAKE("wake", "fishing", "water_wake"),
        /**
         * Underwater bubbles.
         */
        SUSPENDED("suspended", "underwater", "suspended"),
        /**
         * Unused effect.
         */
        SUSPENDED_DEPTH("depthsuspend", "depthsuspend", "suspended_depth"),
        /**
         * Critical damage.
         */
        CRIT("crit", "crit", "critical_hit"),
        /**
         * Critical magical damage.
         */
        CRIT_MAGIC("magicCrit", "enchanted_hit", "magic_critical_hit"),
        /**
         * Water effect.
         */
        CURRENTDOWN("current_down", "current_down", "current_down"),
        /**
         * Smoke.
         */
        SMOKE_NORMAL("smoke", "smoke", "smoke"),
        /**
         * Large Smoke.
         */
        SMOKE_LARGE("largesmoke", "large_smoke", "large_smoke"),
        /**
         * Spell.
         */
        SPELL("spell", "effect", "spell"),
        /**
         * Instant Spell.
         */
        SPELL_INSTANT("instantSpell", "instant_effect", "instant_spell"),
        /**
         * Mob Spell.
         */
        SPELL_MOB("mobSpell", "entity_effect", "instant_spell"),
        /**
         * Mob Ambient Spell.
         */
        SPELL_MOB_AMBIENT("mobSpellAmbient", "mob_spell", "mob_spell"),
        /**
         * Witch Spell.
         */
        SPELL_WITCH("witchMagic", "witch", "witch_spell"),
        /**
         * Drip water.
         */
        DRIP_WATER("dripWater", "dripping_water", "drip_water"),
        /**
         * Drip lava.
         */
        DRIP_LAVA("dripLava", "dripping_lava", "drip_lava"),
        /**
         * Angry villager.
         */
        VILLAGER_ANGRY("angryVillager", "angry_villager", "angry_villager"),
        /**
         * Happy villager.
         */
        VILLAGER_HAPPY("happyVillager", "happy_villager", "happy_villager"),
        /**
         * Mycelium.
         */
        TOWN_AURA("townaura", "mycelium", "town_aura"),
        /**
         * Note..
         */
        NOTE("note", "note", "note"),
        /**
         * Portal.
         */
        PORTAL("portal", "portal", "portal"),
        /**
         * Nautilus.
         */
        NAUTILUS("nautilus", "nautilus", "nautilus"),
        /**
         * Enchantment.
         */
        ENCHANTMENT_TABLE("enchantmenttable", "enchant", "enchanting_glyphs"),
        /**
         * Flame.
         */
        FLAME("flame", "flame", "flame"),
        /**
         * Lava.
         */
        LAVA("lava", "lava", "lava"),
        /**
         * Squid.
         */
        SQUID_INK("squid_ink", "squid_ink", "squid_ink"),
        /**
         * Footstep.
         */
        FOOTSTEP("footstep", "footstep", "footstep"),
        /**
         * Cloud.
         */
        CLOUD("cloud", "cloud", "cloud"),
        /**
         * Redstone.
         */
        REDSTONE("reddust", "dust", "redstone_dust"),
        /**
         * Snowball.
         */
        SNOWBALL("snowballpoof", "item_snowball", "snowball"),
        /**
         * Snowshovel.
         */
        SNOW_SHOVEL("snowshovel", "snowshovel", "snow_shovel"),
        /**
         * Slime.
         */
        SLIME("slime", "item_slime", "slime"),
        /**
         * Heart.
         */
        HEART("heart", "heart", "heart"),
        /**
         * Barrier.
         */
        BARRIER("barrier", "barrier", "barrier"),
        /**
         * ItemCrack.
         */
        ITEM_CRACK("iconcrack", "item", "item_crack"),
        /**
         * BlockCrack.
         */
        BLOCK_CRACK("blockcrack", "block", "block_crack"),
        /**
         * Blockdust.
         */
        BLOCK_DUST("blockdust", "block", "block_dust"),
        /**
         * Rain.
         */
        WATER_DROP("droplet", "rain", "water_drop"),
        /**
         * Unknown.
         */
        TEM_TAKE("take", "take", "instant_spell"),
        /**
         * Guardian scare.
         */
        MOB_APPEARANCE("mobappearance", "elder_guardian", "guardian_appearance"),
        /**
         * Dragon Breath.
         */
        DRAGON_BREATH("dragonbreath", "dragon_breath", "dragon_breath"),
        /**
         * End rod.
         */
        END_ROD("endRod", "end_rod", "end_rod"),
        /**
         * Damage Indicator.
         */
        DAMAGE_INDICATOR("damageIndicator", "damage_indicator", "damage_indicator"),
        /**
         * Sweep Attack.
         */
        SWEEP_ATTACK("sweepAttack", "sweep_attack", "sweep_attack"),
        /**
         * Falling Dust.
         */
        FALLING_DUST("fallingdust", "falling_dust", "falling_dust"),
        /**
         * Totem.
         */
        TOTEM("totem", "totem_of_undying", "instant_spell"),
        /**
         * Spit.
         */
        SPIT("spit", "spit", "instant_spell");

        private final String simpleName;
        private final String gameId113;
        private String minecraftId;

        /**
         * Initializes a new particleEffectType.
         *
         * @param name name
         */
        ParticleEffectType(String name, String gameId113, String minecraftId) {
            this.simpleName = name;
            this.gameId113 = gameId113;
            this.minecraftId = minecraftId;
        }

        /**
         * Returns the vanilla minecraft id.
         *
         * @return vanilla id
         */
        public String getMinecraftId() {
            return this.minecraftId;
        }

        /**
         * Returns the gameid 1.13.
         *
         * @return id
         */
        public String getGameId113() {
            return this.gameId113;
        }

        /**
         * Returns the name of the particleEffectType.
         *
         * @return name
         */
        public String getSimpleName() {
            return this.simpleName;
        }
    }
}
