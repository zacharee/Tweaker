package com.zacharee1.systemuituner.activities.tasker

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import com.joaomgcd.taskerpluginlibrary.config.TaskerPluginConfig
import com.joaomgcd.taskerpluginlibrary.input.TaskerInput
import com.zacharee1.systemuituner.data.tasker.TaskerIconBlacklistData
import com.zacharee1.systemuituner.databinding.ActivityTaskerIconBlacklistInputBinding
import com.zacharee1.systemuituner.util.tasker.helpers.IconBlacklistHelper

open class BaseIconBlacklistConfigureActivity(private val isRemove: Boolean) : AppCompatActivity(),
    TaskerPluginConfig<TaskerIconBlacklistData> {
    override val context: Context
        get() = this
    override val inputForTasker: TaskerInput<TaskerIconBlacklistData>
        get() = TaskerInput(
            TaskerIconBlacklistData(
                isRemove = isRemove,
                key = key
            )
        )

    private val helper by lazy { IconBlacklistHelper(this) }
    protected val binding by lazy { ActivityTaskerIconBlacklistInputBinding.inflate(layoutInflater) }

    private var key: String = ""

    override fun assignFromInput(input: TaskerInput<TaskerIconBlacklistData>) {
        key = input.regular.key
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        helper.onCreate()

        setContentView(binding.root)

        binding.input.setText(key)
        binding.apply.setOnClickListener {
            helper.finishForTasker()
        }
        binding.input.doOnTextChanged { text, _, _, _ ->
            key = text?.toString() ?: ""
        }
    }
}