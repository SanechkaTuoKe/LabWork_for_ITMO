package org.example.auth

import java.sql.Connection
import java.sql.SQLException

class UserService(private val connection: Connection) {
    private val users = mutableMapOf<String, User>()
    var currentUser: User? = null
        private set

    val isLoggedIn: Boolean
        get() = currentUser != null
    val currentUsername: String?
        get() = currentUser?.login

    init {
        loadUsers()
    }

    fun getAllUsers(): List<String> {
        val users = mutableListOf<String>()
        connection.createStatement().use { stmt ->
            stmt.executeQuery("SELECT login FROM users ORDER BY login").use { rs ->
                while (rs.next()) {
                    users.add(rs.getString("login"))
                }
            }
        }
        return users
    }
    // я не очень понимаю смысл save/load из предыдущих этапов когда уже есть база данных,
    // поэтому мне кажется логичным добавить загрузку по пользователю и вообще убрать сохранение
    // т.к. все равно все инструменты и калибровки сохраняются на сервере

    fun register(login: String, password: String): Result<User> {
        if (login.isBlank()) return Result.failure(IllegalArgumentException("Login cannot be empty"))
        if (password.isBlank()) return Result.failure(IllegalArgumentException("Password cannot be empty"))
        if (users.containsKey(login)) return Result.failure(IllegalArgumentException("Login '$login' is already taken"))

        val user = User.create(login, password)

        return try {
            connection.prepareStatement(
                "INSERT INTO users (login, password_hash) VALUES (?, ?)"
            ).use { ps ->
                ps.setString(1, user.login)
                ps.setString(2, user.passwordHash)
                ps.executeUpdate()
            }
            users[login] = user
            Result.success(user)
        } catch (e: SQLException) {
            if (e.sqlState == "23505") {
                Result.failure(IllegalArgumentException("Login '$login' is already taken"))
            } else {
                Result.failure(Exception("Database error: ${e.message}"))
            }
        }
    }

    fun login(login: String, password: String): Result<User> {
        val user = users[login]
            ?: return Result.failure(IllegalArgumentException("User '$login' not found"))
        if (!user.checkPassword(password))
            return Result.failure(IllegalArgumentException("Incorrect password"))
        currentUser = user
        return Result.success(user)
    }

    fun logout() {
        currentUser = null
    }

    private fun loadUsers() {
        try {
            connection.createStatement().use { stmt ->
                stmt.executeQuery("SELECT login, password_hash FROM users").use { rs ->
                    while (rs.next()) {
                        val user = User(
                            login = rs.getString("login"),
                            passwordHash = rs.getString("password_hash")
                        )
                        users[user.login] = user
                    }
                }
            }
        } catch (e: SQLException) {
            System.err.println("Warning: Cannot load users from database: ${e.message}")
        }
    }
}