package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.OlimpikYellow
import kotlinx.coroutines.launch

/**
 * 3D Tactile Game Card with multi-layer beveling, isometric specular border,
 * subtle tilt physics on press, and depth drop shadow.
 */
@Composable
fun Game3DCard(
    modifier: Modifier = Modifier,
    shapeRadius: Dp = 22.dp,
    accentGlow: Color = OlimpikYellow,
    containerColor: Color = Color(0xFF0D172A),
    enableTilt: Boolean = false,
    content: @Composable BoxScope.() -> Unit
) {
    val density = LocalDensity.current
    val tiltX = remember { Animatable(0f) }
    val tiltY = remember { Animatable(0f) }
    val cardScale = remember { Animatable(1f) }
    val coroutineScope = rememberCoroutineScope()

    val shape = RoundedCornerShape(shapeRadius)

    Box(
        modifier = modifier
            .graphicsLayer {
                cameraDistance = 16f * density.density
                rotationX = tiltX.value
                rotationY = tiltY.value
                scaleX = cardScale.value
                scaleY = cardScale.value
                shadowElevation = 18f
            }
            .pointerInput(enableTilt) {
                if (!enableTilt) return@pointerInput
                detectTapGestures(
                    onPress = { offset ->
                        val centerX = size.width / 2f
                        val centerY = size.height / 2f
                        val normX = (offset.x - centerX) / centerX
                        val normY = (offset.y - centerY) / centerY
                        coroutineScope.launch {
                            tiltX.animateTo(-normY * 4f, spring(dampingRatio = 0.6f))
                            tiltY.animateTo(normX * 4f, spring(dampingRatio = 0.6f))
                            cardScale.animateTo(0.985f, spring(dampingRatio = 0.6f))
                        }
                        tryAwaitRelease()
                        coroutineScope.launch {
                            tiltX.animateTo(0f, spring(dampingRatio = 0.5f))
                            tiltY.animateTo(0f, spring(dampingRatio = 0.5f))
                            cardScale.animateTo(1f, spring(dampingRatio = 0.5f))
                        }
                    }
                )
            }
            .clip(shape)
            .background(
                Brush.verticalGradient(
                    listOf(
                        containerColor.copy(alpha = 0.76f),
                        Color(0xFF091424).copy(alpha = 0.88f)
                    )
                )
            )
            .border(
                width = 1.2.dp,
                brush = Brush.verticalGradient(
                    listOf(
                        Color.White.copy(alpha = 0.40f),
                        Color.White.copy(alpha = 0.10f)
                    )
                ),
                shape = shape
            )
            .drawBehind {
                // Subtle top specular glass sheen
                drawRoundRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.08f),
                            Color.Transparent
                        ),
                        startY = 0f,
                        endY = size.height * 0.4f
                    ),
                    cornerRadius = CornerRadius(shapeRadius.toPx(), shapeRadius.toPx())
                )
            },
        content = content
    )
}

/**
 * Vivid candy color palettes matching arcade / mobile puzzle game aesthetics (Olcico Sudoku style).
 * Provides base top gradient, bottom gradient, extruded shadow color, and specular highlight.
 */
