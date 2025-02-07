package com.david.pokecardshop


import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.edit
import androidx.navigation.NavHostController
import kotlinx.coroutines.launch

@Composable
fun Opciones(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    onThemeChange: (Boolean) -> Unit
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("my_preferences", Context.MODE_PRIVATE)

    var dark by remember {
        mutableStateOf(
            sharedPreferences.getBoolean("dark_mode", false)
        )
    }
    var divisa by remember {
        mutableStateOf(
            sharedPreferences.getBoolean("divisa", false)
        )
    }
    LaunchedEffect(divisa) {
        sharedPreferences.edit {
            putBoolean("divisa", divisa)
            apply()
            divisaSeleccionada = if (divisa) {
                "$"
            } else {
                "€"
            }
        }
    }
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 50.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ActivaTemaOscuro(
                modo_oscuro = dark,
                onModoFotosChange = { newDarkMode ->
                    dark = newDarkMode // Update the state
                    onThemeChange(newDarkMode)
                    scope.launch {
                        sharedPreferences.edit {
                            putBoolean("dark_mode", newDarkMode)
                            apply()
                        }
                    }
                }
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 50.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ){
            CambiaDivisa(
                divisa = divisa,
                onDivisaChange = { newDivisa ->
                    divisa = newDivisa
                    scope.launch {
                        sharedPreferences.edit {
                            putBoolean("divisa", newDivisa)
                            apply()
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun ActivaTemaOscuro(
    modo_oscuro: Boolean,
    onModoFotosChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(4.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Tema Oscuro")
        Switch(
            checked = modo_oscuro,
            onCheckedChange = onModoFotosChange,
        )
    }
}
@Composable
fun CambiaDivisa(
    divisa: Boolean,
    onDivisaChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(4.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Divisa - € / $")
        Switch(
            checked = divisa,
            onCheckedChange = onDivisaChange,
        )
    }
}