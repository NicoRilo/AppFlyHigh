package com.example.appflyhigh

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appflyhigh.ui.theme.AppFlyHighTheme
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import java.text.SimpleDateFormat
import java.util.Locale

// Fuente personalizada utilizada en toda la aplicación
val customFont = FontFamily(
    Font(R.font.font, FontWeight.Normal)
)

// Variable global para almacenar el usuario logueado
val loggedInUser = mutableStateOf<UserResponseDetailDTO?>(null)

// Clase principal
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

// ===============================
// NAVEGACIÓN PRINCIPAL
// ===============================

// Controla la navegación entre las diferentes pantallas de la aplicación
@Composable
fun AppNavigation() {
    // Estado para rastrear la pantalla actual
    var currentScreen by remember { mutableStateOf("home") }

    // Navegacion a las distitnas page
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

        "principal" -> PrincipalScreen(
            currentScreen = currentScreen,
            onNavigate = { screen -> currentScreen = screen }
        )

        "noticias" -> NoticiasScreen(
            currentScreen = currentScreen,
            onNavigate = { screen -> currentScreen = screen })

        "chat" -> ChatScreen(
            currentScreen = currentScreen,
            onNavigate = { screen -> currentScreen = screen })

        "ranking" -> RankingScreen(
            currentScreen = currentScreen,
            onNavigate = { screen -> currentScreen = screen })

        "ajustes" -> AjustesScreen(
            currentScreen = currentScreen,
            onNavigate = { screen -> currentScreen = screen },
            user = loggedInUser.value)
        else -> {
            if (currentScreen.startsWith("noticia_detail/")) {
                val noticiaId = currentScreen.split("/").last().toInt()
                NoticiaDetailScreen(
                    noticiaId = noticiaId,
                    onBack = { currentScreen = "noticias" },
                    onNavigateToRankings = { currentScreen = "ranking" }
                )
            }
        }
    }
}

// ===============================
// HOME
// ===============================

@Composable
fun HomeScreen(onLoginClick: () -> Unit, onRegisterClick: () -> Unit) {
    // Box principal que contiene todo el contenido de la pantalla
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Imagen de fondo que ocupa toda la pantalla
        androidx.compose.foundation.Image(
            painter = androidx.compose.ui.res.painterResource(id = R.drawable.fondo),
            contentDescription = "Fondo Bckground",
            modifier = Modifier.fillMaxSize(),
            contentScale = androidx.compose.ui.layout.ContentScale.Crop
        )
        // Columna que alinea los elementos en el centro de la pantalla
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(top = 100.dp)
                .fillMaxSize()
        ) {
            // Imagen del logo de la aplicación
            androidx.compose.foundation.Image(
                painter = androidx.compose.ui.res.painterResource(id = R.drawable.logo),
                contentDescription = "Icono App FlyHigh",
                modifier = Modifier
                    .size(300.dp)
                    .clip(androidx.compose.foundation.shape.RoundedCornerShape(26.dp)),
                contentScale = androidx.compose.ui.layout.ContentScale.Crop
            )

            // Espacio entre los elementos
            Spacer(modifier = Modifier.height(100.dp))

            // Botón de inicio de sesión
            Button(
                onClick = onLoginClick,
                modifier = Modifier
                    .width(300.dp)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = androidx.compose.ui.graphics.Color(
                        android.graphics.Color.parseColor(
                            "#9ECBD7"
                        )
                    )
                )
            ) {
                Box(
                    modifier = Modifier
                ) {
                    Text(
                        text = "INICIAR SESION",
                        fontFamily = customFont,
                        color = Color.Black,
                        fontSize = 20.sp
                    )
                }
            }

            // Espacio entre los elementos
            Spacer(modifier = Modifier.height(50.dp))

            // Botón de registro
            Button(
                onClick = onRegisterClick,
                modifier = Modifier
                    .width(300.dp)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = androidx.compose.ui.graphics.Color(
                        android.graphics.Color.parseColor(
                            "#9ECBD7"
                        )
                    )
                )
            ) {
                Box(
                    modifier = Modifier
                ) {
                    Text(
                        "REGISTRARSE",
                        fontFamily = customFont,
                        color = Color.Black,
                        fontSize = 20.sp
                    )
                }
            }
        }
    }
}

