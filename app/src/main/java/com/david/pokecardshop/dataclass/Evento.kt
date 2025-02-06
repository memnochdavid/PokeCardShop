package com.david.pokecardshop.dataclass

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.david.pokecardshop.CarPequeFB
import com.david.pokecardshop.CartaFB
import com.david.pokecardshop.R
import com.david.pokecardshop.cargaCartasCreadas
import com.david.pokecardshop.cartasCreadas
import com.david.pokecardshop.misCartas
import com.david.pokecardshop.refBBDD
import com.david.pokecardshop.ui.stuff.Boton
import com.david.pokecardshop.ui.theme.*
import com.david.pokecardshop.usuario_key
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Date
import kotlin.collections.getValue
import kotlin.io.path.exists

var eventosCreados by mutableStateOf<List<Evento>>(emptyList())
var misEventos by mutableStateOf<List<String>>(emptyList())

data class Evento(
    var evento_id: String = "",
    var titulo: String ="",
    var descripcion: String = "",
    var actividades: List<String> = listOf(),
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

@Composable
fun CreaEvento(
    modifier: Modifier = Modifier,
    navController: NavHostController
) {
    val context = LocalContext.current
    val scopeUser = rememberCoroutineScope()
    var tituloText by remember { mutableStateOf("Título del evento") }
    var descText by remember { mutableStateOf("Descripción del evento") }
    var lista_actividades by remember { mutableStateOf(mutableListOf("")) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
    ) {
        item {
            FormularioEvento(
                titulo = tituloText,
                descripcion = descText,
                lista_actividades = lista_actividades,
                onTituloChange = { tituloText = it },
                onDescripcionChange = { descText = it },
                onListaActividadesChange = { lista_actividades = it.toMutableList() }
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
                        .padding(horizontal = 10.dp),
                    titulo_evento = tituloText,
                    descripcion_evento = descText,
                    lista_actividades = lista_actividades,
                    onTituloChange = { tituloText = it },
                    onDescripcionChange = { descText = it },
                    onListaActividadesChange = { lista_actividades = it.toMutableList() }
                )
            }
        }
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Boton(
                    text = "Guardar",
                    onClick = {
                        // Remove the last empty activity if it exists
                        val filteredActividades = lista_actividades.filter { it.isNotBlank() }
                        val cartel_creado = Evento(
                            titulo = tituloText,
                            descripcion = descText,
                            actividades = filteredActividades
                        )
                        creaEvento(cartel_creado.titulo, cartel_creado.descripcion, cartel_creado.actividades, context, scopeUser)
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

@Composable
fun FormularioEvento(
    modifier: Modifier = Modifier,
    titulo: String = "Título del evento",
    descripcion: String = "Descripción del evento",
    lista_actividades: MutableList<String> = mutableListOf(""),
    onTituloChange: (String) -> Unit = {},
    onDescripcionChange: (String) -> Unit = {},
    onListaActividadesChange: (MutableList<String>) -> Unit = {}
) {
    Column(
        modifier = modifier
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
        Spacer(modifier = Modifier.height(8.dp))
        // Display all activities
        Column(modifier = Modifier.fillMaxWidth()) {
            lista_actividades.forEachIndexed { index, actividad ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = actividad,
                        onValueChange = { newValue ->
                            val newList = lista_actividades.toMutableList()
                            newList[index] = newValue
                            onListaActividadesChange(newList)
                        },
                        label = { Text("Actividad ${index + 1}") },
                        modifier = Modifier.fillMaxWidth(0.8f)
                    )
                    // Remove button
                    IconButton(onClick = {
                        val newList = lista_actividades.toMutableList()
                        newList.removeAt(index)
                        onListaActividadesChange(newList)
                    }) {
                        Icon(
                            painter = painterResource(id = android.R.drawable.ic_delete),
                            contentDescription = "Eliminar"
                        )
                    }
                    IconButton(onClick = {
                        onListaActividadesChange(lista_actividades.toMutableList().apply { add("") })
                    }) {
                        Icon(
                            painter = painterResource(id = android.R.drawable.ic_input_add),
                            contentDescription = "Añadir"
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CartelEvento(
    modifier: Modifier = Modifier,
    titulo_evento: String = "Título del evento",
    descripcion_evento: String = "Descripción del evento",
    lista_actividades: List<String> = listOf(""),
    onTituloChange: (String) -> Unit,
    onDescripcionChange: (String) -> Unit,
    onListaActividadesChange: (List<String>) -> Unit
) {
    Card(
        modifier = modifier.fillMaxSize(),
        shape = RoundedCornerShape(0.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 20.dp),
    ) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
                .background(color_fonto_cartel)
        ) {
            val (logo, titulo, datos, actividades, sponsors) = createRefs()
            Image(
                painter = painterResource(R.drawable.pokemonlogo),
                contentDescription = "Logo",
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .constrainAs(logo) {
                        top.linkTo(parent.top, margin = 10.dp)
                        start.linkTo(parent.start, margin = 20.dp)
                        end.linkTo(parent.end, margin = 20.dp)
                    }
            )
            BasicTextField(
                value = titulo_evento,
                onValueChange = onTituloChange,
                textStyle = TextStyle(
                    fontSize = 30.sp,
                    color = Color.Red,
                    fontWeight = FontWeight.Bold
                ),
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
            ) {
                Image(
                    painter = painterResource(R.drawable.charizard),
                    contentDescription = "imagen_izq",
                    contentScale = ContentScale.Inside,
                    modifier = Modifier
                        .size(100.dp)
                )
                BasicTextField(
                    modifier = Modifier.fillMaxWidth(0.7f),
                    value = descripcion_evento,
                    onValueChange = onDescripcionChange,
                    textStyle = TextStyle(
                        fontSize = 30.sp,
                        color = Color.Red,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold
                    ),
                )
                Image(
                    painter = painterResource(R.drawable.pikachu_cartel),
                    contentDescription = "imagen_izq",
                    contentScale = ContentScale.Inside,
                    modifier = Modifier
                        .size(100.dp)
                )
            }
            Column( // Changed from LazyColumn to Column
                modifier = Modifier
                    .fillMaxWidth()
                    .constrainAs(actividades) {
                        top.linkTo(datos.bottom, margin = 10.dp)
                        start.linkTo(parent.start, margin = 20.dp)
                        end.linkTo(parent.end, margin = 20.dp)
                    },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                lista_actividades.forEachIndexed { index, actividad ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp, horizontal = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        BasicTextField(
                            value = actividad,
                            onValueChange = { newValue ->
                                val newList = lista_actividades.toMutableList()
                                newList[index] = newValue
                                onListaActividadesChange(newList)
                            },
                            textStyle = TextStyle(
                                fontSize = 30.sp,
                                color = Color.Black,
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Bold
                            ),
                        )
                        if (index < lista_actividades.size - 1) {
                            Text(
                                text = " - ",
                                fontSize = 20.sp,
                                color = Color.Black,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
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
            ) {
                Image(
                    painter = painterResource(R.drawable.pokeball_icon),
                    contentDescription = "imagen_izq",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(80.dp)
                )
                Image(
                    painter = painterResource(R.drawable.icon),
                    contentDescription = "imagen_izq",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(100.dp)
                )
                Image(
                    painter = painterResource(R.drawable.logo_escuela),
                    contentDescription = "imagen_izq",
                    contentScale = ContentScale.Inside,
                    modifier = Modifier
                        .size(100.dp)
                )
            }
        }
    }
}

@Composable
fun EventoPeque(
    modifier: Modifier = Modifier,
    evento: Evento = Evento()
){
    var isPressed by remember { mutableStateOf(false) }
    var click by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    val scale = animateFloatAsState(
        targetValue = if (isPressed) 0.90f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy, // Moderate bouncing
            stiffness = Spring.StiffnessMedium // Moderate stiffness
        ), label = ""
    )
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .scale(scale.value)
            .clickable(
                interactionSource = interactionSource,
                indication = null,

                onClick = {

                }
            )
            .indication(
                interactionSource = interactionSource,
                indication = null
            )
            .pointerInput(Unit) {//lo que hace al pulsar en el Card()
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        try {
                            awaitRelease()
                        } finally {
                            isPressed = false // Reset isPressed in finally block
                        }
                        click = !click

                    }
                )
            },
        shape = RoundedCornerShape(5.dp)
    ){
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .border(5.dp, verde40, RectangleShape)
                .background(verde40)
        ){
            val (logo, nombre, imagen) = createRefs()
            val imagen_evento = rememberAsyncImagePainter(
                model = R.drawable.logo_evento,
                contentScale = ContentScale.FillBounds,
            )
            val backgroundImage = painterResource(R.drawable.evento_background)
            Image(
                modifier = Modifier
                    .constrainAs(imagen){
                        start.linkTo(parent.start)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        //end.linkTo(nombre.start)
                    }
                    .size(100.dp),
                painter = backgroundImage,
                contentDescription = "Background Imagen",
                contentScale = ContentScale.Crop // Adjust as needed
            )
            Image(
                painter = imagen_evento,
                contentDescription = "Evento Imagen",
                modifier = Modifier
                    .size(100.dp)
                    .fillMaxSize()
                    .constrainAs(imagen) {
                        start.linkTo(parent.start)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        //end.linkTo(nombre.start)
                    }
                    .background(Color.White.copy(alpha = 0.3f))
            )
            Text(
                text = evento.titulo,
                color = Color.Black,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .constrainAs(nombre) {
                        start.linkTo(imagen.end)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        end.linkTo(parent.end)
                    }
            )
        }

    }
}


@Composable
fun EventosCreados(
    modifier: Modifier = Modifier,
    navController: NavHostController
){
    var onEventoClick by remember { mutableStateOf(Evento()) }
    var eventoGrande by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }
    //val sesion = UsuarioFromKey(usuario_key, refBBDD)

    cargaEventosCreados(onUpdateIsLoading = { isLoading = it })

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
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                items(eventosCreados) { evento ->

                    if(evento.evento_id !in misEventos){
                        EventoPeque(evento = evento)
                    }
                }
            }
            if (eventoGrande) {
                //meustra evento_grande
            }
        }
    }
}



fun creaEvento(
    titulo: String,
    descripcion: String,
    actividades: List<String>,
    context: Context,
    scopeUser: CoroutineScope
) {
    val evento = Evento(titulo = titulo, descripcion = descripcion, actividades = actividades)

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


fun cargaEventosCreados(
    onUpdateIsLoading: (Boolean) -> Unit
){
    refBBDD.child("tienda").child("eventos").get().addOnSuccessListener { dataSnapshot ->
        val listaEvento = mutableListOf<Evento>()
        for (childSnapshot in dataSnapshot.children) {
            val evento = childSnapshot.getValue(Evento::class.java)
            evento?.let { listaEvento.add(it) }
        }
        eventosCreados = listaEvento
        onUpdateIsLoading(false)
    }.addOnFailureListener { exception ->
        onUpdateIsLoading(false)
    }
}


@Preview (showBackground = true, widthDp = 500, heightDp = 900)
@Composable
fun GreetingPreview() {
    EventoPeque()
}
