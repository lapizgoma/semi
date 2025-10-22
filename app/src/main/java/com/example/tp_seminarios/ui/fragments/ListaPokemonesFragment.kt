package com.example.tp_seminarios.ui.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tp_seminarios.PokemonAdapter
import com.example.tp_seminarios.PokemonDetails
import com.example.tp_seminarios.R
import com.example.tp_seminarios.data.Pokemon
import com.example.tp_seminarios.data.repository.PokemonRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

class ListaPokemonesFragment : Fragment() {

    private lateinit var rvPokemones: RecyclerView
    private lateinit var pokemonAdapter: PokemonAdapter
    private lateinit var btnAnterior: Button
    private lateinit var btnSiguiente: Button
    private lateinit var txtPagina: TextView
    private lateinit var progressBar: ProgressBar

    // Para la búsqueda
    private lateinit var searchView: SearchView

    private val repository = PokemonRepository()
    private var paginaActual = 1
    private val pokemonesPorPagina = 20
    private var totalPaginas = 1
    private var cargando = false

    // Listas para manejar búsqueda
    private var todosLosPokemones: MutableList<Pokemon> = mutableListOf()
    private var pokemonesFiltrados: MutableList<Pokemon> = mutableListOf()
    private var modoBusqueda = false
    private var queryActual = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_lista_pokemones, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        inicializarVistas(view)
        configurarRecyclerView()
        configurarBotones()
        cargarPagina(paginaActual)
    }

    private fun inicializarVistas(view: View) {
        rvPokemones = view.findViewById(R.id.rvPokemones)
        btnAnterior = view.findViewById(R.id.btnAnterior)
        btnSiguiente = view.findViewById(R.id.btnSiguiente)
        txtPagina = view.findViewById(R.id.txtPagina)
        progressBar = view.findViewById(R.id.progressBar)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_lista_pokemones, menu)

        // Configurar el ítem de búsqueda
        val searchItem = menu.findItem(R.id.action_search)
        searchView = searchItem.actionView as SearchView

        configurarSearchView()

        super.onCreateOptionsMenu(menu, inflater)
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
                if (query.trim().isNotBlank()) {
                    buscarPokemon(query.trim())
                }
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                queryActual = newText.trim()

                if (queryActual.length >= 3) {
                    lifecycleScope.launch {
                        delay(500)
                        if (queryActual == newText.trim()) {
                            buscarPokemon(queryActual)
                        }
                    }
                } else if (queryActual.isEmpty()) {
                    salirModoBusqueda()
                }
                return true
            }
        })
    }

    private fun configurarRecyclerView() {
        pokemonAdapter = PokemonAdapter(mutableListOf(), requireContext()) { pokemon ->
            val intent = Intent(requireContext(), PokemonDetails::class.java)
            intent.putExtra("pokemon", pokemon)
            startActivity(intent)
        }
        rvPokemones.layoutManager = LinearLayoutManager(requireContext())
        rvPokemones.adapter = pokemonAdapter
    }

    private fun configurarBotones() {
        btnAnterior.setOnClickListener {
            if (modoBusqueda) {
                Toast.makeText(requireContext(), "Salga del modo búsqueda para usar paginación", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (paginaActual > 1 && !cargando) {
                paginaActual--
                cargarPagina(paginaActual)
            }
        }

        btnSiguiente.setOnClickListener {
            if (modoBusqueda) {
                Toast.makeText(requireContext(), "Salga del modo búsqueda para usar paginación", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (paginaActual < totalPaginas && !cargando) {
                paginaActual++
                cargarPagina(paginaActual)
            }
        }
    }

    private fun buscarPokemon(query: String) {
        if (query.length < 2) return

        modoBusqueda = true
        val queryLower = query.trim().lowercase()

        mostrarCargando(true)
        cargando = true

        lifecycleScope.launch {
            repository.getPokemon(queryLower).fold(
                onSuccess = { pokemon ->
                    pokemonesFiltrados.clear()
                    pokemonesFiltrados.add(pokemon)
                    pokemonAdapter.pokemones = pokemonesFiltrados
                    pokemonAdapter.notifyDataSetChanged()
                    txtPagina.text = "Resultado de búsqueda: ${pokemon.nombre}"
                    mostrarCargando(false)
                    cargando = false
                },
                onFailure = { error ->
                    Toast.makeText(
                        requireContext(),
                        "No se encontró el Pokémon: ${queryLower.replaceFirstChar { it.titlecase() }}",
                        Toast.LENGTH_SHORT
                    ).show()

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

        searchView.setQuery("", false)
        searchView.isIconified = true

        if (todosLosPokemones.isNotEmpty()) {
            pokemonAdapter.pokemones = todosLosPokemones.toMutableList()
            pokemonAdapter.notifyDataSetChanged()
            actualizarPaginacion(paginaActual, paginaActual > 1, paginaActual < totalPaginas)
        }

        hideKeyboard()
    }

    private fun hideKeyboard() {
        val view = activity?.currentFocus
        view?.let {
            val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
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
                    totalPaginas = (resultado.totalCount + pokemonesPorPagina - 1) / pokemonesPorPagina

                    todosLosPokemones.clear()
                    todosLosPokemones.addAll(resultado.pokemones)

                    pokemonAdapter.pokemones = todosLosPokemones.toMutableList()
                    pokemonAdapter.notifyDataSetChanged()

                    actualizarPaginacion(pagina, resultado.hasPrevious, resultado.hasNext)
                    rvPokemones.scrollToPosition(0)

                    mostrarCargando(false)
                    cargando = false
                },
                onFailure = { error ->
                    Toast.makeText(
                        requireContext(),
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