// ===============================
// LOGIN
// ===============================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(onLoginSuccess: () -> Unit, onBackToHome: () -> Unit) {
    // Variables para almacenar el estado del usuario, contraseña y mensajes de error
    val username = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val errorMessage = remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    // Caja principal que contiene todo el contenido de la pantalla
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Imagen de fondo que ocupa toda la pantalla
        androidx.compose.foundation.Image(
            painter = androidx.compose.ui.res.painterResource(id = R.drawable.fondo),
            contentDescription = "Fondo Bckground",
            modifier = Modifier.fillMaxSize(),
            contentScale = androidx.compose.ui.layout.ContentScale.Crop
        )
        // Columna para organizar los elementos en el centro de la pantalla
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Center)
        ) {
            // Título de la pantalla
            Text(
                text = "INICIAR SESION",
                fontFamily = customFont,
                fontWeight = FontWeight.Bold,
                color = Color(android.graphics.Color.parseColor("#fad742")),
                fontSize = 38.sp
            )

            // Espacio entre los elementos
            Spacer(modifier = Modifier.height(10.dp))

            // Imagen del logo de la aplicación
            androidx.compose.foundation.Image(
                painter = androidx.compose.ui.res.painterResource(id = R.drawable.logo),
                contentDescription = "Icono App FlyHigh",
                modifier = Modifier
                    .size(300.dp)
                    .clip(androidx.compose.foundation.shape.RoundedCornerShape(26.dp)),
                contentScale = androidx.compose.ui.layout.ContentScale.Crop
            )

            // Espacio entre los elementos
            Spacer(modifier = Modifier.height(25.dp))

            // Campo de texto para el nombre de usuario
            Box(
                modifier = Modifier
                    .border(
                        width = 1.dp,
                        color = androidx.compose.ui.graphics.Color.Black,
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
                    )
                    .padding(7.dp)
            ) {
                TextField(
                    value = username.value,
                    onValueChange = { username.value = it },
                    label = { Text("Usuario", fontFamily = customFont) },
                    modifier = Modifier.width(285.dp),
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color.Transparent,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedLabelColor = Color(android.graphics.Color.parseColor("#fad742")),
                        unfocusedLabelColor = Color.Black,
                        focusedIndicatorColor = Color(android.graphics.Color.parseColor("#fad742")),
                        unfocusedIndicatorColor = Color.Black
                    )
                )
            }

            // Espacio entre los elementos
            Spacer(modifier = Modifier.height(20.dp))

            // Campo de texto para la contraseña
            Box(
                modifier = Modifier
                    .border(
                        width = 1.dp,
                        color = androidx.compose.ui.graphics.Color.Black,
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
                    )
                    .padding(7.dp)
            ) {
                TextField(
                    value = password.value,
                    onValueChange = { password.value = it },
                    label = { Text("Contraseña", fontFamily = customFont) },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.width(285.dp),
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color.Transparent,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedLabelColor = Color(android.graphics.Color.parseColor("#fad742")),
                        unfocusedLabelColor = Color.Black,
                        focusedIndicatorColor = Color(android.graphics.Color.parseColor("#fad742")),
                        unfocusedIndicatorColor = Color.Black
                    )
                )
            }

            // Si hay un mensaje de error, se muestra debajo de los campos de texto
            if (errorMessage.value.isNotEmpty()) {
                Spacer(modifier = Modifier.height(30.dp))
                Text(
                    text = errorMessage.value,
                    color = androidx.compose.ui.graphics.Color.Red,
                    fontFamily = customFont
                )
            }

            // Espacio entre los elementos
            Spacer(modifier = Modifier.height(50.dp))

            // Botón de inicio de sesión
            androidx.compose.foundation.Image(
                painter = androidx.compose.ui.res.painterResource(id = R.drawable.logo),
                contentDescription = "Iniciar Sesión",
                modifier = Modifier
                    .size(60.dp)
                    .clip(androidx.compose.foundation.shape.CircleShape)
                    .clickable {
                        coroutineScope.launch {
                            try {
                                // Llamada a la API para iniciar sesión
                                val response = RetrofitClient.api.login(LoginRequestDTO(username.value, password.value))
                                if (response.isSuccessful) {
                                    val responseBody = response.body()?.string()
                                    if (responseBody == "Login successful") {
                                        // Obtener detalles del usuario tras un inicio exitoso
                                        val userDetailsResponse = RetrofitClient.api.getUserDetails(username.value)
                                        if (userDetailsResponse.isSuccessful) {
                                            loggedInUser.value = userDetailsResponse.body()
                                            onLoginSuccess() // Navegar a la pantalla principal
                                        } else {
                                            errorMessage.value = "Error al obtener los datos del usuario"
                                        }
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
        // Boton para volver a la pantalla de inicio
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(onRegisterSuccess: () -> Unit, onBackToHome: () -> Unit) {
    // Variables para almacenar el estado del nombre de usuario, contraseña y mensajes de error
    val username = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val errorMessage = remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    var passwordVisible by remember { mutableStateOf(false) }

    // Contenedor principal de la pantalla
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Imagen de fondo
        androidx.compose.foundation.Image(
            painter = androidx.compose.ui.res.painterResource(id = R.drawable.fondo),
            contentDescription = "Fondo Bckground",
            modifier = Modifier.fillMaxSize(),
            contentScale = androidx.compose.ui.layout.ContentScale.Crop
        )
        // Columna para organizar los elementos verticalmente
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Center)
        ) {
            // Título de la pantalla
            Text(
                text = "REGISTRARSE",
                fontFamily = customFont,
                fontWeight = FontWeight.Bold,
                color = Color(android.graphics.Color.parseColor("#fad742")),
                fontSize = 38.sp
            )

            // Espacio entre los elementos
            Spacer(modifier = Modifier.height(10.dp))

            // Imagen del logo de la aplicación
            androidx.compose.foundation.Image(
                painter = androidx.compose.ui.res.painterResource(id = R.drawable.logo),
                contentDescription = "Icono App FlyHigh",
                modifier = Modifier
                    .size(300.dp)
                    .clip(androidx.compose.foundation.shape.RoundedCornerShape(26.dp)),
                contentScale = androidx.compose.ui.layout.ContentScale.Crop
            )

            // Espacio entre los elementos
            Spacer(modifier = Modifier.height(25.dp))

            // Campo de texto para el nombre de usuario
            Box(
                modifier = Modifier
                    .border(
                        width = 1.dp,
                        color = androidx.compose.ui.graphics.Color.Black,
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
                    )
                    .padding(7.dp)
            ) {
                TextField(
                    value = username.value,
                    onValueChange = { username.value = it },
                    label = { Text("Usuario", fontFamily = customFont) },
                    modifier = Modifier.width(285.dp),
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color.Transparent,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedLabelColor = Color(android.graphics.Color.parseColor("#fad742")),
                        unfocusedLabelColor = Color.Black,
                        focusedIndicatorColor = Color(android.graphics.Color.parseColor("#fad742")),
                        unfocusedIndicatorColor = Color.Black
                    )
                )
            }

            // Espacio entre los elementos
            Spacer(modifier = Modifier.height(20.dp))

            // Campo de texto para la contraseña
            Box(
                modifier = Modifier
                    .border(
                        width = 1.dp,
                        color = androidx.compose.ui.graphics.Color.Black,
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
                    )
                    .padding(7.dp)
            ) {
                TextField(
                    value = password.value,
                    onValueChange = { password.value = it },
                    label = { Text("Contraseña", fontFamily = customFont) },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        // Icono para mostrar/ocultar la contraseña
                        androidx.compose.foundation.Image(
                            painter = androidx.compose.ui.res.painterResource(
                                R.drawable.ojo
                            ),
                            contentDescription = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña",
                            modifier = Modifier
                                .size(24.dp)
                                .clickable { passwordVisible = !passwordVisible }
                        )
                    },
                    modifier = Modifier.width(285.dp),
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color.Transparent,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedLabelColor = Color(android.graphics.Color.parseColor("#fad742")),
                        unfocusedLabelColor = Color.Black,
                        focusedIndicatorColor = Color(android.graphics.Color.parseColor("#fad742")),
                        unfocusedIndicatorColor = Color.Black
                    )
                )
            }

            // Si hay un mensaje de error, se muestra debajo de los campos de texto
            if (errorMessage.value.isNotEmpty()) {
                Spacer(modifier = Modifier.height(30.dp))
                Text(
                    text = errorMessage.value,
                    color = androidx.compose.ui.graphics.Color.Red,
                    fontFamily = customFont
                )
            }

            // Espacio entre los elementos
            Spacer(modifier = Modifier.height(50.dp))

            // Botón de registro
            androidx.compose.foundation.Image(
                painter = androidx.compose.ui.res.painterResource(id = R.drawable.logo),
                contentDescription = "Registrarse",
                modifier = Modifier
                    .size(60.dp)
                    .clip(androidx.compose.foundation.shape.CircleShape)
                    .clickable {
                        if (username.value.isBlank() || password.value.isBlank()) {
                            errorMessage.value = "Todos los campos son obligatorios"
                        } else {
                            coroutineScope.launch {
                                try {
                                    // Llamada a la API para registrar un nuevo usuario
                                    val response = RetrofitClient.api.register(
                                        RegisterRequestDTO(username.value, password.value)
                                    )
                                    if (response.isSuccessful) {
                                        onRegisterSuccess() // Navegar a la pantalla de inicio de sesión
                                    } else {
                                        errorMessage.value = "Error al registrar usuario"
                                    }
                                } catch (e: Exception) {
                                    errorMessage.value = "Error de conexión"
                                }
                            }
                        }
                    }
            )
        }
        // Botón para volver a la pantalla de inicio
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
// PAGE PRINCIPAL
// ===============================

@Composable
fun PrincipalScreen(currentScreen: String, onNavigate: (String) -> Unit) {
    // Contenedor principal
    Box(modifier = Modifier.fillMaxSize()) {
        // Componente de menú que muestra el título y maneja la navegación
        MenuComponent("PRINCIPAL", currentScreen = currentScreen, onNavigate = onNavigate)

        // Lista desplazable verticalmente para mostrar el contenido principal
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 120.dp, start = 16.dp, end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            //Contenido
            item {
                Text(
                    text = "¡Bienvenido a FlyHigh!",
                    fontFamily = customFont,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                )
                // Espacio entre los elementos
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Explora las características de nuestra app y disfruta de una experiencia única.",
                    fontFamily = customFont,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                )
            }

            // Imagen del logo de la aplicación
            item {
                Image(
                    painter = androidx.compose.ui.res.painterResource(id = R.drawable.logo),
                    contentDescription = "FlyHigh Logo",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(vertical = 16.dp)
                        .clip(androidx.compose.foundation.shape.RoundedCornerShape(16.dp)),
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                )
            }

            // Sección de características del juego
            item {
                Text(
                    text = "Características del Juego",
                    fontFamily = customFont,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                )
                // Espacio entre los elementos
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "- Personaliza tus partidas.\n" +
                            "- Compite en los rankings globales.\n" +
                            "- Descubre nuevos mapas y personajes.",
                    fontFamily = customFont,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                )
            }

            // Sección de imágenes
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Image(
                        painter = androidx.compose.ui.res.painterResource(id = R.drawable.mosca),
                        contentDescription = "Mosca Img",
                        modifier = Modifier
                            .size(100.dp)
                            .clip(androidx.compose.foundation.shape.CircleShape),
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
                    )
                    Image(
                        painter = androidx.compose.ui.res.painterResource(id = R.drawable.flappyres),
                        contentDescription = "Flappy Img",
                        modifier = Modifier
                            .size(100.dp)
                            .clip(androidx.compose.foundation.shape.CircleShape),
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
                    )
                }
            }

            // Botón para explorar noticias
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { onNavigate("noticias") },
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(android.graphics.Color.parseColor("#9ECBD7"))
                    )
                ) {
                    Text(
                        text = "Explorar Noticias",
                        fontFamily = customFont,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

// ===============================
// PAGE NOTICIAS
// ===============================

@Composable
fun NoticiasScreen(currentScreen: String, onNavigate: (String) -> Unit) {
    // Se obtiene la lista de noticias
    val noticias = NoticiasRepository.noticias

    // Componente de menú que muestra el título y maneja la navegación
    MenuComponent("NOTICIAS", currentScreen = currentScreen, onNavigate = onNavigate)

    // Lista desplazable verticalmente para mostrar las noticias
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 80.dp, start = 16.dp, end = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Por cada noticia, se genera una tarjeta interactiva
        items(noticias) { noticia ->
            NoticiaCard(
                noticia = noticia,
                onClick = { onNavigate("noticia_detail/${noticia.id}") }
            )
        }
    }
}

@Composable
fun NoticiaCard(noticia: Noticia, onClick: () -> Unit) {
    // Contenedor de la tarjeta de noticia
    Box(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .clip(androidx.compose.foundation.shape.RoundedCornerShape(16.dp))
            .background(Color(android.graphics.Color.parseColor("#f0f0f0")))
            .border(
                width = 2.dp,
                color = Color.Gray,
                shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
            )
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        // Columna que organiza los elementos dentro de la tarjeta
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Imagen de la noticia
            androidx.compose.foundation.Image(
                painter = androidx.compose.ui.res.painterResource(id = noticia.image),
                contentDescription = noticia.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clip(androidx.compose.foundation.shape.RoundedCornerShape(8.dp)),
                contentScale = androidx.compose.ui.layout.ContentScale.Crop
            )
            // Espacio entre los elementos
            Spacer(modifier = Modifier.height(8.dp))
            // Título de la noticia
            Text(
                text = noticia.title,
                fontFamily = customFont,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun NoticiaDetailScreen(noticiaId: Int, onBack: () -> Unit, onNavigateToRankings: () -> Unit) {
    // Obtiene la noticia correspondiente al ID
    val noticia = NoticiasRepository.getNoticiaById(noticiaId)

    // Contenedor principal de la pantalla de detalle de la noticia
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Si la noticia existe, se muestra su contenido
        noticia?.let {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Imagen principal de la noticia
                androidx.compose.foundation.Image(
                    painter = androidx.compose.ui.res.painterResource(id = it.image),
                    contentDescription = it.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(androidx.compose.foundation.shape.RoundedCornerShape(8.dp)),
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                )
                // Espacio entre los elementos
                Spacer(modifier = Modifier.height(16.dp))
                // Título y contenido de la noticia
                Text(
                    text = it.title,
                    fontFamily = customFont,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                // Espacio entre los elementos
                Spacer(modifier = Modifier.height(8.dp))
                // Contenido de la noticia
                Text(
                    text = it.content,
                    fontFamily = customFont,
                    fontSize = 16.sp
                )
                // Espacio entre los elementos
                Spacer(modifier = Modifier.height(16.dp))

                // Si el ID de la noticia es 3, se muestra un botón para navegar a los rankings
                if (noticiaId == 3) {
                    Button(
                        onClick = onNavigateToRankings,
                        modifier = Modifier
                            .fillMaxWidth(0.6f)
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(android.graphics.Color.parseColor("#9ECBD7"))
                        )
                    ) {
                        Text(
                            text = "Ir a Rankings",
                            fontFamily = customFont,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Sección de imágenes adicionales de la noticia
                it.extraImages.forEach { imageRes ->
                    androidx.compose.foundation.Image(
                        painter = androidx.compose.ui.res.painterResource(id = imageRes),
                        contentDescription = "Extra Image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .clip(androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
                            .padding(vertical = 8.dp),
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
                    )
                }
            }
        }
        // Botón para volver a la pantalla de noticias
        Button(
            onClick = onBack,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .fillMaxWidth(0.4f)
                .height(50.dp)
                .background(
                    color = Color(android.graphics.Color.parseColor("#f0f0f0")),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
                )
                .border(
                    width = 2.dp,
                    color = Color.Gray,
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
                ),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent
            )
        ) {
            Text(
                text = "Volver",
                fontFamily = customFont,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
    }
}

// ===============================
// PAGE CHAT
// ===============================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(currentScreen: String, onNavigate: (String) -> Unit) {
    val messages = remember { mutableStateListOf<ChatMessage>() }
    val newMessage = remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    // Cargar mensajes al abrir la pantalla
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                val response = RetrofitClient.api.getChatMessages()
                if (response.isSuccessful) {
                    messages.clear()
                    messages.addAll(response.body() ?: emptyList())
                }
            } catch (e: Exception) {
                Log.e("ChatScreen", "Error al mostrar mensajes", e)
            }
        }
    }

    // Menú en la parte superior
    MenuComponent(menuTitle = "CHAT", currentScreen = currentScreen, onNavigate = onNavigate)

    // Contenedor principal
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Contenedor del chat
        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .fillMaxHeight(0.80f)
                .align(Alignment.Center)
                .offset(y = 30.dp)
                .background(
                    color = Color(android.graphics.Color.parseColor("#f0f0f0")),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
                )
                .border(
                    width = 2.dp,
                    color = Color.Gray,
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
                )
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Lista de mensajes
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp),
                    reverseLayout = true
                ) {
                    // Mostrar los mensajes del chat
                    items(messages) { message ->
                        ChatMessageItem(message = message)
                    }
                }

                // Campo de texto y botón para enviar mensajes
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Campo de texto para escribir un nuevo mensaje
                    TextField(
                        value = newMessage.value,
                        onValueChange = { newMessage.value = it },
                        label = { Text("Escribe un mensaje...") },
                        modifier = Modifier.weight(1f),
                        colors = TextFieldDefaults.textFieldColors(
                            containerColor = Color.Transparent,
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedLabelColor = Color(android.graphics.Color.parseColor("#fad742")),
                            unfocusedLabelColor = Color.Black,
                            focusedIndicatorColor = Color(android.graphics.Color.parseColor("#fad742")),
                            unfocusedIndicatorColor = Color.Black
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    // Botón para enviar el mensaje
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                try {
                                    // Enviar el mensaje a la API
                                    val response = RetrofitClient.api.sendMessage(
                                        SendMessageRequest(
                                            username = loggedInUser.value?.username ?: "Anónimo",
                                            message = newMessage.value
                                        )
                                    )
                                    // Si la respuesta es exitosa, actualizar la lista de mensajes
                                    if (response.isSuccessful) {
                                        val updatedMessages = RetrofitClient.api.getChatMessages()
                                        if (updatedMessages.isSuccessful) {
                                            messages.clear()
                                            messages.addAll(updatedMessages.body() ?: emptyList())
                                        }
                                        newMessage.value = ""
                                    }
                                } catch (e: Exception) {
                                    Log.e("ChatScreen", "Error al enviar el mensaje", e)
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(android.graphics.Color.parseColor("#fad742"))
                        )
                    ) {
                        Text(
                            text = "Enviar",
                            fontFamily = customFont,
                            color = Color.Black
                        )
                    }
                }
            }
        }
    }
}

