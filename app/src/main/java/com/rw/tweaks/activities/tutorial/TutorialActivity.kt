package com.rw.tweaks.activities.tutorial

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.heinrichreimersoftware.materialintro.app.IntroActivity
import com.heinrichreimersoftware.materialintro.slide.FragmentSlide
import com.heinrichreimersoftware.materialintro.slide.SimpleSlide
import com.heinrichreimersoftware.materialintro.slide.Slide
import com.rw.tweaks.R
import com.rw.tweaks.data.TutorialStepInfo
import com.rw.tweaks.fragments.tutorial.OSChooseSlide
import com.rw.tweaks.fragments.tutorial.StepsListSlide

class TutorialActivity : IntroActivity() {
    companion object {
        const val EXTRA_PERMISSIONS = "permissions"

        fun start(context: Context, vararg permissions: String) {
            val intent = Intent(context, TutorialActivity::class.java)
            intent.putExtra(EXTRA_PERMISSIONS, permissions)

            context.startActivity(intent)
        }
    }

    private val permissions by lazy { intent.getStringArrayExtra(EXTRA_PERMISSIONS) ?: arrayOf() }

    private val introSlide by lazy {
        SimpleSlide.Builder()
            .title(R.string.adb_intro_title)
            .description(R.string.adb_intro_desc)
            .background(R.color.slide_1)
            .build()
    }

    private val androidSetupSlide by lazy {
        StepsListSlide().setSteps(
            title = getString(R.string.adb_android_guide),
            steps = arrayOf(
                TutorialStepInfo(
                    getString(R.string.adb_install_android_1)
                ),
                TutorialStepInfo(
                    getString(R.string.adb_install_android_2)
                ),
                TutorialStepInfo(
                    getString(R.string.adb_install_android_3)
                ),
                TutorialStepInfo(
                    getString(R.string.adb_install_android_4)
                ),
                TutorialStepInfo(
                    getString(R.string.adb_install_android_5)
                ),
                TutorialStepInfo(
                    getString(R.string.adb_install_android_6)
                ),
                TutorialStepInfo(
                    getString(R.string.adb_install_android_7)
                ),
                TutorialStepInfo(
                    getString(R.string.adb_install_android_8)
                ),
                TutorialStepInfo(
                    getString(R.string.adb_install_android_9)
                ),
                TutorialStepInfo(
                    getString(R.string.adb_install_android_10)
                ),
                TutorialStepInfo(
                    getString(R.string.adb_install_android_11)
                ),
                TutorialStepInfo(
                    getString(R.string.adb_install_android_12)
                )
            )
        )
    }

    private val osSlide by lazy {
        OSChooseSlide { which ->
            val osSlide = when (which) {
                R.id.os_windows -> {
                    windowsAdbSlideSteps to R.string.adb_windows
                }
                R.id.os_mac -> {
                    macAdbSlideSteps to R.string.adb_mac
                }
                R.id.os_fedora -> {
                    fedoraAdbSlideSteps to R.string.adb_linux_fedora
                }
                R.id.os_debian -> {
                    debianAdbSlideSteps to R.string.adb_linux_debian
                }
                R.id.os_rhel -> {
                    rhelAdbSlideSteps to R.string.adb_linux_rhel
                }
                R.id.os_linux -> {
                    linuxOtherAdbSlideSteps to R.string.adb_linux_other
                }
                else -> throw IllegalArgumentException("Given OS ID isn't valid! ${resources.getResourceName(which)}")
            }

            adbSlide.setSteps(
                getString(osSlide.second),
                null,
                osSlide.first
            )
        }
    }

    private val adbSlide by lazy {
        StepsListSlide()
    }

    private val windowsAdbSlideSteps by lazy {
        val commands = permissions.map {
            TutorialStepInfo(
                resources.getString(R.string.adb_command_template_windows, packageName, it),
                true
            )
        }
        arrayOf(
            TutorialStepInfo(
                getString(R.string.adb_install_adb_windows_1)
            ),
            TutorialStepInfo(
                getString(R.string.adb_install_adb_windows_2)
            ),
            TutorialStepInfo(
                getString(R.string.adb_install_adb_windows_3)
            ),
            TutorialStepInfo(
                getString(R.string.adb_install_adb_windows_4)
            ),
            TutorialStepInfo(
                getString(R.string.adb_install_adb_general_end_1)
            )
        ) + commands +
                arrayOf(
                    TutorialStepInfo(
                        getString(R.string.adb_install_adb_general_end_2)
                    ),
                    TutorialStepInfo(
                        getString(R.string.adb_install_adb_general_end_3)
                    )
                )
    }

