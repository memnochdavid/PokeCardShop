package com.david.pokecardshop.ui.stuff

import android.content.Intent
import android.media.MediaPlayer
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.core.FastOutSlowInEasing
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import coil.compose.rememberAsyncImagePainter
import com.david.pokecardshop.R
import com.david.pokecardshop.dataclass.PokeInfoViewModel
import com.david.pokecardshop.dataclass.Pokemon
import com.david.pokecardshop.dataclass.Species
import com.david.pokecardshop.dataclass.Sprites
import com.david.pokecardshop.dataclass.TypeToBackground
import com.david.pokecardshop.dataclass.TypeToColor
import com.david.pokecardshop.dataclass.TypeToDrawableAPI
import com.david.pokecardshop.dataclass.adaptaDescripcion
import com.david.pokecardshop.dataclass.adaptaNombre
import com.david.pokecardshop.dataclass.firstMayus
import com.david.pokecardshop.ui.theme.*
import kotlin.math.PI
import kotlin.math.sin


@Composable
fun CardPeque(pokemon: Pokemon){
    Log.d("POKEMON DE API - TIPOS", "Pokemon: ${pokemon.types}")
    var isPressed by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    val scale = animateFloatAsState(
        targetValue = if (isPressed) 0.90f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy, // Moderate bouncing
            stiffness = Spring.StiffnessMedium // Moderate stiffness
        )
    )
    val viewModel = PokeInfoViewModel()
    val spanishDescription by viewModel.spanishDescription
    viewModel.getPokemonDescription(pokemon.id)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .scale(scale.value)
            .clickable(
                interactionSource = interactionSource,
                indication = null, // Remove default ripple effect

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
                        Log.d("POKEMON CLICK - NOMBRE", "Pokemon: $pokemon")
                        Log.d("POKEMON CLICK - DESCRIP", "Pokemon: $spanishDescription")


                    }
                )
            }
    ) {

        var num = "${(pokemon.id)}"
        if (num.length == 1) num = "00${(pokemon.id)}"
        else if (num.length == 2) num = "0${(pokemon.id)}"

        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 30.dp)
        ) {
            val(pokeball,pokemonImage,numero,pokemonName,tipo1,tipo2, descRow)=createRefs()

            Image(
                painter = painterResource(id = R.drawable.pokeball_icon),
                contentDescription = "Pokeball",
                modifier = Modifier
                    .size(60.dp)
                    .padding(5.dp)
                    .constrainAs(pokeball) {
                        start.linkTo(parent.start)
                        top.linkTo(parent.top)
                    }
            )

            //imagen remota de api
            val painter = rememberAsyncImagePainter(
                model = pokemon.sprites.frontDefault,
                contentScale = ContentScale.FillBounds,
            )

            Image(
                painter = painter,
                contentDescription = "Pokemon Image",
                modifier = Modifier
                    .size(100.dp)
                    .fillMaxSize()
                    .constrainAs(pokemonImage) {
                        start.linkTo(parent.start)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                    },

                )
            Text(
                text = "#$num",
                color = Color.Black,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(start = 15.dp)
                    .constrainAs(numero) {
                        start.linkTo(pokemonImage.end)
                        top.linkTo(parent.top)
                        bottom.linkTo(tipo1.top)
                        end.linkTo(pokemonName.start)
                    }
            )
            Text(
                text = pokemon.name.firstMayus(),
                color = Color.Black,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(start = 20.dp)
                    .constrainAs(pokemonName) {
                        start.linkTo(numero.end)
                        top.linkTo(parent.top)
                        bottom.linkTo(tipo1.top)
                        end.linkTo(parent.end)
                    }
            )

            Text(
                text = spanishDescription,
                modifier = Modifier
                    .constrainAs(descRow) {
                        top.linkTo(pokemonName.bottom)
                        start.linkTo(numero.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)

                    }
            )



            if (pokemon.types.size == 1) {
                Image(
                    painter = painterResource(id = TypeToDrawableAPI(pokemon.types[0])),
                    contentDescription = "Tipo 1",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .width(60.dp)
                        .height(25.dp)
                        .constrainAs(tipo1) {
                            start.linkTo(numero.start)
                            bottom.linkTo(parent.bottom)
                            top.linkTo(pokemonName.bottom)
                            end.linkTo(pokemonName.end)
                        }
                )
            } else {
                Image(
                    painter = painterResource(id = TypeToDrawableAPI(pokemon.types[0])),
                    contentDescription = "Tipo 1",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .width(60.dp)
                        .height(25.dp)
                        .constrainAs(tipo1) {
                            start.linkTo(numero.start)
                            bottom.linkTo(parent.bottom)
                            top.linkTo(pokemonName.bottom)
                            end.linkTo(tipo2.start)
                        }
                )
                Image(
                    painter = painterResource(id = TypeToDrawableAPI(pokemon.types[1])),
                    contentDescription = "Tipo 2",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .width(60.dp)
                        .height(25.dp)
                        .constrainAs(tipo2) {
                            start.linkTo(tipo1.end)
                            bottom.linkTo(parent.bottom)
                            top.linkTo(pokemonName.bottom)
                            end.linkTo(pokemonName.end)
                        }
                )
            }
        }
    }
}

