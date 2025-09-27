package com.example.tp_seminarios.data

import com.example.tp_seminarios.R
import java.io.Serializable
import java.text.Normalizer

// Hacemos que sea Serializable para que luego lo podamos usar con el Intent en casos futuros
data class Pokemon(
    val nombre: String,
    val tipo: String,
    val nivel: Int,
    val hp: Int,
    val ataque: Int,
    val defensa: Int,
    val velocidad: Int,
    val descripcion: String
) : Serializable

// Solo si la API nos devuelve en español.
fun matchPokemonType(spanishType: String): String {
    // las tildes no son necesarias porque el normalizador las elimina.
    val typeMap = mapOf(
        "fuego" to "fire",
        "agua" to "water",
        "planta" to "grass",
        "electrico" to "electric",
        "hielo" to "ice",
        "lucha" to "fighting",
        "veneno" to "poison",
        "tierra" to "ground",
        "volador" to "flying",
        "psiquico" to "psychic",
        "bicho" to "bug",
        "roca" to "rock",
        "fantasma" to "ghost",
        "dragon" to "dragon",
        "siniestro" to "dark",
        "acero" to "steel",
        "hada" to "fairy",
        "normal" to "normal"
    )

    val normalizedSpanishType = Normalizer.normalize(spanishType, Normalizer.Form.NFD)
        .replace("[\\p{InCombiningDiacriticalMarks}]".toRegex(), "")
        .lowercase()

    return typeMap[normalizedSpanishType] ?: "unknown";
}

fun typeToResource (type: String) : Int
{
    val typeResources = mapOf(
        "fire" to R.drawable.tipo_fire_xy,
        "water" to R.drawable.tipo_water_xy,
        "grass" to R.drawable.tipo_grass_xy,
        "electric" to R.drawable.tipo_electric_xy,
        "ice" to R.drawable.tipo_ice_xy,
        "fighting" to R.drawable.tipo_fighting_xy,
        "poison" to R.drawable.tipo_poison_xy,
        "ground" to R.drawable.tipo_ground_xy,
        "flying" to R.drawable.tipo_flying_xy,
        "psychic" to R.drawable.tipo_psychic_xy,
        "bug" to R.drawable.tipo_bug_xy,
        "rock" to R.drawable.tipo_rock_xy,
        "ghost" to R.drawable.tipo_ghost_xy,
        "dragon" to R.drawable.tipo_dragon_xy,
        "dark" to R.drawable.tipo_dark_xy,
        "steel" to R.drawable.tipo_steel_xy,
        "fairy" to R.drawable.tipo_fairy_xy,
        "normal" to R.drawable.tipo_normal_xy
    )

    return typeResources[type] ?: R.drawable.tipo_unknown_xy
}

fun splitPokemonTypes(typesString: String): List<String> {
    return typesString
        .split("/")
        .toList()
}

