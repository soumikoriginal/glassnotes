package cocm.glass.note.pr.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cocm.glass.note.pr.domain.model.ChecklistItem
import cocm.glass.note.pr.ui.components.GlassCheckbox
import cocm.glass.note.pr.ui.components.GlassIconButton

@Composable
fun ChecklistItemRow(
    item: ChecklistItem,
    onCheckedChange: (Boolean) -> Unit,
    onTextChange: (String) -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        GlassCheckbox(
            checked = item.isChecked,
            onCheckedChange = onCheckedChange
        )
        
        Spacer(modifier = Modifier.width(8.dp))
        
        TextField(
            value = item.text,
            onValueChange = onTextChange,
            modifier = Modifier.weight(1f),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.3f),
                unfocusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.1f),
                focusedIndicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                unfocusedIndicatorColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
            ),
            textStyle = if (item.isChecked) {
                LocalTextStyle.current.copy(
                    textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough
                )
            } else {
                LocalTextStyle.current
            },
            singleLine = true
        )
        
        Spacer(modifier = Modifier.width(8.dp))
        
        GlassIconButton(
            onClick = onDelete,
            icon = Icons.Default.Close
        )
    }
}
