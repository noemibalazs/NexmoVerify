package com.example.nexmoverify.region

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.nexmoverify.R
import com.example.nexmoverify.databinding.ItemRegionBinding
import com.example.nexmoverify.helper.DataManager

class RegionAdapter(
    private val regionViewModel: RegionViewModel,
    private val dataManager: DataManager
) : ListAdapter<Region, RegionVH>(RegionDifUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RegionVH {
        val binding: ItemRegionBinding =
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_region,
                parent,
                false
            )
        return RegionVH(binding, regionViewModel)
    }

    override fun onBindViewHolder(holder: RegionVH, position: Int) {
        holder.onBind(getItem(position))
        holder.itemView.setOnClickListener {
            dataManager.saveRegionCountryCode(getItem(position).countryCode)
            dataManager.saveRegionCountry(getItem(position).countryName)
        }
    }
}