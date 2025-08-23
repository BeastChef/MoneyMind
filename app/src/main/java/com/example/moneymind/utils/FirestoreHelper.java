package com.example.moneymind.utils;

import com.example.moneymind.data.Expense;
import com.example.moneymind.data.Category;
import com.example.moneymind.model.CustomCategoryEntity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.SetOptions;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirestoreHelper {

    private static FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private static FirebaseAuth auth = FirebaseAuth.getInstance();

    // Получаем UID текущего пользователя или "guest", если не авторизован
    private static String getUserUid() {
        FirebaseUser user = auth.getCurrentUser();
        return user != null ? user.getUid() : "guest"; // Если пользователь не авторизован, используем "guest"
    }

    // Сохранение данных пользователя в Firestore
    public static void saveUserDataToFirestore(String userId, String email) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Создаем Map для хранения данных пользователя
        Map<String, Object> userData = new HashMap<>();
        userData.put("email", email);  // Добавляем email пользователя
        userData.put("balance", 0.0);  // Начальный баланс (можно позже обновлять)
        userData.put("categories", new ArrayList<>());  // Пустой список категорий по умолчанию
        userData.put("createdAt", FieldValue.serverTimestamp());  // Время создания записи

        // Записываем данные в Firestore в коллекцию "users"
        db.collection("users")
                .document(userId)  // ID пользователя из Firebase
                .set(userData)  // Записываем данные
                .addOnSuccessListener(aVoid -> {
                    System.out.println("User data successfully written!");
                })
                .addOnFailureListener(e -> {
                    System.out.println("Error writing user data: " + e.getMessage());
                });
    }

    // Сохранение кастомной категории в Firestore
    public static void saveCustomCategoryToFirestore(CustomCategoryEntity customCategory) {
        String userId = getUserUid();

        // Создаем Map для данных категории
        Map<String, Object> customCategoryData = new HashMap<>();
        customCategoryData.put("name", customCategory.getName());
        customCategoryData.put("iconResId", customCategory.getIconResId());
        customCategoryData.put("iconName", customCategory.getIconName());
        customCategoryData.put("isIncome", customCategory.isIncome());

        firestore.collection("users")
                .document(userId)
                .collection("categories")
                .document(customCategory.getIconName())  // Используем iconName как уникальный идентификатор
                .set(customCategoryData, SetOptions.merge())  // Сливаем данные, если категория уже существует
                .addOnSuccessListener(aVoid -> {
                    System.out.println("Category successfully added/updated: " + customCategory.getName());
                })
                .addOnFailureListener(e -> {
                    System.out.println("Error adding category: " + e.getMessage());
                });
    }

    // Загружаем расходы для пользователя из Firestore
    public static void loadExpensesFromFirestore(final ExpenseDataCallback callback) {
        firestore.collection("users")
                .document(getUserUid())
                .collection("expenses")
                .get()
                .addOnSuccessListener(result -> {
                    List<Expense> expenses = result.toObjects(Expense.class);
                    callback.onExpensesLoaded(expenses);  // Возвращаем расходы

                })
                .addOnFailureListener(callback::onError);  // Обработка ошибки
    }

    // Загружаем категории для пользователя из Firestore
    public static void loadCategoriesFromFirestore(final CategoryDataCallback callback) {
        firestore.collection("users")
                .document(getUserUid())
                .collection("categories")
                .get()
                .addOnSuccessListener(result -> {
                    List<Category> categories = result.toObjects(Category.class);

                    List<Category> incomeCategories = new ArrayList<>();
                    List<Category> expenseCategories = new ArrayList<>();

                    for (Category category : categories) {
                        if (category.isIncome()) {
                            incomeCategories.add(category);
                        } else {
                            expenseCategories.add(category);
                        }
                    }

                    // Вызываем методы для доходных и расходных категорий
                    callback.onIncomeCategoriesLoaded(incomeCategories);
                    callback.onExpenseCategoriesLoaded(expenseCategories);
                    callback.onCategoriesLoaded(categories);
                })
                .addOnFailureListener(callback::onError);  // Обработка ошибки
    }



    // Сохраняем расход для пользователя в Firestore
    public static void saveExpenseToFirestore(Expense expense) {
        firestore.collection("users")
                .document(getUserUid())  // Получаем UID текущего пользователя
                .collection("expenses")
                .document(String.valueOf(expense.getId()))  // Используем ID расхода
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (!documentSnapshot.exists()) {  // Если расход еще не существует
                        // Создаем новый объект для расхода
                        Map<String, Object> expenseData = new HashMap<>();
                        expenseData.put("amount", expense.getAmount());
                        expenseData.put("category", expense.getCategory());
                        expenseData.put("date", expense.getDate());
                        expenseData.put("type", expense.getType());

                        // Добавляем расход в Firestore
                        firestore.collection("users")
                                .document(getUserUid())  // Используем UID пользователя
                                .collection("expenses")
                                .document(String.valueOf(expense.getId()))  // Уникальный идентификатор
                                .set(expenseData)
                                .addOnSuccessListener(aVoid -> {
                                    System.out.println("Expense successfully added: " + expense.getId());
                                })
                                .addOnFailureListener(e -> {
                                    System.out.println("Error adding expense: " + e.getMessage());
                                });
                    } else {
                        System.out.println("Expense already exists, skipping addition: " + expense.getId());
                    }
                });
    }
    // Очистить все данные из Firestore перед синхронизацией
    public static void clearAndSyncCategories(Context context) {
        String userId = getUserUid();  // Получаем UID текущего пользователя
        firestore.collection("users")
                .document(userId)
                .collection("categories")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    // Удаляем все категории перед новой синхронизацией
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        doc.getReference().delete();
                    }
                    saveDefaultCategoriesToFirestore(context);  // Добавляем дефолтные категории
                })
                .addOnFailureListener(e -> Log.e("FirestoreHelper", "Error clearing categories: " + e.getMessage()));
    }
    // В FirestoreHelper обновите метод syncCategoriesFromFirestore:
    public static void syncCategoriesFromFirestore(Context context, final CategorySyncCallback callback) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        String userId = user.getUid();

        firestore.collection("users")
                .document(userId)
                .collection("categories")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Category> firestoreCategories = queryDocumentSnapshots.toObjects(Category.class);

                    // Очистка и синхронизация данных
                    clearAndSyncCategories(context);

                    // Передаем загруженные категории в callback
                    callback.onCategoriesLoaded(firestoreCategories);
                })
                .addOnFailureListener(e -> {
                    Log.e("FirestoreHelper", "Error syncing categories from Firestore: " + e.getMessage());
                    callback.onError(e);
                });
    }

    // Сохраняем категорию для пользователя в Firestore
    public static void saveCategoryToFirestore(Category category) {
        firestore.collection("users")
                .document(getUserUid())  // Получаем UID текущего пользователя
                .collection("categories")
                .document(category.getIconName())  // Используем iconName как уникальный идентификатор категории
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (!documentSnapshot.exists()) {  // Если категория еще не существует
                        // Создаем новый объект для категории
                        Map<String, Object> categoryData = new HashMap<>();
                        categoryData.put("name", category.getName());
                        categoryData.put("iconResId", category.getIconResId());
                        categoryData.put("iconName", category.getIconName());
                        categoryData.put("isIncome", category.isIncome());

                        // Добавляем категорию в Firestore
                        firestore.collection("users")
                                .document(getUserUid())  // Используем UID пользователя
                                .collection("categories")
                                .document(category.getIconName())  // Уникальный идентификатор
                                .set(categoryData)
                                .addOnSuccessListener(aVoid -> {
                                    System.out.println("Category successfully added: " + category.getName());
                                })
                                .addOnFailureListener(e -> {
                                    System.out.println("Error adding category: " + e.getMessage());
                                });
                    } else {
                        System.out.println("Category already exists, skipping addition: " + category.getName());
                    }
                });
    }


    // Обновить расход в Firestore
    public static void updateExpenseInFirestore(Expense expense) {
        firestore.collection("users")
                .document(getUserUid())
                .collection("expenses")
                .document(String.valueOf(expense.getId()))  // Используем ID расхода для обновления
                .set(expense);  // Обновляем расход
    }

    // Удалить расход из Firestore
    public static void deleteExpenseFromFirestore(Expense expense) {
        firestore.collection("users")
                .document(getUserUid())
                .collection("expenses")
                .document(String.valueOf(expense.getId()))  // Используем ID расхода для удаления
                .delete()
                .addOnSuccessListener(aVoid -> {
                    System.out.println("Expense successfully deleted.");
                })
                .addOnFailureListener(e -> {
                    System.out.println("Error deleting expense: " + e.getMessage());
                });
    }


    // Удалить категорию из Firestore
    public static void deleteCategoryFromFirestore(Category category) {
        firestore.collection("users")
                .document(getUserUid())
                .collection("categories")
                .document(category.getIconName())  // Используем iconName как уникальный идентификатор
                .delete()
                .addOnSuccessListener(aVoid -> {
                    System.out.println("Category successfully deleted.");
                })
                .addOnFailureListener(e -> {
                    System.out.println("Error deleting category: " + e.getMessage());
                });
    }




    // Копируем данные между пользователями (например, из гостевого аккаунта в полноценный)
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



    // Добавить дефолтные категории в Firestore
    public static void saveDefaultCategoriesToFirestore(Context context) {
        String userId = getUserUid();  // Получаем UID пользователя

        // Проверка, если категории уже есть
        firestore.collection("users")
                .document(userId)
                .collection("categories")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (querySnapshot.isEmpty()) {
                        // Если категорий нет, добавляем дефолтные
                        List<Category> defaultCategories = DefaultCategoryInitializer.getDefaultCategories(context.getResources());
                        for (Category category : defaultCategories) {
                            Map<String, Object> categoryData = new HashMap<>();
                            categoryData.put("name", category.getName());
                            categoryData.put("iconResId", category.getIconResId());
                            categoryData.put("iconName", category.getIconName());
                            categoryData.put("isIncome", category.isIncome());

                            firestore.collection("users")
                                    .document(userId)
                                    .collection("categories")
                                    .document(category.getIconName())  // Используем iconName как уникальный идентификатор
                                    .set(categoryData)
                                    .addOnSuccessListener(aVoid -> {
                                        System.out.println("Default category successfully written: " + category.getName());
                                    })
                                    .addOnFailureListener(e -> {
                                        System.out.println("Error writing default category: " + e.getMessage());
                                    });
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    System.out.println("Error checking categories: " + e.getMessage());
                });
    }




    // Проверка на наличие данных в Firestore
    public static void checkAndRestoreData(String fromUid) {
        String currentUserUid = getUserUid();
        if (!fromUid.equals(currentUserUid)) {
            copyDataBetweenUsers(fromUid, currentUserUid);
        }
    }

    // Интерфейсы для получения данных
    public interface ExpenseDataCallback {
        void onExpensesLoaded(List<Expense> expenses);
        void onError(Exception e);
    }

    public interface CategoryDataCallback {
        void onCategoriesLoaded(List<Category> categories);
        void onIncomeCategoriesLoaded(List<Category> incomeCategories);  // Добавляем обработку для доходных категорий
        void onExpenseCategoriesLoaded(List<Category> expenseCategories);  // Добавляем обработку для расходных категорий
        void onError(Exception e);
    }
    public interface CategorySyncCallback {
        void onCategoriesLoaded(List<Category> categories); // Успешная загрузка категорий
        void onError(Exception e); // Обработка ошибок
    }

}