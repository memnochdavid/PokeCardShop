package com.david.pokecardshop.dataclass

import android.os.Parcel
import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.google.firebase.database.DatabaseReference
import kotlinx.parcelize.Parcelize
import java.io.Serializable
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


data class Usuario(
    var nick: String="",
    var email: String="",
    var pass: String="",
    var propiedad: List<String> = emptyList(),
    var key: String? = null,
    var admin: Boolean=false,
):Serializable


@Composable
fun UsuarioFromKey(usuario_key: String, refBBDD: DatabaseReference): Usuario {
    val usuarioState = remember { mutableStateOf<Usuario?>(null) }

    LaunchedEffect(usuario_key) {
        usuarioState.value = fetchUserData(usuario_key, refBBDD)
    }

    return usuarioState.value ?: Usuario() // Return default UserFb if null
}

suspend fun fetchUserData(usuario_key: String, refBBDD: DatabaseReference): Usuario {
    return suspendCoroutine { continuation ->
        refBBDD.child("tienda").child("usuarios").child(usuario_key).get().addOnSuccessListener { snapshot ->
            val user = Usuario(
                nick = snapshot.child("nick").value.toString(),
                email = snapshot.child("email").value.toString(),
                pass = snapshot.child("pass").value.toString(),
                key = snapshot.child("key").value.toString(),
                admin = snapshot.child("admin").value as Boolean,
            )
            continuation.resume(user)
        }.addOnFailureListener { exception ->
            continuation.resumeWithException(exception)
        }
    }
}
suspend fun fetchAllUsers(refBBDD: DatabaseReference): MutableList<Usuario> {
    return suspendCoroutine { continuation ->
        refBBDD.child("tienda").child("usuarios").get().addOnSuccessListener { snapshot ->
            val users = mutableListOf<Usuario>()
            for (userSnapshot in snapshot.children) {
                val user = Usuario(
                    nick = userSnapshot.child("nick").value.toString(),
                    email = userSnapshot.child("email").value.toString(),
                    pass = userSnapshot.child("pass").value.toString(),
                    key = userSnapshot.child("key").value.toString(),
                    admin = userSnapshot.child("admin").value as Boolean,
                )
                users.add(user)
            }
            continuation.resume(users)
        }.addOnFailureListener { exception ->
            continuation.resumeWithException(exception)
        }
    }
}