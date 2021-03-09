package dev.decagon.godday.pokemoncharacters.network

import dev.decagon.godday.pokemoncharacters.models.PokemonDetails
import dev.decagon.godday.pokemoncharacters.retrofit
import retrofit2.http.GET
import retrofit2.http.Path


/**
 * This interface provides the services of getting data from the
 * pokeAPI at the second call.
 * This interface provides all the network services used by the details fragment.
 * The @GET annotation takes an id unique to each pokemon character and uses it to fetch
 * the information of the pokemon with that id.
 * In addition, this interface uses the singleton design pattern making sure, that only one
 * instance of it is available throughout the own application.
 */

internal interface PokemonDetailsApiService {

    @GET("{id}")
    suspend fun getPokemonDetails(@Path("id") pokemonId: String): PokemonDetails

    object PokemonDetailsApi {
        val retrofitDService: PokemonDetailsApiService by lazy {
            retrofit.create(PokemonDetailsApiService::class.java)
        }
    }
}