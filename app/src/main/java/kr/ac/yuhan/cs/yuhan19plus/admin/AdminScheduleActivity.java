package kr.ac.yuhan.cs.yuhan19plus.admin;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;
import java.util.Calendar;

import kr.ac.yuhan.cs.yuhan19plus.R;
import kr.ac.yuhan.cs.yuhan19plus.admin.adapter.TodoListAdapter;
import kr.ac.yuhan.cs.yuhan19plus.admin.data.TodoData;
import kr.ac.yuhan.cs.yuhan19plus.admin.db.DatabaseHelper;
import kr.ac.yuhan.cs.yuhan19plus.admin.util.ChangeMode;
import soup.neumorphism.NeumorphCardView;
import soup.neumorphism.NeumorphImageView;

public class AdminScheduleActivity extends AppCompatActivity {
    private NeumorphImageView backBtn;
    private NeumorphImageView todoAddBtn;
    private ListView todoListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_activity_admin_schedule_page);

        CalendarView calendarView;
        LinearLayout schedulePage;
        NeumorphCardView scheduleCardView;
        NeumorphCardView scheduleRegisterCardView;

        schedulePage = findViewById(R.id.schedulePage);

        // Receives current mode value
        int modeValue = getIntent().getIntExtra("mode", 1);

        // Receives background color value passed from MainActivity
        int backgroundColor = getIntent().getIntExtra("background_color", Color.rgb(236, 240, 243));

        // Setting BackgroundColor
        View backgroundView = getWindow().getDecorView().getRootView();
        backgroundView.setBackgroundColor(backgroundColor);

        // AdminSchedule Page Btn
        backBtn = findViewById(R.id.backBtn);
        todoAddBtn = findViewById(R.id.todoAddBtn);

        // AdminSchedule Page Content
        calendarView = findViewById(R.id.calendarView);
        todoListView = findViewById(R.id.todoListView);
        scheduleCardView = findViewById(R.id.scheduleCardView);
        scheduleRegisterCardView = findViewById(R.id.scheduleRegisterCardView);

        // ListView Init
        todoListView = findViewById(R.id.todoListView);

        if(modeValue == 1) {
            // DarkMode
            ChangeMode.applySubTheme(schedulePage, modeValue);

            // AdminSchedule Page Btn
            ChangeMode.setColorFilterDark(backBtn);
            ChangeMode.setDarkShadowCardView(backBtn);
            ChangeMode.setColorFilterDark(todoAddBtn);
            ChangeMode.setDarkShadowCardView(todoAddBtn);

            // AdminSchedule Page CardView content
            ChangeMode.setDarkShadowCardView(scheduleCardView);
            ChangeMode.setDarkShadowCardView(scheduleRegisterCardView);
        }

        // CalendarView onDateChangeListener(
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                // Code to get date in current calendar view
                Calendar selectedDate = Calendar.getInstance();
                selectedDate.set(year, month, dayOfMonth);

                int currentDay = selectedDate.get(Calendar.DATE); // current day
                int currentMonth = selectedDate.get(Calendar.MONTH); // current month
                int currentYear = selectedDate.get(Calendar.YEAR); // current year
                loadTodoList(currentYear, currentMonth + 1, currentDay);
            }
        });

        // TodoAddBtn onClickListener
        todoAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Change ShapeType to 'pressed' when clicked
                todoAddBtn.setShapeType(1);
                // After clicked, it changes back to 'flat'
                v.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        todoAddBtn.setShapeType(0);
                    }
                }, 200);
                // show Dialog
                showTodoDialog();
            }
        });

        // BackBtn onClickListener
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Change ShapeType to 'pressed' when clicked
                backBtn.setShapeType(1);
                // After clicked, it changes back to 'flat'
                v.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        backBtn.setShapeType(0);
                    }
                }, 200);
                finish();
            }
        });
    }

    // Show Dialog Method
    private void showTodoDialog() {
        DialogFragment dialogFragment = new TodoDialogFragment();
        dialogFragment.show(getSupportFragmentManager(), "TodoDialogFragment");
    }

    // The DialogFragment class that defines the Todo modal window
    public static class TodoDialogFragment extends DialogFragment {
        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = requireActivity().getLayoutInflater();
            View view = inflater.inflate(R.layout.admin_dialog_todo_input, null);

            final EditText editTextDate = view.findViewById(R.id.todoDate);
            final EditText editTextTodo = view.findViewById(R.id.todoText);

            builder.setView(view)
                    .setTitle("할 일 추가")
                    .setPositiveButton("추가", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            String date = editTextDate.getText().toString();
                            String todo = editTextTodo.getText().toString();
                            // Call Todo Data Add Code
                            DatabaseHelper dbHelper = new DatabaseHelper(requireContext());
                            dbHelper.insertTodo("SeongJun1", todo, date);
                        }
                    })
                    .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            TodoDialogFragment.this.getDialog().cancel();
                        }
                    });
            return builder.create();
        }
    }

    // Method to load Todo Data and display it in ListView
    private void loadTodoList(int year, int month, int day) {
        // Create DBHelper Object
        DatabaseHelper dbHelper = new DatabaseHelper(this);

        // View Todo data corresponding to selected month
        ArrayList<TodoData> todoList = dbHelper.getTodosByMonth(year, month, day);

        // Create Adapter
        TodoListAdapter adapter = new TodoListAdapter(this, todoList);

        // Adapter settings for ListView
        todoListView.setAdapter(adapter);
    }
}