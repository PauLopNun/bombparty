package com.bombparty.data.model

import com.bombparty.domain.model.GameLanguage

data class Dictionary(
    val language: GameLanguage,
    val words: Set<String>
) {
    fun isValidWord(word: String): Boolean {
        return word.lowercase() in words
    }

    fun containsSyllable(word: String, syllable: String): Boolean {
        return word.lowercase().contains(syllable.lowercase())
    }

    fun getWordsWithSyllable(syllable: String): List<String> {
        return words.filter { containsSyllable(it, syllable) }
    }

    fun getPossibleSyllables(minWords: Int, maxWords: Int? = null): List<String> {
        val syllableCount = mutableMapOf<String, Int>()

        // Generate 2-3 letter syllables from words
        words.forEach { word ->
            val lowerWord = word.lowercase()
            // 2-letter syllables
            for (i in 0..lowerWord.length - 2) {
                val syllable = lowerWord.substring(i, i + 2)
                syllableCount[syllable] = (syllableCount[syllable] ?: 0) + 1
            }
            // 3-letter syllables
            for (i in 0..lowerWord.length - 3) {
                val syllable = lowerWord.substring(i, i + 3)
                syllableCount[syllable] = (syllableCount[syllable] ?: 0) + 1
            }
        }

        return syllableCount
            .filter { it.value >= minWords }
            .filter { maxWords == null || it.value <= maxWords }
            .keys
            .toList()
    }

    fun getRandomSyllable(minWords: Int, maxWords: Int? = null): String? {
        return getPossibleSyllables(minWords, maxWords).randomOrNull()
    }
}
