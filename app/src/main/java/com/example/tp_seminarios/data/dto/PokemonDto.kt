package com.example.tp_seminarios.data.dto

data class PokemonDto(val id: Int, val name: String, val types: List<TypeSlotDto>, val stats: List<StatInfoDto>)
