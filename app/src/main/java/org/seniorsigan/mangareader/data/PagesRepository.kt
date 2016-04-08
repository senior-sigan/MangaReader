package org.seniorsigan.mangareader.data

interface PagesRepository {
    fun findAll(url: String, callback: (List<String>) -> Unit)
}