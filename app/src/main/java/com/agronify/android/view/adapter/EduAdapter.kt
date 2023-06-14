package com.agronify.android.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.agronify.android.BuildConfig.BUCKET_URL
import com.agronify.android.databinding.ItemEduBinding
import com.agronify.android.model.remote.response.Edu
import com.agronify.android.util.EduDiffCallback
import com.bumptech.glide.Glide

class EduAdapter : ListAdapter<Edu, EduAdapter.ViewHolder>(EduDiffCallback()) {
    private var onClickCallback: OnClickCallback? = null

    interface OnClickCallback {
        fun onClicked(edu: Edu)
    }

    fun setOnClickCallback(onClickCallback: OnClickCallback?) {
        this.onClickCallback = onClickCallback
    }

    inner class ViewHolder(val binding: ItemEduBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemEduBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.let { edu ->
            holder.apply {
                binding.apply {
                    Glide.with(itemView.context)
                        .load(BUCKET_URL + edu.image)
                        .into(ivEduImage)
                    tvEduTitle.text = edu.title
                    if (edu.tags.size > 2) {
                        tvEduTag1.text = if (edu.tags[0].contains(" ")) {
                            edu.tags[0].split(" ")[0]
                        } else {
                            edu.tags[0]
                        }
                        tvEduTag2.text = if (edu.tags[1].contains(" ")) {
                            edu.tags[1].split(" ")[0]
                        } else {
                            edu.tags[1]
                        }
                        tvEduTagMore.text = "..."
                    } else {
                        tvEduTag1.text = if (edu.tags[0].contains(" ")) {
                            edu.tags[0].split(" ")[0]
                        } else {
                            edu.tags[0]
                        }
                        tvEduTag2.text = if (edu.tags[1].contains(" ")) {
                            edu.tags[1].split(" ")[0]
                        } else {
                            edu.tags[1]
                        }
                        tvEduTagMore.visibility = ViewGroup.GONE
                    }
                }

                itemView.setOnClickListener { onClickCallback?.onClicked(edu) }
            }
        }
    }
}