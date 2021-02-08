package com.zacharee1.systemuituner.fragments.tutorial

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.heinrichreimersoftware.materialintro.app.SlideFragment
import com.zacharee1.systemuituner.R
import com.zacharee1.systemuituner.data.TutorialStepInfo
import com.zacharee1.systemuituner.databinding.StepsListBinding
import io.noties.markwon.Markwon
import io.noties.markwon.movement.MovementMethodPlugin

class StepsListSlide : SlideFragment() {
    private val steps = ArrayList<TutorialStepInfo>()
    private val adapter = StepsAdapter(steps)

    private val binding by lazy { StepsListBinding.bind(requireView()) }

    private var title: CharSequence = ""
        set(value) {
            field = value

            if (view != null) {
                binding.miTitle.text = value
            }
        }
    private var desc: CharSequence? = null
        set(value) {
            field = value

            if (view != null) {
                binding.miTitle.text = value
            }
        }

    fun setSteps(title: CharSequence, desc: CharSequence? = null, steps: Array<TutorialStepInfo>): StepsListSlide {
        this.steps.clear()
        this.steps.addAll(steps)
        adapter.notifyDataSetChanged()

        this.title = title
        this.desc = desc

        return this
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return LayoutInflater.from(requireContext()).inflate(R.layout.steps_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.miTitle.text = title
        binding.miDescription.text = desc
        binding.stepsList.adapter = adapter
    }

    class StepsAdapter(private val items: ArrayList<TutorialStepInfo>) : RecyclerView.Adapter<StepsAdapter.BaseVH>() {
        companion object {
            const val TYPE_STEP = 0
            const val TYPE_COMMAND = 1
        }

        override fun getItemCount(): Int {
            return items.size
        }

        override fun getItemViewType(position: Int): Int {
            return if (items[position].isCommand) TYPE_COMMAND else TYPE_STEP
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseVH {
            return BaseVH(
                LayoutInflater.from(parent.context).inflate(if (viewType == TYPE_STEP) R.layout.steps_list_item else R.layout.steps_list_command, parent, false)
            )
        }

        override fun onBindViewHolder(holder: BaseVH, position: Int) {
            holder.onBind(items[position])
        }

        open class BaseVH(view: View) : RecyclerView.ViewHolder(view) {
            //TODO: maybe make this global to the adapter?
            private val markwon = Markwon.builder(itemView.context)
                .usePlugin(MovementMethodPlugin.link())
                .build()

            @SuppressLint("SetTextI18n")
            open fun onBind(text: TutorialStepInfo) {
                itemView.findViewById<TextView>(R.id.step).text = SpannableStringBuilder().apply {
                    if (text.isCommand) {
                        append("- ")
                    }

                    append(markwon.toMarkdown(text.text.toString()))
                }
                itemView.findViewById<TextView>(R.id.step).movementMethod = LinkMovementMethod.getInstance()
            }
        }
    }
}