package com.david.pokecardshop

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.activity.compose.setContent
import androidx.activity.result.launch
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.david.pokecardshop.dataclass.Carta
import com.david.pokecardshop.dataclass.Reserva
import com.david.pokecardshop.dataclass.TypeStringToColor
import com.david.pokecardshop.dataclass.UsuarioFromKey
import com.david.pokecardshop.dataclass.aceptaReservaFB
import com.david.pokecardshop.dataclass.adaptaDescripcion
import com.david.pokecardshop.dataclass.guardaReservaFB
import com.david.pokecardshop.ui.stuff.Boton
import com.david.pokecardshop.ui.theme.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.OutputStream

var cartasCreadas by mutableStateOf<List<Carta>>(emptyList())
var reservasCreadas by mutableStateOf<List<Reserva>>(emptyList())

@Composable
fun CartaFB(modifier: Modifier = Modifier, carta: Carta, onCartaGrandeChange: (Boolean) -> Unit,navController: NavHostController, isPropiedad: Boolean = false) {

    val numberText = carta.number
    val nameText = carta.nombre
    val descText = carta.descripcion
    val habilidadText = carta.habilidad_poke[0]
    val descHabilidad = carta.habilidad_poke[1]

    val color_tipo = TypeStringToColor(carta.tipo,1)
    val color_tipo_transparente = TypeStringToColor(carta.tipo,2)

    val backgroundImage = painterResource(carta.fondo_foto)
    val backgroundCard = painterResource(carta.fondo_carta)
    var imagen_poke = rememberAsyncImagePainter(carta.imagen)

    val density = LocalDensity.current
    val gradientWidth = with(density) { 200.dp.toPx() }
    val shimmer = shimmerBrush(
        gradientWidthPx = gradientWidth,
        durationMillis = 3500,
        colors = listOf(
            Color.Transparent,
            color_tipo.copy(alpha = 0.6f),
            Color.Transparent
        )
    )

    ///////////
    var isSaving by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val graphicsLayer = rememberGraphicsLayer()
    /////////
    val sesion = UsuarioFromKey(usuario_key, refBBDD)

    val context = navController.context
    val scope = rememberCoroutineScope()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .padding(top = 0.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        Card(
            modifier = Modifier
                .background(Color.Transparent)
                .fillMaxWidth(0.75f)
                .drawWithContent {
                    graphicsLayer.record {
                        this@drawWithContent.drawContent()
                    }
                    drawLayer(graphicsLayer)
                }
                .wrapContentHeight(),
            shape = RoundedCornerShape(15.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 20.dp),
        ){
            Box(
                modifier = Modifier
                    .wrapContentSize()
                    .background(Color.Transparent)
            ) {
                Image(
                    painter = backgroundCard,
                    contentDescription = "Background Image",
                    modifier = Modifier
                        .matchParentSize()
                        .drawWithCache {
                            onDrawWithContent {
                                drawContent()
                                drawRect(shimmer)
                            }
                        },
                    contentScale = ContentScale.Crop,
                )
                ConstraintLayout(
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(25.dp)
                ) {
                    val (desc, foto, datos,fondo_tipo, habilidad_poke) = createRefs()
                    Row(
                        modifier = Modifier
                            .zIndex(5f)
                            .fillMaxWidth()
                            .background(color_tipo_transparente)
                            .constrainAs(datos) {
                                top.linkTo(parent.top)
                                start.linkTo(parent.start)
                                end.linkTo(parent.end)
                                bottom.linkTo(foto.top)
                            },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        Text(
                            fontWeight = FontWeight.Bold,
                            text = numberText,
                            color = Color.White,
                            fontSize = 20.sp)

                        Text(
                            fontWeight = FontWeight.Bold,
                            text = nameText,
                            color = Color.White,
                            fontSize = 20.sp)
                    }
                    Image(
                        painter = backgroundImage,
                        contentDescription = "Background Image",
                        modifier = Modifier
                            .size(250.dp)
                            .border(8.dp, color_tipo, RectangleShape)
                            .drawWithCache {
                                onDrawWithContent {
                                    drawContent()
                                    drawRect(shimmer)
                                }
                            }
                            .constrainAs(fondo_tipo) {
                                top.linkTo(foto.top)
                                start.linkTo(foto.start)
                                end.linkTo(foto.end)
                                bottom.linkTo(foto.bottom)
                            },
                        contentScale = ContentScale.Crop,
                    )

                    Image(
                        painter = imagen_poke,
                        contentDescription = "Pokemon",
                        modifier = Modifier
                            .size(300.dp)
                            .fillMaxSize()
                            .constrainAs(foto) {
                                top.linkTo(datos.bottom)
                                start.linkTo(parent.start)
                                end.linkTo(parent.end)
                                //bottom.linkTo(datos.top)
                            }
                    )

                    Row(modifier = Modifier
                        .zIndex(5f)
                        .padding(top = 3.dp)
                        .background(color_tipo_transparente)
                        .constrainAs(desc) {
                            top.linkTo(foto.bottom)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            bottom.linkTo(habilidad_poke.top)
                        }
                    ){
                        Text(
                            text = adaptaDescripcion(descText),
                            fontSize = 12.sp,
                            color = Color.White,
                            modifier = Modifier
                                .wrapContentSize()
                                .padding(horizontal = 10.dp)

                        )

                    }
                    Row(
                        modifier = Modifier
                            .zIndex(5f)
                            .padding(top = 3.dp)
                            .background(color_tipo_transparente)
                            .constrainAs(habilidad_poke) {
                                top.linkTo(desc.bottom)
                                start.linkTo(parent.start)
                                end.linkTo(parent.end)
                                bottom.linkTo(parent.bottom)
                            }
                    ){
                        Text(
                            text = habilidadText,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier
                                .wrapContentSize()
                                .padding(horizontal = 10.dp)
                        )
                        Text(
                            text = adaptaDescripcion(descHabilidad),
                            fontSize = 12.sp,
                            color = Color.White,
                            modifier = Modifier
                                .wrapContentSize()
                                .padding(horizontal = 10.dp)
                        )
                    }
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
            horizontalArrangement = Arrangement.Center
        ){
            Boton(
                text = "Atrás",
                onClick = {
                    onCartaGrandeChange(false)
                }
            )
            if(!isPropiedad){
                //El usuario ve si ha reservado o no la carta
                if(!sesion.admin){
                    if(carta.carta_id in reservasCreadas.map { it.carta_id }){
                        Boton(
                            text = "Pendiente",
                            onClick = {}
                        )
                    }
                    else{
                        Boton(
                            text = "Reserva!",
                            onClick = {

                                val reserva = sesion.key?.let {
                                    Reserva(
                                        usuario_id = it,
                                        carta_id = carta.carta_id
                                    )
                                }
                                if (reserva != null) {
                                    guardaReservaFB(reserva,context,scope)
                                }
                            }
                        )
                    }
                }
                //El administrador ve si ha reservado o no la carta
                else{
                    val reserva = reservasCreadas.find { it.carta_id == carta.carta_id }

                    if (reserva != null) {
                        Boton(
                            text = "Aceptar",
                            onClick = {
                                aceptaReservaFB(reserva, context, scope, misCartas)
                            }
                        )
                    }
                }
            }
            else{
                Boton(
                    text = if (isSaving) "Guardando..." else "Guardar",
                    onClick = {
                        if (!isSaving) {
                            isSaving = true
                            coroutineScope.launch {
                                val bitmap = graphicsLayer.toImageBitmap()
                                try {
                                    withContext(Dispatchers.IO) {
                                        saveImageToGallery(context, bitmap, carta.carta_id)
                                    }
                                } catch (e: Exception) {}
                                finally {
                                    isSaving = false
                                }
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun CarPequeFB(carta: Carta, onClick: () -> Unit){
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
    val color_tipo = TypeStringToColor(carta.tipo,1)

    Card(
        modifier = Modifier
            .wrapContentSize()
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
                        onClick()

                    }
                )
            },
        shape = RoundedCornerShape(5.dp)
    ){
        color_tipo?.let {
            Modifier
                .wrapContentSize()
                .border(5.dp, it, RectangleShape)
                .background(color_tipo)
        }?.let {
            ConstraintLayout(
                modifier = it
            ) {
                val (imagen, nombre, precio) = createRefs()
                val imagen_poke = rememberAsyncImagePainter(
                    model = carta.imagenAPI,
                    contentScale = ContentScale.FillBounds,
                )
                val backgroundImage = painterResource(carta.fondo_foto)
                Image(
                    painter = backgroundImage,
                    contentDescription = "Background Image",
                    modifier = Modifier.size(100.dp),
                    contentScale = ContentScale.Crop // Adjust as needed
                )
                Image(
                    painter = imagen_poke,
                    contentDescription = "Pokemon Image",
                    modifier = Modifier
                        .size(100.dp)
                        .fillMaxSize()
                        .constrainAs(imagen) {
                            start.linkTo(parent.start)
                            top.linkTo(parent.top)
                            bottom.linkTo(nombre.top)
                            end.linkTo(parent.end)
                        }
                )
                Text(
                    text = carta.nombre,
                    color = Color.Black,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .constrainAs(nombre) {
                            start.linkTo(parent.start)
                            top.linkTo(imagen.bottom)
                            bottom.linkTo(precio.top)
                            end.linkTo(parent.end)
                        }
                )
                Text(
                text = carta.precio.toString()+divisaSeleccionada,
                color = Color.Black,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .constrainAs(precio) {
                        start.linkTo(parent.start)
                        top.linkTo(nombre.bottom)
                        bottom.linkTo(parent.bottom)
                        end.linkTo(parent.end)
                    }
                )
            }
        }
    }
}

@Composable
fun CartasCreadas(modifier: Modifier = Modifier, navController: NavHostController){
    var onCardClick by remember { mutableStateOf(Carta()) }
    var cartaGrande by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }
    val sesion = UsuarioFromKey(usuario_key, refBBDD)

    cargaCartasCreadas(onUpdateIsLoading = { isLoading = it })
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
                items(cartasCreadas) { carta ->

                    if(carta.carta_id !in misCartas){
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
                    navController = navController
                )
            }
        }
    }
}

fun cargaCartasCreadas(
    onUpdateIsLoading: (Boolean) -> Unit
){
    refBBDD.child("tienda").child("cartas").get().addOnSuccessListener { dataSnapshot ->
        val listaCartas = mutableListOf<Carta>()
        for (childSnapshot in dataSnapshot.children) {
            val carta = childSnapshot.getValue(Carta::class.java)
            carta?.let { listaCartas.add(it) }
        }
        cartasCreadas = listaCartas
        onUpdateIsLoading(false)
    }.addOnFailureListener { exception ->
        onUpdateIsLoading(false)
    }
}

@Composable
fun shimmerBrush(
    durationMillis: Int = 3000,
    gradientWidthPx: Float,
    colors: List<Color> = listOf(
        Color.Transparent,
        Color.White.copy(alpha = 0.6f),
        Color.Transparent
    )
): Brush {
    val infiniteTransition = rememberInfiniteTransition(label = "infinite transition")
    val offset by infiniteTransition.animateFloat(
        initialValue = -2f * gradientWidthPx,
        targetValue = 2f * gradientWidthPx,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "offset"
    )

    return Brush.linearGradient(
        colors = colors,
        start = Offset(offset, 0f),
        end = Offset(offset + gradientWidthPx, gradientWidthPx),
        tileMode = TileMode.Mirror
    )
}

fun saveImageToGallery(context: Context, bitmap: ImageBitmap, cartaId: String) {
    val androidBitmap = bitmap.asAndroidBitmap()
    val displayName = "PokeCard_$cartaId.png"
    val mimeType = "image/png"
    val relativeLocation = Environment.DIRECTORY_PICTURES + File.separator + "PokeCardShop"

    val values = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, displayName)
        put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            put(MediaStore.MediaColumns.RELATIVE_PATH, relativeLocation)
            put(MediaStore.MediaColumns.IS_PENDING, 1)
        }
    }

    val resolver = context.contentResolver
    var uri: Uri? = null

    try {
        uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            ?: throw Exception("Failed to create new MediaStore record.")

        uri?.let {
            resolver.openOutputStream(it)?.use { outputStream ->
                if (!androidBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)) {
                    throw Exception("Failed to save bitmap.")
                }
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.clear()
            values.put(MediaStore.MediaColumns.IS_PENDING, 0)
            resolver.update(uri, values, null, null)
        }
    } catch (e: Exception) {
        uri?.let { resolver.delete(it, null, null) }
        throw e
    }
}
