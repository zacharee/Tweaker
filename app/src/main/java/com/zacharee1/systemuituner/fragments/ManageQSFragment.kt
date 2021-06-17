package com.zacharee1.systemuituner.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.preference.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.activities.ExtraPermsRetroactive
import com.zacharee1.systemuituner.anim.PrefAnimator
import com.zacharee1.systemuituner.data.BlacklistBackupInfo
import com.zacharee1.systemuituner.data.CustomBlacklistItemInfo
import com.zacharee1.systemuituner.dialogs.CustomBlacklistItemDialogFragment
import com.zacharee1.systemuituner.dialogs.RoundedBottomSheetDialog
import com.zacharee1.systemuituner.fragments.intro.ExtraPermsSlide
import com.zacharee1.systemuituner.interfaces.ColorPreference
import com.zacharee1.systemuituner.interfaces.IColorPreference
import com.zacharee1.systemuituner.prefs.BlacklistBrokenBatteryAndroid10Preference
import com.zacharee1.systemuituner.prefs.BlacklistPreference
import com.zacharee1.systemuituner.prefs.BlacklistRotationLockPreference
import com.zacharee1.systemuituner.prefs.CustomBlacklistAddPreference
import com.zacharee1.systemuituner.util.*
import kotlinx.coroutines.*
import tk.zwander.collapsiblepreferencecategory.CollapsiblePreferenceCategoryNew
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.text.SimpleDateFormat
import java.util.*
import kotlin.Exception
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.HashSet

@SuppressLint("RestrictedApi")
class ManageQSFragment : BasePrefFragment() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.prefs_manage_qs, rootKey)
    }
}