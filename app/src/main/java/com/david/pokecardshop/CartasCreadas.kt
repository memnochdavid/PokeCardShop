package com.david.pokecardshop

import android.util.Log
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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.constraintlayout.compose.ConstraintLayout
import coil.compose.rememberAsyncImagePainter
import com.david.pokecardshop.dataclass.Carta
import com.david.pokecardshop.dataclass.CreaCarta
import com.david.pokecardshop.dataclass.PokeInfoViewModel
import com.david.pokecardshop.dataclass.Pokemon
import com.david.pokecardshop.dataclass.TypeStringToColor
import com.david.pokecardshop.dataclass.TypeToBackground
import com.david.pokecardshop.dataclass.TypeToColor
import com.david.pokecardshop.dataclass.TypeToDrawableAPI
import com.david.pokecardshop.dataclass.adaptaNombre
import com.david.pokecardshop.dataclass.firstMayus
import com.david.pokecardshop.ui.stuff.CardGrande
import com.david.pokecardshop.ui.stuff.CardPeque2
import com.david.pokecardshop.ui.theme.*

var cartasCreadas by mutableStateOf<List<Carta>>(emptyList())

@Composable
fun CartaFB(modifier: Modifier = Modifier, carta: Carta) {

    val numberText = carta.number
    val nameText = carta.nombre
    val descText = carta.descripcion
    val habilidadText = carta.habilidad_poke[0]
    val descHabilidad = carta.habilidad_poke[1]

    val backgroundCard = painterResource(carta.fondo_foto)
    val backgroundImage = painterResource(carta.fondo_carta)
    var imagen_poke = rememberAsyncImagePainter(carta.imagen)

    Card(
        modifier = Modifier
            .fillMaxWidth()
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
                modifier = Modifier.matchParentSize(),
                contentScale = ContentScale.Crop
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
                        .border(8.dp, color_electrico_dark, RectangleShape)
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
                    .constrainAs(desc) {
                        top.linkTo(foto.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(habilidad_poke.top)
                    }
                ){
                    Text(
                        text = descText,
                        fontSize = 10.sp,
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
                        .constrainAs(habilidad_poke) {
                            top.linkTo(desc.bottom)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            bottom.linkTo(parent.bottom)
                        }
                ){
                    Text(
                        text = habilidadText,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier
                            .wrapContentSize()
                            .padding(horizontal = 10.dp)
                    )
                    Text(
                        text = descHabilidad,
                        fontSize = 10.sp,
                        color = Color.White,
                        modifier = Modifier
                            .wrapContentSize()
                            .padding(horizontal = 10.dp)
                    )
                }
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
        )
    )
    val color_tipo = TypeStringToColor(carta.tipo)
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
                val (imagen, nombre, tipos) = createRefs()
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
                            bottom.linkTo(parent.bottom)
                            end.linkTo(parent.end)
                        }
                )
            }
        }
    }
}

@Composable
fun CartasCreadas(modifier: Modifier = Modifier){
    var onCardClick by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }
    cargaCartasCreadas(onUpdateIsLoading = { isLoading = it })
    if (isLoading) { //se asegura de haber cargado los datos de la nube antes de empezar a mostrar nada
        Box(
            modifier = modifier.background(color_fuego_dark).fillMaxSize(),
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
                    CarPequeFB(carta = carta, onClick = {

                    })
                }
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


@Preview(showBackground = true, widthDp = 360, heightDp = 720)
@Composable
fun GreetingPreview22() {
    CartaFB( carta = Carta())
}