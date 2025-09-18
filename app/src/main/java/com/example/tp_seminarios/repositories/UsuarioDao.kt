package com.example.tp_seminarios.repositories

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.tp_seminarios.data.Usuario

@Dao
interface UsuarioDao {
    @Query("SELECT * FROM usuarios")
    fun getAll(): List<Usuario>

    @Query("SELECT * FROM usuarios as u WHERE u.id = :id")
    fun getUsuarioPorId(id: Int): Usuario?

    @Query("SELECT * FROM usuarios as u WHERE u.email = :email")
    fun getUsuarioPorEmail(email: String): Usuario?

    @Query("SELECT * FROM usuarios as u WHERE u.email = :email AND u.contrasenia = :password")
    fun getUsuarioPorEmailYPassword(email: String, password: String): Usuario?

    @Insert
    fun insertUsuario(usuario: Usuario)

    @Delete
    fun deleteUsuario(usuario: Usuario)

}