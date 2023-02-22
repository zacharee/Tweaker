package com.zacharee1.systemuituner.compose

import android.graphics.Typeface
import android.text.util.Linkify
import android.util.TypedValue
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.appcompat.widget.AppCompatTextView
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollScope
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.material.animation.ArgbEvaluatorCompat
import com.zacharee1.systemuituner.R
import io.noties.markwon.Markwon
import io.noties.markwon.core.CorePlugin
import io.noties.markwon.html.HtmlPlugin
import io.noties.markwon.movement.MovementMethodPlugin
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sign

interface IntroPage {
    val canMoveForward: () -> Boolean
    val blockedReason: (@Composable () -> String)?
    val slideColor: @Composable () -> Color

    @Composable
    fun Render(modifier: Modifier)
}

open class SimpleStepsPage(
    title: @Composable () -> String,
    steps: @Composable () -> Array<StepInfo>,
    icon: (@Composable () -> Painter?)? = null,
    slideColor: @Composable () -> Color,
    canMoveForward: () -> Boolean = { true },
    blockedReason: (@Composable () -> String)? = null,
    scrollable: Boolean = true,
    horizontalTitleRow: Boolean = false
) : SimpleIntroPage(
    title = title,
    description = null,
    icon = icon,
    slideColor = slideColor,
    canMoveForward = canMoveForward,
    blockedReason = blockedReason,
    scrollable = scrollable,
    horizontalTitleRow = horizontalTitleRow,
    fullWeightDescription = false,
    extraContent = {
        val items = steps()
        val context = LocalContext.current

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            items(items, { it.text }) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .height(IntrinsicSize.Min),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .width(4.dp)
                            .fillMaxHeight()
                            .clip(MaterialTheme.shapes.small)
                            .background(LocalContentColor.current)
                    )

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .then(
                                if (it.isCommand) {
                                    Modifier
                                        .background(Color(0f, 0f, 0f, 0.5f))
                                        .padding(8.dp)
                                        .horizontalScroll(
                                            state = rememberScrollState(),
                                            flingBehavior = object : FlingBehavior {
                                                override suspend fun ScrollScope.performFling(
                                                    initialVelocity: Float
                                                ): Float {
                                                    return 0f
                                                }
                                            }
                                        )
                                } else {
                                    Modifier
                                }
                            )
                    ) {
                        val textSize = LocalTextStyle.current.fontSize.value

                        AndroidView(
                            factory = {
                                AppCompatTextView(it).apply {
                                    setTextIsSelectable(true)
                                    setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize)
                                }
                            }
                        ) { tv ->
                            tv.text = Markwon.builder(context)
                                .usePlugin(CorePlugin.create())
                                .usePlugin(MovementMethodPlugin.link())
                                .usePlugin(HtmlPlugin.create())
                                .build()
                                .toMarkdown(it.text)
                            tv.typeface = if (it.isCommand) Typeface.MONOSPACE else Typeface.DEFAULT
                            tv.maxLines = if (it.isCommand) 1 else Int.MAX_VALUE
                        }
                    }
                }
            }
        }
    }
) {
    data class StepInfo(
        val text: String,
        val isCommand: Boolean = false,
    )
}

