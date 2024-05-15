package modelos

// Creo una clase para almacenar las rutas a las pantallas de mi aplicación.
sealed class Paths (val path: String){
    object pantallaPrincipal : Paths("pantalla_principal")
    object login : Paths("login")
    object signUp : Paths("signup")
}
