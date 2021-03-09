package dev.decagon.godday.pokemoncharacters.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.decagon.godday.pokemoncharacters.IMAGE_URL
import dev.decagon.godday.pokemoncharacters.bindImage
import dev.decagon.godday.pokemoncharacters.databinding.RecyclerViewBinding
import dev.decagon.godday.pokemoncharacters.getIndexFromUrl
import dev.decagon.godday.pokemoncharacters.models.PokemonCharacters


/**
 * This is the recyclerview adapter class that display the images of all pokemon characters
 * retrieved from the API using a grid layout manager. It also uses a custom click listener to
 * respond to click events, when a view is clicked.
 */

class PokemonCharacterAdapter(private val onClickListener: OnClickListener) :
        RecyclerView.Adapter<PokemonCharacterAdapter.PokemonCharactersViewHolder>() {
    private var pokemonCharacters = mutableListOf<PokemonCharacters>()

    class PokemonCharactersViewHolder(private val binding: RecyclerViewBinding) :
            RecyclerView.ViewHolder(binding.root) {

        // This method binds views with the appropriate data. It uses glide library in
        // loading images into image views.
        fun bind(pokemonCharacter: PokemonCharacters) {
            val name = binding.pokemonName
            val image = binding.pokemonImage
            name.text = pokemonCharacter.name.capitalize()
            val pokemonIndex = getIndexFromUrl(pokemonCharacter.url)
            val pokemonImgUrl = "$IMAGE_URL$pokemonIndex.png"
            pokemonCharacter.imageUrl = pokemonImgUrl
            pokemonCharacter.index = pokemonIndex
            bindImage(pokemonImgUrl, image)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PokemonCharactersViewHolder {
        val binding = RecyclerViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PokemonCharactersViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PokemonCharactersViewHolder, position: Int) {
        val pokemon = pokemonCharacters[position]
        holder.itemView.setOnClickListener { onClickListener.onClick(pokemon) }
        holder.bind(pokemon)
    }

    override fun getItemCount(): Int = pokemonCharacters.size

    // This method is used to supply the adapter class with the data retrieved from the API.
    fun loadPokemonCharacters(pokemon: List<PokemonCharacters>) {
        this.pokemonCharacters = pokemon as MutableList<PokemonCharacters>
        notifyDataSetChanged()
    }

    // Custom clickListener that handles click events
    class OnClickListener(val clickListener: (pokemon: PokemonCharacters) -> Unit) {
        fun onClick(pokemon: PokemonCharacters) = clickListener(pokemon)
    }
}