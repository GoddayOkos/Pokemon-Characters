package dev.decagon.godday.pokemoncharacters.ui

import android.os.Bundle
import android.view.*
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.decagon.godday.pokemoncharacters.DEFAULT_POKEMON_SIZE
import dev.decagon.godday.pokemoncharacters.R
import dev.decagon.godday.pokemoncharacters.adapter.PokemonCharacterAdapter
import dev.decagon.godday.pokemoncharacters.databinding.FragmentOverviewBinding
import dev.decagon.godday.pokemoncharacters.doneButtonAction
import dev.decagon.godday.pokemoncharacters.network.ApiStatus
import dev.decagon.godday.pokemoncharacters.viewmodels.OverviewViewModel

/**
 * This fragment displays the overview of the pokemon characters.
 * It also holds logic for setting the number of pokemon characters to display
 * as well as the login for navigating to the other pages of the app.
 */

class OverviewFragment : Fragment() {

    private var _binding: FragmentOverviewBinding? = null
    private val binding: FragmentOverviewBinding
        get() = _binding!!
    private lateinit var viewModel: OverviewViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var statusImage: ImageView
    private lateinit var status: TextView
    private lateinit var adapter: PokemonCharacterAdapter
    private lateinit var searchBox: EditText
    private lateinit var doneIcon: ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOverviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel = ViewModelProvider(this).get(OverviewViewModel::class.java)
        statusImage = binding.statusImage
        status = binding.errorMsg
        searchBox = binding.filterNumber
        doneIcon = binding.done

        // Sets the number of elements on the screen according to user's requirement
        // and hides it's visibility and that of the search box

        doneIcon.setOnClickListener {
            searchBox.visibility = View.GONE
            doneIcon.visibility = View.GONE
            viewModel.listenToFilter(doneButtonAction(requireContext(), searchBox.text.toString()))
            searchBox.text.clear()
        }

        // Initializing the Pokemon character adapter and defining the actions
        // of the custom click listener

        adapter = PokemonCharacterAdapter(PokemonCharacterAdapter.OnClickListener {
            DetailFragment.idMap[DetailFragment.ID] = it.index
            val action = OverviewFragmentDirections.actionOverviewFragmentToDetailFragment(it)
            view.findNavController().navigate(action)
        })

        recyclerView = binding.recyclerViewGrid
        recyclerView.layoutManager = GridLayoutManager(context, 2)
        recyclerView.adapter = adapter

        // Observing and updating the UI with viewModel and passing data
        // retrieved from the API to the adapter
        viewModel.characters.observe(viewLifecycleOwner, {
            if (viewModel.status.value == ApiStatus.ERROR) {
                statusImage.visibility = View.VISIBLE
                statusImage.setImageResource(R.drawable.ic_connection_error)
                status.visibility = View.VISIBLE
                status.text = viewModel.message.value
            }
            adapter.loadPokemonCharacters(it)
        })

    }

    // Setup menu
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.overview_page_menu, menu)
    }

    // Configuring menu options action
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.enterGallery -> {
                findNavController().navigate(R.id.action_overviewFragment_to_photoReaderFragment)
            }
            R.id.filterList -> showFilteringPopUpMenu()
            R.id.exit -> {
                activity?.finish()
            }
        }
        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Setup extra menu options
    private fun showFilteringPopUpMenu() {
        val view = activity?.findViewById<View>(R.id.filterList) ?: return
        PopupMenu(requireContext(), view).run {
            menuInflater.inflate(R.menu.filter_item_menu, menu)

            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.enterNumber -> {
                        searchBox.visibility = View.VISIBLE
                        doneIcon.visibility = View.VISIBLE
                    }
                    R.id.all -> viewModel.listenToFilter(DEFAULT_POKEMON_SIZE)
                }
                true
            }
            show()
        }
    }

}