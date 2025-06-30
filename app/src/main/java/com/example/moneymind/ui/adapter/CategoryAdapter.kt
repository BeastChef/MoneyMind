package com.example.moneymind.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.moneymind.R
import com.example.moneymind.model.CategoryItem
import com.example.moneymind.utils.CategoryColorHelper

class CategoryAdapter(
    private val onItemClick: (CategoryItem) -> Unit
) : ListAdapter<CategoryItem, CategoryAdapter.CategoryViewHolder>(CategoryDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = getItem(position)
        holder.bind(category)
    }

    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val icon: ImageView = itemView.findViewById(R.id.category_icon)
        private val name: TextView = itemView.findViewById(R.id.category_name)

        fun bind(category: CategoryItem) {
            icon.setImageResource(category.iconResId)

            val resId = itemView.context.resources.getIdentifier(category.name, "string", itemView.context.packageName)
            name.text = if (resId != 0) itemView.context.getString(resId) else category.name

            // Цвет фона по ключу iconName и типу isIncome
            val color = CategoryColorHelper.getColorForCategoryKey(category.iconName, category.isIncome)
            val bgDrawable = DrawableCompat.wrap(icon.background.mutate())
            DrawableCompat.setTint(bgDrawable, color)
            icon.background = bgDrawable

            itemView.setOnClickListener {
                onItemClick(category)
            }
        }
    }

    class CategoryDiffCallback : DiffUtil.ItemCallback<CategoryItem>() {
        override fun areItemsTheSame(oldItem: CategoryItem, newItem: CategoryItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CategoryItem, newItem: CategoryItem): Boolean {
            return oldItem == newItem
        }
    }
}