package com.david.pokecardshop.dataclass


import android.content.Context
import android.net.Uri
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.david.pokecardshop.R
import com.david.pokecardshop.refBBDD
import com.david.pokecardshop.ui.stuff.Boton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class Carta(
    var carta_id: String="",
    var number: String="",
    var nombre: String="",
    var descripcion: String="",
    var imagen: String="",
    var imagenAPI:String="",
    var habilidad_poke: List<String> = listOf(""),
    var fondo_foto: Int=0,
    var fondo_carta: Int=0,
    var tipo: String = "",
    var precio: Float = 0f
){
    init{
        carta_id = refBBDD.child("tienda").child("cartas").push().key!!
        precio = (10..30).random().toFloat()
    }
}


@Composable
fun CreaCarta(modifier: Modifier = Modifier, navController: NavHostController) {
    val context = LocalContext.current
    val scopeUser = rememberCoroutineScope()
    var numberText by remember { mutableStateOf("#000") }
    var nameText by remember { mutableStateOf("Nombre") }
    var descText by remember { mutableStateOf("Aquí la descripción del Pokémon") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var habilidadText by remember { mutableStateOf("Habilidad") }
    var descHabilidad by remember { mutableStateOf("Descripción de la Habilidad") }
    var tipo by remember { mutableStateOf(Type(slot=1, type=TypeInfo(name="electric", url="https://pokeapi.co/api/v2/type/13/")))}
//    var color_fondo by remember { mutableStateOf(TypeToColor(tipo,1))}

    val viewModel = PokeInfoViewModel()
    val pokemon by viewModel.pokemonInfo.observeAsState()
    val habilidad: State<String> = viewModel.effect_description
    val habilidadName: State<String> = viewModel.effect_name
    var spanishDescription: State<String> = viewModel.spanishDescription
    val isDescriptionLoading by viewModel.isDescriptionLoading
    val isAbilityListLoading by viewModel.isAbilityListLoading
    val isAbilityDetailsLoading by viewModel.isAbilityDetailsLoading
    var autoSelect by remember { mutableStateOf(false) }

    LaunchedEffect(autoSelect) {
        if (autoSelect) {
            viewModel.getPokemonInfo(nameText)
        }
    }

    LaunchedEffect(pokemon) {
        pokemon?.let {
            scopeUser.launch {
                numberText = it.id.toString()
                nameText = it.name.firstMayus()
                viewModel.getPokemonAbility(it.id)
                viewModel.getPokemonDescription(it.id)
                delay(2000)
                descText = spanishDescription.value
                habilidadText = habilidadName.value
                Log.d("NOMHabilidad", habilidad.value)
                descHabilidad = habilidad.value
                Log.d("DESCHabilidad", descHabilidad)
                tipo = it.types[0]
                selectedImageUri = Uri.parse(it.sprites.frontDefault)
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            //.background(color_electrico_dark)
    ) {
        item {
            FormularioCarta(
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
                Carta(
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


@Composable
fun FormularioCarta(
    numberText: String,
    nameText: String,
    descText: String,
    habilidadText: String,
    descHabilidad: String,
    onNumberChange: (String) -> Unit,
    onNameChange: (String) -> Unit,
    onHabilidadChange: (String) -> Unit,
    onDescHabilidadChange: (String) -> Unit,
    onDescChange: (String) -> Unit,
    isLoadingDesc: Boolean,
    isLoadingAbility: Boolean,
    isAbilityNameLoading: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        if (isLoadingDesc || isLoadingAbility || isAbilityNameLoading) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Transparent),
            ){
                Text(text = "CARGANDO CONTENIDO...", color = Color.White)
            }
        } else {

            OutlinedTextField(
                value = numberText,
                onValueChange = onNumberChange,
                label = { Text("Número") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = nameText,
                onValueChange = onNameChange,
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = adaptaDescripcion(descText),
                onValueChange = onDescChange,
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = habilidadText,
                onValueChange = onHabilidadChange,
                label = { Text("Habilidad") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = adaptaDescripcion(descHabilidad),
                onValueChange = onDescHabilidadChange,
                label = { Text("Descripción de la Habilidad") },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun Carta(
    modifier: Modifier = Modifier,
    numberText: String,
    nameText: String,
    descText: String,
    selectedImageUri: Uri?,
    habilidadText: String,
    descHabilidad: String,
    tipo: Type,
    isLoadingDesc: Boolean,
    isLoadingAbility: Boolean,
    isAbilityNameLoading: Boolean
){
    val backgroundCard = painterResource(TypeToBackground(tipo))
    val backgroundImage = painterResource(R.drawable.background_foto)
    var imagen_poke = painterResource(R.drawable.silueta)

    val color_tipo = TypeToColor(tipo,1)
    val color_tipo_transparente = TypeToColor(tipo,3)

    if (selectedImageUri != null) {
        imagen_poke = rememberAsyncImagePainter(caraImagenURL(nameText))
    }
    Card(
        modifier = modifier.background(Color.Transparent),
        shape = RoundedCornerShape(15.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 20.dp),
    ){
        if (isLoadingDesc || isLoadingAbility || isAbilityNameLoading) {
            Text(text = "Loading description...", color = Color.White)
        } else {
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
                            }
                            .background(color_tipo_transparente),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        Text(
                            modifier = Modifier.wrapContentWidth(),
                            fontWeight = FontWeight.Bold,
                            text = numberText,
                            color = Color.White,
                            fontSize = 20.sp)

                        Text(
                            modifier = Modifier.wrapContentWidth(),
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
                            },
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
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


    }
}

fun guardaCartaFB(
    carta: Carta,
    context: Context,
    scopeUser: CoroutineScope,

){
    val identificador = carta.carta_id

    scopeUser.launch {
        try{
            refBBDD.child("tienda").child("cartas").child(identificador).setValue(carta)

        }catch (e: Exception){
            Log.e("UserError", "Error al guardar la carta : ${e.message}")
            Toast.makeText(context, "Error al guardar la carta : ${e.message}", Toast.LENGTH_SHORT).show()
        }
        finally {
            Toast.makeText(context, "Carta ${carta.nombre} guardada con éxito", Toast.LENGTH_SHORT).show()
        }
    }
}
