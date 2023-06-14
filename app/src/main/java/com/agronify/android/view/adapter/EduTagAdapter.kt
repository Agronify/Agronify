package com.agronify.android.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.agronify.android.databinding.ItemEduTagBinding

class EduTagAdapter(private val tagList: List<String>) : RecyclerView.Adapter<EduTagAdapter.ViewHolder>() {
    inner class ViewHolder(val itemEduTagBinding: ItemEduTagBinding) : RecyclerView.ViewHolder(itemEduTagBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemEduTagBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return tagList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.apply {
            itemEduTagBinding.apply {
                tvEduTag.text = tagList[position]

                val layoutParams = tvEduTag.layoutParams as ConstraintLayout.LayoutParams
                val margin = (20 * holder.itemView.context.resources.displayMetrics.density).toInt()

                when (position) {
                    0 -> {
                        layoutParams.marginStart = margin
                        layoutParams.marginEnd = (margin / 2)
                    }
                    tagList.size - 1 -> {
                        layoutParams.marginStart = 0
                        layoutParams.marginEnd = margin
                    }
                    else -> {
                        layoutParams.marginStart = 0
                        layoutParams.marginEnd = (margin / 2)
                    }
                }

                tvEduTag.layoutParams = layoutParams
            }
        }
    }
}