enum class GameColorTheme(
    val baseTop: Color,
    val baseBottom: Color,
    val shadowColor: Color,
    val highlightColor: Color,
    val contentColor: Color = Color.White
) {
    BLUE(
        baseTop = Color(0xFF38BDF8),
        baseBottom = Color(0xFF0284C7),
        shadowColor = Color(0xFF0369A1),
        highlightColor = Color(0xFFBAE6FD)
    ),
    PURPLE(
        baseTop = Color(0xFFC084FC),
        baseBottom = Color(0xFF9333EA),
        shadowColor = Color(0xFF6B21A8),
        highlightColor = Color(0xFFF3E8FF)
    ),
    ORANGE(
        baseTop = Color(0xFFFBBF24),
        baseBottom = Color(0xFFF59E0B),
        shadowColor = Color(0xFFB45309),
        highlightColor = Color(0xFFFEF3C7)
    ),
    GREEN(
        baseTop = Color(0xFF4ADE80),
        baseBottom = Color(0xFF16A34A),
        shadowColor = Color(0xFF15803D),
        highlightColor = Color(0xFFDCFCE7)
    ),
    RED(
        baseTop = Color(0xFFF87171),
        baseBottom = Color(0xFFDC2626),
        shadowColor = Color(0xFF991B1B),
        highlightColor = Color(0xFFFEE2E2)
    ),
    CYAN(
        baseTop = Color(0xFF22D3EE),
        baseBottom = Color(0xFF06B6D4),
        shadowColor = Color(0xFF0E7490),
        highlightColor = Color(0xFFCFFAFE)
    ),
    ROSE(
        baseTop = Color(0xFFF472B6),
        baseBottom = Color(0xFFDB2777),
        shadowColor = Color(0xFF9D174D),
        highlightColor = Color(0xFFFCE7F3)
    ),
    YELLOW(
        baseTop = Color(0xFFFDE047),
        baseBottom = Color(0xFFEAB308),
        shadowColor = Color(0xFFA16207),
        highlightColor = Color(0xFFFEF9C3)
    ),
    DARK(
        baseTop = Color(0xFF1E293B),
        baseBottom = Color(0xFF0F172A),
        shadowColor = Color(0xFF070E1A),
        highlightColor = Color(0xFF475569)
    )
}

/**
 * 3D Jelly Candy Pad Card matching AAC Talk Pad / Game Grid buttons (Image 1):
 * - Squircle rounded corners (20.dp - 24.dp)
 * - Corner sparkles (✦) in top-left and top-right
 * - Center white circular badge containing icon/image/emoji with soft 3D gradient
 * - Bottom label text with bold typography + right chevron `>`
 * - 3D tactile extrusion ledge and glossy curved top specular reflection
 * - Dynamic press spring animation
 */
@Composable
fun JellyCandyPadCard(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    theme: GameColorTheme = GameColorTheme.BLUE,
    height: Dp = 100.dp,
    depth: Dp = 5.dp,
    shapeRadius: Dp = 20.dp,
    showChevron: Boolean = true,
    isChecked: Boolean = false,
    testTag: String = "",
    iconContent: @Composable () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val currentOffset by animateFloatAsState(
        targetValue = if (isPressed) depth.value else 0f,
        animationSpec = spring(dampingRatio = 0.55f),
        label = "jellyPadPress"
    )

    Box(
        modifier = modifier
            .height(height + depth)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .let { if (testTag.isNotEmpty()) it.testTag(testTag) else it }
    ) {
        // Bottom 3D Shadow Layer
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .offset(y = depth)
                .clip(RoundedCornerShape(shapeRadius))
                .background(theme.shadowColor)
        )

        // Top Interactive Surface
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .offset(y = currentOffset.dp)
                .clip(RoundedCornerShape(shapeRadius))
                .background(
                    Brush.verticalGradient(
                        listOf(theme.baseTop, theme.baseBottom)
                    )
                )
                .border(
                    width = 1.5.dp,
                    brush = Brush.verticalGradient(
                        listOf(
                            Color.White.copy(alpha = 0.85f),
                            Color.White.copy(alpha = 0.2f)
                        )
                    ),
                    shape = RoundedCornerShape(shapeRadius)
                ),
            contentAlignment = Alignment.Center
        ) {
            // Glossy Specular Sheen (Curved Top Reflection)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.5f)
                    .align(Alignment.TopCenter)
                    .clip(
                        RoundedCornerShape(
                            topStart = shapeRadius,
                            topEnd = shapeRadius,
                            bottomStart = 8.dp,
                            bottomEnd = 8.dp
                        )
                    )
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.White.copy(alpha = 0.45f),
                                Color.White.copy(alpha = 0.08f),
                                Color.Transparent
                            )
                        )
                    )
            )

            // Corner Sparkles (✦) in Top-Left and Top-Right
            Text(
                text = "✦",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 8.dp, top = 6.dp)
            )
            Text(
                text = "✦",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(end = 8.dp, top = 6.dp)
            )

            // Content: White circular badge + Title & Chevron
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 6.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Center White Disc with 3D Depth
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.White, Color(0xFFF1F5F9))
                            )
                        )
                        .border(1.5.dp, Color.White.copy(alpha = 0.95f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    iconContent()
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Bottom Label + Chevron '>'
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(horizontal = 4.dp)
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            fontSize = 12.sp,
                            letterSpacing = 0.2.sp
                        ),
                        maxLines = 1
                    )
                    if (showChevron) {
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "›",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Black,
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 14.sp
                            )
                        )
                    }
                }

                if (!subtitle.isNullOrBlank()) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        maxLines = 1
                    )
                }
            }

            // Golden Checkmark Badge (✓)
            if (isChecked) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = (-4).dp, y = 4.dp)
                        .size(18.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFFFFDF00), Color(0xFFF59E0B))
                            )
                        )
                        .border(1.2.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "✓",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF0F172A),
                            fontSize = 10.sp
                        )
                    )
                }
            }
        }
    }
}

