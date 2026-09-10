package cocm.glass.note.pr.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

@Composable
fun SwipeToActionBox(
    modifier: Modifier = Modifier,
    onSwipeLeft: (() -> Unit)? = null,
    onSwipeRight: (() -> Unit)? = null,
    leftIcon: ImageVector = Icons.Default.Delete,
    rightIcon: ImageVector = Icons.Default.Archive,
    leftColor: Color = MaterialTheme.colorScheme.error,
    rightColor: Color = MaterialTheme.colorScheme.primary,
    swipeThreshold: Float = 200f,
    content: @Composable () -> Unit
) {
    // Swipe gesture support - simplified version for now
    Box(modifier = modifier.fillMaxWidth()) {
        content()
    }
}
