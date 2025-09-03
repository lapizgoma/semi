package com.example.tp_seminarios

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tp_seminarios.data.Pokemon

class PokemonAdapter(
    var pokemones: MutableList<Pokemon>,
    var context: Context
) : RecyclerView.Adapter<PokemonAdapter.PokemonViewHolder>() {

    class PokemonViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtNombre: TextView = view.findViewById(R.id.tvNombre)
        val txtTipo: TextView = view.findViewById(R.id.tvTipo)
        val txtNivel: TextView = view.findViewById(R.id.tvNivel)
    }

    override fun getItemCount(): Int = pokemones.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PokemonViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pokemon, parent, false)
        return PokemonViewHolder(view)
    }

    override fun onBindViewHolder(holder: PokemonViewHolder, position: Int) {
        val item = pokemones[position]
        // Mostramos solo datos principales en la lista
        holder.txtNombre.text = item.nombre
        holder.txtTipo.text = "Tipo: ${item.tipo}"
        holder.txtNivel.text = "Nivel: ${item.nivel}"

        // Aqui se realiza el evento del click para que muestre los detalles del pokemon
    }
}
