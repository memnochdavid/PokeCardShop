package com.david.pokecardshop

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.david.pokecardshop.dataclass.PokeInfoViewModel
import com.david.pokecardshop.dataclass.Pokemon
import com.david.pokecardshop.ui.theme.PokeCardShopTheme
import kotlinx.coroutines.flow.filter
import androidx.lifecycle.viewmodel.compose.viewModel
import com.david.pokecardshop.dataclass.Usuario
import com.david.pokecardshop.ui.stuff.CardGrande
import com.david.pokecardshop.ui.stuff.CardPeque2
import com.david.pokecardshop.ui.theme.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

var isLoadingAPI by mutableStateOf(true)


class Login : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        lateinit var sesionUser: Usuario
        super.onCreate(savedInstanceState)
        if (intent.hasExtra("sesion")) {
            sesionUser = intent.getSerializableExtra("sesion") as Usuario
        }else{
            sesionUser = Usuario()
        }
        enableEdgeToEdge()
        setContent {
            PokeCardShopTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Login(
                        sesionUser = sesionUser,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Login(sesionUser: Usuario, modifier: Modifier) {
    val context = LocalContext.current

    //val intent = Intent(context, Login::class.java)


    var email by remember { mutableStateOf(sesionUser.email) }
    var password by remember { mutableStateOf(sesionUser.pass) }
    var loginExiste by remember { mutableStateOf(false) }

    if(sesionUser.key!="") {
        email = sesionUser.email
        password = sesionUser.pass
    }
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 0.dp)
            .background(color_fuego_dark)

    ) {
        val (col1, col2)=createRefs()
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(col1) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(parent.top)
                    bottom.linkTo(col2.top)
                }
                .fillMaxHeight(0.5f)
        ){}
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 35.dp)
                //.padding(16.dp)
                .constrainAs(col2) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(col1.bottom)
                    bottom.linkTo(parent.bottom)
                }
                .fillMaxHeight(1f),
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                modifier = Modifier
                    .background(Color.Transparent)
                    .fillMaxWidth(),
                value = email,
                onValueChange = { email = it },
                maxLines = 1,
                label = { Text(
                    color= blanco80,
                    text="Correo electrónico") },
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
                value = password,
                onValueChange = { password = it },
                maxLines = 1,
                label = { Text(
                    color= blanco80,
                    text="Contraseña")
                },
                visualTransformation = PasswordVisualTransformation(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = blanco80,
                    unfocusedBorderColor = blanco80,
                    cursorColor = blanco80,
                    focusedContainerColor= rojo60,
                    focusedTextColor= blanco80,
                    unfocusedTextColor= blanco80,
                ),

                )
            Spacer(modifier = Modifier.height(20.dp))

            Button(
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = rojo60,
                    contentColor = blanco80
                ),
                shape = RoundedCornerShape(10.dp),
                onClick = {
                    refBBDD = FirebaseDatabase.getInstance().reference
                    if (email.isNotEmpty() && password.isNotEmpty()) {
                        refBBDD.child("tienda").child("usuarios")
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    loginExiste = false
                                    for (pojo in snapshot.children) {
                                        val checkUser = pojo.getValue(Usuario::class.java)
                                        if (email == checkUser?.email && password == checkUser.pass) {
                                            loginExiste = true
                                            usuario_key=checkUser.key.toString()
                                            sesionUser.email=email
                                            sesionUser.pass=password
                                            //Toast.makeText(context, "Login correcto", Toast.LENGTH_SHORT).show()

                                            val intent = Intent(context, MainActivity::class.java)
                                            intent.putExtra("sesion", checkUser.key)
                                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                            context.startActivity(intent)

                                            break
                                        }
                                    }
                                    if (!loginExiste) {
                                        Toast.makeText(context, "Usuario o Contraseña incorrectos", Toast.LENGTH_SHORT).show()
                                    }
                                    else{
                                        Toast.makeText(context, "Login correcto", Toast.LENGTH_SHORT).show()
                                    }
                                }
                                override fun onCancelled(error: DatabaseError) {
                                    Toast.makeText(context, "Error en la conexión", Toast.LENGTH_SHORT).show()
                                }
                            })
                    } else {
                        Toast.makeText(context, "Rellena todos los campos", Toast.LENGTH_SHORT).show()
                    }
                }) {
                Text("Login")
            }
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text="¿No tienes cuenta? ¡Regístrate!",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = rojo60,
                    contentColor = blanco80
                ),
                shape = RoundedCornerShape(10.dp),
                onClick = {
                    val intent = Intent(context, CreaUsuario::class.java)
                    context.startActivity(intent)
                }) {
                Text("Crear cuenta")
            }
        }
    }
}

















