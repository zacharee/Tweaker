package com.zacharee1.systemuituner.compose.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.zacharee1.systemuituner.R
import tk.zwander.seekbarpreference.slider.Slider
import java.text.DecimalFormat
import kotlin.math.ceil
import kotlin.math.log10
import kotlin.math.roundToInt

@Composable
fun SeekBar(
    minValue: Number,
    maxValue: Number,
    defaultValue: Number,
    scale: Double,
    value: Number,
    onValueChanged: (Number) -> Unit,
    modifier: Modifier = Modifier
) {
    val df = DecimalFormat(
        "0.${Array(ceil(log10(1 / scale)).toInt()) { "0" }.joinToString("")}"
    )

    var seekFromUser = remember {
        false
    }
    var showingValueInput by remember {
        mutableStateOf(false)
    }
    var valueInputValue by remember(value) {
        mutableStateOf(df.format(value))
    }

    Box(
        contentAlignment = Alignment.Center,
    ) {
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(
                onClick = {
                    onValueChanged(defaultValue)
                }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_undo_black_24dp),
                    contentDescription = stringResource(id = R.string.reset),
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                AndroidView(
                    factory = {
                        Slider(it).also { seekBarView ->
                            seekBarView.setOnPositionChangeListener(object : Slider.OnPositionChangeListener {
                                override fun onPositionChanged(
                                    view: Slider?,
                                    fromUser: Boolean,
                                    oldPos: Float,
                                    newPos: Float,
                                    oldValue: Int,
                                    newValue: Int
                                ) {
                                    seekFromUser = fromUser
                                    onValueChanged(df.format(newValue * scale).toFloat())
                                }
                            })
                        }
                    },
                    modifier = Modifier
                        .heightIn(min = 128.dp)
                        .fillMaxWidth(),
                ) { seekBarView ->
                    seekBarView.setValueRange(
                        (minValue.toDouble() / scale).roundToInt(),
                        (maxValue.toDouble() / scale).roundToInt(),
                        false,
                    )
                    seekBarView.setValue(
                        (value.toDouble() / scale).toFloat(),
                        !seekFromUser,
                    )
                    seekBarView.setThumbFillPercent(if (value.toFloat() == defaultValue.toFloat()) 0 else 1)
                    seekBarView.setAlwaysFillThumb(false)
                }
            }

            Text(
                text = df.format(value),
                modifier = Modifier.clickable {
                    showingValueInput = true
                }
            )

            Column {
                IconButton(onClick = { onValueChanged(value.toDouble() + scale) }) {
                    Icon(
                        painter = painterResource(id = R.drawable.arrow_up),
                        contentDescription = stringResource(id = R.string.increase)
                    )
                }

                IconButton(onClick = { onValueChanged(value.toDouble() - scale) }) {
                    Icon(
                        painter = painterResource(id = R.drawable.arrow_up),
                        contentDescription = stringResource(id = R.string.decrease),
                        modifier = Modifier.rotate(180f)
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = showingValueInput,
            modifier = Modifier.fillMaxWidth()
                .padding(8.dp),
            enter = expandIn(expandFrom = Alignment.Center) + fadeIn(),
            exit = shrinkOut(shrinkTowards = Alignment.Center) + fadeOut(),
        ) {
            OutlinedTextField(
                value = valueInputValue,
                onValueChange = { valueInputValue = it },
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = if (scale == 1.0) KeyboardType.Number else KeyboardType.Decimal,
                ),
                trailingIcon = {
                    IconButton(
                        onClick = {
                            val decimalValue = valueInputValue.toDoubleOrNull()

                            if (decimalValue != null) {
                                val coerced = decimalValue.coerceIn(minValue.toDouble()..maxValue.toDouble())

                                onValueChanged(coerced)
                                showingValueInput = false
                            }
                        }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_baseline_check_24),
                            contentDescription = stringResource(id = R.string.apply),
                        )
                    }
                },
                leadingIcon = {
                    IconButton(
                        onClick = {
                            showingValueInput = false
                        }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_baseline_add_24),
                            contentDescription = stringResource(id = android.R.string.cancel),
                            modifier = Modifier.rotate(45f)
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
            )
        }
    }
}
