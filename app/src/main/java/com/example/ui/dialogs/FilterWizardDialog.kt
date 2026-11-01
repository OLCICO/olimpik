package com.example.ui.dialogs

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import androidx.compose.ui.window.DialogProperties
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import com.example.R
import com.example.model.BranchInfo
import com.example.model.HallInfo
import com.example.model.ParentFilterPreferences
import com.example.ui.components.Game3DButton
import com.example.ui.components.Game3DPodium
import com.example.ui.components.GameHudChip
import com.example.ui.components.GameStatBar
import com.example.ui.theme.BasketballOrange
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberGold
import com.example.ui.theme.CyberGoldDark
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.OlimpikYellow
import com.example.ui.theme.VolleyballBlue
import kotlin.math.PI
import kotlin.math.absoluteValue
import kotlin.math.sin
import kotlinx.coroutines.launch

/**
 * 3D Gaming Carousel Item
 */
/**
 * Harici ses dosyası gerektirmeyen, kodla üretilen kısa ses efektleri.
 * swipe(): kaydırma "vuuş" sesi, select(): seçim "ding" sesi.
 */
private object WizardSfx {
    private const val RATE = 22050

    private fun whoosh(): ShortArray {
        val n = RATE * 150 / 1000
        var phase = 0.0
        val rnd = java.util.Random(7)
        return ShortArray(n) { i ->
            val t = i / n.toDouble()
            val freq = 280.0 + 1100.0 * t * t
            phase += 2 * PI * freq / RATE
            val env = sin(PI * t).let { it * it }
            val v = (sin(phase) * 0.45 + (rnd.nextDouble() - 0.5) * 0.35) * env
            (v * Short.MAX_VALUE * 0.6).toInt().toShort()
        }
    }

    private fun ding(): ShortArray {
        val n = RATE * 220 / 1000
        return ShortArray(n) { i ->
            val t = i / n.toDouble()
            val env = (1.0 - t).let { it * it * it }
            val tt = i / RATE.toDouble()
            val v = (sin(2 * PI * 880.0 * tt) * 0.6 + sin(2 * PI * 1320.0 * tt) * 0.3) * env
            (v * Short.MAX_VALUE * 0.6).toInt().toShort()
        }
    }