/**
 * 3D Tactile Game Candy Button with physical bottom extrusion depth, glossy specular
 * reflection on top, right-arrow chevron pill, top-right check badge, and bouncy press physics.
 */
@Composable
fun GameCandy3DButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    theme: GameColorTheme = GameColorTheme.BLUE,
    height: Dp = 54.dp,
    depth: Dp = 5.dp,
    shapeRadius: Dp = 16.dp,
    enabled: Boolean = true,
    tagPillText: String? = null,
    isChecked: Boolean = false,
    showChevron: Boolean = false,
    testTag: String = "",
    content: @Composable androidx.compose.foundation.layout.RowScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val currentOffset by animateFloatAsState(
        targetValue = if (isPressed) depth.value else 0f,
        animationSpec = spring(dampingRatio = 0.55f),
        label = "candyBtnPress"
    )

    Box(
        modifier = modifier
            .height(height + depth)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick
            )
            .let { if (testTag.isNotEmpty()) it.testTag(testTag) else it }
    ) {
        // 1. Bottom 3D Extrusion Shadow Layer
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .offset(y = depth)
                .clip(RoundedCornerShape(shapeRadius))
                .background(theme.shadowColor)
        )

        // 2. Top Interactive Cap
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .offset(y = currentOffset.dp)
                .clip(RoundedCornerShape(shapeRadius))
                .background(
                    Brush.verticalGradient(
                        listOf(theme.baseTop, theme.baseBottom)
                    )
                )
                .border(
                    width = 1.2.dp,
                    brush = Brush.verticalGradient(
                        listOf(
                            Color.White.copy(alpha = 0.75f),
                            Color.White.copy(alpha = 0.15f)
                        )
                    ),
                    shape = RoundedCornerShape(shapeRadius)
                )
        ) {
            // Glossy Specular Sheen (Curved Top Reflection)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.48f)
                    .clip(
                        RoundedCornerShape(
                            topStart = shapeRadius,
                            topEnd = shapeRadius,
                            bottomStart = 8.dp,
                            bottomEnd = 8.dp
                        )
                    )
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.White.copy(alpha = 0.42f),
                                Color.White.copy(alpha = 0.08f),
                                Color.Transparent
                            )
                        )
                    )
            )

            // Content Row
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    content()
                }

                if (showChevron) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.28f))
                            .border(0.8.dp, Color.White.copy(alpha = 0.5f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "›",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                fontSize = 16.sp
                            )
                        )
                    }
                }
            }

            // Top-Left Tag Pill (e.g. Level / Category Tag)
            if (!tagPillText.isNullOrBlank()) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(start = 8.dp, top = 4.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color.Black.copy(alpha = 0.4f))
                        .border(0.6.dp, Color.White.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 6.dp, vertical = 1.dp)
                ) {
                    Text(
                        text = tagPillText,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = 9.sp,
                            color = Color.White,
                            letterSpacing = 0.4.sp
                        )
                    )
                }
            }

            // Top-Right Golden Checkmark Badge (✓)
            if (isChecked) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = (-6).dp, y = 6.dp)
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFFFFDF00), Color(0xFFF59E0B))
                            )
                        )
                        .border(1.2.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "✓",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF0F172A),
                            fontSize = 11.sp
                        )
                    )
                }
            }
        }
    }
}

