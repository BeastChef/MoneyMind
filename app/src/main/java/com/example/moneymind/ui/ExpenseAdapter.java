package com.example.moneymind.ui;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.moneymind.R;
import com.example.moneymind.data.Expense;
import com.example.moneymind.utils.CategoryColorHelper;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder> {

    private List<Expense> expenses;
    private OnExpenseClickListener clickListener;
    private OnExpenseLongClickListener longClickListener;

    public ExpenseAdapter() {
        this.expenses = new ArrayList<>();
    }

    public interface OnExpenseClickListener {
        void onExpenseClick(Expense expense);
    }

    public void setOnExpenseClickListener(OnExpenseClickListener listener) {
        this.clickListener = listener;
    }

    public interface OnExpenseLongClickListener {
        void onExpenseLongClick(Expense expense);
    }

    public void setOnExpenseLongClickListener(OnExpenseLongClickListener listener) {
        this.longClickListener = listener;
    }

    public void setExpenseList(@NonNull List<Expense> newList) {
        this.expenses = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_expense, parent, false);
        return new ExpenseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {
        Expense expense = expenses.get(position);
        Context context = holder.itemView.getContext();

        // Заголовок — либо заметка, либо категория
        holder.title.setText(expense.getNote() != null && !expense.getNote().isEmpty()
                ? expense.getNote()
                : expense.getCategory());

        // Формат суммы
        boolean isIncome = "income".equalsIgnoreCase(expense.getType());
        double amount = expense.getAmount();
        String formatted = (isIncome ? "+ " : "- ") + amount + " ₽";
        holder.amount.setText(formatted);

        // Цвет суммы
        int textColor = ContextCompat.getColor(context,
                isIncome ? R.color.income_color : R.color.expense_color);
        holder.amount.setTextColor(textColor);

        // ✅ Цвет круга по типу и имени категории (без зелёного/синего в расходах и т.д.)
        int color = CategoryColorHelper.getColorForCategoryKey(expense.getCategory(), isIncome);
        Drawable bg = DrawableCompat.wrap(holder.icon.getBackground().mutate());
        DrawableCompat.setTint(bg, color);
        holder.icon.setBackground(bg);

        // Иконка (временно одна)
        holder.icon.setImageResource(R.drawable.ic_baseline_money_24);

        // Клики
        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) clickListener.onExpenseClick(expense);
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (longClickListener != null) longClickListener.onExpenseLongClick(expense);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return expenses.size();
    }

    public static class ExpenseViewHolder extends RecyclerView.ViewHolder {
        TextView title, amount;
        ImageView icon;

        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.expense_title);
            amount = itemView.findViewById(R.id.expense_amount);
            icon = itemView.findViewById(R.id.expense_icon);
        }
    }
}