package kr.ac.yuhan.cs.yuhan19plus.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import kr.ac.yuhan.cs.yuhan19plus.MainActivity;
import kr.ac.yuhan.cs.yuhan19plus.R;

public class MainRegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_register);

        ImageView close_register = findViewById(R.id.Close_Register_Btn);
        close_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainRegisterActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        //석재애몽 도와줘요-->>>>>>>>>>>>>>>>>> 회원가입 페이지 main_activity_register.xml 참조
    }
}
