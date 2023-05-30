package com.agronify.android.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.agronify.android.databinding.ItemEduTagBinding
import java.util.ArrayList

class EduTagAdapter(private val tagList: ArrayList<String>) : RecyclerView.Adapter<EduTagAdapter.ViewHolder>() {
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
            }
        }
    }
}