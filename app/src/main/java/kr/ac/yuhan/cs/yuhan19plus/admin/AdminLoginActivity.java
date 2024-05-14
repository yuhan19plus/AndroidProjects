package kr.ac.yuhan.cs.yuhan19plus.admin;

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

import kr.ac.yuhan.cs.yuhan19plus.admin.util.ChangeMode;
import kr.ac.yuhan.cs.yuhan19plus.R;
import soup.neumorphism.NeumorphButton;
import soup.neumorphism.NeumorphCardView;
import soup.neumorphism.NeumorphImageView;

public class AdminLoginActivity extends AppCompatActivity {
    private NeumorphButton loginBtn;
    private NeumorphImageView backBtn;

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

                // Success Login
                Toast tMsg = Toast.makeText(AdminLoginActivity.this, "Login Success", Toast.LENGTH_SHORT);
                Display display = ((WindowManager) getSystemService(WINDOW_SERVICE))
                        .getDefaultDisplay();
                int xOffset = (int) (Math.random() * display.getWidth()); // x좌표
                int yOffset = (int) (Math.random() * display.getHeight()); // y좌표
                tMsg.setGravity(Gravity.TOP | Gravity.LEFT, xOffset, yOffset);
                tMsg.show();

                finish();
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