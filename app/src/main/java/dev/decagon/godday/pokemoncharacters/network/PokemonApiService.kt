package dev.decagon.godday.pokemoncharacters.network

import dev.decagon.godday.pokemoncharacters.BASE_URL
import dev.decagon.godday.pokemoncharacters.models.Pokemon
import dev.decagon.godday.pokemoncharacters.retrofit
import retrofit2.http.GET
import retrofit2.http.Query


/**
 * This interface provides the services of getting and querying data from
 * pokeAPI. This interface provides all the network services used by the overview fragment.
 * In addition, this interface uses the singleton design pattern making sure, that only one
 * instance of it is available throughout the own application.
 */

internal interface PokemonApiService {
    @GET(BASE_URL)
    suspend fun getPokemon(@Query("offset") id: Int, @Query("limit") lim: Int): Pokemon

    object PokemonApi {
        val retrofitService: PokemonApiService by lazy {
            retrofit.create(PokemonApiService::class.java)
        }
    }
}

