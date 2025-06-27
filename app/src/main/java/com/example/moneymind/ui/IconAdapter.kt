package com.example.moneymind.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.moneymind.R
import com.google.android.material.card.MaterialCardView

class IconAdapter(
    private val icons: List<Int>,
    private val onIconSelected: (Int) -> Unit
) : RecyclerView.Adapter<IconAdapter.IconViewHolder>() {

    private var selectedPosition = -1

    inner class IconViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val iconImage: ImageView = itemView.findViewById(R.id.iconImage)
        val cardView: MaterialCardView = itemView.findViewById(R.id.iconCard)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IconViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_icon, parent, false)
        return IconViewHolder(view)
    }

    override fun onBindViewHolder(holder: IconViewHolder, position: Int) {
        val resId = icons[position]
        holder.iconImage.setImageResource(resId)

        holder.cardView.strokeWidth = if (position == selectedPosition) 4 else 0

        holder.itemView.setOnClickListener {
            val currentPosition = holder.adapterPosition
            if (currentPosition != RecyclerView.NO_POSITION) {
                val prev = selectedPosition
                selectedPosition = currentPosition
                notifyItemChanged(prev)
                notifyItemChanged(currentPosition)
                onIconSelected(icons[currentPosition])
            }
        }
    }

    override fun getItemCount(): Int = icons.size
}