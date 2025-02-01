package com.david.pokecardshop.dataclass


import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.asFlow
import com.david.pokecardshop.R
import com.david.pokecardshop.api.ApiService
import com.david.pokecardshop.ui.theme.*
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.awaitResponse
import java.net.URLDecoder


data class Pokemon(
    @Expose @SerializedName("id") val id: Int,
    @Expose @SerializedName("name") val name: String,
    //@Expose @SerializedName("base_experience") val baseExperience: Int,
    //@Expose @SerializedName("height") val height: Int,
    //@Expose @SerializedName("is_default") val isDefault: Boolean,
    //@Expose @SerializedName("order") val order: Int,
    //@Expose @SerializedName("weight") val weight: Int,
    @Expose @SerializedName("sprites") val sprites: Sprites,
    //@Expose @SerializedName("abilities") val abilities: List<Ability>,
    //@Expose @SerializedName("forms") val forms: List<Form>,
    //@Expose @SerializedName("game_indices") val gameIndices: List<GameIndex>,
    //@Expose @SerializedName("held_items") val heldItems: List<HeldItem>,
    //@Expose @SerializedName("location_area_encounters") val locationAreaEncounters: String,
    //@Expose @SerializedName("moves") val moves: List<Move>,
    @Expose @SerializedName("species") val species: Species,
    //@Expose @SerializedName("stats") val stats: List<Stat>,
    @Expose @SerializedName("types") val types: List<Type>,
    //@Expose @SerializedName("color") val color: String,
    @Expose @SerializedName("flavor_text_entries") val flavorTextEntries: List<FlavorTextEntry>,
    @Expose @SerializedName("abilities") val abilities: List<PokemonAbilitySpecies> = emptyList(),
// added property for the Spanish flavor text entries
    //var spanishFlavorTextEntries: String=""
)

data class FlavorTextEntry(
    @Expose @SerializedName("flavor_text") val flavorText: String,
    @Expose @SerializedName("language") val language: Language
)
data class PokemonAbilitySpecies(
    @Expose @SerializedName("is_hidden") val isHidden: Boolean,
    @Expose @SerializedName("slot") val slot: Int,
    @Expose @SerializedName("pokemon") val pokemon: PokemonInfo
)

data class EffectEntry(
    @Expose @SerializedName("effect") val effect: String,
    @Expose @SerializedName("language") val language: Language
)

data class Language(
    @Expose @SerializedName("name") val name: String,
    @Expose @SerializedName("url") val url: String
)
data class Sprites(
    @Expose @SerializedName("back_default") val backDefault: String?,
    @Expose @SerializedName("back_female") val backFemale: String?,
    @Expose @SerializedName("back_shiny") val backShiny: String?,
    @Expose @SerializedName("back_shiny_female") val backShinyFemale: String?,
    @Expose @SerializedName("front_default") val frontDefault: String?,
    @Expose @SerializedName("front_female") val frontFemale: String?,
    @Expose @SerializedName("front_shiny") val frontShiny: String?,
    @Expose @SerializedName("front_shiny_female") val frontShinyFemale: String?
)

data class Ability(
    @Expose @SerializedName("id") val id: Int,
    @Expose @SerializedName("name") val name: String,
    @Expose @SerializedName("effect_entries") val effectEntries: List<EffectEntry>,
    @Expose @SerializedName("flavor_text_entries") val flavorTextEntries: List<FlavorTextEntry>,
    @Expose @SerializedName("names") val names: List<Name>,
    @Expose @SerializedName("pokemon") val pokemon: List<PokemonAbilitySpecies>
)
data class Name(
    @Expose @SerializedName("name") val name: String,
    @Expose @SerializedName("language") val language: Language
)
data class PokeResult (
    @Expose @SerializedName("name") val name: String,
    @Expose @SerializedName("url") val url: String
)
data class AbilityInfo(
    @Expose @SerializedName("name") val name: String,
    @Expose @SerializedName("url") val url: String
)

