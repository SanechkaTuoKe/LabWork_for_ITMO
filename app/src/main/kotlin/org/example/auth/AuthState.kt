package org.example.auth

sealed class AuthState {
    object Guest : AuthState()
    data class LoggedIn(val user: User) : AuthState()
}
