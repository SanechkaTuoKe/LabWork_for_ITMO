package org.example.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import org.example.auth.UserService

@Composable
fun AuthView(
    userService: UserService,
    onAuthSuccess: () -> Unit
) {
    var isRegisterMode by remember { mutableStateOf(false) }
    var login by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf<String?>(null) }
    var successMsg by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .width(360.dp)
                .background(MaterialTheme.colorScheme.surface)
                .padding(32.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = if (isRegisterMode) "Register" else "Login",
                style = MaterialTheme.typography.titleLarge
            )

            HorizontalDivider(color = MaterialTheme.colorScheme.outline)

            OutlinedTextField(
                value = login,
                onValueChange = { login = it;
                    errorMsg = null },
                label = { Text("Login") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(0.dp)
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it;
                    errorMsg = null },
                label = { Text("Password") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(0.dp)
            )

            val msg = errorMsg
            if (msg != null) {
                Text(
                    text = msg,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            val okMsg = successMsg
            if (okMsg != null) {
                Text(
                    text = okMsg,
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Button(
                onClick = {
                    if (isRegisterMode) {
                        userService.register(login, password)
                            .onSuccess {
                                successMsg = "Registered! Now login."
                                isRegisterMode = false
                                login = ""
                                password = ""
                            }
                            .onFailure { errorMsg = it.message }
                    } else {
                        userService.login(login, password)
                            .onSuccess { onAuthSuccess() }
                            .onFailure { errorMsg = it.message }
                    }
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(0.dp),
                enabled = login.isNotBlank() && password.isNotBlank()
            ) {
                Text(if (isRegisterMode) "Register" else "Login")
            }

            TextButton(
                onClick = {
                    isRegisterMode = !isRegisterMode
                    errorMsg = null
                    successMsg = null
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    if (isRegisterMode) "Already have an account? Login"
                    else "No account? Register"
                )
            }
        }
    }
}