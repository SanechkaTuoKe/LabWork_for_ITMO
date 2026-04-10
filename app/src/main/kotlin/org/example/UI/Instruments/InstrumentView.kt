package org.example.ui.instruments

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.example.domain.Instrument
import org.example.domain.InstrumentType

@Composable
fun InstrumentView(controller: InstrumentController) {

    val instruments: List<Instrument> = controller.instruments.value
    val selected: Instrument? = controller.selected.value
    val error: String? = controller.error.value

    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        controller.load()
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

        Row {
            Button(
                onClick = fun() {
                    showDialog = true
                }
            ) {
                Text("Add")
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (error != null) {
            Text("Error: " + error)
        }

        Row(modifier = Modifier.fillMaxSize()) {

            // MASTER
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(instruments) { inst ->

                    TextButton(
                        onClick = fun() {
                            controller.select(inst)
                        }
                    ) {
                        Text(inst.id.toString() + " | " + inst.name)
                    }
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // DETAIL
            Column(modifier = Modifier.weight(1f)) {

                if (selected == null) {
                    Text("No instrument selected")
                } else {
                    Text("ID: " + selected.id)
                    Text("Name: " + selected.name)
                    Text("Type: " + selected.type)
                    Text("Location: " + selected.location)

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = fun() {
                            controller.delete(selected.id)
                        }
                    ) {
                        Text("Delete")
                    }
                }
            }
        }
    }

    if (showDialog) {

        var name by remember { mutableStateOf("") }
        var location by remember { mutableStateOf("") }
        var inventory by remember { mutableStateOf("") }
        var selectedType by remember { mutableStateOf(InstrumentType.values()[0]) }
        var expanded by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = fun() {
                showDialog = false
            },
            title = {
                Text("Add Instrument")
            },
            text = {
                Column {

                    OutlinedTextField(
                        value = name,
                        onValueChange = fun(newValue: String) {
                            name = newValue
                        },
                        label = { Text("Name") }
                    )

                    OutlinedTextField(
                        value = location,
                        onValueChange = fun(newValue: String) {
                            location = newValue
                        },
                        label = { Text("Location") }
                    )

                    OutlinedTextField(
                        value = inventory,
                        onValueChange = fun(newValue: String) {
                            inventory = newValue
                        },
                        label = { Text("Inventory") }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Box {
                        Button(
                            onClick = fun() {
                                expanded = true
                            }
                        ) {
                            Text(selectedType.name)
                        }

                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = fun() {
                                expanded = false
                            }
                        ) {
                            val types = InstrumentType.values()

                            for (i in types.indices) {
                                val type = types[i]

                                DropdownMenuItem(
                                    text = {
                                        Text(type.name)
                                    },
                                    onClick = fun() {
                                        selectedType = type
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            },

            confirmButton = {
                Button(
                    onClick = fun() {
                        var inv: String? = null

                        if (inventory.isNotBlank()) {
                            inv = inventory
                        }

                        controller.add(
                            name,
                            selectedType,
                            inv,
                            location
                        )

                        showDialog = false
                    }
                ) {
                    Text("OK")
                }
            },

            dismissButton = {
                TextButton(
                    onClick = fun() {
                        showDialog = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}