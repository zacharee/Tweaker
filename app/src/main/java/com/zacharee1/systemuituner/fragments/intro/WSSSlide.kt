package com.zacharee1.systemuituner.fragments.intro

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import com.heinrichreimersoftware.materialintro.app.SlideFragment
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.activities.tutorial.TutorialActivity
import com.zacharee1.systemuituner.util.hasWss
import eu.chainfire.libsuperuser.Shell
import kotlinx.android.synthetic.main.wss_slide.*
import kotlinx.coroutines.*

class WSSSlide : SlideFragment(), CoroutineScope by MainScope() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.wss_slide, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.apply {
            grant.setOnClickListener {
                launch {
                    val hasRoot = async { Shell.SU.available() }

                    if (hasRoot.await()) {
                        val result = async {
                            Shell.Pool.SU.run("pm grant ${requireContext().packageName} ${android.Manifest.permission.WRITE_SECURE_SETTINGS}")
                        }

                        result.await()
                    } else {
                        AlertDialog.Builder(
                            requireActivity()
                        )
                            .setTitle(R.string.no_root_title)
                            .setMessage(R.string.no_root_msg)
                            .setPositiveButton(android.R.string.ok, null)
                            .show()
                    }
                }
            }
            help.setOnClickListener {
                TutorialActivity.start(requireContext(), android.Manifest.permission.WRITE_SECURE_SETTINGS)
            }
        }
    }

    override fun canGoForward(): Boolean {
        return context?.hasWss == true
    }

    override fun onDestroy() {
        super.onDestroy()

        cancel()
    }
}