package kr.ac.yuhan.cs.yuhan19plus.main;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import kr.ac.yuhan.cs.yuhan19plus.R;
import kr.ac.yuhan.cs.yuhan19plus.main.adapter.MainProductPageAdapter;

public class MainProductActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_product);

        // 메인 페이지 이동 부분
        ImageView imageView = findViewById(R.id.Close_Product_Btn);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // ViewPager2 및 어댑터 설정
        ViewPager2 viewPager = findViewById(R.id.viewPager);
        MainProductPageAdapter sectionsPagerAdapter = new MainProductPageAdapter(this);
        viewPager.setAdapter(sectionsPagerAdapter);

        // TabLayout 설정 및 ViewPager와 연결
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    SpannableString spannableString;
                    // 각 탭의 이름 설정
                    switch (position) {
                        case 0:
                            spannableString = new SpannableString("생필품");
                            break;
                        case 1:
                            spannableString = new SpannableString("문구류");
                            break;
                        case 2:
                            spannableString = new SpannableString("주방용품");
                            break;
                        default:
                            spannableString = new SpannableString("");  // 기본값 설정
                            break;
                    }
                    spannableString.setSpan(new ForegroundColorSpan(Color.WHITE), 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    spannableString.setSpan(new RelativeSizeSpan(1.5f), 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                    tab.setText(spannableString);
                }
        ).attach();
    }
}