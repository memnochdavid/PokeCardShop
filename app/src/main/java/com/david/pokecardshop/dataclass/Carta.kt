package com.david.pokecardshop.dataclass

data class Carta(
    var number: String="",
    var nombre: String="",
    var descripcion: String="",
    var imagen: String="",
    var habilidad_poke: List<String> = listOf(""),
    var fondo_foto: Int=0,
    var fondo_carta: Int=0
)