data class Form(
    @Expose @SerializedName("name") val name: String,
    @Expose @SerializedName("url") val url: String
)

data class GameIndex(
    @Expose @SerializedName("game_index") val gameIndex: Int,
    @Expose @SerializedName("version") val version: Version
)

data class Version(
    @Expose @SerializedName("name") val name: String,
    @Expose @SerializedName("url") val url: String
)

data class HeldItem(
    @Expose @SerializedName("item") val item: Item,
    @Expose @SerializedName("version_details") val versionDetails: List<VersionDetail>
)

data class Item(
    @Expose @SerializedName("name") val name: String,
    @Expose @SerializedName("url") val url: String
)

data class VersionDetail(
    @Expose @SerializedName("rarity") val rarity: Int,
    @Expose @SerializedName("version") val version: Version
)

data class Move(
    @Expose @SerializedName("move") val move: MoveInfo,
    @Expose @SerializedName("version_group_details") val versionGroupDetails: List<VersionGroupDetail>
)

data class MoveInfo(
    @Expose @SerializedName("name") val name: String,
    @Expose @SerializedName("url") val url: String
)

data class VersionGroupDetail(
    @Expose @SerializedName("level_learned_at") val levelLearnedAt: Int,
    @Expose @SerializedName("move_learn_method") val moveLearnMethod: MoveLearnMethod,
    @Expose @SerializedName("version_group") val versionGroup: VersionGroup
)

data class MoveLearnMethod(
    @Expose @SerializedName("name") val name: String,
    @Expose @SerializedName("url") val url: String
)

data class VersionGroup(
    @Expose @SerializedName("name") val name: String,
    @Expose @SerializedName("url") val url: String
)

data class Species(
    @Expose @SerializedName("name") val name: String,
    @Expose @SerializedName("url") val url: String
)

data class Stat(
    @Expose @SerializedName("base_stat") val baseStat: Int,
    @Expose @SerializedName("effort") val effort: Int,
    @Expose @SerializedName("stat") val stat: StatInfo
)

data class StatInfo(
    @Expose @SerializedName("name") val name: String,
    @Expose @SerializedName("url") val url: String
)

data class TypeListResponse(
    @SerializedName("results") val results: List<TypeInfo>
)

data class Type(
    @Expose @SerializedName("slot") val slot: Int,
    @Expose @SerializedName("type") val type: TypeInfo
)

data class Color(
    @Expose @SerializedName("name") val name: String,
    @Expose @SerializedName("url") val url: String
)

data class TypeInfo(
    @Expose @SerializedName("name") val name: String,
    @Expose @SerializedName("url") val url: String
)

data class FromDescription(
    @Expose @SerializedName("description") val description: String,
    @Expose @SerializedName("language") val language: Language
)

data class PokemonAbility(
    @Expose @SerializedName("is_hidden") val isHidden: Boolean,
    @Expose @SerializedName("slot") val slot: Int,
    @Expose @SerializedName("ability") val ability: PokemonInfo
)
data class PokemonInfo(
    @Expose @SerializedName("name") val name: String,
    @Expose @SerializedName("url") val url: String
)
data class PokemonSpecies(
    @Expose @SerializedName("id") val id: Int,
    @Expose @SerializedName("name") val name: String,
    @Expose @SerializedName("order") val order: Int,
    @Expose @SerializedName("flavor_text_entries") val flavorTextEntries: List<FlavorTextEntry>,
    @Expose @SerializedName("color") val color: Color,
    @Expose @SerializedName("abilities") val abilities: List<PokemonAbilitySpecies> = emptyList()
)


class PokemonDataRepository(private val viewModel: PokeInfoViewModel) {

    suspend fun getPokemonListByGen(gen: Int): List<Pokemon> {
        viewModel.getListByGeneration(gen) // Trigger API call
        return viewModel.pokemonListByGen.asFlow()
            .filter { it.isNotEmpty() } // Filter until list is not empty
            .first() // Get the first non-empty list
    }
}
data class AbilityListResponse(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<AbilityInfo>
)

