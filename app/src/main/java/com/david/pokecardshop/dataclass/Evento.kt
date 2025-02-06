package com.david.pokecardshop.dataclass

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavHostController
import com.david.pokecardshop.R
import com.david.pokecardshop.refBBDD
import com.david.pokecardshop.ui.stuff.Boton
import com.david.pokecardshop.ui.theme.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Date
import kotlin.collections.getValue
import kotlin.io.path.exists

data class Evento(
    var evento_id: String = "",
    var titulo: String ="",
    var descripcion: String = "",
    var actividades: List<String> = listOf(),
    var fecha: Date,
    var imagen: String = "",
){
    init{
        evento_id = refBBDD.child("tienda").child("eventos").push().key!!
    }
}

data class Inscripcion(
    var inscripcion_id: String = "",
    var evento_id: String = "",
    var inscritos: List<String> = listOf(),
){
    init {
        inscripcion_id = refBBDD.child("tienda").child("inscripciones").push().key!!
    }
}
/*
@Composable
fun CreaEvento(modifier: Modifier = Modifier, navController: NavHostController){
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
        //.background(color_electrico_dark)
    ) {
        item {
            FormularioEvento(
                numberText = numberText,
                nameText = nameText,
                descText = descText,
                habilidadText = habilidadText,
                descHabilidad = descHabilidad,
                onNumberChange = { numberText = it },
                onNameChange = { nameText = it },
                onHabilidadChange = { habilidadText = it },
                onDescHabilidadChange = { descHabilidad = it },
                onDescChange = { descText = it },
                isLoadingDesc = isDescriptionLoading,
                isLoadingAbility = isAbilityListLoading,
                isAbilityNameLoading = isAbilityDetailsLoading
            )
        }
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(10.dp),
                horizontalArrangement = Arrangement.Center,
            ) {
                CartelEvento(
                    modifier = Modifier
                        .fillMaxWidth(0.75f)
                        .wrapContentHeight(),
                    numberText = numberText,
                    nameText = nameText,
                    descText = descText,
                    selectedImageUri = selectedImageUri,
                    habilidadText = habilidadText,
                    descHabilidad = descHabilidad,
                    tipo = tipo,
                    isLoadingDesc = isDescriptionLoading,
                    isLoadingAbility = isAbilityListLoading,
                    isAbilityNameLoading = isAbilityDetailsLoading
                )
            }
        }
        item{
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ){
                Boton(
                    text = "Auto",
                    onClick = {
                        autoSelect = !autoSelect
                    }
                )
                Boton(
                    text = "Guardar",
                    onClick = {
                        val carta_creada = Carta(
                            number = numberText,
                            nombre = nameText,
                            descripcion = descText,
                            imagen = caraImagenURL(nameText),
                            habilidad_poke = listOf(habilidadText, descHabilidad),
                            fondo_foto = R.drawable.background_foto,
                            fondo_carta = TypeToBackground(tipo),
                            tipo = tipo.type.name,
                            imagenAPI = selectedImageUri.toString()

                        )
                        guardaCartaFB(carta_creada, context, scopeUser)
                    }
                )
                Boton(
                    text = "Atras",
                    onClick = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}
*/
@Composable
fun FormularioEvento(
    titulo: String = "Título del evento",
    descripcion: String = "Descripción del evento",
    lista_actividades: List<String> = listOf(""),
    onTituloChange: (String) -> Unit = {},
    onDescripcionChange: (String) -> Unit = {},
    onListaActividadesChange: (List<String>) -> Unit = {}
){
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        OutlinedTextField(
            value = titulo,
            onValueChange = onTituloChange,
            label = { Text("Nombre") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = descripcion,
            onValueChange = onDescripcionChange,
            label = { Text("Descripción") },
            modifier = Modifier.fillMaxWidth()
        )
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ){
            OutlinedTextField(
                value = lista_actividades[0],
                onValueChange = { onListaActividadesChange(listOf(it)) },
                label = { Text("Nueva actividad") },
                modifier = Modifier.fillMaxWidth(0.5f)
            )
            Boton(
                text = "Añadir",
                onClick = {
                    onListaActividadesChange(lista_actividades + listOf(lista_actividades[0]))
                }
            )
        }
    }
}



