package com.david.pokecardshop.dataclass

import android.net.Uri
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.zIndex
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.viewmodel.compose.viewModel
import com.david.pokecardshop.R
import com.david.pokecardshop.ui.stuff.Boton
import com.david.pokecardshop.ui.stuff.CardGrandeDorso
import com.david.pokecardshop.ui.theme.blanco80
import com.david.pokecardshop.ui.theme.*

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
fun CreaCarta(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var numberText by remember { mutableStateOf("#000") }
    var nameText by remember { mutableStateOf("Nombre del Pokémon") }
    var descText by remember { mutableStateOf("Aquí la descripción del Pokémon") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var habilidadText by remember { mutableStateOf("Habilidad") }
    var descHabilidad by remember { mutableStateOf("Descripción de la Habilidad") }
    var tipo by remember { mutableStateOf(Type(slot=1, type=TypeInfo(name="fire", url="https://pokeapi.co/api/v2/type/10/")))}

    val viewModel = PokeInfoViewModel()
    val pokemon by viewModel.pokemonInfo.observeAsState()
    val habilidad: State<String> = viewModel.effect_description
    val habilidadName: State<String> = viewModel.effect_name
    val spanishDescription: State<String> = viewModel.spanishDescription
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
            numberText = it.id.toString()
            nameText = it.name.firstMayus()
            viewModel.getPokemonAbility(it.id)
            viewModel.getPokemonDescription(it.id)
//            habilidadText = habilidadName
//            descHabilidad = habilidad
            Log.d("POKEMON_carta", "Pokemon: $it")
        }
    }


//    val lista_tipos: List<TypeInfo> by viewModel.pokemonTypeList.observeAsState(initial = emptyList())
//    viewModel.getTypeList()
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(color_electrico_dark)
    ) {
        item {
            FormularioCarta(
                numberText = numberText,
                nameText = nameText,
                descText = spanishDescription.value, // Correctly access the value here
                habilidadText = habilidadName.value,
                descHabilidad = habilidad.value,
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
                    numberText = numberText,
                    nameText = nameText,
                    descText = spanishDescription.value, // Correctly access the value here
                    selectedImageUri = selectedImageUri,
                    habilidadText = habilidadName.value,
                    descHabilidad = habilidad.value,
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
                    text = "Atras",
                    onClick = {
                        //todo
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
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
            Text(text = "Loading description...", color = Color.White)
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
                value = descText,
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
                value = descHabilidad,
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
    isLoadingDesc: Boolean,
    isLoadingAbility: Boolean,
    isAbilityNameLoading: Boolean
){
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
}
/*
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeleccionaTipo(
    modifier: Modifier = Modifier,
    tipo: Type,
    onTipoChange: (Type) -> Unit,
) {
    var selectedTipo by remember { mutableStateOf(tipo) }
    var expanded by remember { mutableStateOf(false) }

    val coloresSpinner: TextFieldColors = ExposedDropdownMenuDefaults.textFieldColors(
        focusedTextColor = negro80,
        unfocusedTextColor = negro80,
        focusedContainerColor = TypeToColor(tipo, 1),
        unfocusedContainerColor = TypeToColor(tipo, 1),
        focusedIndicatorColor = md_theme_dark_surfaceTint,
        unfocusedIndicatorColor = md_theme_dark_surfaceTint,
        focusedTrailingIconColor = negro80,
        unfocusedTrailingIconColor = negro80,
    )
    val viewModel: PokeInfoViewModel = viewModel()
    val lista_tipos: List<TypeInfo> by viewModel.pokemonTypeList.observeAsState(initial = emptyList())
    viewModel.getTypeList()

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        TextField(
            value = selectedTipo,
            onValueChange = {
                onTipoChange(it)
            },
            readOnly = true,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expanded,
                    modifier = Modifier.graphicsLayer { compositingStrategy =
                        CompositingStrategy.Offscreen }
                )
            },
            modifier = Modifier
                .menuAnchor()
                .wrapContentWidth(),
            colors = coloresSpinner,
            )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .background(TypeToColor(tipo, 1)),
        ) {
            lista_tipos.forEach { tipo ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = selectedTipo.type.name,
                            color = negro80
                        )
                    },
                    onClick = {
                        selectedTipo = tipo.name
                        expanded = false
                        onTipoChange(tipo)
                    },
                    modifier = Modifier
                        .padding(vertical = 0.dp)
                        .background(TypeToColor(tipo, 1))
                )
            }
        }
    }
}

*/


@Preview(showBackground = true, widthDp = 360, heightDp = 720)
@Composable
fun GreetingPreview2() {
    CreaCarta()
}
