package com.david.pokecardshop

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import coil.compose.AsyncImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.david.pokecardshop.dataclass.Usuario
import com.david.pokecardshop.ui.theme.PokeCardShopTheme
import com.david.pokecardshop.ui.theme.*

private lateinit var identificador: String
class CreaUsuario : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PokeCardShopTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    FormNewUser(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun FormNewUser(
    modifier: Modifier = Modifier
) {
    val scopeUser = rememberCoroutineScope()
    lateinit var newUser : Usuario
    val context = LocalContext.current
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(color_fuego_dark),
    ) {
        var objetoCreado by remember { mutableStateOf(false) }
        var username by remember { mutableStateOf("") }
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var newUserAvatar by remember { mutableStateOf("") }
        var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent(),
            onResult = { uri: Uri? ->
                selectedImageUri = uri
            }
        )

        val (col1)=createRefs()
        Column(
            modifier = Modifier
                .fillMaxSize()
                //.padding(16.dp)
                .padding(vertical = 16.dp, horizontal = 35.dp)
                .constrainAs(col1){
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(col1.bottom)
                    bottom.linkTo(parent.bottom)
                }
                .fillMaxHeight(1f),
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center

        ) {
            OutlinedTextField(
                modifier = Modifier
                    .background(Color.Transparent)
                    .fillMaxWidth(),
                value = username,
                onValueChange = { username = it },
                label = { Text(
                    color= blanco80,
                    text="nick")},
                maxLines = 1,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = blanco80,
                    unfocusedBorderColor = blanco80,
                    cursorColor = blanco80,
                    focusedContainerColor= rojo60,
                    focusedTextColor= blanco80,
                    unfocusedTextColor= blanco80,
                )
            )
            Spacer(modifier = Modifier.height(20.dp))
            OutlinedTextField(
                modifier = Modifier
                    .background(Color.Transparent)
                    .fillMaxWidth(),
                value = email,
                onValueChange = { email = it },
                maxLines = 1,
                label = { Text(
                    color= blanco80,
                    text="email")},
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = blanco80,
                    unfocusedBorderColor = blanco80,
                    cursorColor = blanco80,
                    focusedContainerColor= rojo60,
                    focusedTextColor= blanco80,
                    unfocusedTextColor= blanco80,
                )
            )
            Spacer(modifier = Modifier.height(20.dp))
            OutlinedTextField(
                value = password,
                modifier = Modifier
                    .background(Color.Transparent)
                    .fillMaxWidth(),
                onValueChange = { password = it },
                maxLines = 1,
                label = { Text(
                    color= blanco80,
                    text="contraseña")},
                visualTransformation = PasswordVisualTransformation(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = blanco80,
                    unfocusedBorderColor = blanco80,
                    cursorColor = blanco80,
                    focusedContainerColor= rojo60,
                    focusedTextColor= blanco80,
                    unfocusedTextColor= blanco80,
                )
            )
            Spacer(modifier = Modifier.height(20.dp))
            if (selectedImageUri != null) {
                AsyncImage(
                    model = selectedImageUri,
                    contentDescription = "Selected image",
                    modifier = Modifier.size(100.dp)
                )
            }
            Button(modifier = Modifier
                .padding(vertical = 8.dp),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = rojo60,
                    contentColor = blanco80
                ),
                shape = RoundedCornerShape(10.dp),
                onClick = {
                    if (username.isNotEmpty() || email.isNotEmpty() || password.isNotEmpty()) {
                        identificador = refBBDD.child("tienda").child("usuarios").push().key!!

                        scopeUser.launch {
                            try{
                                newUser = Usuario(username,email,password,identificador)
                                refBBDD.child("tienda").child("usuarios").child(identificador).setValue(newUser)

                            }catch (e: Exception){
                                Log.e("UserError", "Error al crear el usuario: ${e.message}")
                            }
                            finally {
                                Toast.makeText(context, "Usuario $username creado con éxito", Toast.LENGTH_SHORT).show()
                            }
                            withContext(Dispatchers.Main) { // Update on main thread
                                objetoCreado = true
                            }
                        }

                    }else{
                        Toast.makeText(context, "Rellena todos los campos", Toast.LENGTH_SHORT).show()
                    }
                }) {
                Text("Crear usuario")
            }

            if (objetoCreado) {
                val intent = Intent(context, Login::class.java)
                intent.putExtra("sesion", newUser)
                context.startActivity(intent)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PokeCardShopTheme {
        FormNewUser()
    }
}