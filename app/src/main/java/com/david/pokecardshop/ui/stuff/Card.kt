package com.david.pokecardshop.ui.stuff

import android.content.Intent
import android.media.MediaPlayer
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.david.pokecardshop.dataclass.adaptaNombre
import com.david.pokecardshop.dataclass.firstMayus
import com.david.pokecardshop.ui.theme.*


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
                    click = !click
                    onClick()
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

                    }
                )
            },
        shape = RoundedCornerShape(5.dp)
    ){
        ConstraintLayout(
            modifier = Modifier
                .wrapContentSize()
                .border(5.dp, TypeToColor(pokemon.types[0]), RectangleShape)
                .background(TypeToColor(pokemon.types[0]))
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
                    .constrainAs(imagen){
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

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 0.dp)
    ) {
        val (number,desc, nombre, foto, tipo1, tipo2,datos,interacciones,add,evos,regis,formas_unown,gigamax,megaevo) = createRefs()


        Image(
            painter = imagen_poke,
            contentDescription = "Pokemon",
            modifier = Modifier
                .size(350.dp)
                .fillMaxSize()
                .constrainAs(foto) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(datos.top)
                }
                .clickable {

                },

            )

        LazyColumn(
            modifier = Modifier
                .constrainAs(datos) {
                    top.linkTo(foto.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                    height = Dimension.fillToConstraints
                }
                .fillMaxHeight(),
            reverseLayout = true,/////////////////
        ){
            item{
                ConstraintLayout{
                    Text(modifier = Modifier
                        .constrainAs(number) {
                            top.linkTo(parent.top)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            //bottom.linkTo(nombre.top)////////????????????
                        }
                        .padding(bottom = 10.dp)
                        .fillMaxHeight(),//////////
                        fontWeight = FontWeight.Bold,
                        text = "#$numero",
                        fontSize = 35.sp)

                    Text(modifier = Modifier
                        .constrainAs(nombre) {
                            top.linkTo(number.bottom)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            //bottom.linkTo(tipo1.top)
                        }
                        .padding(bottom = 15.dp),
                        fontWeight = FontWeight.Bold,
                        text = nombrePokeLimpio,
                        fontSize = 32.sp)

                    if (pokemon.types.size == 1) {

                        Image(
                            painter = painterResource(id = TypeToDrawableAPI(pokemon.types[0])),
                            contentDescription = "Tipo 1",
                            contentScale = ContentScale.FillWidth,
                            modifier = Modifier
                                .width(80.dp)
                                .padding(top = 5.dp,bottom = 10.dp)
                                .constrainAs(tipo1) {
                                    start.linkTo(parent.start)
                                    end.linkTo(parent.end)
                                    top.linkTo(nombre.bottom)
                                    bottom.linkTo(desc.top)
                                }
                        )
                    } else {
                        Image(
                            painter = painterResource(id = TypeToDrawableAPI(pokemon.types[0])),
                            contentDescription = "Tipo 1",
                            contentScale = ContentScale.FillWidth,
                            modifier = Modifier
                                .width(80.dp)
                                .padding(top = 5.dp,bottom = 10.dp)
                                .constrainAs(tipo1) {
                                    start.linkTo(parent.start)
                                    end.linkTo(tipo2.start)
                                    top.linkTo(nombre.bottom)
                                    bottom.linkTo(desc.top)
                                },
                        )
                        Image(
                            painter = painterResource(id = TypeToDrawableAPI(pokemon.types[1])),
                            contentDescription = "Tipo 2",
                            contentScale = ContentScale.FillWidth,
                            modifier = Modifier
                                .width(80.dp)
                                .padding(top = 5.dp,bottom = 10.dp)
                                .constrainAs(tipo2) {
                                    start.linkTo(tipo1.end)
                                    end.linkTo(parent.end)
                                    top.linkTo(nombre.bottom)
                                    bottom.linkTo(desc.top)
                                }
                        )
                    }

                    Row(modifier = Modifier
                        .constrainAs(desc) {
                            top.linkTo(tipo1.bottom)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            bottom.linkTo(interacciones.top)
                        }){
                        Text(text = spanishDescription,
                            modifier = Modifier
                                .padding(start = 10.dp)
                        )

                    }

                }

            }

        }
    }
}


/*
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CardPeque2()
}*/