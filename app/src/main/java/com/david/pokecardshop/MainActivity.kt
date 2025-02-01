package com.david.pokecardshop

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.david.pokecardshop.dataclass.PokeInfoViewModel
import com.david.pokecardshop.dataclass.Pokemon
import com.david.pokecardshop.dataclass.Usuario
import com.david.pokecardshop.ui.stuff.CardGrande
import com.david.pokecardshop.ui.stuff.CardPeque2
import com.david.pokecardshop.ui.theme.PokeCardShopTheme
import com.david.pokecardshop.ui.theme.*
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.filter

var refBBDD by mutableStateOf<DatabaseReference>(FirebaseDatabase.getInstance().reference)
var usuario_key by mutableStateOf("")

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PokeCardShopTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    VerListaPokeAPI(modifier = Modifier.padding(innerPadding), listaApi=listaByGen(1))
                }
            }
        }
    }
}

@Composable
fun VerListaPokeAPI(modifier: Modifier = Modifier,listaApi: List<Pokemon>) {
    var onCardClick by remember { mutableStateOf(false) }
    var listaFiltrada by remember { mutableStateOf(listaApi) }
    if (isLoadingAPI) { //se asegura de haber cargado los datos de la nube antes de empezar a mostrar nada
        Box(
            modifier = modifier.background(color_fuego_dark).fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                color = colorResource(R.color.white),
                strokeWidth = 10.dp,
                modifier = Modifier.size(100.dp)
            )
        }
    } else {
        listaFiltrada = listaApi
        var pokemonClicked by remember { mutableStateOf(listaFiltrada[0]) }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color_fuego_dark)
                .padding(top = 0.dp) // Apply padding to the Box
                .systemBarsPadding()
                .imePadding(),
            contentAlignment = Alignment.Center,
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.wrapContentSize()
            ) {
                items(listaFiltrada) { pokemon ->
                    CardPeque2(
                        pokemon = pokemon,
                        onClick = {
                            onCardClick = !onCardClick
                            pokemonClicked = pokemon
                        })
                }
            }
            if (onCardClick) {
                CardGrande(pokemon = pokemonClicked)
            }

        }
    }
}
@Composable
fun listaByGen(gen: Int): List<Pokemon> {
    val viewModel: PokeInfoViewModel = viewModel()
    val pokemonListByGen by viewModel.pokemonListByGen.observeAsState(emptyList())

    LaunchedEffect(key1 = gen) {
        isLoadingAPI = true // Set to true before API call
        viewModel.getListByGeneration(gen) // Trigger API call
        // Wait for pokemonListByGen to be populated
        snapshotFlow { pokemonListByGen }
            .filter { it.isNotEmpty() } // Filter until list is not empty
            .collect {
                isLoadingAPI = false // Set to false when data is loaded
            }
    }
    return pokemonListByGen
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview2() {
    PokeCardShopTheme {
        MainActivity()
    }
}