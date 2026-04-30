package com.makhabatusen.access_lab_app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.makhabatusen.access_lab_app.ui.util.Constants
import com.makhabatusen.access_lab_app.ui.util.isTablet

/**
 * Unified button component for consistent styling across the app.
 * Follows the logout button style as the base design.
 * 
 * Features:
 * - Consistent styling with proper accessibility
 * - Responsive design (not full width in landscape)
 * - WCAG/BITV 2.0 compliant contrast ratios
 * - Minimum 48dp touch target
 * - Proper semantic labels
 * - Loading state support
 * - Icon support
 */
@Composable
fun UnifiedButton(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    icon: ImageVector? = null,
    contentDescription: String? = null,
    buttonType: UnifiedButtonType = UnifiedButtonType.PRIMARY,
    maxWidth: Boolean = false
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE
    val isTablet = isTablet()
    
    // Responsive width handling
    val responsiveModifier = if (maxWidth) {
        modifier.fillMaxWidth()
    } else {
        // In landscape mode, don't take full width unless explicitly requested
        if (isLandscape && !isTablet) {
            modifier.widthIn(max = Constants.ContentWidth.LANDSCAPE_MAX.dp)
        } else {
            modifier
        }
    }
    
    // Button colors based on type
    val (containerColor, contentColor) = when (buttonType) {
        UnifiedButtonType.PRIMARY -> {
            MaterialTheme.colorScheme.primary to MaterialTheme.colorScheme.onPrimary
        }
        UnifiedButtonType.SECONDARY -> {
            MaterialTheme.colorScheme.secondary to MaterialTheme.colorScheme.onSecondary
        }
        UnifiedButtonType.ERROR -> {
            MaterialTheme.colorScheme.error to MaterialTheme.colorScheme.onError
        }
        UnifiedButtonType.SURFACE -> {
            MaterialTheme.colorScheme.surface to MaterialTheme.colorScheme.onSurface
        }
    }
    
    // Ensure accessibility compliance
    val semanticModifier = responsiveModifier
        .height(Constants.Heights.BUTTON_STANDARD.dp)
        .semantics {
            contentDescription?.let { desc -> 
                this.contentDescription = desc 
            }
        }
    
    Button(
        onClick = onClick,
        enabled = enabled && !loading,
        modifier = semanticModifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        shape = RoundedCornerShape(Constants.CornerRadius.LG.dp),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = Constants.Elevation.SM.dp,
            pressedElevation = Constants.Elevation.MD.dp,
            disabledElevation = 0.dp
        )
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(
                horizontal = Constants.Spacing.MD.dp,
                vertical = Constants.Spacing.SM.dp
            )
        ) {
            if (loading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(24.dp),
                    color = contentColor,
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(Constants.Spacing.SM.dp))
            } else if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = contentColor
                )
                Spacer(modifier = Modifier.width(Constants.Spacing.MD.dp))
            }
            
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = if (isTablet) 18.sp else 16.sp
                ),
                color = contentColor
            )
        }
    }
}

/**
 * Unified outlined button component for secondary actions.
 * Maintains consistent styling with the primary button.
 */
@Composable
fun UnifiedOutlinedButton(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    icon: ImageVector? = null,
    contentDescription: String? = null,
    maxWidth: Boolean = false
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE
    val isTablet = isTablet()
    
    // Responsive width handling
    val responsiveModifier = if (maxWidth) {
        modifier.fillMaxWidth()
    } else {
        if (isLandscape && !isTablet) {
            modifier.widthIn(max = Constants.ContentWidth.LANDSCAPE_MAX.dp)
        } else {
            modifier
        }
    }
    
    val semanticModifier = responsiveModifier
        .height(Constants.Heights.BUTTON_STANDARD.dp)
        .semantics {
            contentDescription?.let { desc -> 
                this.contentDescription = desc 
            }
        }
    
    androidx.compose.material3.OutlinedButton(
        onClick = onClick,
        enabled = enabled && !loading,
        modifier = semanticModifier,
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.primary,
            disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        shape = RoundedCornerShape(Constants.CornerRadius.LG.dp),
        border = androidx.compose.material3.ButtonDefaults.outlinedButtonBorder(enabled = enabled && !loading).copy(
            brush = androidx.compose.ui.graphics.Brush.linearGradient(
                colors = listOf(
                    MaterialTheme.colorScheme.primary,
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                )
            )
        )
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(
                horizontal = Constants.Spacing.MD.dp,
                vertical = Constants.Spacing.SM.dp
            )
        ) {
            if (loading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(24.dp),
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(Constants.Spacing.SM.dp))
            } else if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(Constants.Spacing.MD.dp))
            }
            
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = if (isTablet) 18.sp else 16.sp
                ),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

/**
 * Unified text button component for tertiary actions.
 * Maintains consistent styling with other button types.
 */
@Composable
fun UnifiedTextButton(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    icon: ImageVector? = null,
    contentDescription: String? = null,
    maxWidth: Boolean = false
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE
    val isTablet = isTablet()
    
    // Responsive width handling
    val responsiveModifier = if (maxWidth) {
        modifier.fillMaxWidth()
    } else {
        if (isLandscape && !isTablet) {
            modifier.widthIn(max = Constants.ContentWidth.LANDSCAPE_MAX.dp)
        } else {
            modifier
        }
    }
    
    val semanticModifier = responsiveModifier
        .height(Constants.Heights.BUTTON_STANDARD.dp)
        .semantics {
            contentDescription?.let { desc -> 
                this.contentDescription = desc 
            }
        }
    
    androidx.compose.material3.TextButton(
        onClick = onClick,
        enabled = enabled && !loading,
        modifier = semanticModifier,
        colors = ButtonDefaults.textButtonColors(
            contentColor = MaterialTheme.colorScheme.primary,
            disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        shape = RoundedCornerShape(Constants.CornerRadius.LG.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(
                horizontal = Constants.Spacing.MD.dp,
                vertical = Constants.Spacing.SM.dp
            )
        ) {
            if (loading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(24.dp),
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 2.dp
                )
                Spacer(modifier = Modifier.width(Constants.Spacing.SM.dp))
            } else if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(Constants.Spacing.MD.dp))
            }
            
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = if (isTablet) 18.sp else 16.sp
                ),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

/**
 * Button type enum for different button styles
 */
enum class UnifiedButtonType {
    PRIMARY,    // Primary actions (e.g., Start Quiz, Login)
    SECONDARY,  // Secondary actions (e.g., Register, Continue as Guest)
    ERROR,      // Destructive actions (e.g., Logout, Delete)
    SURFACE     // Surface-level actions (e.g., Back, Cancel)
} 