    private fun play(samples: ShortArray) {
        try {
            val track = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(RATE)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(samples.size * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()
            track.write(samples, 0, samples.size)
            track.setNotificationMarkerPosition(samples.size - 1)
            track.setPlaybackPositionUpdateListener(object : AudioTrack.OnPlaybackPositionUpdateListener {
                override fun onMarkerReached(t: AudioTrack?) { t?.release() }
                override fun onPeriodicNotification(t: AudioTrack?) {}
            })
            track.play()
        } catch (_: Exception) {
            // Ses çalınamazsa sessizce devam et
        }
    }

    private val whooshData by lazy { whoosh() }
    private val dingData by lazy { ding() }

    fun swipe() = play(whooshData)
    fun select() = play(dingData)
}

private data class WizardItem(
    val value: String,
    val title: String,
    val subtitle: String,
    val accent: Color,
    val ovrRating: Int = 92,
    val speedStat: Int = 88,
    val powerStat: Int = 90,
    val widthFraction: Float = 0.66f,
    val aspect: Float = 1f,
    val key: String = value,
    val visual: @Composable () -> Unit
)

@Composable
fun FilterWizardDialog(
    currentFilters: ParentFilterPreferences,
    halls: List<HallInfo>,
    branches: List<BranchInfo>,
    onDismiss: () -> Unit,
    onApplyFilters: (ParentFilterPreferences) -> Unit
) {
    var step by remember { mutableStateOf(1) } // 1: Branş, 2: Cinsiyet/Karakter, 3: Salon, 4: Yaş grubu
    var selectedBranch by remember { mutableStateOf(currentFilters.branch) }
    var selectedHall by remember { mutableStateOf(currentFilters.hall) }
    var selectedGender by remember { mutableStateOf(currentFilters.gender) }
    var selectedAge by remember { mutableStateOf(currentFilters.ageGroup) }

    // ---- Adım 1: Branş ----
    val branchItems = buildList<WizardItem> {
        add(
            WizardItem(
                value = "Tümü",
                title = "Tüm Branşlar",
                subtitle = "Basketbol ve Voleybol Arenası",
                accent = NeonPurple,
                ovrRating = 99,
                speedStat = 95,
                powerStat = 98,
                widthFraction = 0.72f,
                aspect = 1.144f
            ) {
                Image(
                    painter = painterResource(R.drawable.olimpik_logo_main),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize()
                )
            }
        )
        branches.forEach { b ->
            val isBasket = b.name.contains("Basket", true)
            val isVolley = b.name.contains("Voley", true)
            add(
                WizardItem(
                    value = b.name,
                    title = b.name,
                    subtitle = when {
                        isBasket -> "Parke Saha & Potanın Yıldızları"
                        isVolley -> "File Sahaları & Smaç Takımı"
                        else -> b.description
                    },
                    accent = when {
                        isBasket -> BasketballOrange
                        isVolley -> CyberCyan
                        else -> OlimpikYellow
                    },
                    ovrRating = if (isBasket) 94 else 92,
                    speedStat = if (isBasket) 91 else 89,
                    powerStat = if (isBasket) 93 else 90,
                    widthFraction = if (isBasket || isVolley) 0.85f else 0.66f,
                    aspect = if (isBasket || isVolley) 0.675f else 1f
                ) {
                    Image(
                        painter = painterResource(
                            when {
                                isBasket -> R.drawable.branch_basket
                                isVolley -> R.drawable.branch_volley
                                else -> R.drawable.ic_olimpik_logo
                            }
                        ),
                        contentDescription = b.name,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            )
        }
    }

    // ---- Adım 2: Cinsiyet (branşa göre karusel) ----
    val genderIsBasket = selectedBranch.contains("Basket", true)
    val genderIsVolley = selectedBranch.contains("Voley", true)
    val basketAccent = BasketballOrange
    val volleyAccent = CyberCyan
    fun genderItem(
        key: String, value: String, title: String, subtitle: String,
        res: Int, accent: Color, ovr: Int, speed: Int, power: Int, aspect: Float
    ) = WizardItem(
        value = value,
        title = title,
        subtitle = subtitle,
        accent = accent,
        ovrRating = ovr,
        speedStat = speed,
        powerStat = power,
        widthFraction = 0.85f,
        aspect = aspect,
        key = key
    ) {
        Image(
            painter = painterResource(res),
            contentDescription = title,
            contentScale = ContentScale.Fit,
            modifier = Modifier.fillMaxSize()
        )
    }
    val basketGirl = genderItem("basket_kiz", "Kız", "Kız Basketbolcu", "Oyun Kurucu / Skorer", R.drawable.gender_basket_girl, basketAccent, 94, 92, 90, 0.819f)
    val basketBoy = genderItem("basket_erkek", "Erkek", "Erkek Basketbolcu", "Forvet / Pivot", R.drawable.gender_basket_boy, basketAccent, 95, 91, 93, 0.819f)
    val volleyGirl = genderItem("volley_kiz", "Kız", "Kız Voleybolcu", "Pasör Çaprazı / Smaçör", R.drawable.gender_volley_girl, volleyAccent, 93, 89, 90, 0.819f)
    val volleyBoy = genderItem("volley_erkek", "Erkek", "Erkek Voleybolcu", "Orta Oyuncu / Libero", R.drawable.gender_volley_boy, volleyAccent, 94, 90, 92, 0.819f)
    val genderItems = when {
        genderIsBasket -> listOf(basketBoy, basketGirl)
        genderIsVolley -> listOf(volleyBoy, volleyGirl)
        else -> listOf(basketBoy, basketGirl, volleyBoy, volleyGirl)
    }

    // ---- Adım 3: Salon ----
    val hallItems = buildList<WizardItem> {
        add(
            WizardItem(
                value = "Tümü",
                title = "Tüm Salonlar",
                subtitle = "5 Dev Spor Kompleksi",
                accent = NeonPurple,
                ovrRating = 99,
                speedStat = 100,
                powerStat = 100
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_olimpik_logo),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize()
                )
            }
        )
        halls.forEach { hall ->
            val hallRes = when {
                hall.district.contains("Pendik", true) -> R.drawable.pendik
                hall.district.contains("Şeker", true) || hall.district.contains("Seker", true) -> R.drawable.sekerpinar
                hall.district.contains("Çay", true) || hall.district.contains("Cay", true) -> R.drawable.cayirova
                hall.district.contains("Gebze", true) -> R.drawable.gebze
                hall.district.contains("Mutlu", true) -> R.drawable.mutlukent
                else -> R.drawable.ic_olimpik_logo
            }
            val isBuilding = hallRes != R.drawable.ic_olimpik_logo
            // Tüm salon görselleri cam çerçeveli ve daha kare (960x716)
            val isFramed = isBuilding
            add(
                WizardItem(
                    value = hall.district,
                    title = hall.district,
                    subtitle = hall.name,
                    accent = CyberCyan,
                    ovrRating = 91,
                    speedStat = 87,
                    powerStat = 94,
                    widthFraction = if (isBuilding) 0.82f else 0.66f,
                    aspect = if (isFramed) 1.341f else 1f
                ) {
                    Image(
                        painter = painterResource(hallRes),
                        contentDescription = hall.name,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            )
        }
    }

    // ---- Adım 4: Yaş grubu ----
    val ageItems = listOf(
        WizardItem("Tümü", "Tüm Yaş Grupları", "Tüm Kadrolar", NeonPurple, ovrRating = 99) { IconBadge(Icons.Default.Groups, NeonPurple) },
        WizardItem("U10", "U10 Minikler", "Geleceğin Şampiyonları", Color(0xFF34D399), ovrRating = 85, speedStat = 92, powerStat = 80) { AgeBadge("U10", Color(0xFF34D399)) },
        WizardItem("U12", "U12 Küçükler", "Yükselen Yetenekler", Color(0xFF38BDF8), ovrRating = 89, speedStat = 90, powerStat = 86) { AgeBadge("U12", Color(0xFF38BDF8)) },
        WizardItem("U14", "U14 Yıldız Adayı", "Lig Rekabet Kadrosu", Color(0xFFFBBF24), ovrRating = 93, speedStat = 91, powerStat = 92) { AgeBadge("U14", Color(0xFFFBBF24)) },
        WizardItem("U16", "U16 Yıldız Takım", "Şampiyona Mücadelesi", Color(0xFFF97316), ovrRating = 96, speedStat = 94, powerStat = 95) { AgeBadge("U16", Color(0xFFF97316)) },
        WizardItem("U18", "U18 Gençler", "A Takım Pro Adayları", Color(0xFFEF4444), ovrRating = 98, speedStat = 96, powerStat = 97) { AgeBadge("U18", Color(0xFFEF4444)) }
    )

    androidx.compose.ui.window.Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        // Holographic Cyber Stadium Backdrop
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFF4F7BFF),
                            Color(0xFF2F55D4),
                            Color(0xFF1B3592)
                        )
                    )
                )
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color(0x55FFE066), Color.Transparent),
                        radius = 900f
                    )
                )
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onDismiss() },
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Game Logo & HUD Header
                Box(
                    modifier = Modifier
                        .statusBarsPadding()
                        .padding(top = 10.dp, start = 16.dp, end = 16.dp, bottom = 4.dp)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    // Logo: sayfada ortalı, %50 büyütüldü (60dp -> 90dp)
                    Image(
                        painter = painterResource(R.drawable.ant_logo),
                        contentDescription = "Antrenman Günlüğü",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.height(90.dp)
                    )

                    // 3D HUD Stage indicator
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF0F172A).copy(alpha = 0.9f))
                            .border(1.5.dp, CyberGold, RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.SportsEsports, contentDescription = null, tint = CyberGold, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "AŞAMA $step/4",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    color = CyberGold,
                                    letterSpacing = 1.sp
                                )
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp)
                            .verticalScroll(rememberScrollState())
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { /* absorb dialog backdrop dismiss */ }
                    ) {
                        // 3D Carousel or 3D Avatar Selector
                        androidx.compose.runtime.key(step) {
                            when (step) {
                                1 -> Wizard3DCarousel(branchItems, selectedBranch, onItemClick = { v -> selectedBranch = v; step++ }, pagerHeight = 500.dp, slideOnValue = "Tümü") { selectedBranch = it }
                                2 -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Wizard3DCarousel(
                                        items = genderItems,
                                        selectedValue = selectedGender,
                                        onItemClick = { v -> selectedGender = v; step++ },
                                        pagerHeight = 500.dp,
                                        // "Tümü"/"Karışık" seçiliyken karusel seçimi ezmesin
                                        skipInitialEmit = genderItems.none { it.value.equals(selectedGender, ignoreCase = true) }
                                    ) { selectedGender = it }
                                    Spacer(modifier = Modifier.height(10.dp))
                                    GenderExtraChips(selectedGender = selectedGender) { g ->
                                        WizardSfx.select()
                                        selectedGender = g
                                        step++
                                    }
                                }
                                3 -> Wizard3DCarousel(hallItems, selectedHall, onItemClick = { v -> selectedHall = v; step++ }, pagerHeight = 440.dp, slideOnValue = "Tümü") { selectedHall = it }
                                else -> Wizard3DCarousel(ageItems, selectedAge, onItemClick = { v -> selectedAge = v }, pagerHeight = 420.dp, slideOnValue = "Tümü") { selectedAge = it }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // 3D Game HUD Control Console Card
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("filter_wizard_dialog"),
                            shape = RoundedCornerShape(26.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF0B1426)),
                            elevation = CardDefaults.cardElevation(defaultElevation = 14.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(
                                        width = 2.dp,
                                        brush = Brush.horizontalGradient(
                                            listOf(
                                                CyberGold,
                                                CyberCyan,
                                                CyberGold
                                            )
                                        ),
                                        shape = RoundedCornerShape(26.dp)
                                    )
                                    .background(
                                        Brush.verticalGradient(
                                            listOf(
                                                Color(0xFF0F1E38),
                                                Color(0xFF070E1A)
                                            )
                                        )
                                    )
                                    .padding(vertical = 18.dp, horizontal = 18.dp)
                            ) {
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    // Step Title & Game Progress Gauge
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = "🎮",
                                                fontSize = 20.sp
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = when (step) {
                                                    1 -> "BRANŞ SEÇ"
                                                    2 -> "2. CİNSİYETİNİ SEÇ"
                                                    3 -> "3. TESİS VE ARENANI SEÇ"
                                                    else -> "4. YAŞ KATEGORİNİ SEÇ"
                                                },
                                                style = MaterialTheme.typography.titleMedium.copy(
                                                    fontWeight = FontWeight.Black,
                                                    color = CyberGold,
                                                    fontSize = 15.sp,
                                                    letterSpacing = 0.5.sp
                                                )
                                            )
                                        }

                                        GameHudChip(
                                            text = "LVL $step",
                                            color = CyberCyan
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    // 3D Stepper energy bars
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        for (i in 1..4) {
                                            val active = i <= step
                                            Box(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .height(6.dp)
                                                    .clip(RoundedCornerShape(3.dp))
                                                    .background(
                                                        if (active) Brush.horizontalGradient(
                                                            listOf(CyberGold, CyberCyan)
                                                        ) else Brush.linearGradient(
                                                            listOf(Color(0xFF1E293B), Color(0xFF1E293B))
                                                        )
                                                    )
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Text(
                                        text = when (step) {
                                            1 -> "Yana kaydırarak Basketbol veya Voleybolu seç, karta dokunarak devam et."
                                            2 -> "Karakteri yana kaydır, kartına dokunarak salon seçimine geç."
                                            3 -> "Pendik, Şekerpınar, Çayırova, Gebze veya Mutlukent salonunu seç."
                                            else -> "Sporcunun yaş kategorisini seçerek filtrelemeyi tamamla."
                                        },
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = Color(0xFF94A3B8),
                                            fontSize = 12.sp
                                        )
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))

                                    // 3D Tactile Buttons Row
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        if (step > 1) {
                                            OutlinedButton(
                                                onClick = { step-- },
                                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                                                modifier = Modifier.height(48.dp)
                                            ) {
                                                Icon(Icons.Default.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text("Geri", fontWeight = FontWeight.Bold)
                                            }
                                        } else {
                                            Spacer(modifier = Modifier.width(1.dp))
                                        }

                                        Game3DButton(
                                            onClick = {
                                                WizardSfx.select()
                                                if (step < 4) {
                                                    step++
                                                } else {
                                                    onApplyFilters(
                                                        ParentFilterPreferences(
                                                            branch = selectedBranch,
                                                            hall = selectedHall,
                                                            ageGroup = selectedAge,
                                                            gender = selectedGender
                                                        )
                                                    )
                                                    onDismiss()
                                                }
                                            },
                                            buttonColor = CyberGold,
                                            shadowColor = CyberGoldDark,
                                            textColor = Color(0xFF050B14),
                                            modifier = Modifier
                                                .width(if (step < 4) 140.dp else 210.dp)
                                                .testTag("wizard_next_button")
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.Center
                                            ) {
                                                Text(
                                                    text = if (step < 4) "İLERİ" else "TAKIMI ÇAĞIR & BAŞLA",
                                                    color = Color(0xFF07142A),
                                                    fontWeight = FontWeight.Black,
                                                    fontSize = 13.sp,
                                                    letterSpacing = 0.5.sp
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Icon(
                                                    Icons.Default.ArrowForward,
                                                    contentDescription = null,
                                                    tint = Color(0xFF07142A),
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * 3D Carousel with isometric perspective tilt on the Y-axis,
 * floating 3D podium pedestal, and dynamic gaming stat gauges.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun Wizard3DCarousel(
    items: List<WizardItem>,
    selectedValue: String,
    onItemClick: ((String) -> Unit)? = null,
    pagerHeight: Dp = 380.dp,
    skipInitialEmit: Boolean = false,
    // Bu değere sahip karta tıklanınca sonraki adıma geçmek yerine karusel yana kayar
    slideOnValue: String? = null,
    onSelected: (String) -> Unit
) {
    val initialPage = items.indexOfFirst { it.value.equals(selectedValue, ignoreCase = true) }.coerceAtLeast(0)
    val pagerState = rememberPagerState(initialPage = initialPage) { items.size }
    val scope = rememberCoroutineScope()
    val latestItems by rememberUpdatedState(items)
    val latestOnSelected by rememberUpdatedState(onSelected)
    val density = LocalDensity.current
    val haptic = LocalHapticFeedback.current
    // Tıklanan karta "pop" (büyüyüp geri dönme) efekti
    val tapPop = remember { Animatable(0f) }

    LaunchedEffect(pagerState) {
        var first = true
        snapshotFlow { pagerState.currentPage }.collect { page ->
            val isFirst = first
            first = false
            if (!isFirst) {
                // Kaydırma geçişinde ses + hafif titreşim
                WizardSfx.swipe()
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            }
            if (isFirst && skipInitialEmit) return@collect
            latestItems.getOrNull(page)?.let { latestOnSelected(it.value) }
        }
    }

    val infinitePulse = rememberInfiniteTransition(label = "carouselGlow")
    val pulse by infinitePulse.animateFloat(
        initialValue = 0.7f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1400, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "glowPulse"
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(pagerHeight)
        ) {
            HorizontalPager(
                state = pagerState,
                key = { latestItems[it].key },
                modifier = Modifier.fillMaxSize()
            ) { page ->
                val item = items[page]
                val pageOffset = ((pagerState.currentPage - page) + pagerState.currentPageOffsetFraction)
                val absOffset = pageOffset.absoluteValue.coerceIn(0f, 1f)
                val focus = 1f - absOffset

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(vertical = 10.dp)
                        .graphicsLayer {
                            // 3D Perspective Rotation Y: authentic arcade turntable rotation
                            cameraDistance = 18f * density.density
                            rotationY = pageOffset * -42f
                            translationX = pageOffset * 36f
                            val scale = lerp(0.82f, 1.05f, focus) * (1f + 0.14f * tapPop.value * focus)
                            scaleX = scale
                            scaleY = scale
                            alpha = lerp(0.35f, 1f, focus)
                        }
                        .drawBehind {
                            // Geçiş sırasında kartın arkasında parlayan enerji halkası
                            val flash = (1f - (focus - 0.5f).absoluteValue * 2f).coerceIn(0f, 1f)
                            if (flash > 0f) {
                                drawRect(
                                    brush = Brush.radialGradient(
                                        colors = listOf(
                                            item.accent.copy(alpha = 0.55f * flash),
                                            Color.Transparent
                                        ),
                                        center = Offset(size.width / 2f, size.height * 0.45f),
                                        radius = size.width * 0.75f
                                    )
                                )
                            }
                        },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Visual with 3D Podium Beneath
                    BoxWithConstraints(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                    // Görsel kutusu: hem genişliğe hem yüksekliğe sığacak en büyük boyut
                    val boxH = minOf(maxHeight.value, maxWidth.value * item.widthFraction / item.aspect)
                    val boxW = boxH * item.aspect
                    Box(
                        modifier = Modifier
                            .size(boxW.dp, boxH.dp)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                enabled = onItemClick != null
                            ) {
                                if (slideOnValue != null &&
                                    item.value.equals(slideOnValue, ignoreCase = true) &&
                                    pagerState.currentPage < items.size - 1
                                ) {
                                    // Salon seçilmeden ilerlemesin: efekt + ses ile yana kaysın
                                    WizardSfx.select()
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    scope.launch {
                                        tapPop.animateTo(1f, tween(140, easing = FastOutSlowInEasing))
                                        tapPop.animateTo(0f, tween(220, easing = FastOutSlowInEasing))
                                    }
                                    scope.launch {
                                        // kaydırma sesi sayfa değişince otomatik çalar (whoosh)
                                        pagerState.animateScrollToPage(
                                            pagerState.currentPage + 1,
                                            animationSpec = tween(650, easing = FastOutSlowInEasing)
                                        )
                                    }
                                } else {
                                    WizardSfx.select()
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    onItemClick?.invoke(item.value)
                                }
                            },
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        // 3D Glowing Podium Stage
                        Game3DPodium(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(90.dp)
                                .align(Alignment.BottomCenter),
                            accent = item.accent
                        )

                        // Main Visual Item
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(bottom = 20.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            item.visual()
                        }
                    }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Black,
                            color = CyberGold,
                            fontSize = 22.sp,
                            letterSpacing = 0.5.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = item.subtitle,
                        modifier = Modifier.padding(horizontal = 48.dp),
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color(0xFFCBD5E1),
                            fontSize = 13.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center
                    )

                    // Gaming RPG Attribute Meters
                    Spacer(modifier = Modifier.height(8.dp))
                    // Sabit yükseklik: istatistikler görünüp kaybolurken görsel zıplamasın
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.72f)
                            .height(36.dp)
                            .padding(horizontal = 8.dp)
                    ) {
                    if (focus > 0.85f && item.value != "Tümü") {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            GameStatBar(
                                label = "⚡ HIZ",
                                statValue = item.speedStat,
                                color = CyberCyan,
                                modifier = Modifier.weight(1f)
                            )
                            GameStatBar(
                                label = "🔥 GÜÇ",
                                statValue = item.powerStat,
                                color = item.accent,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    }
                }
            }

            // 3D Navigation Arrows
            if (pagerState.currentPage > 0) {
                Arrow3DButton(
                    left = true,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 2.dp, bottom = 60.dp)
                ) { scope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) } }
            }
            if (pagerState.currentPage < items.size - 1) {
                Arrow3DButton(
                    left = false,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 2.dp, bottom = 60.dp)
                ) { scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) } }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // 3D Diamond Indicator Pips
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.indices.forEach { i ->
                val active = pagerState.currentPage == i
                Box(
                    modifier = Modifier
                        .height(8.dp)
                        .width(if (active) 26.dp else 8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            if (active) Brush.horizontalGradient(
                                listOf(CyberGold, CyberCyan)
                            ) else Brush.linearGradient(
                                listOf(Color.White.copy(alpha = 0.4f), Color.White.copy(alpha = 0.4f))
                            )
                        )
                )
            }
        }
    }
}

