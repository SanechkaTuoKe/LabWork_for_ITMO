package org.example.ui.instruments

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.example.domain.Instrument
import org.example.domain.InstrumentStatus
import org.example.ui.theme.*

@Composable
fun InstrumentMasterView(
    instruments: List<Instrument>,
    selected: Instrument?,
    onSelect: (Instrument) -> Unit
) {
    Column(
        modifier = Modifier
            .width(280.dp)
            .fillMaxHeight()
            .background(ColorSurfaceVar)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(ColorPrimaryVar)
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Text(
                "Instruments (${instruments.size})",
                style = MaterialTheme.typography.titleSmall,
                color = ColorOnPrimary
            )
        }

        if (instruments.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                Text("No instruments", style = MaterialTheme.typography.bodySmall)
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(instruments) { inst ->
                    val isSelected = selected?.id == inst.id
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(if (isSelected) ColorPrimary else ColorSurface)
                            .clickable { onSelect(inst) }
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Text(
                            inst.name,
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (isSelected) ColorOnPrimary else ColorOnSurface
                        )
                        Text(
                            "${inst.type.name.replace("_", " ")}  •  ${inst.location}",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isSelected) ColorOnPrimary.copy(alpha = 0.8f) else ColorOnSurfaceVar
                        )
                        if (inst.status == InstrumentStatus.OUT_OF_SERVICE) {
                            Text(
                                "OUT OF SERVICE",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isSelected) ColorOnPrimary else ColorError
                            )
                        }
                    }
                    Divider(color = ColorDivider, thickness = 0.5.dp)
                }
            }
        }
    }
}
