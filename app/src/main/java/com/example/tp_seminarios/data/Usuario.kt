package com.example.tp_seminarios.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "usuarios")
data class Usuario(
    @ColumnInfo(name = "nombre_usuario")
    var nombreUsuario: String,
    @ColumnInfo(name = "contrasenia")
    var password: String,
    @ColumnInfo(name = "email")
    var email: String
){
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
    @ColumnInfo(name = "fecha_alta")
    var fechaAlta: LocalDate = LocalDate.now()
}
