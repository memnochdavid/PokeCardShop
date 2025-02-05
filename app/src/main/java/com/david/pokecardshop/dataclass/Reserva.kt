package com.david.pokecardshop.dataclass

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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.david.pokecardshop.CarPequeFB
import com.david.pokecardshop.CartaFB
import com.david.pokecardshop.R
import com.david.pokecardshop.cargaCartasCreadas
import com.david.pokecardshop.cartasCreadas
import com.david.pokecardshop.refBBDD
import com.david.pokecardshop.reservasCreadas
import com.david.pokecardshop.ui.theme.color_fuego_dark
import com.david.pokecardshop.usuario_key
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.collections.addAll
import kotlin.text.clear

data class Reserva(
    var reserva_id: String="",
    var usuario_id: String="",
    var carta_id: String=""
){
    init{
        reserva_id = refBBDD.child("tienda").child("reservas").push().key!!
    }
}

@Composable
fun Reservadas(
    modifier: Modifier = Modifier,
    navController: NavHostController
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var onCardClick by remember { mutableStateOf(Carta()) }
    var cartaGrande by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }
    var showToast by remember { mutableStateOf(false) } // New state variable

    val sesion = UsuarioFromKey(usuario_key, refBBDD)

    cargaCartasCreadas(onUpdateIsLoading = { isLoading = it })

    LaunchedEffect(key1 = sesion.admin) {
        if (sesion.admin) {
            cargaTodasReservas(
                context = context,
                scopeUser = scope,
                onUpdateIsLoading = { isLoading = it },
                onSuccess = { showToast = true } // Set showToast to true on success
            )
        } else {
            cargaReservasUsuario(
                usuario_id = usuario_key,
                context = context,
                scopeUser = scope,
                onUpdateIsLoading = { isLoading = it }
            )
        }
    }

    if (isLoading) { //se asegura de haber cargado los datos de la nube antes de empezar a mostrar nada
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
    }
    else{

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
                items(reservasCreadas) { reserva ->
                    val carta = cartasCreadas.find { it.carta_id == reserva.carta_id }
                    if (carta != null)
                        if(carta.carta_id !in sesion.propiedad){
                            CarPequeFB(carta = carta, onClick = {
                                cartaGrande = !cartaGrande
                                onCardClick = carta
                            })
                        }
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
                    isPropiedad = false
                )
            }
        }
    }
}


fun guardaReservaFB(
    reserva: Reserva,
    context: Context,
    scopeUser: CoroutineScope
    ){
    val identificador = reserva.reserva_id

    scopeUser.launch {
        try{
            refBBDD.child("tienda").child("reservas").child(identificador).setValue(reserva)

        }catch (e: Exception){
            Log.e("UserError", "Error al guardar la carta : ${e.message}")
            Toast.makeText(context, "Error al guardar la carta : ${e.message}", Toast.LENGTH_SHORT).show()
        }
        finally {
            Toast.makeText(context, "Reserva con ID ${reserva.reserva_id} guardada con éxito", Toast.LENGTH_SHORT).show()
        }
    }
}

fun aceptaReservaFB(
    reserva: Reserva,
    context: Context,
    scopeUser: CoroutineScope
){
    var propiedad_lista:MutableList<String> = mutableListOf()

    scopeUser.launch {
        try{
            refBBDD.child("tienda").child("usuarios").child(reserva.usuario_id).child("propiedad").get().addOnSuccessListener {

                for (propiedadSnapshot in it.children) {
                    val propiedad_usuario = propiedadSnapshot.getValue(String::class.java)!!
                    propiedad_lista.add(propiedad_usuario)
                }
            }
            propiedad_lista.add(reserva.carta_id)

            refBBDD.child("tienda").child("usuarios").child(reserva.usuario_id).child("propiedad").setValue(propiedad_lista)

            refBBDD.child("tienda").child("reservas").child(reserva.reserva_id).removeValue()

        }catch (e: Exception){
            Log.e("UserError", "Error al guardar la carta : ${e.message}")
            Toast.makeText(context, "Error al guardar la carta : ${e.message}", Toast.LENGTH_SHORT).show()
        }
        finally {
            Toast.makeText(context, "Reserva con ID ${reserva.reserva_id} procesada con éxito", Toast.LENGTH_SHORT).show()
        }
    }
}

fun cargaReservasUsuario(
    usuario_id: String,
    context: Context,
    scopeUser: CoroutineScope,
    onUpdateIsLoading: (Boolean) -> Unit
) {
    onUpdateIsLoading(true) // Start loading
    scopeUser.launch {
        try {
            refBBDD.child("tienda").child("reservas").get().addOnSuccessListener {
                val listaReservas = mutableListOf<Reserva>()
                for (reservaSnapshot in it.children) {
                    val reserva = reservaSnapshot.getValue(Reserva::class.java)!!
                    if (reserva.usuario_id == usuario_id) {
                        listaReservas.add(reserva)
                    }
                    Log.e("ReservasUser", "${reserva.usuario_id} - ${reserva.carta_id}")
                }
                reservasCreadas = listaReservas // Update the mutableStateOf variable
                onUpdateIsLoading(false) // Loading finished successfully
                //Toast.makeText(context, "Reservas cargadas con éxito", Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                onUpdateIsLoading(false) // Loading failed
                Log.e("UserError", "Error al cargar las reservas : ${it.message}")
                Toast.makeText(context, "Error al cargar las reservas : ${it.message}", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            onUpdateIsLoading(false) // Loading failed
            Log.e("UserError", "Error al cargar las reservas : ${e.message}")
            Toast.makeText(context, "Error al cargar las reservas : ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}

fun cargaTodasReservas(
    context: Context,
    scopeUser: CoroutineScope,
    onUpdateIsLoading: (Boolean) -> Unit,
    onSuccess: () -> Unit // New callback for success
) {
    onUpdateIsLoading(true) // Start loading
    scopeUser.launch {
        try {
            refBBDD.child("tienda").child("reservas").get().addOnSuccessListener {
                val listaReservas = mutableListOf<Reserva>()
                for (reservaSnapshot in it.children) {
                    val reserva = reservaSnapshot.getValue(Reserva::class.java)!!
                    listaReservas.add(reserva)
                    //Log.e("ReservasAdmin", "${reserva.usuario_id} - ${reserva.carta_id}")
                }
                reservasCreadas = listaReservas // Update the mutableStateOf variable
                onUpdateIsLoading(false) // Loading finished successfully
                onSuccess() // Call the success callback
            }.addOnFailureListener {
                onUpdateIsLoading(false) // Loading failed
                Log.e("UserError", "Error al cargar las reservas : ${it.message}")
                Toast.makeText(context, "Error al cargar las reservas : ${it.message}", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            onUpdateIsLoading(false) // Loading failed
            Log.e("UserError", "Error al cargar las reservas : ${e.message}")
            Toast.makeText(context, "Error al cargar las reservas : ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}