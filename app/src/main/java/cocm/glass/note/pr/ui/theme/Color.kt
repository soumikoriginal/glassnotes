package cocm.glass.note.pr.ui.theme

import androidx.compose.ui.graphics.Color
import cocm.glass.note.pr.domain.model.NoteColor

// Glass Surface Colors
val GlassWhite = Color(0xFFFFFFFF)
val GlassBlack = Color(0xFF000000)
val GlassSurface = Color(0x1AFFFFFF)
val GlassSurfaceDark = Color(0x1A000000)

// Note Colors - Soft translucent tints
val NoteColorDefault = Color(0x00FFFFFF)
val NoteColorRed = Color(0x33FF5252)
val NoteColorOrange = Color(0x33FF9800)
val NoteColorYellow = Color(0x33FFEB3B)
val NoteColorGreen = Color(0x334CAF50)
val NoteColorBlue = Color(0x332196F3)
val NoteColorPurple = Color(0x339C27B0)
val NoteColorPink = Color(0x33E91E63)
val NoteColorGray = Color(0x33757575)

// System Colors
val PrimaryLight = Color(0xFF2196F3)
val PrimaryDark = Color(0xFF64B5F6)
val BackgroundLight = Color(0xFFF5F5F5)
val BackgroundDark = Color(0xFF121212)
val SurfaceLight = Color(0xFFFFFFFF)
val SurfaceDark = Color(0xFF1E1E1E)

// Glass Border
val GlassBorderLight = Color(0x1A000000)
val GlassBorderDark = Color(0x1AFFFFFF)

// Text Colors
val TextPrimaryLight = Color(0xDE000000)
val TextSecondaryLight = Color(0x99000000)
val TextPrimaryDark = Color(0xFFFFFFFF)
val TextSecondaryDark = Color(0xB3FFFFFF)

// Helper function to get note color
fun getNoteColor(noteColor: NoteColor): Color {
    return when (noteColor) {
        NoteColor.DEFAULT -> NoteColorDefault
        NoteColor.SOFT_WHITE -> NoteColorDefault
        NoteColor.SOFT_GRAY -> NoteColorGray
        NoteColor.RED -> NoteColorRed
        NoteColor.ORANGE -> NoteColorOrange
        NoteColor.YELLOW -> NoteColorYellow
        NoteColor.GREEN -> NoteColorGreen
        NoteColor.BLUE -> NoteColorBlue
        NoteColor.PURPLE -> NoteColorPurple
        NoteColor.PINK -> NoteColorPink
    }
}