open class SimpleIntroPage(
    val title: @Composable () -> String,
    val description: String? = null,
    val icon: (@Composable () -> Painter?)? = null,
    override val slideColor: @Composable () -> Color,
    override val canMoveForward: () -> Boolean = { true },
    override val blockedReason: @Composable() (() -> String)? = null,
    val scrollable: Boolean = true,
    val horizontalTitleRow: Boolean = false,
    val fullWeightDescription: Boolean = true,
    val extraContent: (@Composable ColumnScope.() -> Unit)? = null,
) : IntroPage {
    @Composable
    private fun RenderTitleAndIcon(
        modifier: Modifier,
        includeDescription: Boolean = true,
        landscape: Boolean = false,
    ) {
        Column(
            modifier = modifier,
            verticalArrangement = if (landscape) Arrangement.Center else Arrangement.Top
        ) {
            @Composable
            fun TitleAndIcon() {
                icon?.let { icon ->
                    icon()?.let {
                        Icon(
                            painter = it,
                            contentDescription = null,
                            modifier = Modifier.size(128.dp),
                            tint = MaterialTheme.colorScheme.contentColorFor(slideColor())
                        )
                    }
                }

                Text(
                    text = title(),
                    style = MaterialTheme.typography.headlineMedium
                )
            }

            if (horizontalTitleRow) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    TitleAndIcon()
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    TitleAndIcon()
                }
            }

            if (includeDescription) {
                Spacer(modifier = Modifier.size(8.dp))

                RenderDescription(isLandscape = landscape)
            }
        }
    }

    @Composable
    private fun ColumnScope.RenderDescription(isLandscape: Boolean) {
        description?.let {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .then(
                        if (!fullWeightDescription || isLandscape) Modifier else Modifier.weight(
                            1f
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = description,
                )
            }
        }
    }

    @Composable
    override fun Render(modifier: Modifier) {
        BoxWithConstraints(
            modifier = modifier
        ) {
            if (constraints.maxWidth > with (LocalDensity.current) { 600.dp.toPx() }) {
                Row(
                    modifier = Modifier
                        .fillMaxHeight()
                        .then(
                            if (scrollable) {
                                Modifier.verticalScroll(rememberScrollState())
                            } else {
                                Modifier
                            }
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RenderTitleAndIcon(
                        modifier = Modifier
                            .weight(1f),
                        includeDescription = extraContent != null,
                        landscape = true,
                    )

                    Spacer(Modifier.size(8.dp))

                    Column(
                        modifier = Modifier
                            .weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        extraContent?.invoke(this) ?: RenderDescription(true)
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .then(
                            if (scrollable) {
                                Modifier.verticalScroll(rememberScrollState())
                            } else {
                                Modifier
                            }
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    RenderTitleAndIcon(
                        modifier = Modifier
                            .fillMaxWidth()
                            .then(if (fullWeightDescription) Modifier.weight(1f) else Modifier)
                    )

                    extraContent?.invoke(this)
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun IntroSlider(
    pages: List<IntroPage>,
    onExit: () -> Unit,
    onDone: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state = rememberPagerState()
    val count = pages.size
    val currentPage = pages[state.currentPage]
    val canMoveForward = currentPage.canMoveForward
    val blockedReason = currentPage.blockedReason?.invoke()
    val scope = rememberCoroutineScope()

    val currentColor = Color(run {
        val position = state.currentPage
        val offset = state.currentPageOffsetFraction
        val next = state.currentPage + offset.sign.toInt()
        val scrollPosition = ((next - position) * offset.absoluteValue + position)
            .coerceIn(
                0f,
                (count - 1)
                    .coerceAtLeast(0)
                    .toFloat()
            )

        ArgbEvaluatorCompat.getInstance()
            .evaluate(
                scrollPosition,
                (if (scrollPosition >= 0.5) pages[next] else currentPage).slideColor().toArgb(),
                (if (scrollPosition >= 0.5) currentPage else pages[next]).slideColor().toArgb(),
            )
    })

    val firstBlocked = pages.indexOfFirst { !it.canMoveForward() }
    val filteredPages = pages.take(firstBlocked + 1).ifEmpty { pages }
    val filteredCount = filteredPages.size

    var forwardAlert by remember {
        mutableStateOf<String?>(null)
    }

    val backPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher

    DisposableEffect(backPressedDispatcher, state.currentPage) {
        val callback = object : OnBackPressedCallback(state.currentPage > 0) {
            override fun handleOnBackPressed() {
                scope.launch {
                    state.animateScrollToPage(max(state.currentPage - 1, 0))
                }
            }
        }

        backPressedDispatcher?.addCallback(callback)

        onDispose {
            callback.remove()
        }
    }

    Surface(modifier = modifier, color = currentColor) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .imePadding()
        ) {
            HorizontalPager(
                pageCount = filteredCount,
                state = state,
                modifier = Modifier
                    .weight(1f),
                beyondBoundsPageCount = 3
            ) {
                pages[it].Render(
                    modifier = Modifier
                        .padding(16.dp)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                val showAsBack by remember {
                    derivedStateOf { state.currentPage > 0 }
                }
                val showAsNext by remember {
                    derivedStateOf { state.currentPage < count - 1 }
                }

                IconButton(
                    onClick = {
                        if (showAsBack) {
                            scope.launch {
                                state.animateScrollToPage(max(state.currentPage - 1, 0))
                            }
                        } else {
                            onExit()
                        }
                    }
                ) {
                    Icon(
                        imageVector = if (showAsBack) Icons.Default.ArrowBack else Icons.Default.Close,
                        contentDescription = stringResource(id = if (showAsBack) R.string.previous else R.string.exit)
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                HorizontalPagerIndicator(
                    pagerState = state,
                    pageCount = count,
                )

                Spacer(modifier = Modifier.weight(1f))

                IconButton(
                    onClick = {
                        if (showAsNext) {
                            if (canMoveForward()) {
                                scope.launch {
                                    state.animateScrollToPage(min(state.currentPage + 1, count - 1))
                                }
                            } else {
                                forwardAlert = blockedReason
                            }
                        } else {
                            onDone()
                        }
                    }
                ) {
                    Icon(
                        imageVector = if (showAsNext) Icons.Default.ArrowForward else Icons.Default.Done,
                        contentDescription = stringResource(id = if (showAsNext) R.string.next else R.string.done)
                    )
                }
            }
        }
    }

    forwardAlert?.let { a ->
        AlertDialog(
            onDismissRequest = { forwardAlert = null },
            title = { Text(text = stringResource(id = R.string.error)) },
            text = { Text(text = a) },
            confirmButton = {
                TextButton(onClick = { forwardAlert = null }) {
                    Text(text = stringResource(id = android.R.string.ok))
                }
            }
        )
    }
}