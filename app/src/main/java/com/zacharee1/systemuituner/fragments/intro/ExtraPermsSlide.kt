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
import com.zacharee1.systemuituner.databinding.ExtraPermsSlideBinding
import com.zacharee1.systemuituner.util.grantPermissionThroughShizuku
import com.zacharee1.systemuituner.util.hasShizukuPermission
import com.zacharee1.systemuituner.util.requestShizukuPermission
import eu.chainfire.libsuperuser.Shell
import kotlinx.coroutines.*
import rikka.shizuku.Shizuku

class ExtraPermsSlide : SlideFragment(), CoroutineScope by MainScope(), Shizuku.OnRequestPermissionResultListener {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.extra_perms_slide, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = ExtraPermsSlideBinding.bind(view)

        view.apply {
            binding.grant.setOnClickListener {
                launch {
                    val hasRoot = async { Shell.SU.available() }
                    val hasShizuku = async { Shizuku.pingBinder() }

                    if (hasRoot.await()) {
                        val result = async {
                            Shell.Pool.SU.run("pm grant ${requireContext().packageName} ${android.Manifest.permission.PACKAGE_USAGE_STATS}")
                            Shell.Pool.SU.run("pm grant ${requireContext().packageName} ${android.Manifest.permission.DUMP}")
                        }

                        result.await()
                    } else if (hasShizuku.await()) {
                        if (requireContext().hasShizukuPermission) {
                            requireContext().grantPermissionThroughShizuku(android.Manifest.permission.PACKAGE_USAGE_STATS)
                            requireContext().grantPermissionThroughShizuku(android.Manifest.permission.DUMP)
                        } else {
                            Shizuku.addRequestPermissionResultListener(this@ExtraPermsSlide)
                            requireContext().requestShizukuPermission(WSSSlide.REQ_SHIZUKU)
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
                TutorialActivity.start(requireContext(), android.Manifest.permission.PACKAGE_USAGE_STATS, android.Manifest.permission.DUMP)
            }
        }
    }

    override fun onRequestPermissionResult(requestCode: Int, grantResult: Int) {
        if (requestCode == WSSSlide.REQ_SHIZUKU && grantResult == PackageManager.PERMISSION_GRANTED) {
            try {
                requireContext().grantPermissionThroughShizuku(android.Manifest.permission.PACKAGE_USAGE_STATS)
                requireContext().grantPermissionThroughShizuku(android.Manifest.permission.DUMP)
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
        return true //Maybe add a confirmation dialog?
    }

    override fun onDestroy() {
        super.onDestroy()

        cancel()
    }
}