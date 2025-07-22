package com.example.moneymind.utils;

import com.example.moneymind.data.Expense;
import com.example.moneymind.data.Category;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class FirestoreHelper {

    private static FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    private static String getUserUid() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return user != null ? user.getUid() : "guest";
    }

    public static void loadExpensesFromFirestore(final ExpenseDataCallback callback) {
        firestore.collection("users")
                .document(getUserUid())
                .collection("expenses")
                .get()
                .addOnSuccessListener(result -> {
                    List<Expense> expenses = result.toObjects(Expense.class);
                    callback.onExpensesLoaded(expenses);
                })
                .addOnFailureListener(callback::onError);
    }

    public static void loadCategoriesFromFirestore(final CategoryDataCallback callback) {
        firestore.collection("users")
                .document(getUserUid())
                .collection("categories")
                .get()
                .addOnSuccessListener(result -> {
                    List<Category> categories = result.toObjects(Category.class);
                    callback.onCategoriesLoaded(categories);
                })
                .addOnFailureListener(callback::onError);
    }

    public static void saveExpenseToFirestore(Expense expense) {
        firestore.collection("users")
                .document(getUserUid())
                .collection("expenses")
                .add(expense);

    }

    public static void saveCategoryToFirestore(Category category) {
        firestore.collection("users")
                .document(getUserUid())
                .collection("categories")
                .document(String.valueOf(category.getId()))
                .set(category);
    }

    public interface ExpenseDataCallback {
        void onExpensesLoaded(List<Expense> expenses);
        void onError(Exception e);
    }

    public interface CategoryDataCallback {
        void onCategoriesLoaded(List<Category> categories);
        void onError(Exception e);
    }
    // 🔄 Обновить расход
    public static void updateExpenseInFirestore(Expense expense) {
        firestore.collection("users")
                .document(getUserUid())
                .collection("expenses")
                .document(String.valueOf(expense.getId()))
                .set(expense);
    }

    // ❌ Удалить расход
    public static void deleteExpenseFromFirestore(Expense expense) {
        firestore.collection("users")
                .document(getUserUid())
                .collection("expenses")
                .document(String.valueOf(expense.getId()))
                .delete();
    }
    public static void copyDataBetweenUsers(String fromUid, String toUid) {
        // Копируем расходы
        firestore.collection("users").document(fromUid).collection("expenses")
                .get().addOnSuccessListener(query -> {
                    for (DocumentSnapshot doc : query.getDocuments()) {
                        firestore.collection("users").document(toUid)
                                .collection("expenses").document(doc.getId())
                                .set(doc.getData());
                    }
                });

        // Копируем категории
        firestore.collection("users").document(fromUid).collection("categories")
                .get().addOnSuccessListener(query -> {
                    for (DocumentSnapshot doc : query.getDocuments()) {
                        firestore.collection("users").document(toUid)
                                .collection("categories").document(doc.getId())
                                .set(doc.getData());
                    }
                });
    }
}