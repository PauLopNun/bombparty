package com.bombparty.server.dictionary

import java.io.BufferedReader
import java.io.InputStreamReader

class DictionaryService {
    private val validWords: Set<String>

    init {
        println("🔤 Loading Spanish dictionary...")
        val startTime = System.currentTimeMillis()

        val resourceStream = this::class.java.classLoader.getResourceAsStream("diccionario-es.txt")
        validWords = if (resourceStream != null) {
            BufferedReader(InputStreamReader(resourceStream, Charsets.UTF_8)).use { reader ->
                reader.lineSequence()
                    .map { it.lowercase().trim() }
                    .filter { it.isNotBlank() }
                    .toSet()
            }
        } else {
            println("❌ ERROR: Could not load dictionary file")
            emptySet()
        }

        val loadTime = System.currentTimeMillis() - startTime
        println("✅ Dictionary loaded: ${validWords.size} words in ${loadTime}ms")
    }

    fun isValidWord(word: String): Boolean {
        val normalized = word.lowercase().trim()
        return normalized in validWords
    }

    fun containsWord(word: String): Boolean = isValidWord(word)
}