/**
 * 3D Tactile Game Candy Chip for fast filters, tabs, dates, and category buttons.
 */
@Composable
fun GameCandy3DChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    activeTheme: GameColorTheme = GameColorTheme.ORANGE,
    inactiveTheme: GameColorTheme = GameColorTheme.DARK,
    height: Dp = 44.dp,
    depth: Dp = 4.dp,
    shapeRadius: Dp = 13.dp,
    icon: (@Composable () -> Unit)? = null,
    showChevron: Boolean = false,
    isChecked: Boolean = false,
    testTag: String = ""
) {
    val theme = if (isSelected) activeTheme else inactiveTheme
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val currentOffset by animateFloatAsState(
        targetValue = if (isPressed) depth.value else 0f,
        animationSpec = spring(dampingRatio = 0.55f),
        label = "candyChipPress"
    )

    Box(
        modifier = modifier
            .height(height + depth)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .let { if (testTag.isNotEmpty()) it.testTag(testTag) else it }
    ) {
        // Bottom Shadow Layer
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .offset(y = depth)
                .clip(RoundedCornerShape(shapeRadius))
                .background(theme.shadowColor)
        )

        // Top Interactive Cap
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .offset(y = currentOffset.dp)
                .clip(RoundedCornerShape(shapeRadius))
                .background(
                    Brush.verticalGradient(
                        listOf(theme.baseTop, theme.baseBottom)
                    )
                )
                .border(
                    width = if (isSelected) 1.5.dp else 1.dp,
                    brush = Brush.verticalGradient(
                        listOf(
                            if (isSelected) Color.White.copy(alpha = 0.85f) else Color.White.copy(alpha = 0.3f),
                            Color.Transparent
                        )
                    ),
                    shape = RoundedCornerShape(shapeRadius)
                ),
            contentAlignment = Alignment.Center
        ) {
            // Glossy Specular Reflection
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.48f)
                    .align(Alignment.TopCenter)
                    .clip(
                        RoundedCornerShape(
                            topStart = shapeRadius,
                            topEnd = shapeRadius,
                            bottomStart = 6.dp,
                            bottomEnd = 6.dp
                        )
                    )
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.White.copy(alpha = if (isSelected) 0.40f else 0.15f),
                                Color.Transparent
                            )
                        )
                    )
            )

            Row(
                modifier = Modifier.padding(horizontal = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (icon != null) {
                    icon()
                    Spacer(modifier = Modifier.width(6.dp))
                }
                Text(
                    text = text,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Black,
                        color = if (isSelected) Color.White else Color(0xFFE2E8F0),
                        fontSize = 12.sp,
                        letterSpacing = 0.4.sp
                    )
                )
                if (showChevron) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.25f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "›",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                fontSize = 12.sp
                            )
                        )
                    }
                }
            }

            // Top-right Golden Checkmark
            if (isChecked) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = (-4).dp, y = 4.dp)
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFFFFDF00), Color(0xFFF59E0B))
                            )
                        )
                        .border(1.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "✓",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF0F172A),
                            fontSize = 9.sp
                        )
                    )
                }
            }
        }
    }
}

/**
 * 3D Circular Candy Button (like the top icon buttons in Olcico Sudoku: Grid, Sound, Sun, Profile).
 */
@Composable
fun GameCandyRoundButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    theme: GameColorTheme = GameColorTheme.BLUE,
    size: Dp = 44.dp,
    depth: Dp = 4.dp,
    testTag: String = "",
    content: @Composable () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val currentOffset by animateFloatAsState(
        targetValue = if (isPressed) depth.value else 0f,
        animationSpec = spring(dampingRatio = 0.55f),
        label = "candyRoundBtnPress"
    )

    Box(
        modifier = modifier
            .size(width = size, height = size + depth)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .let { if (testTag.isNotEmpty()) it.testTag(testTag) else it }
    ) {
        // Bottom Shadow Layer
        Box(
            modifier = Modifier
                .size(size)
                .offset(y = depth)
                .clip(CircleShape)
                .background(theme.shadowColor)
        )

        // Top Interactive Cap
        Box(
            modifier = Modifier
                .size(size)
                .offset(y = currentOffset.dp)
                .clip(CircleShape)
                .background(
                    Brush.verticalGradient(
                        listOf(theme.baseTop, theme.baseBottom)
                    )
                )
                .border(
                    width = 1.2.dp,
                    brush = Brush.verticalGradient(
                        listOf(
                            Color.White.copy(alpha = 0.8f),
                            Color.White.copy(alpha = 0.2f)
                        )
                    ),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            // Glossy Specular Arc
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.5f)
                    .align(Alignment.TopCenter)
                    .clip(CircleShape)
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.White.copy(alpha = 0.45f),
                                Color.Transparent
                            )
                        )
                    )
            )

            content()
        }
    }
}