object RetrofitClient {

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://pokeapi.co/api/v2/") // Replace with your API base URL
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    fun create(serviceClass: Class<ApiService>): ApiService {
        return retrofit.create(serviceClass)
    }
}

class PokeInfoViewModel() : ViewModel() {
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://pokeapi.co/api/v2/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val service: ApiService = retrofit.create(ApiService::class.java)
    val pokemonInfo = MutableLiveData<Pokemon>()

    private val _pokemonList = MutableLiveData<List<Pokemon>>(emptyList())
    val pokemonList: LiveData<List<Pokemon>> = _pokemonList

    private val _spanishDescription = mutableStateOf("")
    val spanishDescription: State<String> = _spanishDescription

    private val _pokemonColor = mutableStateOf("")
    val pokemonColor: State<String> = _pokemonColor

    private val _pokemonListByGen = MutableLiveData<List<Pokemon>>(emptyList())
    val pokemonListByGen: LiveData<List<Pokemon>> = _pokemonListByGen

    private val _pokemonTypeList = MutableLiveData<List<TypeInfo>>(emptyList())
    val pokemonTypeList: LiveData<List<TypeInfo>> = _pokemonTypeList

    private val _allPokemonList = MutableLiveData<List<Pokemon>>(emptyList())
    val allPokemonList: LiveData<List<Pokemon>> = _allPokemonList

    private val _effect_description = mutableStateOf("")
    val effect_description: State<String> = _effect_description
    private val _effect_name = mutableStateOf("")
    val effect_name: State<String> = _effect_name

    fun getPokemonInfo(name: String){
        val call = service.getPokemonInfoFromName(name)
        call.enqueue(object : Callback<Pokemon> {
            override fun onResponse(call: Call<Pokemon>, response: Response<Pokemon>) {
                response.body()?.let { pokemon ->
                    pokemonInfo.postValue(pokemon)
                }
            }
            override fun onFailure(call: Call<Pokemon>, t: Throwable) {
                call.cancel()
            }
        })

    }
    fun getPokemonDescription(id: Int) {
        viewModelScope.launch {
            val callDescription = service.getPokemonSpecies(id)
            try {
                val response = callDescription.awaitResponse()
                if (response.isSuccessful) {
                    val pokemon = response.body()
                    val spanishEntries = pokemon?.flavorTextEntries?.filter { it.language.name == "es" }
                    _spanishDescription.value = spanishEntries?.firstOrNull()?.flavorText ?: "caca"
                } else {
                    // Handle error
                    Log.e("PokeInfoViewModel", "Error fetching Pokemon description: ${response.errorBody()?.string()}")
                    _spanishDescription.value = "Error loading description"
                }
            } catch (e: Exception) {
                // Handle network or other errors
                Log.e("PokeInfoViewModel", "Error fetching Pokemon description: ${e.message}")
                _spanishDescription.value = "Error loading description"
            }
        }
    }
    fun getPokemonDescriptionFromName(name: String) {
        viewModelScope.launch {
            val callDescription = service.getPokemonSpeciesFromName(name)
            try {
                val response = callDescription.awaitResponse()
                if (response.isSuccessful) {
                    val pokemon = response.body()
                    val spanishEntries = pokemon?.flavorTextEntries?.filter { it.language.name == "es" }
                    _spanishDescription.value = spanishEntries?.firstOrNull()?.flavorText ?: "caca"
                } else {
                    // Handle error
                    Log.e("PokeInfoViewModel", "Error fetching Pokemon description: ${response.errorBody()?.string()}")
                    _spanishDescription.value = "Error loading description"
                }
            } catch (e: Exception) {
                // Handle network or other errors
                Log.e("PokeInfoViewModel", "Error fetching Pokemon description: ${e.message}")
                _spanishDescription.value = "Error loading description"
            }
        }
    }
    fun getAbilityDetails(abilityId: Int) {
        viewModelScope.launch {
            val callDescription = service.getPokemonAbility(abilityId)
            try {
                val response = callDescription.awaitResponse()
                if (response.isSuccessful) {
                    val ability: Ability? = response.body()
                    if (ability != null) {
                        // Use ability.effectEntries and ability.flavorTextEntries
                        val spanishEffectEntry = ability.effectEntries.firstOrNull { it.language.name == "es" }
                        val spanishEffectDescription = spanishEffectEntry?.effect ?: ""

                        val spanishFlavorTextEntry = ability.flavorTextEntries.firstOrNull { it.language.name == "es" }
                        val spanishFlavorText = spanishFlavorTextEntry?.flavorText ?: ""

                        _effect_description.value = when {
                            spanishFlavorText.isNotEmpty() -> spanishFlavorText
                            spanishEffectDescription.isNotEmpty() -> spanishEffectDescription
                            else -> "No description available in Spanish"
                        }

                        // Find the Spanish name
                        val spanishNameEntry = ability.names.firstOrNull { it.language.name == "es" }
                        val spanishName = spanishNameEntry?.name ?: ability.name // Fallback to English name if Spanish not found
                        _effect_name.value = spanishName
                    } else {
                        _effect_description.value = "Error: Empty response body"
                        Log.e("PokeInfoViewModel", "Error: Empty response body")
                    }
                } else {
                    val errorBody = response.errorBody()?.string() ?: "No error body"
                    Log.e(
                        "PokeInfoViewModel",
                        "Error fetching ability details: ${response.code()} - $errorBody"
                    )
                    _effect_description.value = "Error loading description"
                }
            } catch (e: Exception) {
                Log.e("PokeInfoViewModel", "Error fetching ability details: ${e.message}")
                _effect_description.value = "Error loading description"
            }
        }
    }

