package com.zacharee1.systemuituner.compose.components

import android.content.Context
import android.content.pm.IPackageManager
import android.content.pm.PackageManager
import android.os.UserHandle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.topjohnwu.superuser.Shell
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.activities.tutorial.TutorialActivity
import com.zacharee1.systemuituner.util.hasRoot
import com.zacharee1.systemuituner.util.hasShizukuPermission
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import rikka.shizuku.Shizuku
import rikka.shizuku.ShizukuBinderWrapper
import rikka.shizuku.ShizukuProvider
import rikka.shizuku.SystemServiceHelper

private const val REQ_SHIZUKU = 3003

private fun Context.performShizukuPermissionsGrant(permissions: Array<String>): String {
    return if (!grantPermissionsThroughShizuku(permissions)) {
        resources.getString(R.string.permission_grant_failure)
    } else {
        ""
    }
}

private fun Context.grantPermissionsThroughShizuku(permissions: Array<String>): Boolean {
    return try {
        val ipm = IPackageManager.Stub.asInterface(
            ShizukuBinderWrapper(
                SystemServiceHelper.getSystemService("package")
            )
        )

        permissions.forEach {
            ipm.grantRuntimePermission(packageName, it, UserHandle.USER_SYSTEM)
        }

        true
    } catch (e: Exception) {
        false
    }
}

private suspend fun Context.performRootPermissionsGrant(permissions: Array<String>) = coroutineScope {
    val result = withContext(Dispatchers.IO) {
        @Suppress("DEPRECATION")
        Shell.su(*permissions.map { "pm grant $packageName $it" }.toTypedArray()).exec()
    }

    if (result.isSuccess) {
        ""
    } else {
        resources.getString(
            R.string.permission_grant_failure_short,
            (result.out + result.err).joinToString("\n")
        )
    }
}

private fun requestShizukuPermission(permissionsRequester: ActivityResultLauncher<String>) {
    if (Shizuku.isPreV11() || Shizuku.getVersion() < 11) {
        permissionsRequester.launch(ShizukuProvider.PERMISSION)
    } else {
        Shizuku.requestPermission(REQ_SHIZUKU)
    }
}

@Composable
fun IntroSpecialPermissionGrantGroup(
    permissions: Array<String>,
    modifier: Modifier = Modifier,
    grantCallback: ((Boolean) -> Unit)? = null,
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var error by remember {
        mutableStateOf<String?>(null)
    }
    val hasPermissions = remember(error) {
        permissions.all {
            context.checkCallingOrSelfPermission(it) == PackageManager.PERMISSION_GRANTED
        }
    }

    LaunchedEffect(error, hasPermissions) {
        if (error != null) {
            grantCallback?.invoke(hasPermissions)
        }
    }

    val onRequestPermissionResult: (requestCode: Int, grantResult: Int) -> Unit = { requestCode, grantResult ->
        if (requestCode == REQ_SHIZUKU) {
            scope.launch {
                error = if (grantResult == PackageManager.PERMISSION_GRANTED) {
                    context.performShizukuPermissionsGrant(permissions)
                } else {
                    context.resources.getString(R.string.permission_grant_failure)
                }
            }
        }
    }
    val shizukuPermissionRequester = rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) { granted ->
        onRequestPermissionResult(REQ_SHIZUKU, if (granted) PackageManager.PERMISSION_GRANTED else PackageManager.PERMISSION_DENIED)
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        OutlinedButton(
            onClick = {
                scope.launch(Dispatchers.IO) {
                    val hasRoot = async { hasRoot }
                    val hasShizuku = async { Shizuku.pingBinder() }

                    if (hasRoot.await()) {
                        error = context.performRootPermissionsGrant(permissions)
                    } else if (hasShizuku.await()) {
                        if (context.hasShizukuPermission) {
                            error = context.performShizukuPermissionsGrant(permissions)
                        } else {
                            Shizuku.addRequestPermissionResultListener(onRequestPermissionResult)
                            requestShizukuPermission(shizukuPermissionRequester)
                        }
                    } else {
                        error = context.resources.getString(R.string.no_root_msg)
                    }
                }
            },
            enabled = !hasPermissions,
        ) {
            Text(text = stringResource(id = if (hasPermissions) R.string.permission_grant_success else R.string.grant))
        }

        OutlinedButton(onClick = { TutorialActivity.start(context, *permissions) }) {
            Text(text = stringResource(id = R.string.help))
        }
    }

    error?.let { e ->
        if (e.isNotBlank()) {
            AlertDialog(
                onDismissRequest = { error = null },
                title = { Text(text = stringResource(id = R.string.error)) },
                text = { Text(text = e) },
                confirmButton = {
                    TextButton(onClick = { error = null }) {
                        Text(text = stringResource(id = android.R.string.ok))
                    }
                }
            )
        }
    }
}
