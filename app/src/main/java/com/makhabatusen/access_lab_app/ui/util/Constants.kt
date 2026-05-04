package com.makhabatusen.access_lab_app.ui.util

import androidx.compose.ui.unit.dp

/**
 * Constants for the Access Quiz App
 * Centralized configuration values for better maintainability
 * Follows Material Design 3 guidelines and accessibility best practices
 */
object Constants {
    
    // Gesture and Interaction
    object Gesture {
        const val SWIPE_THRESHOLD_DP = 100
        val SWIPE_THRESHOLD = SWIPE_THRESHOLD_DP.dp
    }
    

    // Quiz Configuration
    object Quiz {
        const val MIN_ANSWERS_REQUIRED = 1
    }

    
    // Responsive Breakpoints (in dp)
    object Breakpoints {
        const val PHONE_MAX_WIDTH = 600
        const val TABLET_MIN_WIDTH = 720  // Increased to avoid phones in landscape being detected as tablets
        const val LARGE_TABLET_MIN_WIDTH = 840
        const val DESKTOP_MIN_WIDTH = 1024
    }
    
    // Content Width Limits (in dp)
    object ContentWidth {
        const val PHONE_MAX = 400
        const val LANDSCAPE_MAX = 600
        const val TABLET_MAX = 800
        const val LARGE_TABLET_MAX = 1000
        const val QUIZ_LANDSCAPE_MAX = 700
        const val QUIZ_TABLET_MAX = 900
        const val QUIZ_LARGE_TABLET_MAX = 1200
    }
    
    // Enhanced Padding and Spacing System (Material Design 3 + Accessibility)
    object Spacing {
        // Base spacing values (in dp)
        const val XS = 4
        const val SM = 8
        const val MD = 12
        const val LG = 16
        const val XL = 24
        const val XXL = 32
        const val XXXL = 40
const val XXXXL = 48
const val XXXXXL = 56
        
        // Semantic spacing names for better readability
        object Screen {
            val horizontal = LG.dp  // Default horizontal screen padding (Material 3)
            val vertical = XL.dp    // Default vertical screen padding
        }
        
        object Content {
            val betweenElements = MD.dp  // Spacing between content elements
            val section = XL.dp          // Spacing between sections
            val card = LG.dp             // Card internal padding
        }
        
        object Interactive {
            val buttonPadding = MD.dp    // Button internal padding
            val touchTarget = XXXXL.dp   // Minimum touch target (48dp for accessibility)
            val betweenButtons = SM.dp   // Spacing between buttons
        }
        
        object List {
            val itemSpacing = SM.dp      // Spacing between list items
            val itemPadding = MD.dp      // List item internal padding
            val sectionHeader = LG.dp    // Spacing above section headers
        }
        
        object Form {
            val fieldSpacing = MD.dp     // Spacing between form fields
            val fieldPadding = SM.dp     // Form field internal padding
            val groupSpacing = LG.dp     // Spacing between form groups
        }
        
        object Navigation {
            val topBarPadding = MD.dp    // Top bar content padding
            val bottomBarPadding = SM.dp // Bottom bar content padding
            val drawerPadding = LG.dp    // Drawer content padding
        }
        
        object Media {
            val playerPadding = LG.dp    // Video player padding
            val controlsPadding = SM.dp  // Media controls padding
            val thumbnailSpacing = MD.dp // Thumbnail grid spacing
        }
        
        object Quiz {
            val questionSpacing = LG.dp  // Spacing between questions
            val answerSpacing = MD.dp    // Spacing between answers
            val resultSpacing = XL.dp    // Spacing in results
        }
    }
    
    // Component Heights (in dp) - Accessibility compliant
    object Heights {
        const val BUTTON_STANDARD = 56
        const val BUTTON_QUIZ = 56  // Updated to meet accessibility standards
        const val BUTTON_LARGE = 72
        const val BUTTON_XLARGE = 80
        const val PROGRESS_INDICATOR = 8
        const val QUIZ_CONTAINER = 300
        const val ICON_BUTTON_MIN = 48  // Minimum touch target for icon buttons
    }
    
    // Corner Radius (in dp)
    object CornerRadius {
        const val SM = 4
        const val MD = 8
        const val LG = 12
        const val XL = 16
    }
    
    // Elevation (in dp)
    object Elevation {
        const val SM = 2
        const val MD = 4
        const val LG = 6
        const val XL = 8
    }
} 