    fun getPokemonAbility(pokemonId: Int) {
        viewModelScope.launch {
            try {
                var offset = 0
                val limit = 367 // Puedes ajustar el límite si lo deseas
                var allAbilities: MutableList<AbilityInfo> = mutableListOf()
                var hasMore = true

                while (hasMore) {
                    val response = service.getAbilityList(limit, offset).awaitResponse()
                    if (response.isSuccessful) {
                        val abilityListResponse = response.body()
                        if (abilityListResponse != null) {
                            allAbilities.addAll(abilityListResponse.results)
                            // Check if there are more pages
                            hasMore = abilityListResponse.next != null
                            offset += limit
                        } else {
                            hasMore = false
                        }
                    } else {
                        val errorBody = response.errorBody()?.string() ?: "No error body"
                        Log.e(
                            "PokeInfoViewModel",
                            "Error fetching ability list: ${response.code()} - $errorBody"
                        )
                        _effect_description.value = "Error loading description"
                        hasMore = false
                    }
                }

                // Ahora que tenemos todas las habilidades, buscamos la del Pokémon
                for (abilityInfo in allAbilities) {
                    val abilityId = abilityInfo.url.extractAbilityId()
                    if (abilityId != null) {
                        val abilityResponse = service.getPokemonAbility(abilityId).awaitResponse()
                        if (abilityResponse.isSuccessful) {
                            val ability = abilityResponse.body()
                            if (ability != null) {
                                val pokemonFound = ability.pokemon.any { it.pokemon.url.contains("/pokemon/$pokemonId/") }
                                if (pokemonFound) {
                                    getAbilityDetails(abilityId)
                                    return@launch // Encontramos la habilidad, salimos del bucle
                                }
                            }
                        } else {
                            val errorBody = abilityResponse.errorBody()?.string() ?: "No error body"
                            Log.e(
                                "PokeInfoViewModel",
                                "Error fetching ability details: ${abilityResponse.code()} - $errorBody"
                            )
                        }
                    }
                }
                // Si llegamos aquí, no se encontró la habilidad para el Pokémon
                _effect_description.value = "No se encontró habilidad para este Pokémon"
                _effect_name.value = ""
            } catch (e: Exception) {
                Log.e("PokeInfoViewModel", "Error fetching ability list: ${e.message}")
                _effect_description.value = "Error loading description"
            }
        }
    }


