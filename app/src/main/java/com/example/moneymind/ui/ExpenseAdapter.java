package com.example.moneymind.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.moneymind.R;
import com.example.moneymind.data.Expense;
import com.example.moneymind.utils.Utils;

import androidx.annotation.NonNull;
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

    // 🔹 Интерфейс для обычного клика
    public interface OnExpenseClickListener {
        void onExpenseClick(Expense expense);
    }

    public void setOnExpenseClickListener(OnExpenseClickListener listener) {
        this.clickListener = listener;
    }

    // 🔹 Интерфейс для долгого клика
    public interface OnExpenseLongClickListener {
        void onExpenseLongClick(Expense expense);
    }

    public void setOnExpenseLongClickListener(OnExpenseLongClickListener listener) {
        this.longClickListener = listener;
    }

    public void setExpenseList(@NonNull List<? extends Expense> newExpenses) {
        this.expenses.clear();
        this.expenses.addAll(newExpenses);
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
        holder.title.setText(expense.getCategory());
        holder.amount.setText("-" + expense.getAmount() + " ₽");
        holder.date.setText(Utils.formatDate(expense.getDate()));
        holder.icon.setImageResource(R.drawable.ic_baseline_money_24);

        // 🔹 Короткий клик — редактирование
        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onExpenseClick(expense);
            }
        });

        // 🔹 Долгий клик — например, удаление
        holder.itemView.setOnLongClickListener(v -> {
            if (longClickListener != null) {
                longClickListener.onExpenseLongClick(expense);
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return expenses.size();
    }

    public static class ExpenseViewHolder extends RecyclerView.ViewHolder {
        TextView title, amount, date;
        ImageView icon;

        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.expenseTitle);
            amount = itemView.findViewById(R.id.expenseAmount);
            date = itemView.findViewById(R.id.expenseDate);
            icon = itemView.findViewById(R.id.icon);
        }
    }
}