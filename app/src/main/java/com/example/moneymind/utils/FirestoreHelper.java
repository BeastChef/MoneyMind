package com.example.moneymind.utils;

import com.example.moneymind.data.AppDatabase;
import com.example.moneymind.data.CategoryDao;
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
import java.util.Collections;
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

        Map<String, Object> data = new HashMap<>();
        data.put("uuid", customCategory.getUuid());
        data.put("name", customCategory.getName());
        data.put("iconResId", customCategory.getIconResId());
        data.put("iconName", customCategory.getIconName());
        data.put("isIncome", customCategory.isIncome());
        data.put("color", customCategory.getColor());
        data.put("isCustom", true); // <— ключевой флаг!

        firestore.collection("users")
                .document(userId)
                .collection("categories")
                .document(customCategory.getUuid()) // документ по UUID
                .set(data, SetOptions.merge())
                .addOnSuccessListener(aVoid ->
                        Log.d("FirestoreHelper", "Custom category upserted: " + customCategory.getName()))
                .addOnFailureListener(e ->
                        Log.e("FirestoreHelper", "Error add/update custom category: " + e.getMessage()));
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
                .document(getUserUid())
                .collection("expenses")
                .document(expense.getUuid())  // ✅ вместо id
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (!documentSnapshot.exists()) {
                        Map<String, Object> expenseData = new HashMap<>();
                        expenseData.put("uuid", expense.getUuid());
                        expenseData.put("title", expense.getTitle());
                        expenseData.put("amount", expense.getAmount());
                        expenseData.put("category", expense.getCategory());
                        expenseData.put("date", expense.getDate());
                        expenseData.put("type", expense.getType());
                        expenseData.put("iconName", expense.getIconName());
                        expenseData.put("categoryColor", expense.getCategoryColor());
                        expenseData.put("note", expense.getNote());

                        firestore.collection("users")
                                .document(getUserUid())
                                .collection("expenses")
                                .document(expense.getUuid())  // ✅ вместо id
                                .set(expenseData)
                                .addOnSuccessListener(aVoid -> {
                                    System.out.println("Expense successfully added: " + expense.getUuid());
                                })
                                .addOnFailureListener(e -> {
                                    System.out.println("Error adding expense: " + e.getMessage());
                                });
                    } else {
                        System.out.println("Expense already exists, skipping addition: " + expense.getUuid());
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
                      // Добавляем дефолтные категории
                })
                .addOnFailureListener(e -> Log.e("FirestoreHelper", "Error clearing categories: " + e.getMessage()));
    }
    // В FirestoreHelper обновите метод syncCategoriesFromFirestore:
    public static void syncCategoriesFromFirestore(Context context, final CategorySyncCallback callback) {
        String userId = getUserUid();

        firestore.collection("users")
                .document(userId)
                .collection("categories")
                .get()
                .addOnSuccessListener(snap -> {
                    List<Category> defaultCategories = new ArrayList<>();
                    List<CustomCategoryEntity> customCategories = new ArrayList<>();

                    for (DocumentSnapshot d : snap.getDocuments()) {
                        Boolean isCustom = d.getBoolean("isCustom");
                        if (isCustom != null && isCustom) {
                            CustomCategoryEntity c = d.toObject(CustomCategoryEntity.class);
                            if (c != null) customCategories.add(c);
                        } else {
                            // дефолтные НЕ пишем в Room через sync
                            Category c = d.toObject(Category.class);
                            if (c != null) defaultCategories.add(c);
                        }
                    }

                    // ⚡ Работаем только с кастомными
                    CustomCategoryDaoWrapper customWrapper = new CustomCategoryDaoWrapper(context);
                    customWrapper.deleteAllCustom();
                    customWrapper.insertCustomCategories(customCategories);

                    // Возвращаем все категории (если надо)
                    callback.onCategoriesLoaded(defaultCategories);
                })
                .addOnFailureListener(e -> {
                    Log.e("FirestoreHelper", "Error syncing categories: " + e.getMessage());
                    callback.onError(e);
                });
    }


    // Сохраняем категорию для пользователя в Firestore
    public static void saveCategoryToFirestore(Category category, Context context) {
        String userId = getUserUid();

        firestore.collection("users")
                .document(userId)
                .collection("categories")
                .document(category.getUuid())  // ✅ вместо iconName
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (!documentSnapshot.exists()) {
                        Map<String, Object> categoryData = new HashMap<>();
                        categoryData.put("uuid", category.getUuid());
                        categoryData.put("name", category.getName());
                        categoryData.put("iconResId", category.getIconResId());
                        categoryData.put("iconName", category.getIconName());
                        categoryData.put("isIncome", category.isIncome());

                        firestore.collection("users")
                                .document(userId)
                                .collection("categories")
                                .document(category.getUuid())  // ✅ вместо iconName
                                .set(categoryData)
                                .addOnSuccessListener(aVoid -> {
                                    System.out.println("Category successfully added: " + category.getName());
                                    if (context != null) {
                                        addCategoryToLocalDb(context, category);
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    System.out.println("Error adding category: " + e.getMessage());
                                });
                    } else {
                        System.out.println("Category already exists, skipping addition: " + category.getName());
                    }
                })
                .addOnFailureListener(e -> {
                    System.out.println("Error checking Firestore for category: " + e.getMessage());
                });
    }
    private static void addCategoryToLocalDb(Context context, Category category) {
        // Используем обертку CategoryDaoWrapper для работы с локальной базой данных
        CategoryDaoWrapper daoWrapper = new CategoryDaoWrapper(context);

        // Вставляем категорию в базу данных через обертку (передаем категорию как список)
        daoWrapper.insertCategories(new ArrayList<>(Collections.singletonList(category))); // Передаем категорию в виде списка
    }

    // Обновить расход в Firestore
    public static void updateExpenseInFirestore(Expense expense) {
        firestore.collection("users")
                .document(getUserUid())
                .collection("expenses")
                .document(expense.getUuid())  // ✅ вместо id
                .set(expense);
    }

    // Удалить расход из Firestore
    public static void deleteExpenseFromFirestore(Expense expense) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userUid = user != null ? user.getUid() : "guest"; // Получаем UID текущего пользователя

        firestore.collection("users")
                .document(userUid)
                .collection("expenses")
                .document(expense.getUuid())  // Уникальный идентификатор расхода
                .delete()
                .addOnSuccessListener(aVoid -> {
                    System.out.println("Expense successfully deleted.");
                    // Удаляем из гостевой учетной записи, если это необходимо
                    deleteExpenseFromGuest(expense);
                })
                .addOnFailureListener(e -> {
                    System.out.println("Error deleting expense: " + e.getMessage());
                });
    }

    private static void deleteExpenseFromGuest(Expense expense) {
        firestore.collection("users")
                .document("guest") // Учитываем гостевую учетную запись
                .collection("expenses")
                .document(expense.getUuid())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    System.out.println("Expense deleted from guest.");
                })
                .addOnFailureListener(e -> {
                    System.out.println("Error deleting expense from guest: " + e.getMessage());
                });
    }




    // Удалить кастомную категорию из Firestore и из гостевого аккаунта
    public static void deleteCustomCategoryFromFirestore(CustomCategoryEntity category) {
        // Удаляем кастомную категорию для текущего пользователя
        firestore.collection("users")
                .document(getUserUid())
                .collection("categories")
                .document(category.getUuid())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d("FirestoreHelper", "Custom category deleted");
                    // Также удаляем кастомную категорию из гостевого аккаунта
                    deleteCustomCategoryFromGuest(category);  // Удаление из гостевого аккаунта
                })
                .addOnFailureListener(e -> {
                    Log.e("FirestoreHelper", "Error deleting custom category: " + e.getMessage());
                });
    }

    // Удалить кастомную категорию из гостевого аккаунта
    private static void deleteCustomCategoryFromGuest(CustomCategoryEntity category) {
        firestore.collection("users")
                .document("guest") // Гостевой аккаунт
                .collection("categories")
                .document(category.getUuid())  // используем UUID
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d("FirestoreHelper", "Custom category deleted from guest account");
                })
                .addOnFailureListener(e -> {
                    Log.e("FirestoreHelper", "Error deleting custom category from guest account: " + e.getMessage());
                });
    }

    // Удалить категорию из Firestore и из гостевого аккаунта
    public static void deleteCategoryFromFirestore(Category category) {
        // Удаляем категорию для текущего пользователя
        firestore.collection("users")
                .document(getUserUid())
                .collection("categories")
                .document(category.getUuid())  // используем UUID
                .delete()
                .addOnSuccessListener(aVoid -> {
                    System.out.println("Category successfully deleted: " + category.getName());
                    // Также удаляем категорию из гостевого аккаунта
                    deleteCategoryFromGuest(category);  // Удаление из гостевого аккаунта
                })
                .addOnFailureListener(e -> {
                    System.out.println("Error deleting category: " + e.getMessage());
                });
    }

    // Удалить категорию из гостевого аккаунта
    private static void deleteCategoryFromGuest(Category category) {
        firestore.collection("users")
                .document("guest") // Гостевой аккаунт
                .collection("categories")
                .document(category.getUuid())  // используем UUID
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d("FirestoreHelper", "Category deleted from guest account: " + category.getName());
                })
                .addOnFailureListener(e -> {
                    Log.e("FirestoreHelper", "Error deleting category from guest account: " + e.getMessage());
                });
    }

    // Копируем данные между пользователями (например, из гостевого аккаунта в полноценный)
    public static void copyDataBetweenUsers(String fromUid, String toUid) {
        // Копирование расходов
        firestore.collection("users").document(fromUid).collection("expenses")
                .get().addOnSuccessListener(query -> {
                    for (DocumentSnapshot doc : query.getDocuments()) {
                        firestore.collection("users").document(toUid)
                                .collection("expenses").document(doc.getId())
                                .set(doc.getData());
                    }
                });

        // Копирование категорий
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


    // ✅ Добавляем метод без context
    public static void saveCategoryToFirestore(Category category) {
        // Просто вызываем оригинальный метод с null вместо context
        // или логикой без локального добавления
        saveCategoryToFirestore(category, null);
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
    // Синхронизация данных между гостевым аккаунтом и полноценным аккаунтом
    public static void syncDataFromGuestToUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            String guestId = "guest"; // ID гостевого аккаунта

            // Копируем данные между учетными записями
            copyDataBetweenUsers(guestId, userId);

            // После копирования данных можно очистить данные гостевой учетной записи
            clearGuestData(guestId);
        }
    }
    // Очистка данных гостевой учетной записи после синхронизации
    public static void clearGuestData(String guestUid) {
        firestore.collection("users").document(guestUid).collection("expenses")
                .get().addOnSuccessListener(querySnapshot -> {
                    // Удаляем все расходы из гостевого аккаунта
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        doc.getReference().delete();
                    }
                });

        firestore.collection("users").document(guestUid).collection("categories")
                .get().addOnSuccessListener(querySnapshot -> {
                    // Удаляем все категории из гостевого аккаунта
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        doc.getReference().delete();
                    }
                });
    }

}
