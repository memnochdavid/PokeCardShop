package com.david.pokecardshop

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.layout.ContentScale
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
import com.david.pokecardshop.dataclass.TypeToBackground
import com.david.pokecardshop.ui.theme.color_electrico_dark

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
fun CartasCreadas(){

}







@Preview(showBackground = true, widthDp = 360, heightDp = 720)
@Composable
fun GreetingPreview22() {
    CartaFB( carta = Carta())
}