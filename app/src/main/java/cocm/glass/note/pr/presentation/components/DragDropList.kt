package cocm.glass.note.pr.presentation.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex

@Composable
fun <T> DraggableList(
    items: List<T>,
    onMove: (Int, Int) -> Unit,
    modifier: Modifier = Modifier,
    itemContent: @Composable (item: T, isDragging: Boolean) -> Unit
) {
    var draggedIndex by remember { mutableStateOf<Int?>(null) }
    var targetIndex by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(draggedIndex) {
        if (draggedIndex == null && targetIndex != null) {
            targetIndex = null
        }
    }

    Column(modifier = modifier) {
        items.forEachIndexed { index, item ->
            val isDragging = draggedIndex == index
            val elevation by animateDpAsState(
                targetValue = if (isDragging) 8.dp else 0.dp,
                label = "elevation"
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .zIndex(if (isDragging) 1f else 0f)
                    .shadow(elevation)
                    .pointerInput(Unit) {
                        detectDragGesturesAfterLongPress(
                            onDragStart = { draggedIndex = index },
                            onDragEnd = {
                                if (draggedIndex != null && targetIndex != null && draggedIndex != targetIndex) {
                                    onMove(draggedIndex!!, targetIndex!!)
                                }
                                draggedIndex = null
                                targetIndex = null
                            },
                            onDragCancel = {
                                draggedIndex = null
                                targetIndex = null
                            },
                            onDrag = { _, _ ->
                                // Calculate target index based on drag position
                                // This is a simplified version
                            }
                        )
                    }
            ) {
                itemContent(item, isDragging)
            }
        }
    }
}
