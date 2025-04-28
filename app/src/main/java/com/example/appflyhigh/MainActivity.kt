package com.example.appflyhigh

import android.os.Bundle
import android.telecom.Call
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import com.example.appflyhigh.ui.theme.AppFlyHighTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppFlyHighTheme {
                AppNavigation()
            }
        }
    }
}
@Composable
fun AppNavigation() {
    var isLoggedIn by remember { mutableStateOf(false) }
    if (isLoggedIn) {
        MainScreen()
    } else {
        LoginScreen(onLoginSuccess = { isLoggedIn = true })
    }
}


// ===============================
// LOGIN
// ===============================

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {
    val username = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val errorMessage = remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(androidx.compose.ui.graphics.Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            TextField(
                value = username.value,
                onValueChange = { username.value = it },
                label = { Text("Usuario") }
            )
            TextField(
                value = password.value,
                onValueChange = { password.value = it },
                label = { Text("Contraseña") },
                visualTransformation = PasswordVisualTransformation()
            )
            if (errorMessage.value.isNotEmpty()) {
                Text(
                    text = errorMessage.value,
                    color = androidx.compose.ui.graphics.Color.Red
                )
            }
            Button(onClick = {
                coroutineScope.launch {
                    try {
                        val response = RetrofitClient.api.login(
                            LoginRequestDTO(username.value, password.value)
                        )
                        if (response.isSuccessful) {
                            val responseBody = response.body()?.string()

                            if (responseBody == "Login successful") {
                                onLoginSuccess()
                            } else {
                                errorMessage.value = "Usuario o contraseña incorrectos"
                            }
                        } else {
                            errorMessage.value = "Usuario o contraseña incorrectos"
                        }

                    } catch (e: Exception) {
                        errorMessage.value = "Error de conexión"
                    }

                }
            }) {
                Text("Iniciar Sesion")
            }
        }
    }
}


// ===============================
// Main
// ===============================

@Composable
fun MainScreen() {
    val users = remember { mutableStateListOf<UserResponseDTO>() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                val response = RetrofitClient.api.getAllUsers()
                users.clear()
                users.addAll(response)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(users) { user ->
            Text(text = "${user.name} - ${user.role}")
        }
    }
}


// ===============================
// Retrofit Client
// ===============================

object RetrofitClient {
    private const val BASE_URL = "http://192.168.18.4:8080/"

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}

// ===============================
// ApiService
// ===============================

interface ApiService {
    @POST("/proyecto/users/login")
    suspend fun login(@Body loginRequest: LoginRequestDTO): Response<ResponseBody>

    @GET("/proyecto/users")
    suspend fun getAllUsers(): List<UserResponseDTO>
}


// ===============================
// Clases / Entidades
// ===============================

data class ScoreResponseDTO(
    val id: Int,
    val score: Int,
    val date: String
)

data class UserResponseDTO(
    val id: Int,
    val name: String,
    val coins: Int,
    val role: String,
    val scores: List<ScoreResponseDTO>
)

data class LoginRequestDTO(
    val username: String,
    val password: String
)
