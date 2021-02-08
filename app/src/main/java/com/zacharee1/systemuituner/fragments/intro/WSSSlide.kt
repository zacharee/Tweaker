package com.zacharee1.systemuituner.fragments.intro

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.heinrichreimersoftware.materialintro.app.SlideFragment
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.activities.tutorial.TutorialActivity
import com.zacharee1.systemuituner.databinding.WssSlideBinding
import com.zacharee1.systemuituner.util.grantPermissionThroughShizuku
import com.zacharee1.systemuituner.util.hasShizukuPermission
import com.zacharee1.systemuituner.util.hasWss
import com.zacharee1.systemuituner.util.requestShizukuPermission
import eu.chainfire.libsuperuser.Shell
import kotlinx.coroutines.*
import rikka.shizuku.Shizuku

class WSSSlide : SlideFragment(), CoroutineScope by MainScope(), Shizuku.OnRequestPermissionResultListener {
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
        val binding = WssSlideBinding.bind(view)

        view.apply {
            binding.grant.setOnClickListener {
                launch {
                    val hasRoot = async { Shell.SU.available() }
                    val hasShizuku = async { Shizuku.pingBinder() }

                    if (hasRoot.await()) {
                        val result = async {
                            Shell.Pool.SU.run("pm grant ${requireContext().packageName} ${android.Manifest.permission.WRITE_SECURE_SETTINGS}")
                        }

                        result.await()
                    } else if (hasShizuku.await()) {
                        if (requireContext().hasShizukuPermission) {
                            requireContext().grantPermissionThroughShizuku(android.Manifest.permission.WRITE_SECURE_SETTINGS)
                        } else {
                            Shizuku.addRequestPermissionResultListener(this@WSSSlide)
                            requireContext().requestShizukuPermission(REQ_SHIZUKU)
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
            binding.help.setOnClickListener {
                TutorialActivity.start(requireContext(), android.Manifest.permission.WRITE_SECURE_SETTINGS)
            }
        }
    }

    override fun onRequestPermissionResult(requestCode: Int, grantResult: Int) {
        if (requestCode == REQ_SHIZUKU && grantResult == PackageManager.PERMISSION_GRANTED) {
            try {
                requireContext().grantPermissionThroughShizuku(android.Manifest.permission.WRITE_SECURE_SETTINGS)
                Toast.makeText(requireContext(), R.string.permission_grant_success, Toast.LENGTH_SHORT).show()
            } catch (e: SecurityException) {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(R.string.failure)
                    .setMessage(R.string.permission_grant_failure)
                    .setPositiveButton(android.R.string.ok, null)
                    .show()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        onRequestPermissionResult(requestCode, grantResults[0])
    }

    override fun canGoForward(): Boolean {
        return context?.hasWss == true
    }

    override fun onDestroy() {
        super.onDestroy()

        cancel()
    }
}