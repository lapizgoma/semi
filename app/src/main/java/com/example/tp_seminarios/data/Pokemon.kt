package com.example.tp_seminarios.data

import java.io.Serializable
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