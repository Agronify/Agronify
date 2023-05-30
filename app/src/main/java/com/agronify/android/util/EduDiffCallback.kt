package com.agronify.android.util

import androidx.recyclerview.widget.DiffUtil
import com.agronify.android.model.remote.response.Edu

class EduDiffCallback : DiffUtil.ItemCallback<Edu>() {
    override fun areItemsTheSame(oldItem: Edu, newItem: Edu): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Edu, newItem: Edu): Boolean {
        return oldItem == newItem
    }
}