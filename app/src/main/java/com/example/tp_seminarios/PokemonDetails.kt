package com.example.tp_seminarios

import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.appcompat.widget.Toolbar
import com.example.tp_seminarios.data.Pokemon
import androidx.appcompat.app.AppCompatActivity


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
        val txtTipo: TextView = findViewById(R.id.txtTipo)
        val txtNivel: TextView = findViewById(R.id.txtNivel)
        val txtHp: TextView = findViewById(R.id.txtHp)
        val txtAtaque: TextView = findViewById(R.id.txtAtaque)
        val txtDefensa: TextView = findViewById(R.id.txtDefensa)
        val txtVelocidad: TextView = findViewById(R.id.txtVelocidad)
        val txtDescripcion: TextView = findViewById(R.id.txtDescripcion)

        txtNombre.text = pokemon.nombre
        txtTipo.text = "Tipo: ${pokemon.tipo}"
        txtNivel.text = "Nivel: ${pokemon.nivel}"
        txtHp.text = "HP: ${pokemon.hp}"
        txtAtaque.text = "Ataque: ${pokemon.ataque}"
        txtDefensa.text = "Defensa: ${pokemon.defensa}"
        txtVelocidad.text = "Velocidad: ${pokemon.velocidad}"
        txtDescripcion.text = pokemon.descripcion
    }
}