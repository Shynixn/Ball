package com.github.shynixn.balls.bukkit.core.logic.persistence.controller

import java.util.*

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
 * OUT OF OR IN CONNECTION WITH THE  SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
open class GenericController<T>(protected val items: MutableList<T> = ArrayList()) where T : Any {

    /** Returns the amount of items in the controller. */
    val count: Int
        get() {
            return items.size
        }

    /**
     * Stores the [item] into the collection.
     */
    fun store(item: T) {
        if (!this.items.contains(item)) {
            this.items.add(item)
        }
    }

    /**
     * Creates a new instance of the given [clazz] parameter.
     */
    fun create(clazz: Class<T>): T {
        return clazz.newInstance()
    }

    /**
     * Returns all items in an unmodifiable list.
     */
    fun getAll(): List<T> {
        return Collections.unmodifiableList(items)
    }

    /**
     * Removes the [item] from the collection.
     */
    fun remove(item: T) {
        if (this.items.contains(item)) {
            this.items.remove(item)
        }
    }
}