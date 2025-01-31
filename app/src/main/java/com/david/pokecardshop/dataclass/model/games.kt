package com.david.pokecardshop.dataclass.model

import com.google.gson.annotations.SerializedName
import java.util.jar.Attributes

data class Generation(
    val id: Int,
    val name: String,
    val abilities: List<NamedApiResource>,
    val names: List<Attributes.Name>,
    val mainRegion: NamedApiResource,
    val moves: List<NamedApiResource>,
    @SerializedName("pokemon_species") val pokemonSpecies: List<PokemonSpecies>,
    val types: List<NamedApiResource>,
    val versionGroups: List<NamedApiResource>,
)
data class PokemonSpecies(
    val name: String,
    val url: String
)