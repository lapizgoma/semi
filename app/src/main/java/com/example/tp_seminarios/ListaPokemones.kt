package com.example.tp_seminarios

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.tp_seminarios.data.Pokemon
import com.example.tp_seminarios.data.repository.PokemonRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import okhttp3.Dispatcher
import kotlinx.coroutines.*


class ListaPokemones : AppCompatActivity() {

    // Declaracion de variables
    private lateinit var rvPokemones: RecyclerView
    private lateinit var pokemonAdapter : PokemonAdapter
    private lateinit var toolbar: Toolbar
    private lateinit var btnAnterior: Button
    private lateinit var btnSiguiente: Button
    private lateinit var txtPagina: TextView
    private lateinit var progressBar: ProgressBar

    // Para la búsqueda
    private lateinit var searchView: SearchView
    private val repository = PokemonRepository()
    private var paginaActual = 1
    private val pokemonesPorPagina = 10
    private var totalPaginas = 1
    private var cargando = false

    // Listas para manejar búsqueda
    private var todosLosPokemones: MutableList<Pokemon> = mutableListOf()
    private var pokemonesFiltrados: MutableList<Pokemon> = mutableListOf()
    private var modoBusqueda = false
    private var queryActual = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_lista_pokemones)

        inicializarVistas()
        configurarToolbar()
        configurarRecyclerView()
        configurarBotones()

        cargarPagina(paginaActual)
    }

    private fun inicializarVistas() {
        toolbar = findViewById(R.id.toolbar)
        rvPokemones = findViewById(R.id.rvPokemones)
        btnAnterior = findViewById(R.id.btnAnterior)
        btnSiguiente = findViewById(R.id.btnSiguiente)
        txtPagina = findViewById(R.id.txtPagina)
        progressBar = findViewById(R.id.progressBar)
        // searchView se inicializa desde el menú
    }

    private fun configurarToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Lista de Pokémones"
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_lista_pokemones, menu)

        // Configurar el ítem de búsqueda
        val searchItem = menu.findItem(R.id.action_search)
        searchView = searchItem.actionView as SearchView

        configurarSearchView()

        return true
    }

    private fun configurarSearchView() {
        searchView.queryHint = "Buscar Pokémon..."

        // Cuando se expande el SearchView
        searchView.setOnSearchClickListener {
            modoBusqueda = true
        }

        // Cuando se cierra el SearchView
        searchView.setOnCloseListener {
            salirModoBusqueda()
            false
        }

        // Cuando se escribe texto
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                // Se llama cuando se presiona enter
                if (query.trim().isNotBlank()) {
                    buscarPokemon(query.trim())
                }
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                // Se llama con cada cambio de texto
                queryActual = newText.trim()

                if (queryActual.length >= 3) {
                    // Buscar después de 3 caracteres (busqueda en tiempo real)
                    lifecycleScope.launch {
                        delay(500) // Delay un poco mas largo para no saturar la API
                        if (queryActual == newText.trim()) {
                            buscarPokemon(queryActual)
                        }
                    }
                } else if (queryActual.isEmpty()) {
                    // Si se borra el texto, volver a la lista normal
                    salirModoBusqueda()
                }
                return true
            }
        })
    }

    private fun configurarRecyclerView() {
        pokemonAdapter = PokemonAdapter(mutableListOf(), this) { pokemon ->
            val intent = Intent(this, PokemonDetails::class.java)
            intent.putExtra("pokemon", pokemon)
            startActivity(intent)
        }
        rvPokemones.adapter = pokemonAdapter
    }

    private fun configurarBotones() {
        btnAnterior.setOnClickListener {
            if (modoBusqueda) {
                Toast.makeText(this, "Salga del modo búsqueda para usar paginación", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (paginaActual > 1 && !cargando) {
                paginaActual--
                cargarPagina(paginaActual)
            }
        }

        btnSiguiente.setOnClickListener {
            if (modoBusqueda) {
                Toast.makeText(this, "Salga del modo búsqueda para usar paginación", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (paginaActual < totalPaginas && !cargando) {
                paginaActual++
                cargarPagina(paginaActual)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                if (modoBusqueda) {
                    // Si está en modo busqueda, cerrar la búsqueda
                    salirModoBusqueda()
                    true
                } else {
                    finish()
                    true
                }
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun buscarPokemon(query: String) {
        if (query.length < 2) return

        modoBusqueda = true
        val queryLower = query.trim().lowercase()

        // Mostrar carga inmediatamente
        mostrarCargando(true)
        cargando = true

        lifecycleScope.launch {
            // Usar directamente el endpoint de búsqueda por nombre
            repository.getPokemon(queryLower).fold(
                onSuccess = { pokemon ->
                    // Éxito: mostrar el Pokémon encontrado
                    pokemonesFiltrados.clear()
                    pokemonesFiltrados.add(pokemon)
                    pokemonAdapter.pokemones = pokemonesFiltrados
                    pokemonAdapter.notifyDataSetChanged()
                    txtPagina.text = "Resultado de búsqueda: ${pokemon.nombre}"
                    mostrarCargando(false)
                    cargando = false
                },
                onFailure = { error ->
                    // Error: no se encontró el Pokémon
                    Toast.makeText(
                        this@ListaPokemones,
                        "No se encontró el Pokémon: ${queryLower.replaceFirstChar { it.titlecase() }}",
                        Toast.LENGTH_SHORT
                    ).show()

                    // Limpiar resultados
                    pokemonesFiltrados.clear()
                    pokemonAdapter.pokemones = mutableListOf()
                    pokemonAdapter.notifyDataSetChanged()
                    txtPagina.text = "Sin resultados"
                    mostrarCargando(false)
                    cargando = false
                }
            )
        }
    }

    private fun salirModoBusqueda() {
        if (!modoBusqueda) return

        modoBusqueda = false
        queryActual = ""

        // Cerrar el SearchView
        searchView.setQuery("", false)
        searchView.isIconified = true

        // Volver a mostrar la página actual
        if (todosLosPokemones.isNotEmpty()) {
            pokemonAdapter.pokemones = todosLosPokemones.toMutableList()
            pokemonAdapter.notifyDataSetChanged()
            actualizarPaginacion(paginaActual, paginaActual > 1, paginaActual < totalPaginas)
        }

        // Ocultar teclado
        hideKeyboard()
    }

    // Función auxiliar para ocultar teclado
    private fun hideKeyboard() {
        val view = this.currentFocus
        view?.let {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }

    private fun cargarPagina(pagina: Int) {
        if (cargando) return

        cargando = true
        mostrarCargando(true)

        val offset = (pagina - 1) * pokemonesPorPagina

        lifecycleScope.launch {
            repository.getPokemonsPaginated(pokemonesPorPagina, offset).fold(
                onSuccess = { resultado ->
                    // Calcular total de páginas
                    totalPaginas = (resultado.totalCount + pokemonesPorPagina - 1) / pokemonesPorPagina

                    // Actualizar listas
                    todosLosPokemones.clear()
                    todosLosPokemones.addAll(resultado.pokemones)

                    pokemonAdapter.pokemones = todosLosPokemones.toMutableList()
                    pokemonAdapter.notifyDataSetChanged()

                    // Actualizar UI de paginación
                    actualizarPaginacion(pagina, resultado.hasPrevious, resultado.hasNext)

                    // Scroll al inicio
                    rvPokemones.scrollToPosition(0)

                    mostrarCargando(false)
                    cargando = false
                },
                onFailure = { error ->
                    Toast.makeText(
                        this@ListaPokemones,
                        "Error al cargar: ${error.message}",
                        Toast.LENGTH_LONG
                    ).show()

                    mostrarCargando(false)
                    cargando = false
                }
            )
        }
    }

    private fun actualizarPaginacion(pagina: Int, hasPrevious: Boolean, hasNext: Boolean) {
        txtPagina.text = "Página $pagina de $totalPaginas"
        btnAnterior.isEnabled = hasPrevious
        btnSiguiente.isEnabled = hasNext
    }

    private fun mostrarCargando(mostrar: Boolean) {
        progressBar.visibility = if (mostrar) View.VISIBLE else View.GONE
        rvPokemones.visibility = if (mostrar) View.GONE else View.VISIBLE

        if (mostrar) {
            btnAnterior.isEnabled = false
            btnSiguiente.isEnabled = false
        }
    }
}