package com.example.appflyhigh

import android.os.Bundle
import android.telecom.Call
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

val customFont = FontFamily(
    Font(R.font.font, FontWeight.Normal)
)

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
    var currentScreen by remember { mutableStateOf("home") }

    when (currentScreen) {
        "home" -> HomeScreen(
            onLoginClick = { currentScreen = "login" },
            onRegisterClick = { currentScreen = "register" }
        )
        "login" -> LoginScreen(
            onLoginSuccess = { currentScreen = "principal" },
            onBackToHome = { currentScreen = "home" }
            )
        "register" -> RegisterScreen(
            onRegisterSuccess = { currentScreen = "login" },
            onBackToHome = { currentScreen = "home" }
        )
        "principal" -> PrincipalScreen()
    }
}

@Composable
fun HomeScreen(onLoginClick: () -> Unit, onRegisterClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        androidx.compose.foundation.Image(
            painter = androidx.compose.ui.res.painterResource(id = R.drawable.fondo),
            contentDescription = "Fondo Bckground",
            modifier = Modifier.fillMaxSize(),
            contentScale = androidx.compose.ui.layout.ContentScale.Crop
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(top = 100.dp)
                .fillMaxSize()
        ) {
            androidx.compose.foundation.Image(
                painter = androidx.compose.ui.res.painterResource(id = R.drawable.logo),
                contentDescription = "Icono App FlyHigh",
                modifier = Modifier
                    .size(300.dp)
                    .clip(androidx.compose.foundation.shape.RoundedCornerShape(26.dp)),
                contentScale = androidx.compose.ui.layout.ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(100.dp))

            Button(
                onClick = onLoginClick,
                modifier = Modifier
                    .width(300.dp)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = androidx.compose.ui.graphics.Color(android.graphics.Color.parseColor("#9ECBD7"))
                )
            ) {
                Box(
                    modifier = Modifier
                        .border(
                            width = 1.dp,
                            color = androidx.compose.ui.graphics.Color.White,
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
                        )
                        .padding(7.dp)
                ) {
                    Text(text = "INICIAR SESION", fontFamily = customFont)
                }
            }

            Spacer(modifier = Modifier.height(50.dp))

            Button(
                onClick = onRegisterClick,
                modifier = Modifier
                    .width(300.dp)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = androidx.compose.ui.graphics.Color(android.graphics.Color.parseColor("#9ECBD7"))
                )
            ) {
                Box(
                    modifier = Modifier
                        .border(
                            width = 1.dp,
                            color = androidx.compose.ui.graphics.Color.White,
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
                        )
                        .padding(7.dp)
                ) {
                    Text("REGISTRARSE", fontFamily = customFont)
                }
            }
        }
    }
}


