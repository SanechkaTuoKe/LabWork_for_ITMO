package org.example.auth

import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class UserService(
    private val usersFilePath: Path = Paths.get("users.csv")
) {
    // Логин
    private val users = mutableMapOf<String, User>()

    // Текущий авторизованный пользователь сессии
    var currentUser: User? = null
        private set

    init {
        loadUsers()
    }

    val isLoggedIn: Boolean get() = currentUser != null
    val currentUsername: String get() = currentUser?.login ?: "SYSTEM"

    fun register(login: String, password: String): Result<User> {
        if (login.isBlank()) return Result.failure(IllegalArgumentException("Login cannot be empty"))
        if (password.isBlank()) return Result.failure(IllegalArgumentException("Password cannot be empty"))
        if (login.contains(',')) return Result.failure(IllegalArgumentException("Login cannot contain commas"))
        if (users.containsKey(login)) return Result.failure(IllegalArgumentException("Login '$login' is already taken"))

        val user = User.create(login, password)
        users[login] = user
        saveUsers()
        return Result.success(user)
    }

    fun login(login: String, password: String): Result<User> {
        val user = users[login]
            ?: return Result.failure(IllegalArgumentException("User '$login' not found"))
        if (!user.checkPassword(password)) {
            return Result.failure(IllegalArgumentException("Incorrect password"))
        }
        currentUser = user
        return Result.success(user)
    }

    fun logout() {
        currentUser = null
    }

    private fun saveUsers() {
        usersFilePath.parent?.let { Files.createDirectories(it) }
        Files.newBufferedWriter(usersFilePath, StandardCharsets.UTF_8).use { writer ->
            writer.write("login,passwordHash")
            writer.newLine()
            users.values.forEach { user ->
                writer.write("${user.login},${user.passwordHash}")
                writer.newLine()
            }
        }
    }

    private fun loadUsers() {
        if (!Files.exists(usersFilePath)) return
        val lines = Files.readAllLines(usersFilePath, StandardCharsets.UTF_8)
        if (lines.size < 2) return
        lines.drop(1).filter { it.isNotBlank() }.forEach { line ->
            val parts = line.split(",")
            if (parts.size >= 2) {
                val user = User(login = parts[0].trim(), passwordHash = parts[1].trim())
                users[user.login] = user
            }
        }
    }
}
