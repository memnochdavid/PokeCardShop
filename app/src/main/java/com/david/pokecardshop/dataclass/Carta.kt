package com.david.pokecardshop.dataclass

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.zIndex
import androidx.constraintlayout.compose.ConstraintLayout
import com.david.pokecardshop.R
import com.david.pokecardshop.ui.stuff.CardGrandeDorso
import com.david.pokecardshop.ui.theme.color_electrico_dark

data class Carta(
    var number: String="",
    var nombre: String="",
    var descripcion: String="",
    var imagen: String="",
    var habilidad_poke: List<String> = listOf(""),
    var fondo_foto: Int=0,
    var fondo_carta: Int=0
)


@Composable
fun FormularioCarta(modifier: Modifier = Modifier){
    val backgroundCard = painterResource(R.drawable.background_dark)
    val backgroundImage = painterResource(R.drawable.background_foto)
    val imagen_poke = painterResource(R.drawable.silueta)
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
                        text = "#000",
                        color = Color.White,
                        fontSize = 20.sp)

                    Text(
                        fontWeight = FontWeight.Bold,
                        text = "Nombre del Pokémon",
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
                        text = "Aquí la descripción del Pokémon",
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
                        text = "Habilidad",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier
                            .wrapContentSize()
                            .padding(horizontal = 10.dp)
                    )
                    Text(
                        text = "Descripción de la Habilidad",
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


@Preview(showBackground = true, widthDp = 360, heightDp = 720)
@Composable
fun GreetingPreview2() {
    FormularioCarta()
}
