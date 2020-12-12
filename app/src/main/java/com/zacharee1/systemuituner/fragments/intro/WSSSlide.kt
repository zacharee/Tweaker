package com.zacharee1.systemuituner.fragments.intro

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import com.heinrichreimersoftware.materialintro.app.SlideFragment
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.activities.tutorial.TutorialActivity
import com.zacharee1.systemuituner.util.grantPermissionThroughShizuku
import com.zacharee1.systemuituner.util.hasShizukuPermission
import com.zacharee1.systemuituner.util.hasWss
import eu.chainfire.libsuperuser.Shell
import kotlinx.android.synthetic.main.wss_slide.*
import kotlinx.coroutines.*
import moe.shizuku.api.ShizukuApiConstants
import moe.shizuku.api.ShizukuProvider

class WSSSlide : SlideFragment(), CoroutineScope by MainScope() {
    companion object {
        const val REQ_SHIZUKU = 3003
    }

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
                    val hasShizuku = async { ShizukuProvider.isShizukuInstalled(requireContext()) }

                    if (hasRoot.await()) {
                        val result = async {
                            Shell.Pool.SU.run("pm grant ${requireContext().packageName} ${android.Manifest.permission.WRITE_SECURE_SETTINGS}")
                        }

                        result.await()
                    } else if (hasShizuku.await()) {
                        if (requireContext().hasShizukuPermission) {
                            requireContext().grantPermissionThroughShizuku(android.Manifest.permission.WRITE_SECURE_SETTINGS)
                        } else {
                            requestPermissions(arrayOf(ShizukuApiConstants.PERMISSION), REQ_SHIZUKU)
                        }
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQ_SHIZUKU && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            requireContext().grantPermissionThroughShizuku(android.Manifest.permission.WRITE_SECURE_SETTINGS)
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