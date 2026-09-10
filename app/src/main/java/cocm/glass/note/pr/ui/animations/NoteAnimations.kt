package cocm.glass.note.pr.ui.animations

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.IntOffset

val SpringSpec = spring<Float>(
    dampingRatio = Spring.DampingRatioMediumBouncy,
    stiffness = Spring.StiffnessLow
)

val SpringSpecIntOffset = spring<IntOffset>(
    dampingRatio = Spring.DampingRatioMediumBouncy,
    stiffness = Spring.StiffnessLow
)

@Composable
fun AnimatedScaleInOut(
    visible: Boolean,
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = scaleIn(animationSpec = SpringSpec) + fadeIn(),
        exit = scaleOut(animationSpec = SpringSpec) + fadeOut(),
        content = content
    )
}

@Composable
fun AnimatedSlideInOut(
    visible: Boolean,
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(
            initialOffsetY = { it },
            animationSpec = tween(300)
        ) + fadeIn(),
        exit = slideOutVertically(
            targetOffsetY = { it },
            animationSpec = tween(300)
        ) + fadeOut(),
        content = content
    )
}

fun <T> createNoteTransitionSpec(): FiniteAnimationSpec<T> {
    return tween(
        durationMillis = 300,
        easing = FastOutSlowInEasing
    )
}

val fabAnimation = tween<Float>(
    durationMillis = 200,
    easing = FastOutSlowInEasing
)
