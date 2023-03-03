package com.zacharee1.systemuituner.compose

import android.content.Context
import android.os.Build
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.zacharee1.systemuituner.R
import dev.zwander.composeintroslider.IntroPage
import dev.zwander.composeintroslider.SimpleIntroPage
import dev.zwander.composeintroslider.SimpleStepsPage

private sealed class SelectedOS(val nameRes: Int, val iconRes: Int) {
    object Windows : SelectedOS(R.string.adb_windows, R.drawable.microsoft_windows) {
        override fun Context.makeSteps(permissions: Array<String>): Array<SimpleStepsPage.StepInfo> {
            val commands = permissions.map {
                SimpleStepsPage.StepInfo(
                    resources.getString(R.string.adb_command_template_windows, packageName, it),
                    true
                )
            }
            return arrayOf(
                SimpleStepsPage.StepInfo(
                    getString(R.string.adb_install_adb_windows_1)
                ),
                SimpleStepsPage.StepInfo(
                    getString(R.string.adb_install_adb_windows_2)
                ),
                SimpleStepsPage.StepInfo(
                    getString(R.string.adb_install_adb_windows_3)
                ),
                SimpleStepsPage.StepInfo(
                    getString(R.string.adb_install_adb_windows_4)
                ),
                SimpleStepsPage.StepInfo(
                    getString(R.string.adb_install_adb_general_end_1)
                )
            ) + commands +
                    arrayOf(
                        SimpleStepsPage.StepInfo(
                            getString(R.string.adb_install_adb_general_end_2)
                        ),
                        SimpleStepsPage.StepInfo(
                            getString(R.string.adb_install_adb_general_end_3)
                        )
                    )
        }
    }
    object MacOS : SelectedOS(R.string.adb_mac, R.drawable.baseline_keyboard_command_key_24) {
        override fun Context.makeSteps(permissions: Array<String>): Array<SimpleStepsPage.StepInfo> {
            val commands = permissions.map {
                SimpleStepsPage.StepInfo(
                    resources.getString(R.string.adb_command_template_mac_linux, packageName, it),
                    true
                )
            }
            return arrayOf(
                SimpleStepsPage.StepInfo(
                    getString(R.string.adb_install_adb_mac_1)
                ),
                SimpleStepsPage.StepInfo(
                    getString(R.string.adb_install_adb_mac_2)
                ),
                SimpleStepsPage.StepInfo(
                    getString(R.string.adb_install_adb_mac_3)
                ),
                SimpleStepsPage.StepInfo(
                    getString(R.string.adb_install_adb_mac_4)
                ),
                SimpleStepsPage.StepInfo(
                    getString(R.string.adb_install_adb_general_end_1)
                )
            ) + commands +
                    arrayOf(
                        SimpleStepsPage.StepInfo(
                            getString(R.string.adb_install_adb_general_end_2)
                        ),
                        SimpleStepsPage.StepInfo(
                            getString(R.string.adb_install_adb_general_end_3)
                        )
                    )
        }
    }
    object Fedora : SelectedOS(R.string.adb_linux_fedora, R.drawable.penguin) {
        override fun Context.makeSteps(permissions: Array<String>): Array<SimpleStepsPage.StepInfo> {
            val commands = permissions.map {
                SimpleStepsPage.StepInfo(
                    resources.getString(R.string.adb_command_template_linux_installed, packageName, it),
                    true
                )
            }
            return arrayOf(
                SimpleStepsPage.StepInfo(
                    getString(R.string.adb_install_adb_fedora_1)
                ),
                SimpleStepsPage.StepInfo(
                    getString(R.string.adb_install_adb_fedora_2)
                ),
                SimpleStepsPage.StepInfo(
                    getString(R.string.adb_install_adb_general_end_1)
                )
            ) + commands +
                    arrayOf(
                        SimpleStepsPage.StepInfo(
                            getString(R.string.adb_install_adb_general_end_2)
                        ),
                        SimpleStepsPage.StepInfo(
                            getString(R.string.adb_install_adb_general_end_3)
                        )
                    )
        }
    }
    object Debian : SelectedOS(R.string.adb_linux_debian, R.drawable.penguin) {
        override fun Context.makeSteps(permissions: Array<String>): Array<SimpleStepsPage.StepInfo> {
            val commands = permissions.map {
                SimpleStepsPage.StepInfo(
                    resources.getString(R.string.adb_command_template_linux_installed, packageName, it),
                    true
                )
            }
            return arrayOf(
                SimpleStepsPage.StepInfo(
                    getString(R.string.adb_install_adb_debian_1)
                ),
                SimpleStepsPage.StepInfo(
                    getString(R.string.adb_install_adb_debian_2)
                ),
                SimpleStepsPage.StepInfo(
                    getString(R.string.adb_install_adb_general_end_1)
                )
            ) + commands +
                    arrayOf(
                        SimpleStepsPage.StepInfo(
                            getString(R.string.adb_install_adb_general_end_2)
                        ),
                        SimpleStepsPage.StepInfo(
                            getString(R.string.adb_install_adb_general_end_3)
                        )
                    )
        }
    }
    object RHEL : SelectedOS(R.string.adb_linux_rhel, R.drawable.penguin) {
        override fun Context.makeSteps(permissions: Array<String>): Array<SimpleStepsPage.StepInfo> {
            val commands = permissions.map {
                SimpleStepsPage.StepInfo(
                    resources.getString(R.string.adb_command_template_linux_installed, packageName, it),
                    true
                )
            }
            return arrayOf(
                SimpleStepsPage.StepInfo(
                    getString(R.string.adb_install_adb_rhel_1)
                ),
                SimpleStepsPage.StepInfo(
                    getString(R.string.adb_install_adb_rhel_2)
                ),
                SimpleStepsPage.StepInfo(
                    getString(R.string.adb_install_adb_general_end_1)
                )
            ) + commands +
                    arrayOf(
                        SimpleStepsPage.StepInfo(
                            getString(R.string.adb_install_adb_general_end_2)
                        ),
                        SimpleStepsPage.StepInfo(
                            getString(R.string.adb_install_adb_general_end_3)
                        )
                    )
        }
    }
    object GenericLinux : SelectedOS(R.string.adb_linux_other, R.drawable.penguin) {
        override fun Context.makeSteps(permissions: Array<String>): Array<SimpleStepsPage.StepInfo> {
            val commands = permissions.map {
                SimpleStepsPage.StepInfo(
                    resources.getString(R.string.adb_command_template_mac_linux, packageName, it),
                    true
                )
            }
            return arrayOf(
                SimpleStepsPage.StepInfo(
                    getString(R.string.adb_install_adb_linux_other_1)
                ),
                SimpleStepsPage.StepInfo(
                    getString(R.string.adb_install_adb_linux_other_2)
                ),
                SimpleStepsPage.StepInfo(
                    getString(R.string.adb_install_adb_linux_other_3)
                ),
                SimpleStepsPage.StepInfo(
                    getString(R.string.adb_install_adb_linux_other_4)
                ),
                SimpleStepsPage.StepInfo(
                    getString(R.string.adb_install_adb_general_end_1)
                )
            ) + commands +
                    arrayOf(
                        SimpleStepsPage.StepInfo(
                            getString(R.string.adb_install_adb_general_end_2)
                        ),
                        SimpleStepsPage.StepInfo(
                            getString(R.string.adb_install_adb_general_end_3)
                        )
                    )
        }
    }
    object LocalADB : SelectedOS(R.string.adb_local, R.drawable.ic_baseline_android_24) {
        override fun Context.makeSteps(permissions: Array<String>): Array<SimpleStepsPage.StepInfo> {
            return arrayOf(
                SimpleStepsPage.StepInfo(
                    getString(R.string.adb_install_local_1)
                ),
                SimpleStepsPage.StepInfo(
                    getString(R.string.adb_install_local_2)
                ),
                SimpleStepsPage.StepInfo(
                    getString(R.string.adb_install_local_3)
                )
            )
        }
    }
    