// Estilo de los Text de los mensajes del chat
@Composable
fun ChatMessageItem(message: ChatMessage) {
    val isCurrentUser = message.username == loggedInUser.value?.username

    // Formatear el timestamp
    val formattedTimestamp = try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val date = inputFormat.parse(message.timestamp)
        outputFormat.format(date ?: message.timestamp)
    } catch (e: Exception) {
        message.timestamp // En caso de error, mostrar el timestamp original
    }

    // Contenedor del mensaje
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = if (isCurrentUser) Arrangement.End else Arrangement.Start
    ) {
        Column(
            modifier = Modifier
                .background(
                    color = if (isCurrentUser) Color(android.graphics.Color.parseColor("#D1E8FF")) else Color(android.graphics.Color.parseColor("#F5F5F5")),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
                )
                .padding(12.dp)
                .widthIn(max = 250.dp)
        ) {
            // Mostrar el nombre de usuario, mensaje y timestamp
            Text(
                text = message.username,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = if (isCurrentUser) Color.Black else Color.DarkGray
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = message.message,
                fontSize = 16.sp,
                color = if (isCurrentUser) Color.Black else Color.DarkGray
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = formattedTimestamp,
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}

// ===============================
// PAGE RANKING
// ===============================

@Composable
fun RankingScreen(currentScreen: String, onNavigate: (String) -> Unit) {
    // Lista mutable para almacenar las puntuaciones
    val topScores = remember { mutableStateListOf<ScoreTop10ResponseDTO>() }
    // Variable mutable para almacenar el puntaje personal del usuario
    val personalScore = remember { mutableStateOf(0) }
    val coroutineScope = rememberCoroutineScope()

    // Efecto lanzado al cargar la pantalla para obtener los datos
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                // Llamada a la API para obtener los puntajes
                val response = RetrofitClient.api.getTopScores()
                if (response.isSuccessful) {
                    // Filtra los puntajes para evitar duplicados
                    val uniqueScores = mutableSetOf<Pair<String, Int>>()
                    val filteredScores = response.body()?.filter { score ->
                        uniqueScores.add(score.username to score.maxScore)
                    } ?: emptyList()

                    // Ordena los puntajes de mayor a menor
                    val sortedScores = filteredScores.sortedByDescending { it.maxScore }

                    // Si hay menos de 10 puntajes, completa con los restantes
                    val finalScores = if (sortedScores.size < 10) {
                        sortedScores + response.body()!!.filterNot { score ->
                            uniqueScores.contains(score.username to score.maxScore)
                        }.take(10 - sortedScores.size)
                    } else {
                        sortedScores
                    }

                    // Actualiza la lista de puntajes
                    topScores.clear()
                    topScores.addAll(finalScores)
                }

                // Obtiene el puntaje personal del usuario logueado
                loggedInUser.value?.let { user ->
                    val personalResponse = RetrofitClient.api.getPersonalScore(user.username)
                    if (personalResponse.isSuccessful) {
                        personalScore.value = personalResponse.body() ?: 0
                    }
                }
            } catch (e: Exception) {
                Log.e("RankingScreen", "Error fetching scores", e)
            }
        }
    }

    // Componente de menú que muestra el título y maneja la navegación
    MenuComponent("RANKING", currentScreen = currentScreen, onNavigate = onNavigate)

    // Contenedor principal que muestra el ranking y el puntaje personal
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 140.dp, start = 24.dp, end = 24.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            // Título del ranking
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .background(
                        color = Color(android.graphics.Color.parseColor("#fad742")),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
                    )
                    .padding(12.dp)
            ) {
                Text(
                    text = "Top 10 Scores",
                    fontFamily = customFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Espacio entre los elementos
            Spacer(modifier = Modifier.height(12.dp))

            // Lista de puntajes
            LazyColumn(
                modifier = Modifier.fillMaxWidth(0.9f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(topScores) { index, score ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = Color(android.graphics.Color.parseColor("#f0f0f0")),
                                shape = androidx.compose.foundation.shape.RoundedCornerShape(10.dp)
                            )
                            .border(
                                width = 1.dp,
                                color = Color.Gray,
                                shape = androidx.compose.foundation.shape.RoundedCornerShape(10.dp)
                            )
                            .padding(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${index + 1}° ${score.username}",
                                fontFamily = customFont,
                                fontSize = 16.sp,
                                color = Color.Black
                            )
                            Text(
                                text = score.maxScore.toString(),
                                fontFamily = customFont,
                                fontSize = 16.sp,
                                color = Color.Black
                            )
                        }
                    }
                }
            }

            // Espacio entre los elementos
            Spacer(modifier = Modifier.height(16.dp))

            // Puntaje personal del usuario
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .background(
                        color = Color(android.graphics.Color.parseColor("#9ECBD7")),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
                    )
                    .padding(12.dp)
            ) {
                Text(
                    text = "Score Personal: ${personalScore.value}",
                    fontFamily = customFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

// ===============================
// PAGE AJUSTES
// ===============================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AjustesScreen(currentScreen: String, onNavigate: (String) -> Unit, user: UserResponseDetailDTO?) {
    // Variables para almacenar los valores de los campos
    var nombre by remember { mutableStateOf(user?.username ?: "") }
    var contrasena by remember { mutableStateOf(user?.password ?: "") }
    var biografia by remember { mutableStateOf(user?.bibliography ?: "") }
    var passwordVisible by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    // Componente de menú que muestra el título y maneja la navegación
    MenuComponent("AJUSTES", currentScreen = currentScreen, onNavigate = onNavigate)

    // Contenedor principal
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        // Recuadro principal
        Box(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .height(620.dp)
                .background(
                    color = Color(android.graphics.Color.parseColor("#f0f0f0")),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
                )
                .border(
                    width = 2.dp,
                    color = Color.Gray,
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
                )
                .padding(16.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Spacer(modifier = Modifier.height(32.dp))

                // Imagen de perfil
                androidx.compose.foundation.Image(
                    painter = androidx.compose.ui.res.painterResource(id = R.drawable.logo),
                    contentDescription = "Foto de perfil",
                    modifier = Modifier
                        .size(150.dp)
                        .clip(androidx.compose.foundation.shape.CircleShape)
                        .background(Color.LightGray)
                )

                // Espacio entre los elementos
                Spacer(modifier = Modifier.height(32.dp))

                // Campo de texto para el nombre
                TextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre", fontFamily = customFont) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color.Transparent,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedLabelColor = Color(android.graphics.Color.parseColor("#fad742")),
                        unfocusedLabelColor = Color.Black,
                        focusedIndicatorColor = Color(android.graphics.Color.parseColor("#fad742")),
                        unfocusedIndicatorColor = Color.Black
                    )
                )

                // Espacio entre los elementos
                Spacer(modifier = Modifier.height(42.dp))

                // Campo de texto para la contraseña
                TextField(
                    value = contrasena,
                    onValueChange = { contrasena = it },
                    label = { Text("Contraseña", fontFamily = customFont) },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        // Icono para mostrar/ocultar la contraseña
                        androidx.compose.foundation.Image(
                            painter = androidx.compose.ui.res.painterResource(
                                id = R.drawable.ojo
                            ),
                            contentDescription = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña",
                            modifier = Modifier
                                .size(24.dp)
                                .clickable { passwordVisible = !passwordVisible }
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color.Transparent,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedLabelColor = Color(android.graphics.Color.parseColor("#fad742")),
                        unfocusedLabelColor = Color.Black,
                        focusedIndicatorColor = Color(android.graphics.Color.parseColor("#fad742")),
                        unfocusedIndicatorColor = Color.Black
                    )
                )

                // Espacio entre los elementos
                Spacer(modifier = Modifier.height(42.dp))

                // Campo de texto para la biografía
                TextField(
                    value = biografia,
                    onValueChange = { if (it.length <= 100) biografia = it },
                    label = { Text("Biografía", fontFamily = customFont) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color.Transparent,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedLabelColor = Color(android.graphics.Color.parseColor("#fad742")),
                        unfocusedLabelColor = Color.Black,
                        focusedIndicatorColor = Color(android.graphics.Color.parseColor("#fad742")),
                        unfocusedIndicatorColor = Color.Black
                    )
                )

                // Espacio entre los elementos
                Spacer(modifier = Modifier.height(22.dp))

                // Botón para aplicar cambios
                Button(
                    onClick = {
                        coroutineScope.launch {
                            try {
                                // Llamada a la API para actualizar los datos del usuario
                                val response = RetrofitClient.api.updateUser(
                                    UserResponseDetailDTO(
                                        username = nombre,
                                        password = contrasena,
                                        bibliography = biografia
                                    )
                                )
                                // Verifica si la respuesta fue exitosa
                                if (response.isSuccessful) {
                                    // Actualiza el usuario logueado con los nuevos datos
                                    loggedInUser.value = UserResponseDetailDTO(
                                        username = nombre,
                                        password = contrasena,
                                        bibliography = biografia
                                    )
                                } else {
                                    Log.e("AjustesScreen", "Error al actualizar los datos: ${response.errorBody()?.string()}")
                                }
                            } catch (e: Exception) {
                                Log.e("AjustesScreen", "Error de conexión", e)
                            }
                        }
                    },
                    modifier = Modifier
                        .width(200.dp)
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(android.graphics.Color.parseColor("#9ECBD7"))
                    )
                ) {
                    Text(
                        text = "Aplicar cambios",
                        fontFamily = customFont,
                        color = Color.Black,
                        fontSize = 18.sp
                    )
                }
            }
        }
    }
}

