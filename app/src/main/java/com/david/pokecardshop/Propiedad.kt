package com.david.pokecardshop

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.david.pokecardshop.dataclass.Carta
import com.david.pokecardshop.dataclass.Reserva
import com.david.pokecardshop.ui.theme.color_fuego_dark
import com.google.firebase.database.GenericTypeIndicator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

var misCartas by mutableStateOf<List<String>>(emptyList())


@Composable
fun MisCartas(modifier: Modifier = Modifier, navController: NavHostController) {
    var onCardClick by remember { mutableStateOf(Carta()) }
    var cartaGrande by remember { mutableStateOf(false) }
    var isLoadingPropiedad by remember { mutableStateOf(true) }
    var misCartasCompradas by remember { mutableStateOf<List<Carta>>(emptyList()) }
    var isLoadingCreadas by remember { mutableStateOf(true) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    LaunchedEffect(key1 = Unit) {
        scope.launch {
            cargaCartasCreadas(
                onUpdateIsLoading = { isLoadingCreadas = it }
            )

            cargaCartasUsuario(
                context = context,
                scopeUser = scope,
                onUpdateIsLoading = { isLoadingPropiedad = it }
            )
        }
    }


    LaunchedEffect(key1 = misCartas) {
        misCartasCompradas = misCartas.mapNotNull { cartaId ->
            cartasCreadas.find { it.carta_id == cartaId }
        }
    }

    if (isLoadingPropiedad) { //se asegura de haber cargado los datos de la nube antes de empezar a mostrar nada
        Box(
            modifier = modifier
                .background(color_fuego_dark)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                color = colorResource(R.color.white),
                strokeWidth = 10.dp,
                modifier = Modifier.size(100.dp)
            )
        }
    } else {
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
                items(misCartasCompradas) { carta ->
                    CarPequeFB(carta = carta, onClick = {
                        cartaGrande = !cartaGrande
                        onCardClick = carta
                    })
                }
            }
            if (cartaGrande) {
                CartaFB(
                    modifier = Modifier
                        .fillMaxSize(0.75f)
                        .wrapContentHeight(),
                    carta = onCardClick,
                    onCartaGrandeChange = { cartaGrande = it },
                    navController = navController,
                    isPropiedad = true
                )
            }
        }
    }
}
fun cargaCartasUsuario(
    context: Context,
    scopeUser: CoroutineScope,
    onUpdateIsLoading: (Boolean) -> Unit
) {
    Log.d("cargaCartasUsuario", "cargaCartasUsuario called")
    Log.d("UserKey", "usuario_key: $usuario_key")
    onUpdateIsLoading(true) // Start loading
    scopeUser.launch {
        try {
            Log.d("cargaCartasUsuario", "Trying to fetch data from Firebase")
            refBBDD.child("tienda").child("usuarios").child(usuario_key).child("propiedad").get().addOnSuccessListener {
                Log.d("cargaCartasUsuario", "Firebase data fetch successful")
                val listaCartas = it.getValue(object : GenericTypeIndicator<List<String>>() {})
                if (listaCartas != null) {
                    misCartas = listaCartas // Update the mutableStateOf variable
                    Log.d("cargaCartasUsuario", "misCartas updated: $misCartas")
                } else {
                    Log.e("cargaCartasUsuario", "listaCartas is null")
                    misCartas = emptyList()
                }
                onUpdateIsLoading(false) // Loading finished successfully
                Log.d("cargaCartasUsuario", "onUpdateIsLoading(false) called")
            }.addOnFailureListener {
                onUpdateIsLoading(false) // Loading failed
                Log.e("UserError", "Error al cargar las reservas : ${it.message}")
                Toast.makeText(context, "Error al cargar las reservas : ${it.message}", Toast.LENGTH_SHORT).show()
                Log.d("cargaCartasUsuario", "onUpdateIsLoading(false) called (failure)")
            }
        } catch (e: Exception) {
            onUpdateIsLoading(false) // Loading failed
            Log.e("UserError", "Error al cargar las reservas : ${e.message}")
            Toast.makeText(context, "Error al cargar las reservas : ${e.message}", Toast.LENGTH_SHORT).show()
            Log.d("cargaCartasUsuario", "onUpdateIsLoading(false) called (exception)")
        }
    }
}