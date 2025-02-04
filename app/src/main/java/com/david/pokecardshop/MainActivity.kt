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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.david.pokecardshop.dataclass.PokeInfoViewModel
import com.david.pokecardshop.dataclass.Pokemon
import com.david.pokecardshop.dataclass.Usuario
import com.david.pokecardshop.dataclass.UsuarioFromKey
import com.david.pokecardshop.dataclass.model.Menu
import com.david.pokecardshop.dataclass.model.Navigation
import com.david.pokecardshop.dataclass.model.Screen
import com.david.pokecardshop.ui.stuff.CardGrande
import com.david.pokecardshop.ui.stuff.CardPeque2
import com.david.pokecardshop.ui.theme.PokeCardShopTheme
import com.david.pokecardshop.ui.theme.*
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        setContent {
            PokeCardShopTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var selectedItem by remember { mutableIntStateOf(0) }
    val sesion = UsuarioFromKey(usuario_key, refBBDD)

    val currentRoute = when(sesion.admin) {
        true -> {
            when (selectedItem) {
                0 -> Screen.Lista.route
                1 -> Screen.CrearCarta.route
                2 -> Screen.CartasCreadas.route
                3 -> Screen.Opciones.route
                else -> {Screen.Lista.route}
            }
        }
            false -> {
                when (selectedItem) {
                    0 -> Screen.Lista.route
                    1 -> Screen.CartasCreadas.route
                    3 -> Screen.Opciones.route
                    else -> Screen.Lista.route // Default to Lista
                }
            }
        }



    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            Menu(
                selectedItem = selectedItem,
                onItemSelected = { index ->
                    selectedItem = index
                    when(sesion.admin) {
                        true -> {
                            when (selectedItem) {
                                0 -> Screen.Lista.route
                                1 -> Screen.CrearCarta.route
                                2 -> Screen.CartasCreadas.route
                                3 -> Screen.Opciones.route
                                else -> {Screen.Lista.route}
                            }
                        }
                        false -> {
                            when (selectedItem) {
                                0 -> Screen.Lista.route
                                1 -> Screen.CartasCreadas.route
                                3 -> Screen.Opciones.route
                                else -> Screen.Lista.route // Default to Lista
                            }
                        }
                    }
                },
                onCloseDrawer = { scope.launch { drawerState.close() } }
            )
        }
    ){
        Box {
            Scaffold() { paddingValues ->
                Navigation(navController = navController, modifier = Modifier.padding(paddingValues))
            }
            FloatingActionButton(
                onClick = { scope.launch { drawerState.open() } },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 32.dp, end = 16.dp)
            ) {
                Icon(Icons.Filled.Menu, contentDescription = "Abrir menú")
            }
        }
    }
    // New: Navigate when the current route changes
    LaunchedEffect(currentRoute) {
        navController.navigate(currentRoute) {
            popUpTo(navController.graph.startDestinationId) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }
}





@Composable
fun VerListaPokeAPI(modifier: Modifier = Modifier,listaApi: List<Pokemon>, navController: NavHostController) {
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