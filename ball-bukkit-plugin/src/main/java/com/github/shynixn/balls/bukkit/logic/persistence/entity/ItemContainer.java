package com.github.shynixn.balls.bukkit.logic.persistence.entity;

import com.github.shynixn.balls.bukkit.BallsPlugin;
import com.github.shynixn.balls.bukkit.logic.persistence.configuration.Config;
import com.github.shynixn.balls.bukkit.core.logic.business.helper.NBTTagHelper;
import com.github.shynixn.balls.bukkit.core.logic.business.helper.SkinHelper;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

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
public class ItemContainer {

    private ItemStack cache;

    private int position = -1;
    private int id;
    private int damage;
    private String skin;
    private boolean unbreakable;
    private String name;
    private String[] lore;

    /**
     * Initializes a new itemContainer
     *
     * @param orderNumber orderNumber
     * @param data        data
     * @throws Exception exception
     */
    public ItemContainer(int orderNumber, Map<String, Object> data) throws Exception {
        super();
        System.out.println("GOT YOU");
        this.position = orderNumber;
        if (data.containsKey("id"))
            this.id = (int) data.get("id");
        if (data.containsKey("damage"))
            this.damage = (int) data.get("damage");
        if (data.containsKey("skin") && !data.get("skin").equals("none"))
            this.skin = (String) data.get("skin");
        if (data.containsKey("name")) {
            if (data.get("name").equals("default")) {
                this.name = null;
            } else if (data.get("name").equals("none")) {
                this.name = " ";
            } else {
                this.name = ChatColor.translateAlternateColorCodes('&', (String) data.get("name"));
            }
        }

        if (data.containsKey("unbreakable"))
            this.unbreakable = (boolean) data.get("unbreakable");
        if (data.containsKey("lore")) {
            final List<String> m = (List<String>) data.get("lore");
            if (m != null) {
                final List<String> lore = new ArrayList<>();
                for (final String s : m) {
                    if (!s.equals("none"))
                        lore.add(ChatColor.translateAlternateColorCodes('&', s));
                }
                this.lore = lore.toArray(new String[lore.size()]);
            }
        }
    }

    /**
     * Generates a new itemStack for the player and his permissions
     *
     * @param player      player
     * @param permissions permission
     * @return itemStack
     */
    public ItemStack generate(Player player, String... permissions) {
        if (this.cache != null) {
            this.updateLore(player, permissions);
            return this.cache.clone();
        }
        try {
            final ItemStack itemStack = new ItemStack(Material.getMaterial(this.id), 1, (short) this.damage);
            if (itemStack.getType() == Material.SKULL_ITEM && this.skin != null) {
                SkinHelper.setItemStackSkin(itemStack, skin);
            }
            final Map<String, Object> data = new HashMap<>();
            data.put("Unbreakable", this.unbreakable);
            NBTTagHelper.setItemStackNBTTag(itemStack, data);
            final ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName(this.name);
            itemStack.setItemMeta(itemMeta);
            this.cache = itemStack;
            this.updateLore(player, permissions);
            return itemStack;
        } catch (final Exception ex) {
            BallsPlugin.logger().log(Level.WARNING, "Failed to generate itemStack.", ex);
        }
        return new ItemStack(Material.AIR);
    }

    /**
     * Returns the position of the itemStack in the ui
     *
     * @return position
     */
    public int getPosition() {
        return this.position;
    }

    private void updateLore(Player player, String... permissions) {
        final String[] lore = this.provideLore(player, permissions);
        if (lore != null) {
            final ItemMeta meta = this.cache.getItemMeta();
            meta.setLore(Arrays.asList(lore));
            this.cache.setItemMeta(meta);
        }
    }

    private String[] provideLore(Player player, String... permissions) {
        if (permissions != null && permissions.length == 1 && permissions[0] != null) {
            if (permissions.length == 1 && permissions[0].equals("minecraft-heads")) {
                return new String[]{ChatColor.GRAY + "Use exclusive pet heads as costume.", ChatColor.YELLOW + "Sponsored by Minecraft-Heads.com"};
            }
            if (permissions.length == 1 && permissions[0].equals("head-database")) {
                final Plugin plugin = Bukkit.getPluginManager().getPlugin("HeadDatabase");
                if (plugin == null) {
                    return new String[]{ChatColor.DARK_RED + "" + ChatColor.ITALIC + "Plugin is not installed - " + ChatColor.YELLOW + "Click me!"};
                }
            }
        }
        final String[] modifiedLore = new String[this.lore.length];
        for (int i = 0; i < modifiedLore.length; i++) {
            modifiedLore[i] = this.lore[i];
            if (this.lore[i].contains("<permission>")) {
                if (permissions != null && (permissions.length == 0 || this.hasPermission(player, permissions))) {
                    modifiedLore[i] = this.lore[i].replace("<permission>", Config.getInstance().getPermissionIconYes());
                } else {
                    modifiedLore[i] = this.lore[i].replace("<permission>", Config.getInstance().getPermissionIconNo());
                }
            }
        }
        return modifiedLore;
    }

    private boolean hasPermission(Player player, String... permissions) {
        for (final String permission : permissions) {
            if (permission.endsWith(".all")) {
                final String subPermission = permission.substring(0, permission.indexOf("all")) + this.position;
                if (player.hasPermission(subPermission)) {
                    return true;
                }
            }
            if (player.hasPermission(permission))
                return true;
        }
        return false;
    }

    public Optional<String> getDisplayName() {
        return Optional.ofNullable(this.name);
    }
}
