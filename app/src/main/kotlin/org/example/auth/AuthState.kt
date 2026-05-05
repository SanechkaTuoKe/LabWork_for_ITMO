package org.example.auth

sealed class AuthState {
    object Anon : AuthState()
    data class LoggedIn(val user: User) : AuthState()
}
