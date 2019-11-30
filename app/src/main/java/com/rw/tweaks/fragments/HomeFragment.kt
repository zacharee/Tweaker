package com.rw.tweaks.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.rw.tweaks.R
import com.rw.tweaks.activities.PersistentActivity
import com.rw.tweaks.util.updateTitle
import kotlinx.android.synthetic.main.home.*

class HomeFragment : Fragment(R.layout.home) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        persistent_text.setOnClickListener {
            startActivity(Intent(requireContext(), PersistentActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()

        updateTitle(R.string.home)
    }
}