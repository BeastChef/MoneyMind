package com.example.moneymind.ui.choose

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.moneymind.R

class ChooseCategoryAdapter(
    private val categories: List<CategoryIcon>,
    private val onCategorySelected: (CategoryIcon) -> Unit
) : RecyclerView.Adapter<ChooseCategoryAdapter.CategoryViewHolder>() {

    inner class CategoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val iconView: ImageView = view.findViewById(R.id.iconCategory)
        val nameView: TextView = view.findViewById(R.id.textCategoryName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_choose_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]
        holder.iconView.setImageResource(category.iconResId)
        holder.nameView.setText(category.nameResId)

        holder.itemView.setOnClickListener {
            onCategorySelected(category)
        }
    }

    override fun getItemCount(): Int = categories.size
}

