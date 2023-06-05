package com.agronify.android.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.agronify.android.BuildConfig.BUCKET_URL
import com.agronify.android.databinding.ItemScanBinding
import com.agronify.android.model.remote.response.Scan
import com.bumptech.glide.Glide

class ScanAdapter(private val scanList: List<Scan>) : RecyclerView.Adapter<ScanAdapter.ViewHolder>() {
    private var onClickCallback: OnClickCallback? = null

    interface OnClickCallback {
        fun onClicked(scan: Scan)
    }

    fun setOnClickCallback(onClickCallback: OnClickCallback?) {
        this.onClickCallback = onClickCallback
    }

    inner class ViewHolder(val itemScanBinding: ItemScanBinding) : RecyclerView.ViewHolder(itemScanBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemScanBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return scanList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.apply {
            itemScanBinding.apply {
                Glide.with(itemView.context)
                    .load(BUCKET_URL + scanList[position].image)
                    .into(ivItem)
                tvTitle.text = scanList[position].name

                itemView.setOnClickListener { onClickCallback?.onClicked(scanList[position]) }
            }
        }
    }
}