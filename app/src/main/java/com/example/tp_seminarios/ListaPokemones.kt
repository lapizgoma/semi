package com.example.tp_seminarios

import android.os.Bundle
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.tp_seminarios.data.Pokemon

class ListaPokemones : AppCompatActivity() {

    // Declaracion de variables
    private lateinit var rvPokemones: RecyclerView
    private lateinit var pokemonAdapter : PokemonAdapter
    private lateinit var toolbar: Toolbar

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
        enableEdgeToEdge()
        setContentView(R.layout.activity_lista_pokemones)

        // Inicializamos las variables
        toolbar = findViewById(R.id.toolbar)
        rvPokemones = findViewById(R.id.rvPokemones)
        pokemonAdapter = PokemonAdapter(getPokemones(),this)

        // Logica
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.lista_pokemones)
        // Flecha para volver hacia atras
        rvPokemones.adapter = pokemonAdapter
    }

    private fun getPokemones(): MutableList<Pokemon>{

        return mutableListOf(
            Pokemon(
                "Pikachu", "Eléctrico", 15, 35, 55, 40, 90,
                "Un Pokémon ratón eléctrico. Almacena electricidad en sus mejillas."
            ),
            Pokemon(
                "Charmander", "Fuego", 12, 39, 52, 43, 65,
                "Prefiere cosas calientes. Cuando llueve, se dice que echa vapor de la punta de su cola."
            ),
            Pokemon(
                "Squirtle", "Agua", 10, 44, 48, 65, 43,
                "Su caparazón no solo lo protege, también le ayuda a nadar más rápido."
            ),
            Pokemon(
                "Bulbasaur", "Planta/Veneno", 14, 45, 49, 49, 45,
                "Este Pokémon nace con una semilla en el lomo que crece con él."
            ),
            Pokemon(
                "Jigglypuff", "Normal/Hada", 18, 115, 45, 20, 20,
                "Usa su dulce voz para cantar y dormir a sus oponentes."
            )
        )
    }
}