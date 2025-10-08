package com.example.tp_seminarios

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tp_seminarios.data.Pokemon
import com.example.tp_seminarios.data.matchPokemonType
import com.example.tp_seminarios.data.repository.PokemonRepository
import com.example.tp_seminarios.data.splitPokemonTypes
import com.example.tp_seminarios.data.typeToResource

class PokemonAdapter(
    var pokemones: MutableList<Pokemon>,
    var context: Context,
    var onItemClick : (Pokemon) -> Unit
) : RecyclerView.Adapter<PokemonAdapter.PokemonViewHolder>() {

    class PokemonViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtNombre: TextView = view.findViewById(R.id.tvNombre)
        val tipo1_imgView: ImageView = view.findViewById(R.id.tvTipo1)
        val tipo2_imgView: ImageView = view.findViewById(R.id.tvTipo2)
        val tipo3_imgView: ImageView = view.findViewById(R.id.tvTipo3)
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

        // Mostrar nombre
        holder.txtNombre.text = item.nombre
        holder.txtNivel.text = "Nivel: ${item.nivel}"

        // Manejar tipos - ahora usamos la lista directamente
        val imageViews = listOf(holder.tipo1_imgView, holder.tipo2_imgView, holder.tipo3_imgView)

        // Ocultar todas las ImageViews primero
        imageViews.forEach { it.visibility = View.GONE }

        // Mostrar los tipos que existen en la lista
        for ((index, tipo) in item.tipo.withIndex()) {
            if (index < imageViews.size) {
                try {
                    // Verificar si el tipo está en español (basado en tu mapa)
                    val spanishTypes = listOf("fuego", "agua", "planta", "electrico", "hielo",
                        "lucha", "veneno", "tierra", "volador", "psiquico", "bicho",
                        "roca", "fantasma", "dragon", "siniestro", "acero", "hada", "normal")

                    val englishType = if (tipo.lowercase() in spanishTypes) {
                        // Si los tipos están en español, usar matchPokemonType para convertirlos
                        matchPokemonType(tipo)
                    } else {
                        // Si ya estan en ingles, usar directamente
                        tipo.lowercase()
                    }

                    val resourceId = typeToResource(englishType)
                    imageViews[index].setImageResource(resourceId)
                    imageViews[index].visibility = View.VISIBLE
                } catch (e: Exception) {
                    imageViews[index].visibility = View.GONE
                }
            }
        }

        // Click listener
        holder.itemView.setOnClickListener {
            onItemClick(item)
        }
    }
}