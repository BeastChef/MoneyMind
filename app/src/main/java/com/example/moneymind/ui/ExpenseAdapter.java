package com.example.moneymind.ui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.moneymind.R;
import com.example.moneymind.data.Expense;

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

        // Название категории
        holder.title.setText(expense.getCategory());

        // Название траты
        holder.expenseTitle.setText(expense.getTitle());  // Отображаем название траты

        // Формат суммы
        boolean isIncome = "income".equalsIgnoreCase(expense.getType());
        double amount = expense.getAmount();
        String formatted = (isIncome ? "+ " : "- ") + amount;
        holder.amount.setText(formatted);

        // Формат даты
        String formattedDate = DateFormat.format("dd/MM/yyyy", expense.getDate()).toString();
        holder.date.setText(formattedDate);  // Отображаем дату

        // Цвет текста суммы
        int textColor = ContextCompat.getColor(context,
                isIncome ? R.color.income_color : R.color.expense_color);
        holder.amount.setTextColor(textColor);

        // Установка иконки
        int iconResId = context.getResources().getIdentifier(
                expense.getIconName(),
                "drawable",
                context.getPackageName()
        );
        if (iconResId == 0) {
            iconResId = R.drawable.ic_category_default;
        }
        holder.icon.setImageResource(iconResId);

        // Цвет фона иконки из categoryColor
        Drawable bg = DrawableCompat.wrap(holder.icon.getBackground().mutate());
        DrawableCompat.setTint(bg, expense.getCategoryColor());
        holder.icon.setBackground(bg);

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
        TextView title, amount, date, expenseTitle;  // Новые TextView для даты и названия траты
        ImageView icon;

        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.expense_title);
            amount = itemView.findViewById(R.id.expense_amount);
            date = itemView.findViewById(R.id.expense_date);  // Инициализация для даты
            expenseTitle = itemView.findViewById(R.id.expense_title_item);  // Инициализация для названия траты
            icon = itemView.findViewById(R.id.expense_icon);
        }
    }
}