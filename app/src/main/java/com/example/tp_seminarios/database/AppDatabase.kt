package com.example.tp_seminarios.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.tp_seminarios.config.Converters
import com.example.tp_seminarios.data.Usuario
import com.example.tp_seminarios.repositories.UsuarioDao

@Database(entities = [Usuario::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase: RoomDatabase() {
    abstract fun usuarioDao(): UsuarioDao

    companion object{
        private var INSTANCIA: AppDatabase?=null
        fun getDataBase(context: Context): AppDatabase {
            if(INSTANCIA == null){
                synchronized(this){
                    INSTANCIA = Room.databaseBuilder(
                        context,
                        AppDatabase::class.java,"dbmon")
                        .allowMainThreadQueries()
                        .fallbackToDestructiveMigration()
                        .build()
                }
            }
            return INSTANCIA!!;
        }
    }
}