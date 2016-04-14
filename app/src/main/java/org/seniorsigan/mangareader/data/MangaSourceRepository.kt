package org.seniorsigan.mangareader.data

import android.content.Context

class MangaSourceRepository(
        private val context: Context,
        private val controller: MangaSearchController
) {
    private val storage = context.getSharedPreferences("MangaSource", 0)
    private val key = "default"

    var current: Int
        get() = storage.getInt(key, 0)
        set(value) {
            synchronized(this, {
                if (value < controller.engineNames().size && value > -1) {
                    with(storage.edit(), {
                        putInt(key, value)
                        commit()
                    })
                }
            })
        }

    val currentName: String
        get() = sources()[current]

    fun sources(): List<String> {
        return controller.engineNames()
    }

    /**
     * Set default source if it's not set already
     */
    fun setDefault(name: String) {
        synchronized(this, {
            if (storage.getInt(key, -1) == -1) {
                val id = sources().indexOf(name)
                if (id > -1) {
                    current = id
                }
            }
        })
    }
}