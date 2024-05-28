package kr.ac.yuhan.cs.yuhan19plus.admin;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
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

/** 담당자 : 임성준
 * 관리자 일정 기능구현 및 다크모드 적용 */
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

        // 현재 모드 값을 수신합니다
        int modeValue = getIntent().getIntExtra("mode", 1);

        // MainActivity에서 전달된 배경 색상 값을 수신합니다
        int backgroundColor = getIntent().getIntExtra("background_color", Color.rgb(236, 240, 243));

        // 배경 색상 설정
        View backgroundView = getWindow().getDecorView().getRootView();
        backgroundView.setBackgroundColor(backgroundColor);

        // AdminSchedule 페이지 버튼
        backBtn = findViewById(R.id.backBtn);
        todoAddBtn = findViewById(R.id.todoAddBtn);

        // AdminSchedule 페이지 내용
        calendarView = findViewById(R.id.calendarView);
        todoListView = findViewById(R.id.todoListView);
        scheduleCardView = findViewById(R.id.scheduleCardView);
        scheduleRegisterCardView = findViewById(R.id.scheduleRegisterCardView);

        // ListView 초기화
        todoListView = findViewById(R.id.todoListView);

        if(modeValue == 1) {
            // 다크 모드
            ChangeMode.applySubTheme(schedulePage, modeValue);

            // AdminSchedule 페이지 버튼
            ChangeMode.setColorFilterDark(backBtn);
            ChangeMode.setDarkShadowCardView(backBtn);
            ChangeMode.setColorFilterDark(todoAddBtn);
            ChangeMode.setDarkShadowCardView(todoAddBtn);

            // AdminSchedule 페이지 CardView 내용
            ChangeMode.setDarkShadowCardView(scheduleCardView);
            ChangeMode.setDarkShadowCardView(scheduleRegisterCardView);
        }

        // CalendarView onDateChangeListener(
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                // 현재 캘린더 뷰에서 날짜를 가져오는 코드
                Calendar selectedDate = Calendar.getInstance();
                selectedDate.set(year, month, dayOfMonth);

                int currentDay = selectedDate.get(Calendar.DATE); // 현재 일
                int currentMonth = selectedDate.get(Calendar.MONTH); // 현재 월
                int currentYear = selectedDate.get(Calendar.YEAR); // 현재 연도
                loadTodoList(currentYear, currentMonth + 1, currentDay);
            }
        });

        // todoAddBtn onClickListener
        todoAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 클릭 시 'pressed'로 ShapeType 변경
                todoAddBtn.setShapeType(1);
                // 클릭 후 'flat'으로 변경
                v.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        todoAddBtn.setShapeType(0);
                    }
                }, 200);
                // 다이얼로그 표시
                showTodoDialog();
            }
        });

        // backBtn onClickListener
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 클릭 시 'pressed'로 ShapeType 변경
                backBtn.setShapeType(1);
                // 클릭 후 'flat'으로 변경
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

    // 다이얼로그 표시 메소드
    private void showTodoDialog() {
        DialogFragment dialogFragment = new TodoDialogFragment();
        dialogFragment.show(getSupportFragmentManager(), "TodoDialogFragment");
    }

    // 할일 모달 창을 정의하는 DialogFragment 클래스
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
                            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("AdminSession", MODE_PRIVATE);
                            String adminId = sharedPreferences.getString("admin_id", null);

                            // 할일 데이터 추가 코드 호출
                            DatabaseHelper dbHelper = new DatabaseHelper(requireContext());
                            dbHelper.insertTodo(adminId, todo, date); // 관리자 ID 적용 필요
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

    // 할일 데이터를 로드하고 ListView에 표시하는 메소드
    private void loadTodoList(int year, int month, int day) {
        // DBHelper 객체 생성
        DatabaseHelper dbHelper = new DatabaseHelper(this);

        // 선택한 월에 해당하는 Todo 데이터 조회
        ArrayList<TodoData> todoList = dbHelper.getTodosByMonth(year, month, day);

        // 어댑터 생성
        TodoListAdapter adapter = new TodoListAdapter(this, todoList);

        // ListView에 어댑터 설정
        todoListView.setAdapter(adapter);
    }
}
