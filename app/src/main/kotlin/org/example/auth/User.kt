package org.example.auth
import java.security.MessageDigest

data class User(
    val login: String,
    val passwordHash: String  // SHA-256 хеш, не открытый пароль
) {
    companion object {
        fun hashPassword(password: String): String {
            val digest = MessageDigest.getInstance("SHA-256")
            val bytes = digest.digest(password.toByteArray(Charsets.UTF_8))
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
