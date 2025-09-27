package com.example.tp_seminarios

import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.appcompat.widget.Toolbar
import com.example.tp_seminarios.data.Pokemon
import androidx.appcompat.app.AppCompatActivity
import com.example.tp_seminarios.data.matchPokemonType
import com.example.tp_seminarios.data.splitPokemonTypes
import com.example.tp_seminarios.data.typeToResource


class PokemonDetails : AppCompatActivity() {
    lateinit var toolbar : Toolbar

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> { // flecha atrás
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vista_detallada)

        // TOOLBAR
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Detalle"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // MAPEO
        val pokemon  = intent.extras?.getSerializable("pokemon") as Pokemon

        val txtNombre: TextView = findViewById(R.id.txtNombre)
        var tipo1_imgView: ImageView = findViewById(R.id.tipo1_imageView)
        var tipo2_imgView: ImageView = findViewById(R.id.tipo2_imageView)
        var tipo3_imgView: ImageView = findViewById(R.id.tipo3_imageView)
        val txtNivel: TextView = findViewById(R.id.txtNivel)
        val txtHp: TextView = findViewById(R.id.txtHp)
        val txtAtaque: TextView = findViewById(R.id.txtAtaque)
        val txtDefensa: TextView = findViewById(R.id.txtDefensa)
        val txtVelocidad: TextView = findViewById(R.id.txtVelocidad)
        val txtDescripcion: TextView = findViewById(R.id.txtDescripcion)

        // tipos
        var imageViews = listOf(tipo1_imgView, tipo2_imgView, tipo3_imgView)
        var typesList = splitPokemonTypes(pokemon.tipo)

        for ((index, tipo) in typesList.withIndex ())
        {
            if (index < imageViews.size && tipo != null)
            {
                imageViews[index].setImageResource (
                    typeToResource (matchPokemonType (tipo))
                )
            }
        }

        txtNombre.text = pokemon.nombre
        txtNivel.text = "Nivel: ${pokemon.nivel}"
        txtHp.text = "HP: ${pokemon.hp}"
        txtAtaque.text = "Ataque: ${pokemon.ataque}"
        txtDefensa.text = "Defensa: ${pokemon.defensa}"
        txtVelocidad.text = "Velocidad: ${pokemon.velocidad}"
        txtDescripcion.text = pokemon.descripcion
    }




}