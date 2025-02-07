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
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.david.pokecardshop.R
import com.david.pokecardshop.dataclass.model.truncaDosDecimales
import com.david.pokecardshop.divisaSeleccionada
import com.david.pokecardshop.refBBDD
import com.david.pokecardshop.ui.stuff.Boton
import com.david.pokecardshop.ui.theme.*
import com.david.pokecardshop.usuario_key
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.collections.toMutableList
import kotlin.random.Random
import kotlin.text.toFloat

var eventosCreados by mutableStateOf<List<Evento>>(emptyList())
var misEventos by mutableStateOf<List<String>>(emptyList())
var inscritosEventoActual by mutableStateOf<List<String>>(emptyList())

data class Evento(
    var evento_id: String = "",
    var titulo: String ="",
    var descripcion: String = "",
    var actividades: List<String> = listOf(),
    var precio: Float = 0f,
){
    init{
        evento_id = refBBDD.child("tienda").child("eventos").push().key!!
        precio = truncaDosDecimales(Random.nextDouble(30.0, 100.0).toFloat())
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
    evento: Evento,
    onEventoClick: () -> Unit = {},
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
                            isPressed = false
                        }
                        click = !click
                        onEventoClick()

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
        ) {
            val (datos, nombre, imagen) = createRefs()
            val imagen_evento = rememberAsyncImagePainter(
                model = R.drawable.logo_evento,
                contentScale = ContentScale.FillBounds,
            )
            val backgroundImage = painterResource(R.drawable.evento_background)

            Box(
                modifier = Modifier
                    .constrainAs(imagen) {
                        start.linkTo(parent.start)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                    }
            ) {
                Image(
                    modifier = Modifier
                        .size(100.dp),
                    painter = backgroundImage,
                    contentDescription = "Background Imagen",
                    contentScale = ContentScale.Crop
                )

                Image(
                    painter = imagen_evento,
                    contentDescription = "Evento Imagen",
                    modifier = Modifier
                        .matchParentSize()
                        .background(Color.White.copy(alpha = 0.3f))
                        .zIndex(1f)
                )
            }
            Column(
                modifier = Modifier
                    .constrainAs(datos) {
                        start.linkTo(imagen.end)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        end.linkTo(parent.end)
                    }
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = evento.titulo,
                        color = Color.Black,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Text(
                        text = evento.precio.toString() + divisaSeleccionada,
                        color = Color.Black,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}

@Composable
fun EventoGrande(
    modifier: Modifier = Modifier,
    evento: Evento = Evento(),
    onEventoClick: () -> Unit = {}
){
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var isLoading by remember { mutableStateOf(true) }

    var usuarioInscrito by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = evento.evento_id) {
        cargaInscripcionesEvento(evento.evento_id) { loading, inscritos ->
            isLoading = loading
            if (!loading) {
                inscritosEventoActual = inscritos
            }
        }
    }

    LaunchedEffect(key1 = inscritosEventoActual) {
        usuarioInscrito = inscritosEventoActual.contains(usuario_key)
    }

    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ){
            CircularProgressIndicator()
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Transparent)
                .padding(top = 0.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ){
            Card(
                modifier = modifier.fillMaxSize(0.8f),
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
                    Text(
                        text = evento.titulo,
                        color = Color.Red,
                        fontSize = 30.sp,
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
                    ) {
                        Image(
                            painter = painterResource(R.drawable.charizard),
                            contentDescription = "imagen_izq",
                            contentScale = ContentScale.Inside,
                            modifier = Modifier
                                .size(100.dp)
                        )
                        Text(
                            text = evento.descripcion,
                            fontSize = 30.sp,
                            color = Color.Red,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.fillMaxWidth(0.7f),
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
                        evento.actividades.forEachIndexed { index, actividad ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 5.dp, horizontal = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = actividad,
                                    fontSize = 20.sp,
                                    color = Color.Black,
                                    textAlign = TextAlign.Center,
                                    fontWeight = FontWeight.Bold
                                )
                                if (index < evento.actividades.size - 1) {
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
            Spacer(modifier = Modifier
                .height(15.dp)
                .background(Color.Transparent))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Transparent),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Boton(
                    text = "Atrás",
                    onClick = {
                        onEventoClick()
                    }
                )
                if (!usuarioInscrito) {
                    Boton(
                        text = "Apúntate!",
                        onClick = {
                            creaInscripcion(
                                evento_id = evento.evento_id,
                                nuevo_inscrito = usuario_key,
                                context = context,
                                scopeUser = scope
                            )
                        }
                    )
                }
            }
        }
    }

}

@Composable
fun EventosCreados(
    modifier: Modifier = Modifier,
    navController: NavHostController
){
    var eventoGrande by remember { mutableStateOf(false) }
    var eventoSeleccionado by remember { mutableStateOf<Evento?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var isLoadingInscripciones by remember { mutableStateOf(false) }

    cargaEventosCreados(onUpdateIsLoading = { isLoading = it })

    LaunchedEffect(key1 = eventoSeleccionado?.evento_id) {
        if (eventoSeleccionado != null) {
            isLoadingInscripciones = true
            cargaInscripcionesEvento(eventoSeleccionado!!.evento_id) { loading, inscritos ->
                isLoadingInscripciones = loading
                if (!loading) {
                    inscritosEventoActual = inscritos
                }
            }
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
            LazyColumn(//este lazycolumn
                modifier = Modifier
                    .fillMaxSize()
            ) {
                items(eventosCreados, key = { it.evento_id }) { evento ->
                    if(evento.evento_id !in misEventos){
                        EventoPeque(
                            evento = evento,
                            onEventoClick = {
                                eventoSeleccionado = evento
                                eventoGrande = !eventoGrande
                            }
                        )
                    }
                }
            }
            if (eventoGrande && eventoSeleccionado != null) {
                //meustra evento_grande
                EventoGrande(evento = eventoSeleccionado!!, onEventoClick = {eventoGrande = !eventoGrande})
            }
        }
    }
}

@Composable
fun MisEventos(
    modifier: Modifier = Modifier,
    navController: NavHostController
){
    var eventoGrande by remember { mutableStateOf(false) }
    var eventoSeleccionado by remember { mutableStateOf<Evento?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    cargaEventosCreados( onUpdateIsLoading = { isLoading = it })
    cargaMisEventos(onUpdateIsLoading = { isLoading = it })



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
            LazyColumn(//este lazycolumn
                modifier = Modifier
                    .fillMaxSize()
            ) {
                val eventosFiltrados = eventosCreados.filter { it.evento_id in misEventos }
                items(eventosFiltrados) { evento ->
                    EventoPeque(
                        evento = evento,
                        onEventoClick = {
                            eventoSeleccionado = evento
                            eventoGrande = !eventoGrande
                        }
                    )
                }
            }
            if (eventoGrande && eventoSeleccionado != null) {
                //meustra evento_grande
                EventoGrande(evento = eventoSeleccionado!!, onEventoClick = {eventoGrande = !eventoGrande})
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
    val inscripcionesRef = refBBDD.child("tienda").child("inscripciones")

    // Query to find existing inscription by event_id
    val query = inscripcionesRef.orderByChild("evento_id").equalTo(evento_id)

    query.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            scopeUser.launch {
                try {
                    if (snapshot.exists()) {
                        // Inscription exists, update it
                        for (inscripcionSnapshot in snapshot.children) {
                            val inscripcion = inscripcionSnapshot.getValue(Inscripcion::class.java)
                            if (inscripcion != null) {
                                val updatedInscritos = inscripcion.inscritos.toMutableList()
                                if (!updatedInscritos.contains(nuevo_inscrito)) {
                                    updatedInscritos.add(nuevo_inscrito)
                                    inscripcion.inscritos = updatedInscritos
                                    inscripcionesRef.child(inscripcion.inscripcion_id).setValue(inscripcion)
                                    Log.d("creaInscripcion", "User $nuevo_inscrito added to existing inscription for event $evento_id")
                                } else {
                                    Log.d("creaInscripcion", "User $nuevo_inscrito already in inscription for event $evento_id")
                                }
                            }
                        }
                    } else {
                        // Inscription doesn't exist, create a new one
                        val inscripcion = Inscripcion(evento_id = evento_id, inscritos = listOf(nuevo_inscrito))
                        inscripcionesRef.child(inscripcion.inscripcion_id).setValue(inscripcion)
                        Log.d("creaInscripcion", "New inscription created for event $evento_id with user $nuevo_inscrito")
                    }
                } catch (e: Exception) {
                    Log.e("UserError", "Error al guardar la inscripcion : ${e.message}")
                    Toast.makeText(context, "Error al guardar la inscripcion : ${e.message}", Toast.LENGTH_SHORT).show()
                } finally {
                    Toast.makeText(context, "Inscripción guardada con éxito", Toast.LENGTH_SHORT).show()
                }
            }
        }

        override fun onCancelled(error: DatabaseError) {
            Log.e("UserError", "Error al leer la inscripcion : ${error.message}")
            Toast.makeText(context, "Error al leer la inscripcion : ${error.message}", Toast.LENGTH_SHORT).show()
        }
    })
}

fun cargaInscripcionesEvento(
    evento_id: String,
    onUpdateIsLoading: (Boolean, List<String>) -> Unit
) {
    val inscripcionesRef = refBBDD.child("tienda").child("inscripciones")

    val query = inscripcionesRef.orderByChild("evento_id").equalTo(evento_id)

    onUpdateIsLoading(true, emptyList())
    Log.d("cargaInscripcionesEvento", "Fetching inscriptions for event: $evento_id")

    query.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            try {
                if (snapshot.exists()) {
                    for (inscripcionSnapshot in snapshot.children) {
                        val inscripcion = inscripcionSnapshot.getValue(Inscripcion::class.java)
                        if (inscripcion != null) {
                            Log.d("cargaInscripcionesEvento", "Inscriptions found for event $evento_id: ${inscripcion.inscritos}")
                            onUpdateIsLoading(false, inscripcion.inscritos)
                            return
                        }
                    }
                    Log.d("cargaInscripcionesEvento", "No inscriptions found for event $evento_id")
                    onUpdateIsLoading(false, emptyList())
                } else {
                    Log.d("cargaInscripcionesEvento", "No inscriptions found for event $evento_id")
                    onUpdateIsLoading(false, emptyList())
                }
            } catch (e: Exception) {
                Log.e("UserError", "Error al cargar las inscripciones : ${e.message}")
                onUpdateIsLoading(false, emptyList())
            }
        }

        override fun onCancelled(error: DatabaseError) {
            Log.e("UserError", "Error al leer las inscripciones : ${error.message}")
            onUpdateIsLoading(false, emptyList())
        }
    })
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

fun cargaMisEventos(
    onUpdateIsLoading: (Boolean) -> Unit
) {
    refBBDD.child("tienda").child("inscripciones").get().addOnSuccessListener { dataSnapshot ->
        val listaEventos = mutableListOf<String>()
        for (inscripcionSnapshot in dataSnapshot.children) {
            val inscripcion = inscripcionSnapshot.getValue(Inscripcion::class.java)
            if (inscripcion != null && inscripcion.inscritos.contains(usuario_key)) {
                listaEventos.add(inscripcion.evento_id)
            }
        }
        misEventos = listaEventos
        onUpdateIsLoading(false)
    }.addOnFailureListener { exception ->
        onUpdateIsLoading(false)
    }
}

fun eventoFromID(evento_id: String): Evento? {
    return eventosCreados.find { it.evento_id == evento_id }
}

//@Preview (showBackground = true, widthDp = 500, heightDp = 900)
//@Composable
//fun GreetingPreview() {
//    EventoPeque()
//}