@Composable
fun CartelEvento(
    modifier: Modifier = Modifier,
    titulo_evento: String = "Título del evento",
    descripcion_evento: String = "Descripción del evento",
    lista_actividades: List<String> = listOf("Actividad 1", "Actividad 2", "Actividad 3")
){
    Card(
        shape = RoundedCornerShape(0.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 20.dp),
    ){
        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
                .background(color_fonto_cartel)
        ) {
            val (logo, titulo, datos, actividades, sponsors) = createRefs()
            Image(
                painter = painterResource(R.drawable.pokemonlogo)
                , contentDescription = "Logo",
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .constrainAs(logo) {
                        top.linkTo(parent.top, margin = 10.dp)
                        start.linkTo(parent.start, margin = 20.dp)
                        end.linkTo(parent.end, margin = 20.dp)
                    }
            )
            Text(
                text = titulo_evento,
                fontSize = 30.sp,
                color = Color.Red,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .constrainAs(titulo) {
                        top.linkTo(logo.bottom, margin = 10.dp)
                        start.linkTo(parent.start, margin = 10.dp)
                        end.linkTo(parent.end, margin = 10.dp)
                    }
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .constrainAs(datos) {
                        top.linkTo(titulo.bottom, margin = 10.dp)
                        start.linkTo(parent.start, margin = 20.dp)
                        end.linkTo(parent.end, margin = 20.dp)
                    },
                horizontalArrangement = Arrangement.SpaceEvenly
            ){
                Image(
                    painter = painterResource(R.drawable.charizard)
                    , contentDescription = "imagen_izq",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(100.dp)
                )
                Text(
                    text = descripcion_evento,
                    fontSize = 30.sp,
                    color = Color.Red,
                    fontWeight = FontWeight.Bold,
                )
                Image(
                    painter = painterResource(R.drawable.pikachu_cartel)
                    , contentDescription = "imagen_izq",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(100.dp)
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .constrainAs(actividades) {
                        top.linkTo(datos.bottom, margin = 10.dp)
                        start.linkTo(parent.start, margin = 20.dp)
                        end.linkTo(parent.end, margin = 20.dp)
                    },
                horizontalArrangement = Arrangement.Center
            ){
                lista_actividades.forEach(
                    action = {
                        Text(
                            text = it,
                            fontSize = 20.sp,
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                        )
                        if (it != lista_actividades.last()){
                            Text(
                                text = " - ",
                                fontSize = 20.sp,
                                color = Color.Black,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .constrainAs(sponsors) {
                        top.linkTo(actividades.bottom, margin = 10.dp)
                        start.linkTo(parent.start, margin = 20.dp)
                        end.linkTo(parent.end, margin = 20.dp)
                        bottom.linkTo(parent.bottom, margin = 10.dp)
                    },
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ){
                Image(
                    painter = painterResource(R.drawable.pokeball_icon)
                    , contentDescription = "imagen_izq",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(80.dp)
                )
                Image(
                    painter = painterResource(R.drawable.icon)
                    , contentDescription = "imagen_izq",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(100.dp)
                )
                Image(
                    painter = painterResource(R.drawable.logo_escuela)
                    , contentDescription = "imagen_izq",
                    contentScale = ContentScale.Inside,
                    modifier = Modifier
                        .size(100.dp)
                )
            }
        }
    }
}














fun creaEvento(
    titulo: String,
    descripcion: String,
    actividades: List<String>,
    fecha: Date,
    imagen: String,
    context: Context,
    scopeUser: CoroutineScope
) {
    val evento = Evento(titulo = titulo, descripcion = descripcion, actividades = actividades,fecha = fecha, imagen = imagen)

    scopeUser.launch {
        try {
            refBBDD.child("tienda").child("eventos").child(evento.evento_id).setValue(evento)
        } catch (e: Exception) {
            Log.e("UserError", "Error al guardar el evento : ${e.message}")
            Toast.makeText(context, "Error al guardar el evento : ${e.message}", Toast.LENGTH_SHORT).show()
        } finally {
            Toast.makeText(context, "Evento ${evento.titulo} guardado con éxito", Toast.LENGTH_SHORT).show()
        }
    }
}

fun creaInscripcion(
    evento_id: String,
    nuevo_inscrito: String,
    context: Context,
    scopeUser: CoroutineScope
) {
    scopeUser.launch {
        try {
            // Check if the event exists
            val eventoRef = refBBDD.child("tienda").child("eventos").child(evento_id)
            val eventoSnapshot = eventoRef.get().await()

            if (!eventoSnapshot.exists()) {
                // Event does not exist, show an error and return
                Toast.makeText(context, "El evento no existe", Toast.LENGTH_SHORT).show()
                return@launch
            }

            // Get the current inscripcion or create a new one
            val inscripcionRef = refBBDD.child("tienda").child("inscripciones").orderByChild("evento_id").equalTo(evento_id)
            val snapshot = inscripcionRef.get().await()
            var inscripcion: Inscripcion? = null
            if (snapshot.exists()) {
                for (childSnapshot in snapshot.children) {
                    inscripcion = childSnapshot.getValue(Inscripcion::class.java)
                    break
                }
            }
            if (inscripcion == null) {
                inscripcion = Inscripcion(evento_id = evento_id, inscritos = listOf(nuevo_inscrito))
            } else {
                val inscritos = inscripcion.inscritos.toMutableList()
                inscritos.add(nuevo_inscrito)
                inscripcion.inscritos = inscritos.toList()
            }

            // Save the inscripcion
            refBBDD.child("tienda").child("inscripciones").child(inscripcion.inscripcion_id).setValue(inscripcion)
            Toast.makeText(context, "Inscripción guardada con éxito", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Log.e("UserError", "Error al guardar la inscripción : ${e.message}")
            Toast.makeText(context, "Error al guardar la inscripción : ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}

@Preview (showBackground = true, widthDp = 500, heightDp = 600)
@Composable
fun GreetingPreview() {
    FormularioEvento()
}
