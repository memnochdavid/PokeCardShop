package com.david.pokecardshop.dataclass

import androidx.compose.runtime.getValue
import androidx.compose.ui.layout.ContentScale
import coil.compose.rememberAsyncImagePainter
import com.david.pokecardshop.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

data class Carta(
    var number: String="",
    var nombre: String="",
    var descripcion: String="",
    var imagen: String="",
    var habilidad_poke: List<String> = listOf(""),
    var fondo_foto: Int=0,
    var fondo_carta: Int=0
)
