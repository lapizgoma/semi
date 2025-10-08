package com.example.tp_seminarios.data.dto

data class PokemonListResponse(val count: Int,
                               val next: String?,
                               val previous: String?,
                               val results: List<PokemonBasicDto>)
