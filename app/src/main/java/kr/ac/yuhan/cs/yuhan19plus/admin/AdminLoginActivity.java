package kr.ac.yuhan.cs.yuhan19plus.admin;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import kr.ac.yuhan.cs.yuhan19plus.admin.func.PasswordEncryption;
import kr.ac.yuhan.cs.yuhan19plus.admin.util.ChangeMode;
import kr.ac.yuhan.cs.yuhan19plus.R;
import soup.neumorphism.NeumorphButton;
import soup.neumorphism.NeumorphCardView;
import soup.neumorphism.NeumorphImageView;

/** 담당자 : 임성준, 이석재
 * 초기 작성 및 다크모드 적용 : 임성준
 * 로그인 처리 기능 구현 : 이석재 */
public class AdminLoginActivity extends AppCompatActivity {
    private NeumorphButton loginBtn;
    private NeumorphImageView backBtn;

    // Admin Firebase
    private FirebaseFirestore adminDBFireStore;
    // Session Object
    private SharedPreferences sharedPreferences;
    // Password Encryption
    private PasswordEncryption pe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_activity_login_page);

        LinearLayout loginPage = findViewById(R.id.loginPage);

        // Receives current mode value
        int modeValue = getIntent().getIntExtra("mode", 1);

        // Receives background color value passed from MainActivity
        int backgroundColor = getIntent().getIntExtra("background_color", Color.rgb(236, 240, 243));

        // Setting BackgroundColor
        View backgroundView = getWindow().getDecorView().getRootView();
        backgroundView.setBackgroundColor(backgroundColor);

        Drawable darkIdImage = getResources().getDrawable(R.drawable.member);
        Drawable darkPwImage = getResources().getDrawable(R.drawable.lock);

        // Login Page Btn
        backBtn = findViewById(R.id.backBtn);
        loginBtn = findViewById(R.id.loginBtn);

        // Login Page CardView content
        NeumorphCardView loginCardView = findViewById(R.id.loginCardView);
        NeumorphCardView editTextIdField = findViewById(R.id.editTextIdField);
        NeumorphCardView editTextPwField = findViewById(R.id.editTextPwField);
        EditText input_id = findViewById(R.id.input_id);
        EditText input_pw = findViewById(R.id.input_pw);

        // Admin Firebase
        adminDBFireStore = FirebaseFirestore.getInstance();

        if(modeValue == 1) {
            // DarkMode
            ChangeMode.applySubTheme(loginPage, modeValue);

            // Login Page Image
            darkIdImage.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
            darkPwImage.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
            input_id.setCompoundDrawablesWithIntrinsicBounds(darkIdImage, null, null, null);
            input_pw.setCompoundDrawablesWithIntrinsicBounds(darkPwImage, null, null, null);

            // Login Page Btn
            ChangeMode.setColorFilterDark(backBtn);
            ChangeMode.setDarkShadowCardView(backBtn);
            ChangeMode.setDarkShadowCardView(loginBtn);

            // Login Page CardView content
            ChangeMode.setDarkShadowCardView(loginCardView);
            ChangeMode.setDarkShadowCardView(editTextIdField);
            ChangeMode.setDarkShadowCardView(editTextPwField);

        }
        else {
            // LightMode
            loginBtn.setBackgroundColor(Color.rgb(0, 174, 142));
            ChangeMode.setLightShadowCardView(loginBtn);
        }

        // LoginBtn onClickListener
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Change ShapeType to 'pressed' when clicked
                loginBtn.setShapeType(1);
                // After clicked, it changes back to 'flat'
                v.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loginBtn.setShapeType(0);
                    }
                }, 200);

                // 에디트 텍스트에서 입력한 아이디, 비밀번호 값 받아오기
                String adminId = input_id.getText().toString().trim();
                String adminPwd = input_pw.getText().toString().trim();

                // 관리자 아이디나 비밀번호를 입력하지 않은 경우 토스트 메시지 출력
                if(adminId.isEmpty()){
                    input_id.setError("관리자 아이디를 입력하세요.");
                    input_id.requestFocus();
                    return;
                }
                else if(adminPwd.isEmpty()){
                    input_pw.setError("관리자 비밀번호를 입력하세요.");
                    input_pw.requestFocus();
                    return;
                }

                // 비밀번호 암호화
                pe = new PasswordEncryption();
                String hashedPassword = pe.hashPassword(adminPwd);

                // 로그인 처리
                adminDBFireStore.collection("admins").document(adminId).get().addOnCompleteListener(task -> {
                    // Success Login
                    if (task.isSuccessful()) {
                        if (task.getResult().exists() && task.getResult().getString("adminPassword").equals(hashedPassword)) {
                            // 로그인 성공 후 토스트 메시지 출력
                            Toast tMsg = Toast.makeText(AdminLoginActivity.this, "Login Success", Toast.LENGTH_SHORT);
                            Display display = ((WindowManager) getSystemService(WINDOW_SERVICE))
                                    .getDefaultDisplay();
                            int xOffset = (int) (Math.random() * display.getWidth()); // x좌표
                            int yOffset = (int) (Math.random() * display.getHeight()); // y좌표
                            tMsg.setGravity(Gravity.TOP | Gravity.LEFT, xOffset, yOffset);
                            tMsg.show();

                            // 로그인 세션 생성
                            sharedPreferences = getSharedPreferences("AdminSession", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("admin_id", adminId);
                            editor.apply();

                            finish();
                        }
                        // 관리자 아이디나 비밀번호가 일치하지 않는 경우
                        else {
                            Toast.makeText(AdminLoginActivity.this, "관리자 아이디나 패스워드가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                        }
                        // 로그인 중 오류가 발생한 경우
                            } else {
                                Toast.makeText(AdminLoginActivity.this, "로그인 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                            }
                });
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
}