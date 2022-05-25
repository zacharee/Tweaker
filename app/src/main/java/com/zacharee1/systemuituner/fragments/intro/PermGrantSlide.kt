package com.zacharee1.systemuituner.fragments.intro

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.heinrichreimersoftware.materialintro.app.SlideFragment
import com.topjohnwu.superuser.Shell
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.activities.tutorial.TutorialActivity
import com.zacharee1.systemuituner.util.grantPermissionThroughShizuku
import com.zacharee1.systemuituner.util.hasShizukuPermission
import kotlinx.coroutines.*
import rikka.shizuku.Shizuku
import rikka.shizuku.ShizukuProvider

abstract class PermGrantSlide : SlideFragment(), CoroutineScope by MainScope(), Shizuku.OnRequestPermissionResultListener {
    companion object {
        const val REQ_SHIZUKU = 3003
    }

    protected abstract val permissions: Array<String>

    private val permissionsRequester = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        onRequestPermissionResult(REQ_SHIZUKU, if (granted) PackageManager.PERMISSION_GRANTED else PackageManager.PERMISSION_DENIED)
    }

    abstract override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?

    protected fun tryPermissionsGrant() {
        launch {
            val hasRoot = async { Shell.rootAccess() }
            val hasShizuku = async { Shizuku.pingBinder() }

            if (hasRoot.await()) {
                performRootPermissionsGrant()
            } else if (hasShizuku.await()) {
                if (requireContext().hasShizukuPermission) {
                    performShizukuPermissionsGrant()
                } else {
                    Shizuku.addRequestPermissionResultListener(this@PermGrantSlide)
                    requestShizukuPermission()
                }
            } else {
                AlertDialog.Builder(requireActivity())
                    .setTitle(R.string.no_root_title)
                    .setMessage(R.string.no_root_msg)
                    .setPositiveButton(android.R.string.ok, null)
                    .show()
            }
        }
    }

    protected fun startTutorialActivity() {
        TutorialActivity.start(
            requireContext(),
            *permissions
        )
    }

    override fun onRequestPermissionResult(requestCode: Int, grantResult: Int) {
        if (requestCode == REQ_SHIZUKU && grantResult == PackageManager.PERMISSION_GRANTED) {
            performShizukuPermissionsGrant()
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        cancel()
    }

    private fun performShizukuPermissionsGrant() {
        if (!requireContext().grantPermissionThroughShizuku(*permissions)) {
            showFailureDialog(resources.getString(R.string.permission_grant_failure))
        } else {
            showSuccessToast()
        }
    }

    private suspend fun performRootPermissionsGrant() {
        val result = withContext(Dispatchers.IO) {
            Shell.su(*permissions.map { "pm grant ${requireContext().packageName} $it" }.toTypedArray()).exec()
        }

        if (result.isSuccess) {
            showSuccessToast()
        } else {
            showFailureDialog(resources.getString(R.string.permission_grant_failure_short, (result.out + result.err).joinToString("\n")))
        }
    }

    private fun showSuccessToast() {
        Toast.makeText(requireContext(), R.string.permission_grant_success, Toast.LENGTH_SHORT).show()
    }

    private fun showFailureDialog(message: String) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.failure)
            .setMessage(message)
            .setPositiveButton(android.R.string.ok, null)
            .show()
    }

    private fun requestShizukuPermission() {
        if (Shizuku.isPreV11() || Shizuku.getVersion() < 11) {
            permissionsRequester.launch(ShizukuProvider.PERMISSION)
        } else {
            Shizuku.requestPermission(REQ_SHIZUKU)
        }
    }
}