// ===============================
// MENU COMPONENT RECYCLER
// ===============================

@Composable
fun MenuComponent(menuTitle: String, currentScreen: String, onNavigate: (String) -> Unit) {
    // Estado para controlar si el menú desplegable está activo
    var isMenuActive by remember { mutableStateOf(false) }

    // Contenedor principal
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp),
        contentAlignment = Alignment.TopCenter,

        ) {
        // Contenedor del menú superior
        Box(
            modifier = Modifier
                .width(300.dp)
                .height(50.dp)
                .background(
                    color = Color(android.graphics.Color.parseColor("#fad742")),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(25.dp)
                )
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Fila que organiza los elementos del menú
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(androidx.compose.foundation.shape.CircleShape)
                        .clickable { isMenuActive = true }
                        .align(Alignment.CenterVertically)
                        .offset(x = (-12).dp)
                )
                Box(
                    modifier = Modifier
                        .size(55.dp)
                        .align(Alignment.CenterVertically)
                        .offset(x = (-42).dp)
                ) {
                    androidx.compose.foundation.Image(
                        painter = androidx.compose.ui.res.painterResource(id = R.drawable.menuicon),
                        contentDescription = "Menu Icon",
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Título del menú
                Text(
                    text = menuTitle,
                    fontFamily = customFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .weight(1f)
                        .align(Alignment.CenterVertically)
                        .offset(x = (-27).dp)
                )

                // Ícono del logo de la aplicación
                androidx.compose.foundation.Image(
                    painter = androidx.compose.ui.res.painterResource(id = R.drawable.logo),
                    contentDescription = "Icono",
                    modifier = Modifier
                        .size(34.dp)
                        .clip(androidx.compose.foundation.shape.CircleShape)
                        .align(Alignment.CenterVertically)
                )
            }

            // Menú desplegable
            DropdownMenu(
                expanded = isMenuActive,
                onDismissRequest = { isMenuActive = false },
                modifier = Modifier
                    .background(Color(android.graphics.Color.parseColor("#faf4bb")))
            ) {
                // Opción: Home
                DropdownMenuItem(
                    text = {
                        Text(
                            text = "Home",
                            fontFamily = customFont,
                            color = Color.Black,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    onClick = {
                        isMenuActive = false
                        onNavigate("home")
                    }
                )
                Divider(color = Color.Gray, thickness = 1.dp)

                // Opción: Principal
                DropdownMenuItem(
                    text = {
                        FlappyTextMenuAnimation(
                            text = "Principal",
                            fontFamily = customFont,
                            color = Color.Black,
                            currentPage = currentScreen,
                            targetPage = "principal",
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    onClick = {
                        isMenuActive = false
                        onNavigate("principal")
                    }
                )
                Divider(color = Color.Gray, thickness = 1.dp)

                // Opción: Noticias
                DropdownMenuItem(
                    text = {
                        FlappyTextMenuAnimation(
                            text = "Noticias",
                            fontFamily = customFont,
                            color = Color.Black,
                            currentPage = currentScreen,
                            targetPage = "noticias",
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    onClick = {
                        isMenuActive = false
                        onNavigate("noticias")
                    }
                )
                Divider(color = Color.Gray, thickness = 1.dp)

                // Opción: Chat
                DropdownMenuItem(
                    text = {
                        FlappyTextMenuAnimation(
                            text = "Chat",
                            fontFamily = customFont,
                            color = Color.Black,
                            currentPage = currentScreen,
                            targetPage = "chat",
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    onClick = {
                        isMenuActive = false
                        onNavigate("chat")
                    }
                )
                Divider(color = Color.Gray, thickness = 1.dp)

                // Opción: Ranking
                DropdownMenuItem(
                    text = {
                        FlappyTextMenuAnimation(
                            text = "Ranking",
                            fontFamily = customFont,
                            color = Color.Black,
                            currentPage = currentScreen,
                            targetPage = "ranking",
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    onClick = {
                        isMenuActive = false
                        onNavigate("ranking")
                    }
                )
                Divider(color = Color.Gray, thickness = 1.dp)

                // Opción: Ajustes
                DropdownMenuItem(
                    text = {
                        FlappyTextMenuAnimation(
                            text = "Ajustes",
                            fontFamily = customFont,
                            color = Color.Black,
                            currentPage = currentScreen,
                            targetPage = "ajustes",
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    onClick = {
                        isMenuActive = false
                        onNavigate("ajustes")
                    }
                )
            }
        }
    }
}

// ===============================
// ANIMATION MENU
// ===============================

@Composable
fun FlappyTextMenuAnimation(text: String, fontFamily: FontFamily, currentPage: String, targetPage: String, modifier: Modifier = Modifier, color: Color = Color.Black) {
    // Animación de desplazamiento vertical
    val offsetY = remember { Animatable(0f) }

    // Efecto lanzado cuando la pagina actual cambia
    LaunchedEffect(currentPage) {
        if (currentPage == targetPage) {
            while (true) {
                // Anima el texto hacia arriba
                offsetY.animateTo(
                    targetValue = -20f,
                    animationSpec = tween(durationMillis = 300)
                )
                // Anima el texto de regreso a su posición original
                offsetY.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(durationMillis = 300)
                )
            }
        }
    }

    // Muestra el texto con la animación aplicada
    Text(
        text = text,
        fontFamily = fontFamily,
        textAlign = TextAlign.Center,
        color = color,
        modifier = modifier.graphicsLayer(translationY = offsetY.value)
    )
}

// ===============================
// RETROFIT CLIENT
// ===============================

object RetrofitClient {
    // URL base del servidor API
    private const val BASE_URL = "http://192.168.18.4:8080/"

    // Instancia de la API
    val api: ApiService by lazy {
        // Configuración del cliente Retrofit
        Retrofit.Builder()
            .baseUrl(BASE_URL) // URL para las solicitudes
            .addConverterFactory(GsonConverterFactory.create()) // Convierte automáticamente JSON a objetos Kotlin usando Gson
            .build() // Construye la instancia de Retrofit
            .create(ApiService::class.java) // Crea la implementación de la interfaz ApiService
    }
}

// ===============================
// APISERVICE
// ===============================

// Interfaz que define los endpoints de la API
interface ApiService {
    // Endpoint para iniciar sesión
    @POST("/proyecto/users/login")
    suspend fun login(@Body loginRequest: LoginRequestDTO): Response<ResponseBody>

    // Endpoint para registrar un usuario
    @POST("/proyecto/users/register")
    suspend fun register(@Body registerRequest: RegisterRequestDTO): Response<ResponseBody>

    // Endpoint para obtener detalles de un usuario por nombre de usuario
    @GET("/proyecto/users/details/{username}")
    suspend fun getUserDetails(@Path("username") username: String): Response<UserResponseDetailDTO>

    // Endpoint para actualizar los detalles de un usuario
    @PUT("/proyecto/users/update")
    suspend fun updateUser(@Body user: UserResponseDetailDTO): Response<ResponseBody>

    // Endpoint para obtener el top 10 de puntuaciones
    @GET("/proyecto/scores/top10")
    suspend fun getTopScores(): Response<List<ScoreTop10ResponseDTO>>

    // Endpoint para obtener la puntuación personal de un usuario
    @GET("/proyecto/scores/max/{username}")
    suspend fun getPersonalScore(@Path("username") username: String): Response<Int>

    // Endpoint para obtener los mensajes del chat
    @GET("/proyecto/chat/messages")
    suspend fun getChatMessages(): Response<List<ChatMessage>>

    // Endpoint para enviar un mensaje al chat
    @POST("/proyecto/chat/send")
    suspend fun sendMessage(@Body request: SendMessageRequest): Response<ResponseBody>
}


// ===============================
// CLASES / ENTIDADES
// ===============================

// Representa la respuesta de puntaje del top 10
data class ScoreTop10ResponseDTO(
    val id: Int,
    val maxScore: Int,
    val username: String
)

// Representa la solicitud para iniciar sesión
data class LoginRequestDTO(
    val username: String,
    val password: String
)

// Representa la solicitud para registrar un nuevo usuario
data class RegisterRequestDTO(
    val username: String,
    val password: String,
)

// Representa los detalles de un usuario
data class UserResponseDetailDTO(
    val username: String,
    val password: String,
    val bibliography: String
)

// Representa una noticia en la aplicación
data class Noticia(
    val id: Int,
    val title: String,
    val image: Int,
    val content: String,
    val extraImages: List<Int> = emptyList()
)

// Representa un mensaje en el chat
data class ChatMessage(
    val id: Int, // Identificador único del mensaje
    val username: String, // Nombre del usuario que envió el mensaje
    val message: String, // Contenido del mensaje
    val timestamp: String // Marca de tiempo del mensaje
)

// Representa la solicitud para enviar un mensaje
data class SendMessageRequest(
    val username: String, // Nombre del usuario que envía el mensaje
    val message: String // Contenido del mensaje
)

// ===============================
// REPOSITORIO NOTICIAS
// ===============================

object NoticiasRepository {
    val noticias = listOf(
        Noticia(
            id = 1,
            title = "Bienvenidos a FlyHigh",
            image = R.drawable.logo,
            content = "¡Estamos emocionados de darte la bienvenida a FlyHigh!\n\n" +
                    "Prepárate para vivir una experiencia única con nuestro juego.\n" +
                    "Nuestro objetivo es ofrecerte diversión y desafíos inolvidables.\n\n" +
                    "FlyHigh es una semejanza a Flappy Bird pero que ha querido ofrecer una nueva forma más interactiva de jugarlo.\n" +
                    "Con la APP de FlyHigh podrás ver las noticias, los rankings, incluso charlar con gente.\n\n" +
                    "Además, el juego te brinda la posibilidad de poder personalizar tus partidas a tu gusto."
        ),
        Noticia(
            id = 2,
            title = "Mapas, personajes y más...",
            image = R.drawable.fondo,
            content = "Explora nuevos mapas y personajes que hemos añadido para mejorar tu experiencia de juego.\n\n" +
                    "Podrás seleccionarlos desde dentro del juego. Cada mapa está diseñado para ofrecerte retos únicos y emocionantes.\n" +
                    "¡Descúbrelos ahora!",
            extraImages = listOf(R.drawable.mosca, R.drawable.draco)
        ),
        Noticia(
            id = 3,
            title = "Rankings",
            image = R.drawable.rankings,
            content = "Compite con otros jugadores y alcanza los primeros lugares en nuestros rankings globales.\n\n" +
                    "Demuestra tus habilidades y conviértete en el mejor jugador de FlyHigh.\n" +
                    "¡La competencia nunca ha sido tan emocionante!"
        ),
        Noticia(
            id = 4,
            title = "Futuras mejoras",
            image = R.drawable.mejoras,
            content = "Estamos trabajando en nuevas características y mejoras para hacer FlyHigh aún mejor.\n\n" +
                    "Tu opinión es importante para nosotros, así que no dudes en compartir tus ideas y sugerencias."
        )
    )

    fun getNoticiaById(id: Int): Noticia? {
        return noticias.firstOrNull { it.id == id }
    }
}