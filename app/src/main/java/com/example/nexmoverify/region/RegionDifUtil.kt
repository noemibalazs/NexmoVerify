package com.example.nexmoverify.region

import androidx.recyclerview.widget.DiffUtil

class RegionDifUtil : DiffUtil.ItemCallback<Region>() {

    override fun areItemsTheSame(oldItem: Region, newItem: Region): Boolean {
        return oldItem.countryName == newItem.countryName
    }

    override fun areContentsTheSame(oldItem: Region, newItem: Region): Boolean {
        return oldItem == newItem
    }
}