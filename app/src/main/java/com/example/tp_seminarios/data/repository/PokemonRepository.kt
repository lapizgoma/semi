package com.example.tp_seminarios.data.repository

import android.util.Log
import com.example.tp_seminarios.data.Pokemon
import com.example.tp_seminarios.data.mapers.PokemonMapper
import com.example.tp_seminarios.data.network.RetrofitClient

class PokemonRepository {
    private val api = RetrofitClient.pokeApiService

    // Clase para resultado paginado
    data class PaginatedResult(
        val pokemones: List<Pokemon>,
        val totalCount: Int,
        val hasNext: Boolean,
        val hasPrevious: Boolean
    )

    // Obtener pokemones con paginación
    suspend fun getPokemonsPaginated(
        limit: Int = 20,
        offset: Int = 0
    ): Result<PaginatedResult> {
        return try {
            Log.d("PAGINATION_DEBUG", "=== INICIANDO PAGINACIÓN ===")
            Log.d("PAGINATION_DEBUG", "Limit: $limit, Offset: $offset")

            // Paso 1: Obtener lista de nombres
            val listResponse = api.getPokemonList(limit, offset)
            Log.d("PAGINATION_DEBUG", "Lista respuesta - Código: ${listResponse.code()}")

            if (!listResponse.isSuccessful || listResponse.body() == null) {
                Log.e("PAGINATION_DEBUG", "ERROR en lista: ${listResponse.code()}")
                return Result.failure(Exception("Error al cargar lista: ${listResponse.code()}"))
            }

            val listBody = listResponse.body()!!
            Log.d("PAGINATION_DEBUG", "Lista recibida - Total: ${listBody.count}, Resultados: ${listBody.results.size}")

            val nombres = listBody.results.map { it.name }
            Log.d("PAGINATION_DEBUG", "Nombres a procesar: $nombres")

            val pokemones = nombres.mapNotNull { nombre ->
                getPokemonInterno(nombre).getOrNull().also { pokemon ->
                    if (pokemon == null) {
                        Log.w("PAGINATION_DEBUG", "No se pudo obtener: $nombre")
                    }
                }
            }

            Log.d("PAGINATION_DEBUG", "Pokémones obtenidos: ${pokemones.size}/${nombres.size}")
            Log.d("PAGINATION_DEBUG", "=== PAGINACIÓN COMPLETADA ===\n")

            val result = PaginatedResult(
                pokemones = pokemones,
                totalCount = listBody.count,
                hasNext = listBody.next != null,
                hasPrevious = listBody.previous != null
            )

            Result.success(result)

        } catch (e: Exception) {
            Log.e("PAGINATION_DEBUG", "EXCEPCIÓN en paginación: ${e.message}", e)
            Result.failure(e)
        }
    }

    // Obtener un pokemon por nombre (público)
    suspend fun getPokemon(name: String): Result<Pokemon> {
        return getPokemonInterno(name)
    }

    // Obtener un pokemon por ID (público)
    suspend fun getPokemonById(id: Int): Result<Pokemon> {
        return getPokemonInterno(id.toString())
    }

    // Metodo interno para obtener pokemon
    private suspend fun getPokemonInterno(nameOrId: String): Result<Pokemon> {
        return try {
            Log.d("API_DEBUG", "=== INICIANDO LLAMADA API ===")
            Log.d("API_DEBUG", "Buscando Pokémon: $nameOrId")

            // Paso 1: Obtener datos básicos del pokemon
            Log.d("API_DEBUG", "Llamando a getPokemon($nameOrId)")
            val pokemonResponse = api.getPokemon(nameOrId.lowercase())

            Log.d("API_DEBUG", "Respuesta getPokemon - Código: ${pokemonResponse.code()}")
            Log.d("API_DEBUG", "Respuesta getPokemon - Exitosa: ${pokemonResponse.isSuccessful}")

            if (!pokemonResponse.isSuccessful || pokemonResponse.body() == null) {
                Log.e("API_DEBUG", "ERROR en getPokemon: ${pokemonResponse.code()} - ${pokemonResponse.message()}")
                return Result.failure(Exception("Error al cargar $nameOrId: ${pokemonResponse.code()}"))
            }

            val pokemonDto = pokemonResponse.body()!!
            Log.d("API_DEBUG", "PokemonDto recibido:")
            Log.d("API_DEBUG", "  ID: ${pokemonDto.id}")
            Log.d("API_DEBUG", "  Nombre: ${pokemonDto.name}")
            Log.d("API_DEBUG", "  Tipos: ${pokemonDto.types?.size ?: 0}")
            Log.d("API_DEBUG", "  Stats: ${pokemonDto.stats?.size ?: 0}")

            // Debug detallado de stats
            pokemonDto.stats?.forEach { stat ->
                Log.d("API_DEBUG", "    Stat: ${stat.stat?.name} = ${stat.base_stat}")
            }

            // Paso 2: Obtener descripción de especies
            Log.d("API_DEBUG", "Llamando a getPokemonsSpecies(${pokemonDto.id})")
            val speciesResponse = api.getPokemonsSpecies(pokemonDto.id)

            Log.d("API_DEBUG", "Respuesta getPokemonsSpecies - Código: ${speciesResponse.code()}")
            Log.d("API_DEBUG", "Respuesta getPokemonsSpecies - Exitosa: ${speciesResponse.isSuccessful}")

            val speciesDto = if (speciesResponse.isSuccessful) {
                speciesResponse.body().also {
                    Log.d("API_DEBUG", "SpeciesDto recibido - Descripciones: ${it?.flavor_text_entries?.size ?: 0}")
                }
            } else {
                Log.w("API_DEBUG", "No se pudo obtener species, usando null")
                null
            }

            // Paso 3: Mapear a modelo de dominio
            Log.d("API_DEBUG", "Llamando a PokemonMapper.toPokemon()")
            val pokemon = PokemonMapper.toPokemon(pokemonDto, speciesDto)

            Log.d("API_DEBUG", "Pokemon mapeado exitosamente:")
            Log.d("API_DEBUG", "  Nombre: ${pokemon.nombre}")
            Log.d("API_DEBUG", "  Tipo: ${pokemon.tipo}")
            Log.d("API_DEBUG", "  Nivel: ${pokemon.nivel}")
            Log.d("API_DEBUG", "  HP: ${pokemon.hp}")
            Log.d("API_DEBUG", "  Ataque: ${pokemon.ataque}")
            Log.d("API_DEBUG", "  Defensa: ${pokemon.defensa}")
            Log.d("API_DEBUG", "  Velocidad: ${pokemon.velocidad}")
            Log.d("API_DEBUG", "=== LLAMADA API COMPLETADA ===\n")

            Result.success(pokemon)

        } catch (e: Exception) {
            Log.e("API_DEBUG", "EXCEPCIÓN en getPokemonInterno: ${e.message}", e)
            Result.failure(e)
        }
    }

    // Obtener multiples pokemones
    suspend fun getMultiplePokemon(names: List<String>): List<Pokemon> {
        return names.mapNotNull { name ->
            getPokemonInterno(name).getOrNull()
        }
    }
}