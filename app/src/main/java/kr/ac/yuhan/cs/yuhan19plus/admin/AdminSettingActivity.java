package kr.ac.yuhan.cs.yuhan19plus.admin;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import kr.ac.yuhan.cs.yuhan19plus.R;
import kr.ac.yuhan.cs.yuhan19plus.admin.func.ChangeTextColor;
import kr.ac.yuhan.cs.yuhan19plus.admin.util.ChangeMode;
import soup.neumorphism.NeumorphCardView;
import soup.neumorphism.NeumorphImageView;

/** 담당자 : 임성준, 이석재
 * 초기 작성 및 다크모드 적용 : 임성준
 * 관리자 세션처리 기능구현 : 이석재 */
public class AdminSettingActivity extends AppCompatActivity {
    private NeumorphImageView backBtn;
    // 관리자 Firebase
    private FirebaseFirestore adminDBFireStore;
    // 세션 객체
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_activity_setting_page);
        LinearLayout settingPage = (LinearLayout) findViewById(R.id.settingPage);

        // 현재 모드 값을 받음
        int modeValue = getIntent().getIntExtra("mode", 1);

        // MainActivity에서 전달된 배경 색상 값을 받음
        int backgroundColor = getIntent().getIntExtra("background_color", Color.rgb(236, 240, 243));

        // 배경 색상 설정
        View backgroundView = getWindow().getDecorView().getRootView();
        backgroundView.setBackgroundColor(backgroundColor);

        // 설정 CardViews
        NeumorphCardView adminIDCardView = (NeumorphCardView) findViewById(R.id.adminIDCardView);
        NeumorphCardView addAdminCardView = (NeumorphCardView) findViewById(R.id.addAdminCardView);

        backBtn = (NeumorphImageView) findViewById(R.id.backBtn);

        // 관리자 Firebase
        adminDBFireStore = FirebaseFirestore.getInstance();

        if(modeValue == 1) {
            // 폰트 색상 변경
            ChangeTextColor.changeDarkTextColor(settingPage, Color.WHITE);

            // 설정 페이지 버튼
            ChangeMode.setColorFilterDark(backBtn);
            ChangeMode.setDarkShadowCardView(backBtn);

            // 설정 페이지 CardView 내용
            ChangeMode.setDarkShadowCardView(adminIDCardView);
            ChangeMode.setDarkShadowCardView(addAdminCardView);
        }

        // adminIDCardView onClickListener
        adminIDCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 세션 객체를 이용하여 현재 로그인한 관리자의 아이디 값을 가져옵니다.
                sharedPreferences = getSharedPreferences("AdminSession", MODE_PRIVATE);
                String adminId = sharedPreferences.getString("admin_id", null);

                // 'Admins' 컬렉션에서 현재 로그인한 관리자의 ID에 해당하는 문서를 조회
                adminDBFireStore.collection("admins").document(adminId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot document = task.getResult();
                        // 문서에서 관리자 직책 정보를 가져옴
                        String adminPosition = document.getString("adminPosition");

                        // 다이얼로그 생성
                        AlertDialog.Builder builder = new AlertDialog.Builder(AdminSettingActivity.this);
                        builder.setTitle("관리자 정보");
                        builder.setMessage("관리자 ID: " + adminId + "\n관리자 직책: " + adminPosition);

                        // "확인" 버튼 추가 및 onClickListener
                        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss(); // 다이얼로그 닫기
                            }
                        });
                        // 다이얼로그 표시
                        builder.show();
                    }
                });
            }
        });

        // addAdminCardView onClickListener
        addAdminCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isFirstAdmin()) return;
                // 설정 페이지로 이동하고 메인 페이지 배경 색상 전달
                Intent intent = new Intent(getApplicationContext(), AdminFormActivity.class);
                intent.putExtra("background_color", backgroundColor);
                intent.putExtra("mode", modeValue);
                startActivity(intent);
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

    // 최초 관리자 여부 확인 메서드 - 이석재 작성
    private boolean isFirstAdmin() {
        sharedPreferences = getSharedPreferences("AdminSession", MODE_PRIVATE);
        String adminId = sharedPreferences.getString("admin_id" , null);
        if (!adminId.equals("jun")) {
            Toast.makeText(this, "최초 관리자만 접근 가능합니다.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