/**
 * Prominent 3D Candy Pill Button (like the "▶ OYNA" button in Olcico Sudoku).
 */
@Composable
fun GamePlayPillButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    theme: GameColorTheme = GameColorTheme.CYAN,
    height: Dp = 46.dp,
    depth: Dp = 4.dp,
    icon: (@Composable () -> Unit)? = null,
    testTag: String = ""
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val currentOffset by animateFloatAsState(
        targetValue = if (isPressed) depth.value else 0f,
        animationSpec = spring(dampingRatio = 0.55f),
        label = "pillBtnPress"
    )

    Box(
        modifier = modifier
            .height(height + depth)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .let { if (testTag.isNotEmpty()) it.testTag(testTag) else it }
    ) {
        // Bottom Shadow Layer
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .offset(y = depth)
                .clip(RoundedCornerShape(30.dp))
                .background(theme.shadowColor)
        )

        // Top Interactive Cap
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .offset(y = currentOffset.dp)
                .clip(RoundedCornerShape(30.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(theme.baseTop, theme.baseBottom)
                    )
                )
                .border(
                    width = 1.5.dp,
                    brush = Brush.verticalGradient(
                        listOf(Color.White.copy(alpha = 0.85f), Color.White.copy(alpha = 0.25f))
                    ),
                    shape = RoundedCornerShape(30.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            // Glossy Specular Top Pill
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.48f)
                    .align(Alignment.TopCenter)
                    .clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.White.copy(alpha = 0.45f),
                                Color.Transparent
                            )
                        )
                    )
            )

            Row(
                modifier = Modifier.padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (icon != null) {
                    icon()
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = text,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        fontSize = 14.sp,
                        letterSpacing = 0.8.sp
                    )
                )
            }
        }
    }
}

/**
 * 3D Pushable Arcade/Game Button with physical bottom extrusion depth.
 * Compresses downwards on press with spring physics, giving a tactile console controller feel.
 */
@Composable
fun Game3DButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    buttonColor: Color = OlimpikYellow,
    shadowColor: Color = Color(0xFFC78C00),
    textColor: Color = Color(0xFF07142A),
    depth: Dp = 4.dp,
    shapeRadius: Dp = 14.dp,
    enabled: Boolean = true,
    content: @Composable () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val currentOffset by animateFloatAsState(
        targetValue = if (isPressed) depth.value else 0f,
        animationSpec = spring(dampingRatio = 0.6f),
        label = "btnDepth"
    )

    Box(
        modifier = modifier
            .height(48.dp + depth)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick
            )
    ) {
        // Bottom 3D Extrusion Shadow Layer
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .offset(y = depth)
                .clip(RoundedCornerShape(shapeRadius))
                .background(shadowColor)
        )

        // Top Interactive Surface Layer
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .offset(y = currentOffset.dp)
                .clip(RoundedCornerShape(shapeRadius))
                .background(
                    Brush.verticalGradient(
                        listOf(
                            buttonColor,
                            buttonColor.copy(alpha = 0.88f)
                        )
                    )
                )
                .border(
                    width = 1.dp,
                    brush = Brush.verticalGradient(
                        listOf(
                            Color.White.copy(alpha = 0.5f),
                            Color.Transparent
                        )
                    ),
                    shape = RoundedCornerShape(shapeRadius)
                ),
            contentAlignment = Alignment.Center
        ) {
            content()
        }
    }
}

/**
 * Isometric 3D glowing stage/podium placed underneath characters and buildings.
 * Adds ground elevation, glowing cyber grid lines, and floor reflection.
 */
