package com.example.tp_seminarios.services

import com.example.tp_seminarios.data.dto.PokemonDto
import com.example.tp_seminarios.data.dto.PokemonListResponse
import com.example.tp_seminarios.data.dto.PokemonSpeciesDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PokeApiService {

    @GET("pokemon")
    suspend fun getPokemonList(
        @Query("limit") limit : Int,
        @Query("offset") offset: Int = 0
    ): Response<PokemonListResponse>

    @GET("pokemon/{name}")
    suspend fun getPokemon(
                @Path("name") name: String
    ): Response<PokemonDto>

    @GET("pokemon-species/{nameOrId}")
    suspend fun getPokemonsSpecies(
        @Path("nameOrId") nameOrId: String
    ): Response<PokemonSpeciesDto>
}