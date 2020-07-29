package com.example.nexmoverify.region

import androidx.recyclerview.widget.RecyclerView
import com.example.nexmoverify.databinding.ItemRegionBinding
import com.example.nexmoverify.generatecode.GenerateCodeViewModel

class RegionVH(
    private val binding: ItemRegionBinding,
    private val generateCodeViewModel: GenerateCodeViewModel
) : RecyclerView.ViewHolder(binding.root) {

    fun onBind(region: Region) {
        binding.viewModel = generateCodeViewModel
        binding.apply {
            tvFullRegion.text = region.toString()
        }
    }
}