@Composable
fun CardPeque2(pokemon: Pokemon, onClick: () -> Unit){
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
                        Log.d("POKEMON CLICK - NOMBRE", "Pokemon: $pokemon")

                    }
                )
            },
        shape = RoundedCornerShape(5.dp)
    ){
        ConstraintLayout(
            modifier = Modifier
                .wrapContentSize()
                .border(5.dp, TypeToColor(pokemon.types[0], 1), RectangleShape)
                .background(TypeToColor(pokemon.types[0], 1))
        ) {
            val (imagen, nombre, tipos) = createRefs()
            val imagen_poke = rememberAsyncImagePainter(
                model = pokemon.sprites.frontDefault,
                contentScale = ContentScale.FillBounds,
            )
            val backgroundImage = painterResource(TypeToBackground(pokemon.types[0]))
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
                text = adaptaNombre(pokemon.name).firstMayus(),
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

@Composable
fun CardGrande(pokemon: Pokemon) {
    var isFlipped by remember { mutableStateOf(false) }
    val rotationY by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing), label = ""
    )

    val num=pokemon.id
    var numero = "${(num)}"
    if(numero.length == 1) numero = "00${(num)}"
    else if(numero.length == 2) numero = "0${(num)}"

    val viewModel = PokeInfoViewModel()
    val spanishDescription by viewModel.spanishDescription
    viewModel.getPokemonDescription(pokemon.id)

    val nombrePokeLimpio=adaptaNombre(pokemon.name)

    val imagen_poke = rememberAsyncImagePainter(
        model = "https://cloud.appwrite.io/v1/storage/buckets/677bc72c000eb680c80b/files/${adaptaNombre(pokemon.name)}/view?project=6738854a0011e2bc643f&mode=admin",
        contentScale = ContentScale.FillBounds,
    )
//    val shimmer_color = shimmerBrush(
//        showShimmer = true,
//        durationMillis=3000,
//        gradientStartColor = TypeToColor(pokemon.types[0],1),
//        gradientEndColor = Color.White
//    )

    val backgroundCard = painterResource(TypeToBackground(pokemon.types[0]))
    val backgroundImage = painterResource(R.drawable.background_foto)

    val habilidad by viewModel.effect_description
    viewModel.getPokemonAbility(pokemon.id)
    val habilidadName by viewModel.effect_name


    Card(
        modifier = Modifier
            .fillMaxWidth(0.75f)
            .wrapContentHeight()
            //.border(15.dp, color_electrico_dark, RoundedCornerShape(15.dp))
            .padding(top = 0.dp)
            .graphicsLayer {
                this.rotationY = rotationY
                cameraDistance = 8 * density
                if (rotationY > 90f) {
                    scaleX = -1f
                } else {
                    scaleX = 1f
                }
            }
            .clickable {
                isFlipped = !isFlipped
            },

        shape = RoundedCornerShape(15.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 20.dp),
    ){
        Box(
            modifier = Modifier
                .wrapContentSize()
                .background(Color.Transparent)
        ) {
            if (isFlipped) {
                // Mostrar el dorso
                CardGrandeDorso(pokemon = pokemon)
            } else {
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
                            text = "#$numero",
                            color = Color.White,
                            fontSize = 20.sp)

                        Text(
                            fontWeight = FontWeight.Bold,
                            text = nombrePokeLimpio.firstMayus(),
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
                            text = adaptaDescripcion(spanishDescription),
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
                            text = habilidadName,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier
                                .wrapContentSize()
                                .padding(horizontal = 10.dp)
                        )
                        Text(
                            text = habilidad,
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


}


@Composable
fun CardGrandeDorso(pokemon: Pokemon){
    Card(
        modifier = Modifier
            .padding(top = 0.dp),
        shape = RoundedCornerShape(15.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 20.dp),
    ){
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Transparent)
        ) {
            Image(
                painter = painterResource(TypeToBackground(pokemon.types[0])),
                contentDescription = "Background Image",
                modifier = Modifier
                    .matchParentSize(),
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Composable
fun shimmerBrush(
    showShimmer: Boolean = true,
    targetValue: Float = 1000f,
    durationMillis: Int = 1500,
    gradientStartColor: Color = Color.LightGray.copy(alpha = 0.5f),
    gradientEndColor: Color = Color.LightGray,
): Brush {
    return if (showShimmer) {
        val transition = rememberInfiniteTransition(label = "")
        val translateAnimation by transition.animateFloat(
            initialValue = 0f,
            targetValue = targetValue,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = durationMillis,
                    easing = FastOutSlowInEasing
                ),
                repeatMode = RepeatMode.Restart
            ), label = ""
        )
        Brush.linearGradient(
            colors = listOf(gradientStartColor, gradientEndColor, gradientStartColor),
            start = Offset(x = translateAnimation, y = 0f),
            end = Offset(x = translateAnimation + targetValue, y = 0f)
        )
    } else {
        Brush.linearGradient(listOf(Color.Transparent, Color.Transparent))
    }
}





/*
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CardPeque2()
}*/