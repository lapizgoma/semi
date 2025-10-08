package com.example.tp_seminarios.data.mapers

import com.example.tp_seminarios.data.Pokemon
import com.example.tp_seminarios.data.dto.PokemonDto
import com.example.tp_seminarios.data.dto.PokemonSpeciesDto
import java.util.Locale

object PokemonMapper {
    fun toPokemon(pokemonDto: PokemonDto, speciesDto: PokemonSpeciesDto?): Pokemon {

        val tipos = pokemonDto.types
            .sortedBy { it.slot }
            .map { it.type.name }
            // La API devuelve en ingles, pero lo podemos cambiar a español
            .map { typeEnIngles ->
                typeEnIngles
            }

        // Obtener stats
        val hp = pokemonDto.stats.find { it.stat.name == "hp" }?.base_stat ?: 0
        val ataque = pokemonDto.stats.find { it.stat.name == "attack" }?.base_stat ?: 0
        val defensa = pokemonDto.stats.find { it.stat.name == "defense" }?.base_stat ?: 0
        val velocidad = pokemonDto.stats.find { it.stat.name == "speed" }?.base_stat ?: 0

        // Calcular nivel
        val nivel = if ((hp + ataque + defensa + velocidad) > 0) {
            ((hp + ataque + defensa + velocidad) / 4 / 5).coerceIn(1, 100)
        } else {
            1
        }

        // Obtener descripción en español
        val descripcion = speciesDto?.flavor_text_entries
            ?.firstOrNull { it.language.name == "es" }
            ?.flavor_text
            ?.replace("\n", " ")
            ?.replace("\u000c", " ")
            ?.trim()
            ?: "Un Pokémon misterioso."

        // Capitalizar nombre (Pikachu, Charizard,etc)
        val nombreCapitalizado = pokemonDto.name.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
        }

        return Pokemon(
            nombre = nombreCapitalizado,
            tipo = tipos,
            nivel = nivel,
            hp = hp,
            ataque = ataque,
            defensa = defensa,
            velocidad = velocidad,
            descripcion = descripcion
        )
    }

    // Funcion opcional para traducir tipos a español
    private fun translateTypeToSpanish(englishType: String): String {
        val typeMap = mapOf(
            "fire" to "fuego",
            "water" to "agua",
            "grass" to "planta",
            "electric" to "electrico",
            "ice" to "hielo",
            "fighting" to "lucha",
            "poison" to "veneno",
            "ground" to "tierra",
            "flying" to "volador",
            "psychic" to "psiquico",
            "bug" to "bicho",
            "rock" to "roca",
            "ghost" to "fantasma",
            "dragon" to "dragon",
            "dark" to "siniestro",
            "steel" to "acero",
            "fairy" to "hada",
            "normal" to "normal"
        )
        return typeMap[englishType.lowercase()] ?: englishType
    }
}