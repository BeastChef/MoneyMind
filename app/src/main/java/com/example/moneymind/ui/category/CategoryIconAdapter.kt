package com.example.moneymind.ui.category

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.moneymind.R
import com.example.moneymind.databinding.ItemCategoryIconBinding

class CategoryIconAdapter(
    private val categories: List<CategoryItem>,
    private val onCategoryClick: (CategoryItem) -> Unit
) : RecyclerView.Adapter<CategoryIconAdapter.CategoryViewHolder>() {

    inner class CategoryViewHolder(private val binding: ItemCategoryIconBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(category: CategoryItem) {
            binding.iconView.setImageResource(category.iconResId)
            binding.nameView.text = category.name
            binding.root.setOnClickListener {
                onCategoryClick(category)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemCategoryIconBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(categories[position])
    }

    override fun getItemCount() = categories.size
}

// 🔵 Класс для представления категории с иконкой
data class CategoryItem(
    val name: String,
    val iconResId: Int
)