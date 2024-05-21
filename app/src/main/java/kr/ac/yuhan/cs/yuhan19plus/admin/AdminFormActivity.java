package kr.ac.yuhan.cs.yuhan19plus.admin;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import kr.ac.yuhan.cs.yuhan19plus.R;
import kr.ac.yuhan.cs.yuhan19plus.admin.util.ChangeMode;
import soup.neumorphism.NeumorphButton;
import soup.neumorphism.NeumorphCardView;
import soup.neumorphism.NeumorphImageView;

public class AdminFormActivity extends AppCompatActivity {
    private NeumorphImageView backBtn;
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
    }
}
