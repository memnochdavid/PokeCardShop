package com.david.pokecardshop.dataclass.model

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.RectangleShape
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.david.pokecardshop.CartasCreadas
import com.david.pokecardshop.MisCartas
import com.david.pokecardshop.Opciones
import com.david.pokecardshop.dataclass.CreaCarta
import com.david.pokecardshop.dataclass.CreaEvento
import com.david.pokecardshop.dataclass.EventosCreados
import com.david.pokecardshop.dataclass.Reservadas
import com.david.pokecardshop.ui.theme.*

@Composable
fun Menu(
    menuItems: List<String>,
    selectedItem: Int,
    onItemSelected: (Int) -> Unit,
) {
    ModalDrawerSheet(
        modifier = Modifier.widthIn(max = 250.dp),
        drawerShape = RectangleShape,
    ) {
        Column(modifier = Modifier.padding(top = 16.dp)) {
            menuItems.forEachIndexed { index, item ->
                NavigationDrawerItem(
                    icon = {
                        when (item) {
                            "MisCartas" -> Icon(Icons.AutoMirrored.Filled.List, contentDescription = item)
                            "Crear Carta" -> Icon(Icons.Filled.Add, contentDescription = item)
                            "Cartas Creadas" -> Icon(Icons.Filled.Face, contentDescription = item)
                            "Reservas" -> Icon(Icons.Filled.Star, contentDescription = item)
                            "Crear Evento" -> Icon(Icons.Filled.DateRange, contentDescription = item)
                            "Eventos Creados" -> Icon(Icons.Filled.Email, contentDescription = item)
                            "Opciones" -> Icon(Icons.Filled.Settings, contentDescription = item)
                            else -> Icon(Icons.AutoMirrored.Filled.List, contentDescription = item)
                        }
                    },
                    label = { Text(item) },
                    selected = selectedItem == index,
                    onClick = {
                        onItemSelected(index)
                    },
                    modifier = Modifier
                        .padding(NavigationDrawerItemDefaults.ItemPadding)
                        .fillMaxWidth(),
                    shape = RectangleShape,
                )
            }
        }
    }
}

sealed class Screen(val route: String) {
    object MisCartas : Screen("MisCartas")
    object CrearCarta : Screen("CrearCarta")
    object CartasCreadas : Screen("CartasCreadas")
    object Reservas : Screen("Reservas")
    object CrearEventos : Screen("Crear Evento")
    object EventosCreados : Screen("Eventos Creados")
    object Opciones : Screen("Opciones")
}
@Composable
fun Navigation(navController: NavHostController, modifier: Modifier,onThemeChange: (Boolean) -> Unit) {
    //val pokemonList = listaByGen(1)
    NavHost(navController = navController, startDestination = Screen.MisCartas.route) {
        composable(Screen.MisCartas.route) {
            PokeCardShopTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    //VerListaPokeAPI(modifier = Modifier.padding(innerPadding), listaApi = pokemonList, navController = navController)
                    MisCartas(modifier = Modifier.padding(innerPadding), navController = navController)
                }
            }
        }
        composable(Screen.Opciones.route) {
            // Content for the Opciones screen
            PokeCardShopTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Opciones(modifier = Modifier.padding(innerPadding), navController = navController, onThemeChange = onThemeChange)
                }
            }
        }
        composable(Screen.CrearCarta.route) {
            PokeCardShopTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    CreaCarta( modifier = Modifier.padding(innerPadding), navController = navController)
                }
            }
        }
        composable(Screen.CartasCreadas.route) {
            PokeCardShopTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    CartasCreadas(modifier = Modifier.padding(innerPadding), navController = navController)
                }
            }
        }
        composable(Screen.Reservas.route) {
            PokeCardShopTheme {
                Scaffold(modifier = Modifier.fillMaxSize()){ innerPadding ->
                    Reservadas(modifier = Modifier.padding(innerPadding), navController = navController)
                }
            }
        }
        composable(Screen.CrearEventos.route) {
            PokeCardShopTheme {
                Scaffold(modifier = Modifier.fillMaxSize()){ innerPadding ->
                    CreaEvento(modifier = Modifier.padding(innerPadding), navController = navController)
                }
            }
        }
        composable(Screen.EventosCreados.route) {
            PokeCardShopTheme {
                Scaffold(modifier = Modifier.fillMaxSize()){ innerPadding ->
                    EventosCreados(modifier = Modifier.padding(innerPadding), navController = navController)
                }
            }
        }


    }
}