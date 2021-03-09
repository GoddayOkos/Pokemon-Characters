package dev.decagon.godday.pokemoncharacters.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.decagon.godday.pokemoncharacters.models.PokemonDetails
import dev.decagon.godday.pokemoncharacters.network.ApiStatus
import dev.decagon.godday.pokemoncharacters.network.PokemonDetailsApiService
import dev.decagon.godday.pokemoncharacters.ui.DetailFragment
import kotlinx.coroutines.launch

/**
 * This viewModel class handles all networks operation on behalf of the
 * details fragment and updates the fragment's UI accordingly. It uses a coroutine scope in
 * making network calls to the different endpoints.
 */

class DetailViewModel : ViewModel() {

    // Holds the details of the pokemon retrieved from the API
    private val _pokemon = MutableLiveData<PokemonDetails>()
    val pokemon = _pokemon as LiveData<PokemonDetails>

    // Keeps tracks of the API status
    private val _status = MutableLiveData<ApiStatus>()
    val status = _status as LiveData<ApiStatus>

    // Keeps track of exceptions which may occur during network call
    private val _message = MutableLiveData<String>()
    val message = _message as LiveData<String>

    init {
        // make a network call to the API with the id of the pokemon
        // character clicked in the overview page and populate the UI with the details retrieved.
        getPokemonDetailedInfo(DetailFragment.idMap[DetailFragment.ID]!!)
    }

    // Network call to the endpoints with error handling
    private fun getPokemonDetailedInfo(pokemonId: String) {
        viewModelScope.launch {
            _status.value = ApiStatus.LOADING
            try {
                _pokemon.value = PokemonDetailsApiService.PokemonDetailsApi.retrofitDService.getPokemonDetails(pokemonId)
                _status.value = ApiStatus.DONE
            } catch (e: Exception) {
                _status.value = ApiStatus.ERROR
                _message.value = "${status.value}: ${e.message}"
                println("Error: ${e.message}")
            }
        }
    }

}