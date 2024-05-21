package kr.ac.yuhan.cs.yuhan19plus.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import kr.ac.yuhan.cs.yuhan19plus.R;

public class MainMyPageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_mypage);


        ImageView closeButton = findViewById(R.id.Close_Mypage_Btn);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Button updateButton = findViewById(R.id.Update_Info_Btn);
        updateButton .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainMyPageActivity.this, MainRetouchInfoActivity.class);
                startActivity(intent);
            }
        });
        //석재애몽 도와줘요->>>>>>>>>>> 회원 정보 보여주게 하기

    }
}