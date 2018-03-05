package com.github.shynixn.ball.bukkit.logic.persistence.configuration

import com.github.shynixn.ball.bukkit.logic.business.Permission
import com.github.shynixn.ball.bukkit.logic.persistence.controller.FixedGUIItemConfiguration
import com.github.shynixn.ball.bukkit.logic.persistence.controller.GUIBallConfiguration
import org.bukkit.entity.Player

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
internal object Config : SimpleConfig() {

    val guiTitle: String?
        get() {
            return this.getData<String>("messages.gui-title")
        }

    val prefix: String?
        get() {
            return this.getData<String>("messages.prefix")
        }

    val permissionYes: String?
        get() {
            return this.getData<String>("messages.perms-ico-yes")
        }

    val permissionNo: String?
        get() {
            return this.getData<String>("messages.perms-ico-no")
        }

    val metrics: Boolean?
        get() {
            return this.getData<Boolean>("metrics")
        }

    var fixedGuiItemsController: FixedGUIItemConfiguration? = null
        get() {
            if (field == null) {
                this.fixedGuiItemsController = FixedGUIItemConfiguration(this.plugin!!)
            }
            return field
        }
        private set

    var ballItemsController: GUIBallConfiguration? = null
        get() {
            if (field == null) {
                this.ballItemsController = GUIBallConfiguration(plugin!!, "config.yml")
            }
            return field
        }
        private set

    /**
     * Reloads the plugin configuration.
     */
    override fun reload() {
        super.reload()
        this.plugin?.reloadConfig()
        this.fixedGuiItemsController?.reload()
        this.ballItemsController?.reload();
    }
}

