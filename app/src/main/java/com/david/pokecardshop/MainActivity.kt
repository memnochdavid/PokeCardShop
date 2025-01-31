package com.david.pokecardshop

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.constraintlayout.compose.ConstraintLayout
import com.david.pokecardshop.dataclass.PokeInfoViewModel
import com.david.pokecardshop.dataclass.Pokemon
import com.david.pokecardshop.dataclass.Type
import com.david.pokecardshop.ui.stuff.CardPeque
import com.david.pokecardshop.ui.theme.PokeCardShopTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filter
import androidx.lifecycle.viewmodel.compose.viewModel
import com.david.pokecardshop.ui.stuff.CardGrande
import com.david.pokecardshop.ui.stuff.CardPeque2
import com.david.pokecardshop.ui.theme.*

var isLoadingAPI by mutableStateOf(true)

class MainActivity : ComponentActivity() {
    lateinit var viewModel: PokeInfoViewModel
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

//@SuppressLint("UnrememberedMutableState")
@Composable
fun VerListaPokeAPI(modifier: Modifier = Modifier,listaApi: List<Pokemon>) {
    var onCardClick by remember { mutableStateOf(false) }
    //var pokemonClicked by remember { mutableStateOf(List<Pokemon>())}
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
                .background(color = Color.Red)
                .padding(top = 0.dp) // Apply padding to the Box
                .systemBarsPadding()
                .imePadding()
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

@Composable
fun listaPokemon(): List<Pokemon> {
    val viewModel: PokeInfoViewModel = viewModel()
    val pokemonList by viewModel.pokemonList.observeAsState(emptyList())
    LaunchedEffect(Unit) {
        isLoadingAPI = true // Set to true before API call
        viewModel.getAllGen() // Trigger API call
        // Wait for pokemonListByGen to be populated
        snapshotFlow { pokemonList }
            .filter { it.isNotEmpty() } // Filter until list is not empty
            .collect {
                isLoadingAPI = false // Set to false when data is loaded
            }
    }
    return pokemonList
}


@Composable
fun listaTipos(): List<Type> {
    val viewModel: PokeInfoViewModel = viewModel()
    val pokemonTypeList by viewModel.pokemonTypeList.observeAsState(emptyList())

    LaunchedEffect(Unit) {
        viewModel.getTypeList() // Trigger API call to fetch the type list
    }

    return pokemonTypeList.map { typeInfo ->
        Type(slot = 0, type = typeInfo) // Assuming 'slot' is always 0
    }
}
















