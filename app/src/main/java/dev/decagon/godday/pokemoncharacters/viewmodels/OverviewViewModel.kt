package dev.decagon.godday.pokemoncharacters.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.decagon.godday.pokemoncharacters.DEFAULT_POKEMON_SIZE
import dev.decagon.godday.pokemoncharacters.models.PokemonCharacters
import dev.decagon.godday.pokemoncharacters.network.PokemonApiService
import dev.decagon.godday.pokemoncharacters.network.ApiStatus
import kotlinx.coroutines.launch

/**
 * This viewModel class handles all networks operation on behalf of the
 * overview fragment and updates the fragment accordingly. It uses a coroutine scope in
 * making network calls to the endpoints.
 */

class OverviewViewModel : ViewModel() {

    // Keeps tracks of the API status
    private val _status = MutableLiveData<ApiStatus>()
    val status: LiveData<ApiStatus>
        get() = _status

    // Holds the data fetched from the API which is passed to the
    // Pokemon character adapter
    private val _characters = MutableLiveData<List<PokemonCharacters>>()
    val characters: LiveData<List<PokemonCharacters>>
        get() = _characters

    // Keeps track of exceptions which may occur during network call
    private val _message = MutableLiveData<String>()
    val message = _message as LiveData<String>


    init {

        // Get all the pokemon characters details initially
        getPokemonCharacters(DEFAULT_POKEMON_SIZE)
    }

    // Method to get pokemon characters from the API and
    // handles network exception if any
    private fun getPokemonCharacters(id: Int) {
        viewModelScope.launch {
            _status.value = ApiStatus.LOADING
            try {
                _characters.value = PokemonApiService.PokemonApi.retrofitService.getPokemon(0, id).results
                _status.value = ApiStatus.DONE
            } catch (e: Exception) {
                _status.value = ApiStatus.ERROR
                _message.value = "${status.value}: ${e.message}"
                _characters.value = ArrayList()
            }
        }
    }

    // Method to call the API with the number of items to display
    // on the overview fragment's UI
    fun listenToFilter(id: Int) {
        getPokemonCharacters(id)
    }
}