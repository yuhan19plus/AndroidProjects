package kr.ac.yuhan.cs.yuhan19plus.AndroidProjects;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainPageActivity extends AppCompatActivity implements Button.OnClickListener{
    private View firstView;
    private View secondView;
    private View thirdView;
    private View fourthView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainpage);

        init();
    }

    // 뷰 초기화
    public void init() {
        firstView = findViewById(R.id.View01);
        secondView = findViewById(R.id.View02);
        thirdView = findViewById(R.id.View03);
        fourthView = findViewById(R.id.View04);
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.firstBtn) {
            firstView.setVisibility(View.VISIBLE);
            secondView.setVisibility(View.GONE);
            thirdView.setVisibility(View.GONE);
            fourthView.setVisibility(View.GONE);
        }
        else if (v.getId()==R.id.secondBtn) {
            secondView.setVisibility(View.VISIBLE);
            thirdView.setVisibility(View.GONE);
            fourthView.setVisibility(View.GONE);
            firstView.setVisibility(View.GONE);
        }
        else if (v.getId()==R.id.thirdBtn) {
            firstView.setVisibility(View.GONE);
            secondView.setVisibility(View.GONE);
            thirdView.setVisibility(View.GONE);
            fourthView.setVisibility(View.GONE);
        }
        else if (v.getId()==R.id.fourthBtn) {
            thirdView.setVisibility(View.VISIBLE);
            fourthView.setVisibility(View.GONE);
            secondView.setVisibility(View.GONE);
            firstView.setVisibility(View.GONE);
        }
        else if (v.getId()==R.id.fifthBtn) {
            fourthView.setVisibility(View.VISIBLE);
            firstView.setVisibility(View.GONE);
            secondView.setVisibility(View.GONE);
            thirdView.setVisibility(View.GONE);
        }

    }
}

