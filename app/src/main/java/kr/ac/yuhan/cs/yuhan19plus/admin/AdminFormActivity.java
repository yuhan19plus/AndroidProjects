package kr.ac.yuhan.cs.yuhan19plus.admin;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import kr.ac.yuhan.cs.yuhan19plus.R;
import kr.ac.yuhan.cs.yuhan19plus.admin.func.PasswordEncryption;
import kr.ac.yuhan.cs.yuhan19plus.admin.util.ChangeMode;
import soup.neumorphism.NeumorphButton;
import soup.neumorphism.NeumorphCardView;
import soup.neumorphism.NeumorphImageView;

public class AdminFormActivity extends AppCompatActivity {
    private NeumorphImageView backBtn;

    // Admin Firebase
    private FirebaseFirestore adminDBFireStore;
    // Password Encryption
    private PasswordEncryption pe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_activity_admin_form_page);
        LinearLayout adminFormPage = findViewById(R.id.adminFormPage);

        // 현재 색상 모드값 가져오기
        int modeValue = getIntent().getIntExtra("mode", 1);

        // MainActivity 색상 값 가져오기
        int backgroundColor = getIntent().getIntExtra("background_color", Color.rgb(236, 240, 243));

        // Setting BackgroundColor
        View backgroundView = getWindow().getDecorView().getRootView();
        backgroundView.setBackgroundColor(backgroundColor);

        // AdminForm Page CardView content
        NeumorphCardView adminAddCardView = (NeumorphCardView) findViewById(R.id.adminAddCardView);
        NeumorphCardView editTextIdField = (NeumorphCardView) findViewById(R.id.editTextIdField);
        NeumorphCardView editTextPwField = (NeumorphCardView) findViewById(R.id.editTextPwField);
        NeumorphCardView editTextPositionField = (NeumorphCardView) findViewById(R.id.editTextPositionField);
        NeumorphButton adminAddBtn = (NeumorphButton) findViewById(R.id.adminAddBtn);

        // AdminForm Page Btn
        backBtn = (NeumorphImageView) findViewById(R.id.backBtn);


        // 관리자 아이디, 비밀번호, 직책 에디트 텍스트
        EditText input_adminId = (EditText) findViewById(R.id.input_adminId);
        EditText input_adminPw = (EditText) findViewById(R.id.input_adminPW);
        EditText input_adminPosition = (EditText) findViewById(R.id.input_adminPosition);

        // Admin Firebase
        adminDBFireStore = FirebaseFirestore.getInstance();


        if(modeValue == 1) {
            // DarkMode 적용
            ChangeMode.applySubTheme(adminFormPage, modeValue);

            // AdminForm Page Btn
            ChangeMode.setColorFilterDark(backBtn);
            ChangeMode.setDarkShadowCardView(backBtn);

            // AdminForm Page CardView content
            ChangeMode.setDarkShadowCardView(adminAddCardView);
            ChangeMode.setDarkShadowCardView(editTextIdField);
            ChangeMode.setDarkShadowCardView(editTextPwField);
            ChangeMode.setDarkShadowCardView(editTextPositionField);
            ChangeMode.setDarkShadowCardView(adminAddBtn);
        }
        else {
            // LightMode 적용
            adminAddBtn.setBackgroundColor(Color.rgb(0, 174, 142));
            ChangeMode.setLightShadowCardView(adminAddBtn);
        }

        // 뒤로가기
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 클릭되면 'pressed'으로 바꾸기
                backBtn.setShapeType(1);
                // 클릭 이후 'flat'으로 바꾸기
                v.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        backBtn.setShapeType(0);
                    }
                }, 200);
                finish();
            }
        });

        // 등록 버튼
        adminAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // EditText에 입력한 관리자 정보를 가져옴
                String adminId = input_adminId.getText().toString();
                String adminPw = input_adminPw.getText().toString();
                String adminPosition = input_adminPosition.getText().toString();

                // 관리자 아이디를 입력하지 않은 경우
                if (adminId.isEmpty()) {
                    input_adminId.setError("관리자 아이디를 입력하세요.");
                    input_adminId.requestFocus();
                    return;
                }
                // 관리자 비밀번호를 입력하지 않은 경우
                else if (adminPw.isEmpty()) {
                    input_adminPw.setError("관리자 비밀번호를 입력하세요.");
                    input_adminPw.requestFocus();
                    return;
                }
                // 관리자 직책을 입력하지 않은 경우
                else if (adminPosition.isEmpty()) {
                    input_adminPosition.setError("관리자 직책을 입력하세요.");
                    input_adminPosition.requestFocus();
                    return;
                }

                // 관리자 등록 시작
                adminDBFireStore.collection("admins").document(adminId).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        // 이미 존재하는 관리자 아이디인 경우 토스트 메시지 출력
                        if (document.exists()) {
                            Toast.makeText(AdminFormActivity.this, "이미 존재하는 관리자 ID입니다.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        // 사용 가능한 관리자 아이디인 경우 관리자 등록 진행
                        else {
                            // 비밀번호 암호화
                            pe = new PasswordEncryption();
                            String hashedPassword = pe.hashPassword(adminPw);

                            // DB에 저장할 데이터 맵 생성
                            Map<String, Object> admin = new HashMap<>();
                            admin.put("adminId", adminId);
                            admin.put("adminPassword", hashedPassword);
                            admin.put("adminPosition", adminPosition);


                            adminDBFireStore.collection("admins").document(adminId).set(admin)
                                    // 성공한 경우 토스트 메시지 출력 후 관리자 메인 액티비티로 이동
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(AdminFormActivity.this, "관리자 추가에 성공했습니다.", Toast.LENGTH_SHORT).show();
                                        finish();
                                    })
                                    // 실패한 경우 토스트 메시지 출력
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(AdminFormActivity.this, "관리자 추가에 실패했습니다.", Toast.LENGTH_SHORT).show();
                                    });
                        }
                    }
                    // DB 접근 오류 발생 시 토스트 메시지 출력
                    else {
                        Toast.makeText(AdminFormActivity.this, "데이터베이스 접근 오류 발생", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
    }
}
