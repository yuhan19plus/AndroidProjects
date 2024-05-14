package kr.ac.yuhan.cs.yuhan19plus.admin.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import kr.ac.yuhan.cs.yuhan19plus.admin.data.TodoData;


public class DatabaseHelper extends SQLiteOpenHelper {
    // Todo Database Info
    private static final String DATABASE_NAME = "todo_db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_TODO = "Todo";
    private static final String COLUMN_NUM = "num";
    private static final String COLUMN_ADMIN_ID = "adminId";
    private static final String COLUMN_TODO_CONTENT = "todoContent";
    private static final String COLUMN_SELECTED_DATE = "selectedDate";
    private static final String COLUMN_CREATION_DATE = "creationDate";
    private Context context;

    // Create Table
    private static final String CREATE_TODO_TABLE = "CREATE TABLE " + TABLE_TODO + "("
            + COLUMN_NUM + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_ADMIN_ID + " TEXT NOT NULL,"
            + COLUMN_TODO_CONTENT + " TEXT NOT NULL,"
            + COLUMN_SELECTED_DATE + " TEXT NOT NULL,"
            + COLUMN_CREATION_DATE + " TEXT NOT NULL)";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TODO_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TODO);
        onCreate(db);
    }

    // Check Selected Date "yyyy-mm-dd" (Method)
    private boolean isValidDate(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        sdf.setLenient(false);
        try {
            sdf.parse(date);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    // Insert Data
    public void insertTodo(String adminId, String todoContent, String selectedDate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        // Check Selected Date "yyyy-mm-dd"
        if (!isValidDate(selectedDate)) {
            Toast.makeText(context, "날짜 형식이 잘못되었습니다.", Toast.LENGTH_SHORT).show();
            db.close();
            return;
        }
        // Add Data
        values.put(COLUMN_ADMIN_ID, adminId);
        values.put(COLUMN_TODO_CONTENT, todoContent);
        values.put(COLUMN_SELECTED_DATE, selectedDate);
        values.put(COLUMN_CREATION_DATE, getCurrentDate()); // Add CurrentDate
        db.insert(TABLE_TODO, null, values); // insert
        db.close();
    }

    public ArrayList<TodoData> getTodosByMonth(int year, int month, int day) {
        // Log.d("getTodosByMonth", year + " " + month + " " + day); // ex) 2024 5 10
        String selectedYear = String.valueOf(year);
        String selectedMonth = String.format("%02d", month); // 1 ~ 9 => 01 ~ 09
        String selectedDay = String.format("%02d", day);

        ArrayList<TodoData> todoList = new ArrayList<>(); // todoList Init
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            // Query to retrieve Todo items corresponding to the selected month
            String selectQuery = "SELECT * FROM " + TABLE_TODO +
                    " WHERE substr(" + COLUMN_SELECTED_DATE + ", 1, 10) = ?";
            String[] selectionArgs = new String[]{selectedYear + "-" + selectedMonth + "-" + selectedDay};
            cursor = db.rawQuery(selectQuery, selectionArgs);

            if (cursor.moveToFirst()) {
                do {
                    TodoData todoData = new TodoData();
                    todoData.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NUM)));
                    todoData.setAdminId(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ADMIN_ID)));
                    todoData.setTodoContent(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TODO_CONTENT)));
                    todoData.setSelectedDate(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SELECTED_DATE)));
                    todoData.setCreationDate(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CREATION_DATE)));
                    todoList.add(todoData);
                } while (cursor.moveToNext());
            }
            // Display an empty listview when there are no results
            if (todoList.isEmpty()) {
                Toast.makeText(context, "데이터가 없습니다.", Toast.LENGTH_SHORT).show();
                // Log.d("TodoData", "No items found for the selected month.");
            }
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        db.close();
        return todoList;
    }

    // Method that returns the current date as a string in yyyy-mm-dd format
    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date());
    }

    // Delete Todo Data
    public void deleteTodo(int todoId, String adminId) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete under the condition that both adminId and todoId are satisfied
        db.delete(TABLE_TODO, COLUMN_NUM + " = ? AND " + COLUMN_ADMIN_ID + " = ?",
                new String[]{String.valueOf(todoId), adminId});
        db.close();
    }
}