package com.david.pokecardshop

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.edit
import androidx.navigation.compose.rememberNavController
import com.david.pokecardshop.dataclass.UsuarioFromKey
import com.david.pokecardshop.dataclass.model.Menu
import com.david.pokecardshop.dataclass.model.Navigation
import com.david.pokecardshop.dataclass.model.Screen
import com.david.pokecardshop.ui.theme.PokeCardShopTheme
import kotlinx.coroutines.launch

var divisaSeleccionada by mutableStateOf<String>("")

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPreferences = getSharedPreferences("my_preferences", Context.MODE_PRIVATE)

        setContent {
            val isDollarSelected = sharedPreferences.getBoolean("divisa", false)
            divisaSeleccionada = if (isDollarSelected) {
                "$"
            } else {
                "€"
            }
            var darkTheme by remember {
                mutableStateOf(sharedPreferences.getBoolean("dark_mode", false))
            }

            PokeCardShopTheme(darkTheme = darkTheme) {
                MainScreen(
                    onThemeChange = { isDark ->
                        darkTheme = isDark
                        sharedPreferences.edit { putBoolean("dark_mode", isDark) }
                    }
                )
            }
        }
    }
}

@Composable
fun MainScreen(onThemeChange: (Boolean) -> Unit) {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var selectedItem by remember { mutableIntStateOf(0) }
    val sesion = UsuarioFromKey(usuario_key, refBBDD)

    val menuItems = if (sesion.admin) {
        listOf("Crear Carta", "Cartas Creadas", "Reservas", "Crear Eventos", "Eventos Creados","Opciones")
    } else {
        listOf("MisCartas", "Cartas Creadas", "Reservas", "Eventos Creados", "Mis Eventos","Opciones")
    }

    val currentRoute = when {
        sesion.admin && selectedItem == 0 -> Screen.CrearCarta.route
        sesion.admin && selectedItem == 1 -> Screen.CartasCreadas.route
        sesion.admin && selectedItem == 2 -> Screen.Reservas.route
        sesion.admin && selectedItem == 3 -> Screen.CrearEventos.route
        sesion.admin && selectedItem == 4 -> Screen.EventosCreados.route
        sesion.admin && selectedItem == 5 -> Screen.Opciones.route
        !sesion.admin && selectedItem == 0 -> Screen.MisCartas.route
        !sesion.admin && selectedItem == 1 -> Screen.CartasCreadas.route
        !sesion.admin && selectedItem == 2 -> Screen.Reservas.route
        !sesion.admin && selectedItem == 3 -> Screen.EventosCreados.route
        !sesion.admin && selectedItem == 4 -> Screen.MisEventos.route
        !sesion.admin && selectedItem == 5 -> Screen.Opciones.route
        else -> Screen.Reservas.route
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            Menu(
                menuItems = menuItems,
                selectedItem = selectedItem,
                onItemSelected = { index ->
                    selectedItem = index
                    scope.launch { drawerState.close() } // Close drawer after selection
                },
            )
        }
    ) {
        Box {
            Scaffold() { paddingValues ->
                Navigation(navController = navController, modifier = Modifier.padding(paddingValues), onThemeChange = onThemeChange)
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




/*
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
*/
@Preview(showBackground = true)
@Composable
fun GreetingPreview2() {
    PokeCardShopTheme {
        MainActivity()
    }
}