/**
 * Cinsiyet adımında karusel dışında kalan "Tümü" ve "Karışık" seçenekleri.
 */
@Composable
private fun GenderExtraChips(
    selectedGender: String,
    onPick: (String) -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        listOf("Tümü" to "Fark Etmez / Tümü", "Karışık" to "Karma Takım").forEach { (value, label) ->
            val isSelected = selectedGender.equals(value, ignoreCase = true)
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (isSelected) CyberGold.copy(alpha = 0.2f) else Color(0xFF0F172A))
                    .border(
                        1.5.dp,
                        if (isSelected) CyberGold else Color(0xFF334155),
                        RoundedCornerShape(14.dp)
                    )
                    .clickable { onPick(value) }
                    .padding(horizontal = 16.dp, vertical = 9.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) CyberGold else Color.White
                    )
                )
            }
        }
    }
}

@Composable
private fun Arrow3DButton(left: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .size(50.dp)
            .clip(CircleShape)
            .background(Color(0xCC07101E))
            .border(2.dp, CyberGold, CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = if (left) Icons.Default.ChevronLeft else Icons.Default.ChevronRight,
            contentDescription = if (left) "Önceki" else "Sonraki",
            tint = CyberGold,
            modifier = Modifier.size(32.dp)
        )
    }
}

@Composable
private fun IconBadge(icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(CircleShape)
            .background(Brush.verticalGradient(listOf(color, color.copy(alpha = 0.45f))))
            .border(3.dp, Color.White.copy(alpha = 0.85f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.fillMaxSize(0.6f))
    }
}

@Composable
private fun AgeBadge(label: String, color: Color) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    listOf(
                        color,
                        Color(0xFF07142A)
                    )
                )
            )
            .border(3.dp, CyberGold, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = label,
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    fontSize = 58.sp
                )
            )
            Text(
                text = "KATEGORİ",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Black,
                    color = CyberGold,
                    fontSize = 11.sp,
                    letterSpacing = 2.sp
                )
            )
        }
    }
}