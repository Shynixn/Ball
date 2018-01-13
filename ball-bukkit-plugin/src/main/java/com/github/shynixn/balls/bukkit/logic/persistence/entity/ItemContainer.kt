package com.github.shynixn.balls.bukkit.logic.persistence.entity

import com.github.shynixn.balls.bukkit.BallPlugin
import com.github.shynixn.balls.bukkit.core.logic.business.helper.NBTTagHelper
import com.github.shynixn.balls.bukkit.core.logic.business.helper.SkinHelper
import com.github.shynixn.balls.bukkit.logic.business.Permission
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.*
import java.util.logging.Level

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
class ItemContainer(val position : Int, data : Map<String, Any>)
{
    private var cache : ItemStack? = null

    var id : Int = 0
    var damage : Int = 0
    lateinit var skin : String

    var name : String? = null
    var lore: Array<String>? = null
    var unbreakable: Boolean = false

    init {
        if (data.containsKey("id"))
            this.id = data["id"] as Int
        if (data.containsKey("damage"))
            this.damage = data["damage"] as Int
        if (data.containsKey("skin") && data["skin"] != "none")
            this.skin = data["skin"] as String
        if (data.containsKey("name")) {
            when {
                data["name"] == "default" -> this.name = null
                data["name"] == "none" -> this.name = " "
                else -> this.name = ChatColor.translateAlternateColorCodes('&', data["name"] as String)
            }
        }

        if (data.containsKey("unbreakable"))
            this.unbreakable = data["unbreakable"] as Boolean
        if (data.containsKey("lore")) {
            val m = data["lore"] as List<*>
            val lore = m
                    .filter { it != "none" }
                    .map { ChatColor.translateAlternateColorCodes('&', it.toString()) }
            this.lore = lore.toTypedArray()
        }
    }


    /**
     * Generates a new itemStack for the player and his permissions
     *
     * @param player      player
     * @param permissions permission
     * @return itemStack
     */
    fun generate(player: Player, vararg permissions: Permission): ItemStack {
        if (this.cache != null) {
            this.updateLore(player, permissions)
            return this.cache!!.clone()
        }
        try {
            var itemStack: ItemStack? = ItemStack(Material.getMaterial(this.id), 1, this.damage.toShort())
            if (itemStack!!.type == Material.SKULL_ITEM) {
                SkinHelper.setItemStackSkin(itemStack, skin)
            }
            val data = HashMap<String, Any>()
            data.put("Unbreakable", this.unbreakable)
            itemStack = NBTTagHelper.setItemStackNBTTag(itemStack, data)
            val itemMeta = itemStack!!.itemMeta
            itemMeta.displayName = this.name
            itemStack.itemMeta = itemMeta
            this.cache = itemStack
            this.updateLore(player, permissions)
            return itemStack
        } catch (ex: Exception) {
            BallPlugin.logger().log(Level.WARNING, "Failed to generate itemStack.", ex)
        }
        return ItemStack(Material.AIR)
    }

    private fun updateLore(player: Player, permissions: Array<out Permission>) {
      //  val lore = this.provideLore(player, permissions)
        val lore = null;
        val meta = this.cache?.itemMeta
      //  meta?.lore = Arrays.asList(lore)
        this.cache?.itemMeta = meta
    }

    private fun hasPermission(player: Player, vararg permissions: String): Boolean {
        for (permission in permissions) {
            if (permission.endsWith(".all")) {
                val subPermission = permission.substring(0, permission.indexOf("all")) + this.position
                if (player.hasPermission(subPermission)) {
                    return true
                }
            }
            if (player.hasPermission(permission))
                return true
        }
        return false
    }
}