    private val macAdbSlideSteps by lazy {
        val commands = permissions.map {
            TutorialStepInfo(
                resources.getString(R.string.adb_command_template_mac_linux, packageName, it),
                true
            )
        }
        arrayOf(
            TutorialStepInfo(
                getString(R.string.adb_install_adb_mac_1)
            ),
            TutorialStepInfo(
                getString(R.string.adb_install_adb_mac_2)
            ),
            TutorialStepInfo(
                getString(R.string.adb_install_adb_mac_3)
            ),
            TutorialStepInfo(
                getString(R.string.adb_install_adb_mac_4)
            ),
            TutorialStepInfo(
                getString(R.string.adb_install_adb_general_end_1)
            )
        ) + commands +
                arrayOf(
                    TutorialStepInfo(
                        getString(R.string.adb_install_adb_general_end_2)
                    ),
                    TutorialStepInfo(
                        getString(R.string.adb_install_adb_general_end_3)
                    )
                )
    }

    private val fedoraAdbSlideSteps by lazy {
        val commands = permissions.map {
            TutorialStepInfo(
                resources.getString(R.string.adb_command_template_linux_installed, packageName, it),
                true
            )
        }
        arrayOf(
            TutorialStepInfo(
                getString(R.string.adb_install_adb_fedora_1)
            ),
            TutorialStepInfo(
                getString(R.string.adb_install_adb_fedora_2)
            ),
            TutorialStepInfo(
                getString(R.string.adb_install_adb_general_end_1)
            )
        ) + commands +
                arrayOf(
                    TutorialStepInfo(
                        getString(R.string.adb_install_adb_general_end_2)
                    ),
                    TutorialStepInfo(
                        getString(R.string.adb_install_adb_general_end_3)
                    )
                )
    }

    private val debianAdbSlideSteps by lazy {
        val commands = permissions.map {
            TutorialStepInfo(
                resources.getString(R.string.adb_command_template_linux_installed, packageName, it),
                true
            )
        }
        arrayOf(
            TutorialStepInfo(
                getString(R.string.adb_install_adb_debian_1)
            ),
            TutorialStepInfo(
                getString(R.string.adb_install_adb_debian_2)
            ),
            TutorialStepInfo(
                getString(R.string.adb_install_adb_general_end_1)
            )
        ) + commands +
                arrayOf(
                    TutorialStepInfo(
                        getString(R.string.adb_install_adb_general_end_2)
                    ),
                    TutorialStepInfo(
                        getString(R.string.adb_install_adb_general_end_3)
                    )
                )
    }

    private val rhelAdbSlideSteps by lazy {
        val commands = permissions.map {
            TutorialStepInfo(
                resources.getString(R.string.adb_command_template_linux_installed, packageName, it),
                true
            )
        }
        arrayOf(
            TutorialStepInfo(
                getString(R.string.adb_install_adb_rhel_1)
            ),
            TutorialStepInfo(
                getString(R.string.adb_install_adb_rhel_2)
            ),
            TutorialStepInfo(
                getString(R.string.adb_install_adb_general_end_1)
            )
        ) + commands +
                arrayOf(
                    TutorialStepInfo(
                        getString(R.string.adb_install_adb_general_end_2)
                    ),
                    TutorialStepInfo(
                        getString(R.string.adb_install_adb_general_end_3)
                    )
                )
    }

    private val linuxOtherAdbSlideSteps by lazy {
        val commands = permissions.map {
            TutorialStepInfo(
                resources.getString(R.string.adb_command_template_mac_linux, packageName, it),
                true
            )
        }
        arrayOf(
            TutorialStepInfo(
                getString(R.string.adb_install_adb_linux_other_1)
            ),
            TutorialStepInfo(
                getString(R.string.adb_install_adb_linux_other_2)
            ),
            TutorialStepInfo(
                getString(R.string.adb_install_adb_linux_other_3)
            ),
            TutorialStepInfo(
                getString(R.string.adb_install_adb_linux_other_4)
            ),
            TutorialStepInfo(
                getString(R.string.adb_install_adb_general_end_1)
            )
        ) + commands +
                arrayOf(
                    TutorialStepInfo(
                        getString(R.string.adb_install_adb_general_end_2)
                    ),
                    TutorialStepInfo(
                        getString(R.string.adb_install_adb_general_end_3)
                    )
                )
    }

    private val finalSlide by lazy {
        SimpleSlide.Builder()
            .title(R.string.adb_final_title)
            .description(R.string.adb_final_desc)
            .background(R.color.slide_5)
            .id(100L)
            .build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        addSlides(
            arrayListOf(
                introSlide,
                androidSetupSlide.toSlide(R.color.slide_2),
                osSlide.toSlide(R.color.slide_3),
                adbSlide.toSlide(R.color.slide_4),
                finalSlide
            )
        )

        buttonBackFunction = BUTTON_BACK_FUNCTION_BACK
    }

    private fun Fragment.toSlide(background: Int): Slide =
        FragmentSlide.Builder().fragment(this).background(background).build()
}