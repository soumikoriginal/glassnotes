package cocm.glass.note.pr.presentation.onboarding

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cocm.glass.note.pr.ui.components.GlassButton

data class OnboardingPage(
    val title: String,
    val description: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

@Composable
fun OnboardingScreen(
    onFinish: () -> Unit
) {
    val pages = listOf(
        OnboardingPage(
            title = "Your notes. Completely private.",
            description = "Everything stays on your device. No account needed.",
            icon = Icons.Default.Lock
        ),
        OnboardingPage(
            title = "Create & Organize",
            description = "Text notes, checklists, images, and drawings. All in one place.",
            icon = Icons.Default.Edit
        ),
        OnboardingPage(
            title = "Never forget",
            description = "Set reminders and organize with labels and colors.",
            icon = Icons.Default.Notifications
        ),
        OnboardingPage(
            title = "Backup locally",
            description = "Export and import your notes whenever you want. Your data, your control.",
            icon = Icons.Default.Backup
        )
    )

    var currentPage by remember { mutableIntStateOf(0) }
    val isLastPage = currentPage == pages.size - 1

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Skip button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                GlassButton(
                    text = "Skip",
                    onClick = onFinish,
                    modifier = Modifier
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Content
            OnboardingPageContent(
                page = pages[currentPage],
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Page indicator
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(16.dp)
            ) {
                repeat(pages.size) { index ->
                    val width by animateDpAsState(
                        targetValue = if (currentPage == index) 24.dp else 8.dp,
                        label = "indicator_width"
                    )
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .height(8.dp)
                            .width(width)
                            .clip(CircleShape)
                            .background(
                                if (currentPage == index)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action button
            GlassButton(
                text = if (isLastPage) "Start Using Glass Notes" else "Next",
                onClick = {
                    if (isLastPage) {
                        onFinish()
                    } else {
                        currentPage++
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            )
        }
    }
}

@Composable
fun OnboardingPageContent(
    page: OnboardingPage,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = page.icon,
            contentDescription = null,
            modifier = Modifier.size(120.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = page.title,
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = page.description,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
        )
    }
}