    fun getEffect(id: Int) {
        viewModelScope.launch {
            val callDescription = service.getPokemonAbility(id)
            try {
                val response = callDescription.awaitResponse()
                if (response.isSuccessful) {
                    val ability: Ability? = response.body()
                    if (ability != null) {
                        // Use ability.effectEntries and ability.flavorTextEntries
                        val spanishEffectEntry = ability.effectEntries.firstOrNull { it.language.name == "es" }
                        val spanishEffectDescription = spanishEffectEntry?.effect ?: ""

                        val spanishFlavorTextEntry = ability.flavorTextEntries.firstOrNull { it.language.name == "es" }
                        val spanishFlavorText = spanishFlavorTextEntry?.flavorText ?: ""

                        _effect_description.value = when {
                            spanishFlavorText.isNotEmpty() -> spanishFlavorText
                            spanishEffectDescription.isNotEmpty() -> spanishEffectDescription
                            else -> "No description available in Spanish"
                        }

                        // Find the Spanish name
                        val spanishNameEntry = ability.names.firstOrNull { it.language.name == "es" }
                        val spanishName = spanishNameEntry?.name ?: ability.name // Fallback to English name if Spanish not found
                        _effect_name.value = spanishName
                    } else {
                        _effect_description.value = "Error: Empty response body"
                        Log.e("PokeInfoViewModel", "Error: Empty response body")
                    }
                } else {
                    Log.e(
                        "PokeInfoViewModel",
                        "Error fetching Pokemon description: ${response.errorBody()?.string()}"
                    )
                    _effect_description.value = "Error loading description"
                }
            } catch (e: Exception) {
                Log.e("PokeInfoViewModel", "Error fetching Pokemon description: ${e.message}")
                _effect_description.value = "Error loading description"
            }
        }
    }

