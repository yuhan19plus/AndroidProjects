package kr.ac.yuhan.cs.yuhan19plus.admin;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import kr.ac.yuhan.cs.yuhan19plus.R;
import kr.ac.yuhan.cs.yuhan19plus.admin.func.ChangeTextColor;
import kr.ac.yuhan.cs.yuhan19plus.admin.util.ChangeMode;
import soup.neumorphism.NeumorphCardView;
import soup.neumorphism.NeumorphImageView;

public class AdminSettingActivity extends AppCompatActivity {
    private NeumorphImageView backBtn;

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
        NeumorphCardView adminExitCardView = (NeumorphCardView) findViewById(R.id.adminExitCardView);

        backBtn = (NeumorphImageView) findViewById(R.id.backBtn);

        if(modeValue == 1) {
            // Change FontColor
            ChangeTextColor.changeDarkTextColor(settingPage, Color.WHITE);

            // Setting Page Btn
            ChangeMode.setColorFilterDark(backBtn);
            ChangeMode.setDarkShadowCardView(backBtn);

            // Setting Page CardView content
            ChangeMode.setDarkShadowCardView(adminIDCardView);
            ChangeMode.setDarkShadowCardView(addAdminCardView);
            ChangeMode.setDarkShadowCardView(adminExitCardView);
        }

        // AdminIDCardView onClickListener
        adminIDCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create Dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(AdminSettingActivity.this);
                builder.setTitle("관리자 정보");
                builder.setMessage("관리자 ID: [여기에 관리자 ID 입력]\n관리자 직책: [여기에 관리자 직책 입력]");

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

        // AdminExitCardView onClickListener
        adminExitCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AdminSettingActivity.this, "adminExitCardView가 클릭되었습니다!", Toast.LENGTH_SHORT).show();
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