@Composable
fun Game3DPodium(
    modifier: Modifier = Modifier,
    accent: Color = OlimpikYellow
) {
    val infiniteTransition = rememberInfiniteTransition(label = "podiumPulse")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.65f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1600, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "podiumGlow"
    )

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // Bottom stadium reflection glow
        drawOval(
            brush = Brush.radialGradient(
                colors = listOf(
                    accent.copy(alpha = 0.45f * pulse),
                    accent.copy(alpha = 0.15f * pulse),
                    Color.Transparent
                ),
                center = Offset(w / 2f, h * 0.7f),
                radius = w * 0.55f
            ),
            topLeft = Offset(w * 0.05f, h * 0.35f),
            size = Size(w * 0.9f, h * 0.65f)
        )

        // Lower extruded 3D base pedestal
        val pathExtrusion = Path().apply {
            moveTo(w * 0.12f, h * 0.70f)
            lineTo(w * 0.12f, h * 0.82f)
            cubicTo(w * 0.25f, h * 0.98f, w * 0.75f, h * 0.98f, w * 0.88f, h * 0.82f)
            lineTo(w * 0.88f, h * 0.70f)
            close()
        }
        drawPath(
            path = pathExtrusion,
            brush = Brush.verticalGradient(
                colors = listOf(
                    accent.copy(alpha = 0.35f),
                    Color(0xFF030712).copy(alpha = 0.9f)
                ),
                startY = h * 0.68f,
                endY = h * 0.95f
            )
        )

        // Upper isometric platform ellipse
        drawOval(
            brush = Brush.radialGradient(
                colors = listOf(
                    accent.copy(alpha = 0.3f),
                    Color(0xFF0D172A).copy(alpha = 0.85f)
                ),
                center = Offset(w / 2f, h * 0.68f),
                radius = w * 0.42f
            ),
            topLeft = Offset(w * 0.10f, h * 0.52f),
            size = Size(w * 0.80f, h * 0.34f)
        )

        // Inner glowing stadium rim
        drawOval(
            color = accent.copy(alpha = 0.8f * pulse),
            topLeft = Offset(w * 0.12f, h * 0.54f),
            size = Size(w * 0.76f, h * 0.30f),
            style = Stroke(width = 2.5.dp.toPx())
        )

        // Center holographic focal dot
        drawCircle(
            color = Color.White.copy(alpha = 0.9f * pulse),
            radius = 3.dp.toPx(),
            center = Offset(w / 2f, h * 0.68f)
        )
    }
}

/**
 * Game-style attribute bar (FIFA / NBA 2K / RPG style stat gauge)
 */
@Composable
fun GameStatBar(
    label: String,
    statValue: Int, // e.g. 88 / 100
    color: Color,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = (statValue.coerceIn(0, 100) / 100f),
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        label = "statProgress"
    )

    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFCBD5E1),
                    fontSize = 10.sp,
                    letterSpacing = 0.8.sp
                )
            )
            Text(
                text = "$statValue",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Black,
                    color = color,
                    fontSize = 11.sp
                )
            )
        }
        Spacer(modifier = Modifier.height(3.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(CircleShape)
                .background(Color(0xFF1E293B))
                .border(0.5.dp, Color.White.copy(alpha = 0.15f), CircleShape)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedProgress)
                    .fillMaxHeight()
                    .clip(CircleShape)
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                color.copy(alpha = 0.7f),
                                color,
                                Color.White
                            )
                        )
                    )
            )
        }
    }
}

/**
 * 3D Angled Cyber Chip Badge
 */
@Composable
fun GameHudChip(
    text: String,
    color: Color = OlimpikYellow,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(
                Brush.linearGradient(
                    listOf(
                        color.copy(alpha = 0.25f),
                        Color(0xFF0F172A).copy(alpha = 0.7f)
                    )
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.horizontalGradient(
                    listOf(
                        color,
                        Color.White.copy(alpha = 0.6f)
                    )
                ),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 8.dp, vertical = 3.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Black,
                color = color,
                fontSize = 10.sp,
                letterSpacing = 0.5.sp
            )
        )
    }
}
