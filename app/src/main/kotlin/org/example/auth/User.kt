package org.example.auth
import java.security.MessageDigest

data class User(
    val login: String,
    val passwordHash: String
) {
    companion object {
        fun hashPassword(password: String): String {
            val md = MessageDigest.getInstance("SHA-256")
            val input = password.toByteArray(Charsets.UTF_8)
            val bytes = md.digest(input)
            return bytes.joinToString("") { "%02x".format(it) }
        }

        fun create(login: String, password: String): User {
            return User(login = login, passwordHash = hashPassword(password))
        }
    }

    fun checkPassword(password: String): Boolean {
        return passwordHash == hashPassword(password)
    }
}
//import java.security.MessageDigest
//import java.util.Base64
//fun main() {
//        val md = MessageDigest.getInstance("SHA-256")
//        val input = "test".toByteArray(Charsets.UTF_8)
//        val bytes = md.digest(input)
//        println(Base64.getUrlEncoder().encodeToString(bytes))
//} - брала отсюда https://stackoverflow.com/questions/61465494/sha256-different-hash-in-nodejs-and-kotlin