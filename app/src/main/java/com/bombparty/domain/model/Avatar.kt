package com.bombparty.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Avatar(
    val emoji: String,
    val name: String
)

object AvatarOptions {
    val HAPPY = Avatar("😀", "Happy")
    val COOL = Avatar("😎", "Cool")
    val NERD = Avatar("🤓", "Nerd")
    val DEVIL = Avatar("😈", "Devil")
    val ALIEN = Avatar("👽", "Alien")
    val ROBOT = Avatar("🤖", "Robot")
    val DOG = Avatar("🐶", "Dog")
    val CAT = Avatar("🐱", "Cat")
    val LION = Avatar("🦁", "Lion")
    val PANDA = Avatar("🐼", "Panda")
    val FOX = Avatar("🦊", "Fox")
    val FROG = Avatar("🐸", "Frog")
    val UNICORN = Avatar("🦄", "Unicorn")
    val DRAGON = Avatar("🐉", "Dragon")
    val FIRE = Avatar("🔥", "Fire")
    val STAR = Avatar("⭐", "Star")

    val ALL = listOf(
        HAPPY, COOL, NERD, DEVIL,
        ALIEN, ROBOT, DOG, CAT,
        LION, PANDA, FOX, FROG,
        UNICORN, DRAGON, FIRE, STAR
    )

    fun getByEmoji(emoji: String): Avatar? = ALL.find { it.emoji == emoji }

    fun getDefault(): Avatar = HAPPY
}
