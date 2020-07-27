package com.example.nexmoverify.region

import androidx.recyclerview.widget.RecyclerView
import com.example.nexmoverify.databinding.ItemRegionBinding

class RegionVH(
    private val binding: ItemRegionBinding,
    private val regionViewModel: RegionViewModel
) : RecyclerView.ViewHolder(binding.root) {

    fun onBind(region: Region) {
        binding.viewModel = regionViewModel
        binding.apply {
            tvFullRegion.text = region.toString()
        }
    }
}