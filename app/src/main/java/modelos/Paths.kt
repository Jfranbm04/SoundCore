package modelos

// Creo una clase para almacenar las rutas a las pantallas de mi aplicación.
sealed class Paths (val path: String){
    object login : Paths("login")
    object signUp : Paths("signup")
    object pantallaPrincipal : Paths("pantalla_principal")
    object Home : Paths("home")
    object Search : Paths("buscar")
    object Profile : Paths("perfil")
    object Ajustes : Paths("ajustes")
    object EditarPerfil : Paths("editarPerfil")
    object Solicitudes : Paths("solicitudes")
    object ListaAmigos : Paths("listaAmigos")
    object RankingLocal : Paths("rankingLocal")
    object RankingAmigos : Paths("rankingAmigos")
    object RankingGlobal : Paths("rankingGlobal")
    object PerfilUsuario : Paths("perfilUsuario")
}
