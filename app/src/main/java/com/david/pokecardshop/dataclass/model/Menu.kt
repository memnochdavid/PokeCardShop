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
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.RectangleShape
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.david.pokecardshop.VerListaPokeAPI
import com.david.pokecardshop.dataclass.FormularioCarta
import com.david.pokecardshop.dataclass.Pokemon
import com.david.pokecardshop.dataclass.UsuarioFromKey
import com.david.pokecardshop.listaByGen
import com.david.pokecardshop.refBBDD
import com.david.pokecardshop.ui.theme.*
import com.david.pokecardshop.usuario_key

@Composable
fun Menu(
    selectedItem: Int,
    onItemSelected: (Int) -> Unit,
    onCloseDrawer: () -> Unit,
) {
    val sesion = UsuarioFromKey(usuario_key, refBBDD = refBBDD)
    Log.d("ADMIN_menu", "$sesion")
    Log.d("usuario_key_menu", usuario_key)
    val items = if (!sesion.admin) {
        listOf("Lista", "Opciones")
    } else {
        listOf("Lista", "Crear Carta", "Opciones")
    }
    val selectedColor = blanco80
    val unselectedColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
    val selectedtextColor = negro80
    val unselectedtextColor = blanco80

    ModalDrawerSheet(
        modifier = Modifier
            .widthIn(max = 250.dp),
        drawerShape = RectangleShape,
    ) {
        Column(modifier = Modifier.padding(top = 16.dp)) {
            items.forEachIndexed { index, item ->
                NavigationDrawerItem(
                    icon = {
                        when (item) {
                            "Lista" -> Icon(Icons.AutoMirrored.Filled.List, contentDescription = item)
                            "Crear Carta" -> Icon(Icons.Filled.Add, contentDescription = item)
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
    object Lista : Screen("Lista")
    object CrearCarta : Screen("CrearCarta")
    object Opciones : Screen("Opciones")
}
@Composable
fun Navigation(navController: NavHostController, modifier: Modifier) {
    val pokemonList = listaByGen(1)
    NavHost(navController = navController, startDestination = Screen.Lista.route) {
        composable(Screen.Lista.route) {
            PokeCardShopTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    VerListaPokeAPI(modifier = Modifier.padding(innerPadding), listaApi = pokemonList)
                }
            }
        }
        composable(Screen.Opciones.route) {
            // Content for the Opciones screen
            PokeCardShopTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "Opciones Screen", style = MaterialTheme.typography.headlineLarge)
                    }
                }
            }
        }
        composable(Screen.CrearCarta.route) {
            PokeCardShopTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    FormularioCarta( modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}