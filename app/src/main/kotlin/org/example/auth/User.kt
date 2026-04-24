package org.example.auth
import java.security.MessageDigest

data class User(
    val login: String,
    val passwordHash: String  // SHA-256 хеш, не открытый пароль
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
