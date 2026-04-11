package org.example.ui.instruments

import androidx.compose.foundation.clickable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import org.example.domain.Instrument


@Composable
fun InstrumentMasterView(
    instruments: List<Instrument>,
    selected: Instrument?,
    onSelect: (Instrument) -> Unit
) {
    LazyColumn {
        items(instruments) { inst ->
            val isSelected = selected != null && selected.id == inst.id
            Card(
                modifier = Modifier.clickable { onSelect(inst) }
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Text("ID: ${inst.id}")
                    Text("Name: ${inst.name}")
                    Text("Type: ${inst.type}")
                    Text("Location: ${inst.location}")
                    if (isSelected) {
                        Text("SELECTED", color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }
}