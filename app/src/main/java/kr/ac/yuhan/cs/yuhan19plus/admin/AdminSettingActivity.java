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

public class AdminSettingActivity extends AppCompatActivity {
    private NeumorphImageView backBtn;

    // Admin Firebase
    private FirebaseFirestore adminDBFireStore;
    // Session Object
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_activity_setting_page);
        LinearLayout settingPage = (LinearLayout) findViewById(R.id.settingPage);

        // Receives current mode value
        int modeValue = getIntent().getIntExtra("mode", 1);

        // Receives background color value passed from MainActivity
        int backgroundColor = getIntent().getIntExtra("background_color", Color.rgb(236, 240, 243));

        // Setting BackgroundColor
        View backgroundView = getWindow().getDecorView().getRootView();
        backgroundView.setBackgroundColor(backgroundColor);

        // Setting CardViews
        NeumorphCardView adminIDCardView = (NeumorphCardView) findViewById(R.id.adminIDCardView);
        NeumorphCardView addAdminCardView = (NeumorphCardView) findViewById(R.id.addAdminCardView);

        backBtn = (NeumorphImageView) findViewById(R.id.backBtn);

        // Admin Firebase
        adminDBFireStore = FirebaseFirestore.getInstance();

        if(modeValue == 1) {
            // Change FontColor
            ChangeTextColor.changeDarkTextColor(settingPage, Color.WHITE);

            // Setting Page Btn
            ChangeMode.setColorFilterDark(backBtn);
            ChangeMode.setDarkShadowCardView(backBtn);

            // Setting Page CardView content
            ChangeMode.setDarkShadowCardView(adminIDCardView);
            ChangeMode.setDarkShadowCardView(addAdminCardView);
        }

        // AdminIDCardView onClickListener
        adminIDCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 세션 객체를 이용하여 현재 로그인한 관리자의 아이디 값을 가져온다.
                sharedPreferences = getSharedPreferences("AdminSession", MODE_PRIVATE);
                String adminId = sharedPreferences.getString("admin_id", null);

                // 'Admins' 컬렉션에서 현재 로그인한 관리자의 ID에 해당하는 문서를 조회합니다.
                adminDBFireStore.collection("admins").document(adminId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot document = task.getResult();
                        // 문서에서 관리자 직책 정보를 가져옵니다.
                        String adminPosition = document.getString("adminPosition");

                        // Create Dialog
                        AlertDialog.Builder builder = new AlertDialog.Builder(AdminSettingActivity.this);
                        builder.setTitle("관리자 정보");
                        builder.setMessage("관리자 ID: " + adminId + "\n관리자 직책: " + adminPosition);

                        // Add "확인" Btn & onClickListener
                        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss(); // Close Dialog
                            }
                        });
                        // Show Dialog
                        builder.show();
                    }
                });





            }
        });

        // AddAdminCardView onClickListener
        addAdminCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Move to Setting page and transfer main page background color
                Intent intent = new Intent(getApplicationContext(), AdminFormActivity.class);
                intent.putExtra("background_color", backgroundColor);
                intent.putExtra("mode", modeValue);
                startActivity(intent);
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
