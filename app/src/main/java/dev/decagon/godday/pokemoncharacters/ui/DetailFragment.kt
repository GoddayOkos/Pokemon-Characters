package dev.decagon.godday.pokemoncharacters.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import dev.decagon.godday.pokemoncharacters.R
import dev.decagon.godday.pokemoncharacters.bindImage
import dev.decagon.godday.pokemoncharacters.databinding.FragmentDetailBinding
import dev.decagon.godday.pokemoncharacters.models.PokemonCharacters
import dev.decagon.godday.pokemoncharacters.network.ApiStatus
import dev.decagon.godday.pokemoncharacters.viewmodels.DetailViewModel

/**
 * This fragment displays more details about a pokemon character that was clicked
 */

class DetailFragment : Fragment() {
    private lateinit var viewModel: DetailViewModel
    private lateinit var image: ImageView
    private lateinit var name: TextView
    private lateinit var features: TextView
    private lateinit var features2: TextView
    private lateinit var statusImage: ImageView
    private lateinit var status: TextView
    private lateinit var pokemonCharacter: PokemonCharacters

    private var _binding: FragmentDetailBinding? = null
    private val binding: FragmentDetailBinding
        get() = _binding!!

    companion object {
        val idMap = mutableMapOf<String, String>()
        const val ID = "id"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        pokemonCharacter = DetailFragmentArgs.fromBundle(arguments!!).pokemonCharacter
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this).get(DetailViewModel::class.java)
        image = binding.mainPhotoImage
        bindImage(pokemonCharacter.imageUrl, image)
        name = binding.name.also { it.text = pokemonCharacter.name.capitalize() }
        features = binding.items
        features2 = binding.items2

        status = binding.errorMsg
        statusImage = binding.statusImage

        viewModel.status.observe(viewLifecycleOwner, {
            if (it == ApiStatus.ERROR) {
                statusImage.visibility = View.VISIBLE
               // status.text = viewModel.message.value
                status.visibility = View.VISIBLE
            }
        })

        // Using viewModel to observe the UI and display the details of the pokemon
        // character that was clicked
        viewModel.pokemon.observe(viewLifecycleOwner, {
            bindImage(it.sprites.frontDefault, binding.pokemonImage1)
            bindImage(it.sprites.backDefault, binding.pokemonImage2)
            bindImage(it.sprites.frontShiny, binding.pokemonImage3)
            bindImage(it.sprites.backShiny, binding.pokemonImage4)

            features.text = getString(R.string.features_text, it.abilities[0].ability.name.capitalize(),
                    it.abilities[0].isHidden.toString(), it.abilities[0].slot.toString(), it.abilities[1].ability.name.capitalize(),
                    it.abilities[1].isHidden.toString(), it.abilities[1].slot.toString(), it.baseExperience.toString())

            features2.text = getString(R.string.features_text_2, it.gameIndices[0].gameIndex.toString(),
                    it.height.toString(), it.id.toString(),
                    it.moves[0].move.name.capitalize(), it.moves[1].move.name.capitalize(), it.moves[2].move.name.capitalize(),
                    it.moves[3].move.name.capitalize(), it.moves[4].move.name.capitalize(), it.moves[0].move.name.capitalize(),
                    it.moves[2].move.name.capitalize(), it.moves[4].move.name.capitalize(), it.moves[1].move.name.capitalize(),
                    it.moves[3].move.name.capitalize(), it.stats[0].baseStat.toString(), it.stats[0].effort.toString(),
                    it.stats[0].stat.name,
                    it.stats[1].baseStat.toString(), it.stats[1].effort.toString(), it.stats[1].stat.name,
                    it.stats[2].baseStat.toString(), it.stats[2].effort.toString(),
                    it.stats[2].stat.name, it.stats[3].baseStat.toString(), it.stats[3].effort.toString(), it.stats[3].stat.name,
                    it.stats[4].baseStat.toString(),
                    it.stats[4].effort.toString(), it.stats[4].stat.name, it.stats[5].baseStat.toString(),
                    it.stats[5].effort.toString(), it.stats[5].stat.name,
                    it.types[0].slot.toString(), it.types[0].type.name, it.weight.toString()
            )
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}