    fun getPokemonList(pokemonIds: List<Int>) {
        var apiService = RetrofitClient.create(ApiService::class.java)
        viewModelScope.launch {
            try {
                // Fetch Pokemon info for each ID and await the responses
                val pokemonInfoList = pokemonIds.map { id ->
                    apiService.getPokemonInfo(id).awaitResponse().body() // Get the Pokemon object from the response body
                }
                // Update pokemonList LiveData
                _pokemonList.value = pokemonInfoList.filterNotNull() // Filter out null values in case of errors
            } catch (e: Exception) {
                // Handle error
                Log.e("PokeInfoViewModel", "Error fetching Pokemon info: ${e.message}")
            }
        }
    }
    fun getTypeList() {
        viewModelScope.launch {
            try {
                val typeListResponse = service.getTypeList().awaitResponse()
                if (typeListResponse.isSuccessful) {
                    val typeList = typeListResponse.body()?.results
                    // Update _pokemonTypeList with the list of TypeInfo objects
                    _pokemonTypeList.value = typeList ?: emptyList()
                } else {
                    Log.e("PokeInfoViewModel", "Error fetching type list: ${typeListResponse.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("PokeInfoViewModel", "Error fetching type list: ${e.message}")
            }
        }
    }
    fun getListByGeneration(id: Int) {
        viewModelScope.launch {
            try {
                // 1. Get the generation details
                val generationResponse = service.getGeneration(id).awaitResponse()
                if (generationResponse.isSuccessful) {
                    val generation = generationResponse.body()
                    Log.d("PokeInfoViewModel", "Generation Response: ${generationResponse.body()?.pokemonSpecies}")
                    val pokemonSpecies = generation?.pokemonSpecies ?: emptyList()

                    // 2. Fetch Pokemon details for each species
                    val pokemonInfoList = pokemonSpecies.mapNotNull { species ->
                        val pokemonId = species.url.extractPokemonId() // Extract Pokemon ID from URL
                        if (pokemonId != null) {
                            service.getPokemonInfo(pokemonId).awaitResponse().body()
                        } else {
                            null
                        }
                    }
                    // 3. Sort pokemonInfoList by ID
                    val sortedPokemonList = pokemonInfoList.sortedBy { it.id }

                    // 4. Update pokemonListByGen LiveData with sorted list
                    _pokemonListByGen.value = sortedPokemonList

                    // Update pokemonListByGen LiveData
                    //_pokemonListByGen.value = pokemonInfoList
                    Log.d("PokeInfoViewModel", "Pokemon IDs: ${pokemonSpecies.mapNotNull { it.url.extractPokemonId() }}")
                } else {
                    // Handle error
                    Log.e("PokeInfoViewModel", "Error fetching generation details: ${generationResponse.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                // Handle network or other errors
                Log.e("PokeInfoViewModel", "Error fetching Pokemon list by generation: ${e.message}")
            }
        }
    }

}


fun TypeToDrawableAPI(tipo: Type):Int{
    return when (tipo.type.name) {
        "grass" -> R.drawable.planta
        "water" -> R.drawable.agua
        "fire" -> R.drawable.fuego
        "fighting" -> R.drawable.lucha
        "poison" -> R.drawable.veneno
        "steel" -> R.drawable.acero
        "bug" -> R.drawable.bicho
        "dragon" -> R.drawable.dragon
        "electric" -> R.drawable.electrico
        "fairy" -> R.drawable.hada
        "ice" -> R.drawable.hielo
        "psychic" -> R.drawable.psiquico
        "rock" -> R.drawable.roca
        "ground" -> R.drawable.tierra
        "dark" -> R.drawable.siniestro
        "normal" -> R.drawable.normal
        "flying" -> R.drawable.volador
        "ghost" -> R.drawable.fantasma
        else -> { R.drawable.error}
    }
}
fun TypeToDrawableAPI_grandes(tipo: Type):Int{
    return when (tipo.type.name) {
        "grass" -> R.drawable.planta2
        "water" -> R.drawable.agua2
        "fire" -> R.drawable.fuego2
        "fighting" -> R.drawable.lucha2
        "poison" -> R.drawable.veneno2
        "steel" -> R.drawable.acero2
        "bug" -> R.drawable.bicho2
        "dragon" -> R.drawable.dragon2
        "electric" -> R.drawable.electrico2
        "fairy" -> R.drawable.hada2
        "ice" -> R.drawable.hielo2
        "psychic" -> R.drawable.psiquico2
        "rock" -> R.drawable.roca2
        "ground" -> R.drawable.tierra2
        "dark" -> R.drawable.siniestro2
        "normal" -> R.drawable.normal2
        "flying" -> R.drawable.volador2
        "ghost" -> R.drawable.fantasma2
        else -> { R.drawable.error}
    }
}
fun String.firstMayus(): String {
    return this.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
}
private fun String.extractPokemonId(): Int? {
    if (this.isNullOrEmpty()) return null // Check for null or empty URLs

    val decodedUrl = URLDecoder.decode(this, "UTF-8") // Decode URL if necessary

    val pattern = "/pokemon-species/(\\d+)/".toRegex() // Updated regex pattern
    val matchResult = pattern.find(decodedUrl)
    return matchResult?.groupValues?.getOrNull(1)?.toIntOrNull()
}
fun String.extractPokemonId2(): Int? {
    val pattern = "/pokemon/(\\d+)/".toRegex()
    val matchResult = pattern.find(this)
    return matchResult?.groupValues?.get(1)?.toIntOrNull()
}


fun TypeToColor(tipo:Type, opc:Int):Color{
    if(opc==1) {
        return when (tipo.type.name) {
            "grass" -> color_planta_light
            "water" -> color_agua_light
            "fire" -> color_fuego_light
            "fighting" -> color_lucha_light
            "poison" -> color_veneno_light
            "steel" -> color_acero_light
            "bug" -> color_bicho_light
            "dragon" -> color_dragon_light
            "electric" -> color_electrico_light
            "fairy" -> color_hada_light
            "ice" -> color_hielo_light
            "psychic" -> color_psiquico_light
            "rock" -> color_roca_light
            "ground" -> color_tierra_light
            "dark" -> color_siniestro_light
            "normal" -> color_normal_light
            "flying" -> color_volador_light
            "ghost" -> color_fantasma_light
            else -> { negro80}
        }
    }
    else{
        return when (tipo.type.name) {
            "grass" -> color_planta_dark
            "water" -> color_agua_dark
            "fire" -> color_fuego_dark
            "fighting" -> color_lucha_dark
            "poison" -> color_veneno_dark
            "steel" -> color_acero_dark
            "bug" -> color_bicho_dark
            "dragon" -> color_dragon_dark
            "electric" -> color_electrico_dark
            "fairy" -> color_hada_dark
            "ice" -> color_hielo_dark
            "psychic" -> color_psiquico_dark
            "rock" -> color_roca_dark
            "ground" -> color_tierra_dark
            "dark" -> color_siniestro_dark
            "normal" -> color_normal_dark
            "flying" -> color_volador_dark
            "ghost" -> color_fantasma_dark
            else -> { negro80}
        }
    }
}

fun ColorStringToColorType(string_color:String):Color{
    return when (string_color) {
        "black" -> color_siniestro_dark
        "blue" -> color_agua_light
        "brown" -> color_tierra_dark
        "gray" -> color_normal_dark
        "green" -> color_planta_light
        "pink" -> color_hada_light
        "purple" -> color_veneno_light
        "red" -> color_fuego_light
        "yellow" -> color_electrico_light
        "white" -> blanco60
        else -> { Color.Red}
    }
}

fun TypeToBackground(tipo:Type):Int{
    return when (tipo.type.name) {
        "grass" -> R.drawable.background_grass
        "water" -> R.drawable.background_water
        "fire" -> R.drawable.background_fire
        "fighting" -> R.drawable.background_fighting
        "poison" -> R.drawable.background_poison
        "steel" -> R.drawable.background_steel
        "bug" -> R.drawable.background_bug
        "dragon" -> R.drawable.background_dragon
        "electric" -> R.drawable.background_electric
        "fairy" -> R.drawable.background_fairy
        "ice" -> R.drawable.background_ice
        "psychic" -> R.drawable.background_psychic
        "rock" -> R.drawable.background_rock
        "ground" -> R.drawable.background_ground
        "dark" -> R.drawable.background_dark
        "normal" -> R.drawable.background_normal
        "flying" -> R.drawable.background_flying
        "ghost" ->R.drawable.background_ghost
        else -> { R.drawable.error}
    }
}
fun adaptaNombre(nombre: String): String {
    val devuelve = nombre
        .replace(Regex("[áéíóúü]"), {
            when (it.value) {
                "á" -> "a"
                "é" -> "e"
                "í" -> "i"
                "ó" -> "o"
                "ú" -> "u"
                "ü" -> "u"
                else -> it.value
            }
        })
        .split("-")[0] // Take only the part before the first hyphen
        .replace("[^a-zA-Z0-9]".toRegex(), "") // Elimina otros caracteres especiales
        .lowercase()
    //para que encuentre las fotos en appwrite. Algunos nombres no están correctamente allí. Mea culpa
    if(nombre.equals("beedril")) return "beedrill"
    return devuelve
}

fun adaptaDescripcion(desc: String): String {
    val devuelve = desc
        .replace("\n","")
    return devuelve
}

fun String.extractAbilityId(): Int? {
    val regex = Regex("/ability/(\\d+)/")
    val matchResult = regex.find(this)
    return matchResult?.groupValues?.get(1)?.toIntOrNull()
}