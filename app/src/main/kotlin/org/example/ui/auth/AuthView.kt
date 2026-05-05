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
import org.example.ui.theme.*

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
        modifier = Modifier.fillMaxSize().background(ColorBackground),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .width(360.dp)
                .background(ColorSurface)
                .padding(32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = if (isRegisterMode) "Register" else "Login",
                style = MaterialTheme.typography.titleLarge
            )

            Divider(color = ColorDivider)

            OutlinedTextField(
                value = login,
                onValueChange = { login = it; errorMsg = null },
                label = { Text("Login") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(0.dp)
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it; errorMsg = null },
                label = { Text("Password") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(0.dp)
            )

            if (errorMsg != null) {
                Text(errorMsg!!, color = ColorError, style = MaterialTheme.typography.bodySmall)
            }
            if (successMsg != null) {
                Text(successMsg!!, color = ColorPrimary, style = MaterialTheme.typography.bodySmall)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        if (isRegisterMode) {
                            userService.register(login, password)
                                .onSuccess {
                                    successMsg = "Registered! Now login."
                                    isRegisterMode = false
                                    login = ""; password = ""
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
            }

            TextButton(
                onClick = { isRegisterMode = !isRegisterMode; errorMsg = null; successMsg = null },
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
