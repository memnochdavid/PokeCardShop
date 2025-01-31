package com.david.pokecardshop.api

import com.david.pokecardshop.dataclass.Pokemon
import com.david.pokecardshop.dataclass.TypeListResponse
import com.david.pokecardshop.dataclass.model.EvolutionChain
import com.david.pokecardshop.dataclass.model.Generation
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("pokemon/{id}")
    fun getPokemonInfo(@Path("id") id: Int): Call<Pokemon>
    @GET("pokemon")
    fun getPokemonList(@Query("limit") limit: Int, @Query("offset") offset: Int): Call<PokeApiResponse>
    @GET("pokemon-species/{id}")
    fun getPokemonSpecies(@Path("id") id: Int): Call<Pokemon>
    @GET("evolution-chain/{id}")
    fun getEvolutionChain(@Path("id") id: Int): Call<EvolutionChain>
    @GET("generation/{id}")
    fun getGeneration(@Path("id") id: Int): Call<Generation>
    @GET("type")
    fun getTypeList(): Call<TypeListResponse>
}