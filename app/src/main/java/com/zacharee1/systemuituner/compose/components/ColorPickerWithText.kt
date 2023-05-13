package com.zacharee1.systemuituner.compose.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.accompanist.themeadapter.material3.Mdc3Theme
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.util.hexToColor
import com.zacharee1.systemuituner.util.hexToColorIntOrNull
import com.zacharee1.systemuituner.util.toHex

@Preview
@Composable
fun ColorPickerPreview() {
    var color by remember {
        mutableStateOf(Color.Blue)
    }

    Mdc3Theme {
        ColorPickerWithText(
            color = color,
            defaultColor = Color.Red,
            onColorChanged = { color = it }
        )
    }
}

@Composable
fun ColorPickerWithText(
    color: Color,
    defaultColor: Color,
    onColorChanged: (Color) -> Unit,
    modifier: Modifier = Modifier,
) {
    var hexCode by remember(color) {
        mutableStateOf(color.toHex(true).uppercase())
    }
    var error by remember(color, hexCode) {
        mutableStateOf(hexCode.hexToColorIntOrNull() == null)
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        AndroidView(
            factory = {
                com.jaredrummler.android.colorpicker.ColorPickerView(it).apply {
                    this.color = color.toArgb()
                    this.setAlphaSliderVisible(true)
                    this.setOnColorChangedListener { c ->
                        onColorChanged(Color(c))
                    }
                }
            },
            modifier = Modifier
        ) {
            it.color = color.toArgb()
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(color = Color.White)
                    .size(48.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.transparency),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(color = color),
                )
            }

            OutlinedTextField(
                value = hexCode,
                onValueChange = {
                    val noHex = it.replace("#", "")
                    val removeDisallowedCharacters = noHex.replace(Regex("[^a-fA-F0-9]"), "")
                    val prependHex = "#$removeDisallowedCharacters"

                    hexCode = prependHex.trim()
                },
                trailingIcon = if (hexCode != color.toHex(true)) {
                    {
                        if (error) {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_error_24),
                                contentDescription = stringResource(id = R.string.error),
                                tint = MaterialTheme.colorScheme.error,
                            )
                        } else {
                            IconButton(
                                onClick = {
                                    try {
                                        onColorChanged(
                                            hexCode.hexToColor()
                                        )
                                    } catch (e: IllegalArgumentException) {
                                        error = true
                                    }
                                }
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_baseline_check_24),
                                    contentDescription = stringResource(id = R.string.apply),
                                )
                            }
                        }
                    }
                } else null,
                isError = error,
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Characters,
                    autoCorrect = false,
                    imeAction = ImeAction.Done,
                    keyboardType = KeyboardType.Ascii,
                )
            )

            OutlinedButton(onClick = { onColorChanged(defaultColor) }) {
                Text(text = stringResource(id = R.string.reset))
            }
        }
    }
}