// ===============================
// LOGIN
// ===============================

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit, onBackToHome: () -> Unit) {
    val username = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val errorMessage = remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        androidx.compose.foundation.Image(
            painter = androidx.compose.ui.res.painterResource(id = R.drawable.fondo),
            contentDescription = "Fondo Bckground",
            modifier = Modifier.fillMaxSize(),
            contentScale = androidx.compose.ui.layout.ContentScale.Crop
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Center)
        ) {
            androidx.compose.foundation.Image(
                painter = androidx.compose.ui.res.painterResource(id = R.drawable.logo),
                contentDescription = "Icono App FlyHigh",
                modifier = Modifier
                    .size(300.dp)
                    .clip(androidx.compose.foundation.shape.RoundedCornerShape(26.dp)),
                contentScale = androidx.compose.ui.layout.ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "INICIAR SESION",
                fontFamily = customFont,
                fontWeight = FontWeight.Bold,
                color = Color(android.graphics.Color.parseColor("#fad742")),
                fontSize = 38.sp
            )

            Spacer(modifier = Modifier.height(25.dp))

            Box(
                modifier = Modifier
                    .border(width = 1.dp, color = androidx.compose.ui.graphics.Color.Black, shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
                    .padding(7.dp)
            ) {
                TextField(
                    value = username.value,
                    onValueChange = { username.value = it },
                    label = { Text("Usuario", fontFamily = customFont) },
                    modifier = Modifier.width(285.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Box(
                modifier = Modifier
                    .border(width = 1.dp, color = androidx.compose.ui.graphics.Color.Black, shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
                    .padding(7.dp)
            ) {
                TextField(
                    value = password.value,
                    onValueChange = { password.value = it },
                    label = { Text("Contraseña", fontFamily = customFont) },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.width(285.dp)
                )
            }

            if (errorMessage.value.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = errorMessage.value,
                    color = androidx.compose.ui.graphics.Color.Red,
                    fontFamily = customFont
                )
            }

            Spacer(modifier = Modifier.height(50.dp))

            androidx.compose.foundation.Image(
                painter = androidx.compose.ui.res.painterResource(id = R.drawable.logo),
                contentDescription = "Iniciar Sesión",
                modifier = Modifier
                    .size(60.dp)
                    .clip(androidx.compose.foundation.shape.CircleShape)
                    .clickable {
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
                    }
            )
        }
        androidx.compose.foundation.Image(
            painter = androidx.compose.ui.res.painterResource(id = R.drawable.flecha),
            contentDescription = "Volver al inicio",
            modifier = Modifier
                .size(70.dp)
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .alpha(0.7f)
                .clickable { onBackToHome() }
        )
    }
}

// ===============================
// REGISTER
// ===============================

@Composable
fun RegisterScreen(onRegisterSuccess: () -> Unit, onBackToHome: () -> Unit) {
    val username = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val errorMessage = remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        androidx.compose.foundation.Image(
            painter = androidx.compose.ui.res.painterResource(id = R.drawable.fondo),
            contentDescription = "Fondo Bckground",
            modifier = Modifier.fillMaxSize(),
            contentScale = androidx.compose.ui.layout.ContentScale.Crop
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Center)
        ) {
            androidx.compose.foundation.Image(
                painter = androidx.compose.ui.res.painterResource(id = R.drawable.logo),
                contentDescription = "Icono App FlyHigh",
                modifier = Modifier
                    .size(300.dp)
                    .clip(androidx.compose.foundation.shape.RoundedCornerShape(26.dp)),
                contentScale = androidx.compose.ui.layout.ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "REGISTRARSE",
                fontFamily = customFont,
                fontWeight = FontWeight.Bold,
                color = Color(android.graphics.Color.parseColor("#fad742")),
                fontSize = 38.sp
            )

            Spacer(modifier = Modifier.height(25.dp))

            Box(
                modifier = Modifier
                    .border(width = 1.dp, color = androidx.compose.ui.graphics.Color.Black, shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
                    .padding(7.dp)
            ) {
                TextField(
                    value = username.value,
                    onValueChange = { username.value = it },
                    label = { Text("Usuario", fontFamily = customFont) },
                    modifier = Modifier.width(285.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Box(
                modifier = Modifier
                    .border(width = 1.dp, color = androidx.compose.ui.graphics.Color.Black, shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
                    .padding(7.dp)
            ) {
                TextField(
                    value = password.value,
                    onValueChange = { password.value = it },
                    label = { Text("Contraseña", fontFamily = customFont) },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.width(285.dp)
                )
            }

            if (errorMessage.value.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = errorMessage.value,
                    color = androidx.compose.ui.graphics.Color.Red,
                    fontFamily = customFont
                )
            }

            Spacer(modifier = Modifier.height(50.dp))

            androidx.compose.foundation.Image(
                painter = androidx.compose.ui.res.painterResource(id = R.drawable.logo),
                contentDescription = "Registrarse",
                modifier = Modifier
                    .size(60.dp)
                    .clip(androidx.compose.foundation.shape.CircleShape)
                    .clickable {
                        coroutineScope.launch {
                            try {
                                val response = RetrofitClient.api.register(
                                    RegisterRequestDTO(username.value, password.value)
                                )
                                if (response.isSuccessful) {
                                    onRegisterSuccess()
                                } else {
                                    errorMessage.value = "Error al registrar usuario"
                                }
                            } catch (e: Exception) {
                                errorMessage.value = "Error de conexión"
                            }
                        }
                    }
            )
        }
        androidx.compose.foundation.Image(
            painter = androidx.compose.ui.res.painterResource(id = R.drawable.flecha),
            contentDescription = "Volver al inicio",
            modifier = Modifier
                .size(70.dp)
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .alpha(0.7f)
                .clickable { onBackToHome() }
        )
    }
}


// ===============================
// PAGE INICIO
// ===============================

@Composable
fun PrincipalScreen() {
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
// RETROFIT CLIENT
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
// APISERVICE
// ===============================

interface ApiService {
    @POST("/proyecto/users/login")
    suspend fun login(@Body loginRequest: LoginRequestDTO): Response<ResponseBody>

    @POST("/proyecto/users/register")
    suspend fun register(@Body registerRequest: RegisterRequestDTO): Response<ResponseBody>

    @GET("/proyecto/users")
    suspend fun getAllUsers(): List<UserResponseDTO>
}


// ===============================
// CLASES / ENTIDADES
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

data class RegisterRequestDTO(
    val username: String,
    val password: String,
)
