package com.bombparty.data.repository

import android.content.Context
import com.bombparty.data.model.Dictionary
import com.bombparty.domain.model.GameLanguage
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DictionaryRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dictionaries = mutableMapOf<GameLanguage, Dictionary>()

    suspend fun getDictionary(language: GameLanguage): Dictionary = withContext(Dispatchers.IO) {
        dictionaries.getOrPut(language) {
            when (language) {
                GameLanguage.SPANISH -> loadSpanishDictionary()
                GameLanguage.ENGLISH -> loadEnglishDictionary()
                GameLanguage.BOTH -> loadCombinedDictionary()
            }
        }
    }

    private fun loadSpanishDictionary(): Dictionary {
        val words = loadWordsFromAsset("dictionary_es.txt")
        return Dictionary(GameLanguage.SPANISH, words)
    }

    private fun loadEnglishDictionary(): Dictionary {
        val words = loadWordsFromAsset("dictionary_en.txt")
        return Dictionary(GameLanguage.ENGLISH, words)
    }

    private fun loadCombinedDictionary(): Dictionary {
        val spanishWords = loadWordsFromAsset("dictionary_es.txt")
        val englishWords = loadWordsFromAsset("dictionary_en.txt")
        return Dictionary(GameLanguage.BOTH, spanishWords + englishWords)
    }

    private fun loadWordsFromAsset(fileName: String): Set<String> {
        return try {
            context.assets.open(fileName)
                .bufferedReader()
                .useLines { lines ->
                    lines
                        .map { it.trim().lowercase() }
                        .filter { it.length >= 3 && it.all { c -> c.isLetter() } }
                        .toSet()
                }
        } catch (e: Exception) {
            // If file doesn't exist, return default words
            getDefaultWords(fileName)
        }
    }

    private fun getDefaultWords(fileName: String): Set<String> {
        return when {
            fileName.contains("es") -> spanishDefaultWords
            fileName.contains("en") -> englishDefaultWords
            else -> spanishDefaultWords + englishDefaultWords
        }
    }

    companion object {
        // Default Spanish words for testing
        private val spanishDefaultWords = setOf(
            "casa", "perro", "gato", "mesa", "silla", "puerta", "ventana", "libro",
            "papel", "lápiz", "computadora", "teléfono", "agua", "comida", "tiempo",
            "persona", "día", "noche", "mañana", "tarde", "año", "mes", "semana",
            "hora", "minuto", "segundo", "mundo", "país", "ciudad", "pueblo",
            "calle", "camino", "carretera", "auto", "coche", "bicicleta", "avión",
            "barco", "tren", "escuela", "universidad", "hospital", "tienda", "mercado",
            "parque", "bosque", "montaña", "río", "mar", "océano", "playa",
            "cielo", "sol", "luna", "estrella", "nube", "lluvia", "viento",
            "fuego", "tierra", "piedra", "árbol", "flor", "planta", "animal",
            "pájaro", "pez", "insecto", "araña", "serpiente", "caballo", "vaca",
            "cerdo", "gallina", "pato", "oveja", "cabra", "ratón", "conejo",
            "zorro", "lobo", "oso", "león", "tigre", "elefante", "jirafa",
            "mono", "delfín", "ballena", "tiburón", "pulpo", "calamar", "medusa",
            "padre", "madre", "hijo", "hija", "hermano", "hermana", "abuelo",
            "abuela", "tío", "tía", "primo", "prima", "amigo", "amiga",
            "hombre", "mujer", "niño", "niña", "bebé", "adulto", "joven",
            "viejo", "alto", "bajo", "grande", "pequeño", "largo", "corto",
            "ancho", "delgado", "gordo", "flaco", "fuerte", "débil", "rápido",
            "lento", "nuevo", "viejo", "joven", "antiguo", "moderno", "simple",
            "complejo", "fácil", "difícil", "bueno", "malo", "mejor", "peor",
            "primero", "último", "siguiente", "anterior", "arriba", "abajo", "dentro",
            "fuera", "delante", "detrás", "cerca", "lejos", "aquí", "allí",
            "ahora", "antes", "después", "siempre", "nunca", "mucho", "poco",
            "todo", "nada", "algo", "alguien", "nadie", "más", "menos",
            "ser", "estar", "tener", "hacer", "decir", "ir", "venir",
            "ver", "dar", "saber", "poder", "querer", "poner", "parecer",
            "llevar", "dejar", "seguir", "encontrar", "llamar", "vivir", "sentir",
            "trabajar", "estudiar", "jugar", "comer", "beber", "dormir", "despertar",
            "hablar", "escuchar", "mirar", "tocar", "oler", "gustar", "amar",
            "odiar", "pensar", "creer", "recordar", "olvidar", "aprender", "enseñar",
            "abrir", "cerrar", "entrar", "salir", "subir", "bajar", "correr",
            "caminar", "saltar", "nadar", "volar", "caer", "levantarse", "sentarse",
            "acostarse", "vestirse", "lavarse", "peinarse", "ducharse", "bañarse", "cocinar",
            "limpiar", "comprar", "vender", "pagar", "ganar", "perder", "buscar",
            "encontrar", "esperar", "necesitar", "usar", "ayudar", "preguntar", "responder",
            "comenzar", "terminar", "continuar", "parar", "cambiar", "mover", "romper",
            "arreglar", "construir", "destruir", "crear", "escribir", "leer", "dibujar",
            "pintar", "cantar", "bailar", "tocar", "instrumento", "música", "canción",
            "película", "teatro", "arte", "cultura", "historia", "ciencia", "matemáticas",
            "física", "química", "biología", "geografía", "literatura", "filosofía", "religión",
            "política", "economía", "sociedad", "gobierno", "ley", "derecho", "justicia",
            "policía", "ejército", "guerra", "paz", "libertad", "democracia", "república",
            "monarquía", "dictadura", "presidente", "rey", "reina", "ministro", "senador",
            "diputado", "alcalde", "gobernador", "juez", "abogado", "médico", "enfermero",
            "profesor", "estudiante", "ingeniero", "arquitecto", "artista", "músico", "actor",
            "escritor", "periodista", "científico", "investigador", "programador", "diseñador", "fotógrafo"
        )

        // Default English words for testing
        private val englishDefaultWords = setOf(
            "house", "dog", "cat", "table", "chair", "door", "window", "book",
            "paper", "pencil", "computer", "phone", "water", "food", "time",
            "person", "day", "night", "morning", "evening", "year", "month", "week",
            "hour", "minute", "second", "world", "country", "city", "town",
            "street", "road", "highway", "car", "vehicle", "bicycle", "airplane",
            "boat", "train", "school", "university", "hospital", "store", "market",
            "park", "forest", "mountain", "river", "sea", "ocean", "beach",
            "sky", "sun", "moon", "star", "cloud", "rain", "wind",
            "fire", "earth", "stone", "tree", "flower", "plant", "animal",
            "bird", "fish", "insect", "spider", "snake", "horse", "cow",
            "pig", "chicken", "duck", "sheep", "goat", "mouse", "rabbit",
            "fox", "wolf", "bear", "lion", "tiger", "elephant", "giraffe",
            "monkey", "dolphin", "whale", "shark", "octopus", "squid", "jellyfish",
            "father", "mother", "son", "daughter", "brother", "sister", "grandfather",
            "grandmother", "uncle", "aunt", "cousin", "friend", "boyfriend", "girlfriend",
            "man", "woman", "boy", "girl", "baby", "adult", "young",
            "old", "tall", "short", "big", "small", "long", "brief",
            "wide", "thin", "fat", "skinny", "strong", "weak", "fast",
            "slow", "new", "ancient", "modern", "simple", "complex", "easy",
            "difficult", "good", "bad", "better", "worse", "best", "worst",
            "first", "last", "next", "previous", "above", "below", "inside",
            "outside", "front", "back", "near", "far", "here", "there",
            "now", "before", "after", "always", "never", "often", "sometimes",
            "all", "nothing", "something", "someone", "nobody", "more", "less",
            "be", "have", "do", "make", "say", "go", "come",
            "see", "give", "know", "can", "want", "put", "seem",
            "take", "leave", "follow", "find", "call", "live", "feel",
            "work", "study", "play", "eat", "drink", "sleep", "wake",
            "speak", "listen", "look", "touch", "smell", "like", "love",
            "hate", "think", "believe", "remember", "forget", "learn", "teach",
            "open", "close", "enter", "exit", "rise", "fall", "run",
            "walk", "jump", "swim", "fly", "drop", "stand", "sit",
            "lie", "dress", "wash", "comb", "shower", "bathe", "cook",
            "clean", "buy", "sell", "pay", "earn", "lose", "search",
            "wait", "need", "use", "help", "ask", "answer", "begin",
            "finish", "continue", "stop", "change", "move", "break", "fix",
            "build", "destroy", "create", "write", "read", "draw", "paint",
            "sing", "dance", "instrument", "music", "song", "movie", "theater",
            "art", "culture", "history", "science", "mathematics", "physics", "chemistry",
            "biology", "geography", "literature", "philosophy", "religion", "politics", "economy",
            "society", "government", "law", "right", "justice", "police", "army",
            "war", "peace", "freedom", "democracy", "republic", "monarchy", "dictatorship",
            "president", "king", "queen", "minister", "senator", "deputy", "mayor",
            "governor", "judge", "lawyer", "doctor", "nurse", "teacher", "student",
            "engineer", "architect", "artist", "musician", "actor", "writer", "journalist",
            "scientist", "researcher", "programmer", "designer", "photographer", "chef", "waiter"
        )
    }
}
