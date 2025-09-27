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
import com.example.tp_seminarios.data.splitPokemonTypes
import com.example.tp_seminarios.data.typeToResource

class PokemonAdapter(
    var pokemones: MutableList<Pokemon>,
    var context: Context
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
        // Mostramos solo datos principales en la lista
        holder.txtNombre.text = item.nombre
        // tipos
        var imageViews = listOf(holder.tipo1_imgView, holder.tipo2_imgView, holder.tipo3_imgView)
        var typesList = splitPokemonTypes(item.tipo)

        for ((index, tipo) in typesList.withIndex ())
        {
            if (index < imageViews.size && tipo != null)
            {
                imageViews[index].setImageResource (
                    typeToResource (matchPokemonType (tipo))
                )
            }
        }

        holder.txtNivel.text = "Nivel: ${item.nivel}"

        // Aqui se realiza el evento del click para que muestre los detalles del pokemon
        holder.itemView.setOnClickListener {
            val intent = Intent(context, PokemonDetails::class.java)
            intent.putExtra("pokemon", item)
            context.startActivity(intent)
        }
    }
}