    abstract fun Context.makeSteps(permissions: Array<String>): Array<SimpleStepsPage.StepInfo>
    
    companion object {
        fun values(): Array<SelectedOS> {
            return arrayOf(Windows, MacOS, Fedora, Debian, RHEL, GenericLinux, LocalADB)
        }

        fun valueOf(value: String): SelectedOS {
            return when (value) {
                "WINDOWS" -> Windows
                "MACOS" -> MacOS
                "FEDORA" -> Fedora
                "DEBIAN" -> Debian
                "RHEL" -> RHEL
                "LINUX" -> GenericLinux
                "LOCAL" -> LocalADB
                else -> throw IllegalArgumentException("No object com.zacharee1.systemuituner.compose.SelectedOS.$value")
            }
        }
    }
}

@Composable
fun rememberTutorialSlides(
    permissions: Array<String>
): List<IntroPage> {
    val context = LocalContext.current
    val slides = remember(permissions) {
        mutableStateListOf<IntroPage>()
    }

    var selectedOs by remember {
        mutableStateOf<SelectedOS?>(null)
    }

    if (slides.isEmpty()) {
        slides.add(SimpleIntroPage(
            title = { stringResource(id = R.string.adb_intro_title) },
            description = {
                stringResource(id = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    R.string.adb_intro_desc_no_computer
                } else {
                    R.string.adb_intro_desc
                })
            },
            slideColor = { colorResource(id = R.color.slide_1) },
            icon = { painterResource(id = R.drawable.ic_baseline_adb_24) }
        ))

        slides.add(SimpleStepsPage(
            title = { stringResource(id = R.string.adb_android_guide) },
            slideColor = { colorResource(id = R.color.slide_2) },
            icon = { painterResource(id = R.drawable.ic_baseline_smartphone_24) },
            steps = {
                arrayOf(
                    SimpleStepsPage.StepInfo(
                        stringResource(id = R.string.adb_install_android_1)
                    ),
                    SimpleStepsPage.StepInfo(
                        stringResource(id = R.string.adb_install_android_2)
                    ),
                    SimpleStepsPage.StepInfo(
                        stringResource(id = R.string.adb_install_android_3)
                    ),
                    SimpleStepsPage.StepInfo(
                        stringResource(id = R.string.adb_install_android_4)
                    ),
                    SimpleStepsPage.StepInfo(
                        stringResource(id = R.string.adb_install_android_5)
                    ),
                    SimpleStepsPage.StepInfo(
                        stringResource(id = R.string.adb_install_android_6)
                    ),
                    SimpleStepsPage.StepInfo(
                        stringResource(id = R.string.adb_install_android_7)
                    ),
                    SimpleStepsPage.StepInfo(
                        stringResource(id = R.string.adb_install_android_8)
                    ),
                    SimpleStepsPage.StepInfo(
                        stringResource(id = R.string.adb_install_android_9)
                    ),
                    SimpleStepsPage.StepInfo(
                        stringResource(id = R.string.adb_install_android_10)
                    ),
                    SimpleStepsPage.StepInfo(
                        stringResource(id = R.string.adb_install_android_11)
                    ),
                    SimpleStepsPage.StepInfo(
                        stringResource(id = R.string.adb_install_android_12)
                    )
                )
            }
        ))

        slides.add(SimpleIntroPage(
            title = { stringResource(id = R.string.adb_choose_computer_os) },
            description = { stringResource(id = R.string.adb_choose_computer_os_desc) },
            slideColor = { colorResource(id = R.color.slide_3) },
            icon = { painterResource(id = R.drawable.baseline_computer_24) },
            fullWeightDescription = false,
            canMoveForward = { selectedOs != null },
            blockedReason = { stringResource(id = R.string.blocked_reason_choose_os) },
            extraContent = {
                val items = remember {
                    SelectedOS.values().filter {
                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.R || it != SelectedOS.LocalADB
                    }
                }

                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .selectableGroup()
                        .padding(top = 16.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    items(items, { it.nameRes }) {
                        val interactionSource = remember {
                            MutableInteractionSource()
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(
                                    interactionSource = interactionSource,
                                    onClick = { selectedOs = it },
                                    indication = null,
                                ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedOs == it,
                                onClick = { selectedOs = it },
                                interactionSource = interactionSource
                            )

                            Text(
                                text = stringResource(id = it.nameRes),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.weight(1f)
                            )

                            Icon(
                                painter = painterResource(id = it.iconRes),
                                contentDescription = stringResource(id = it.nameRes)
                            )
                        }
                    }
                }
            },
        ))

        slides.add(SimpleStepsPage(
            title = { selectedOs?.nameRes?.let { stringResource(id = it) } ?: "" },
            steps = { selectedOs?.run { context.makeSteps(permissions) } ?: arrayOf() },
            icon = { selectedOs?.iconRes?.let { painterResource(id = it) } },
            slideColor = { colorResource(id = R.color.slide_4) }
        ))

        slides.add(SimpleIntroPage(
            title = { stringResource(id = R.string.adb_final_title) },
            description = { stringResource(id = R.string.adb_final_desc) },
            slideColor = { colorResource(id = R.color.slide_5) },
            icon = { painterResource(id = R.drawable.ic_baseline_check_24) }
        ))
    }

    return slides
}
