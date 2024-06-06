package kr.ac.yuhan.cs.yuhan19plus.main;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import kr.ac.yuhan.cs.yuhan19plus.MainActivity;
import kr.ac.yuhan.cs.yuhan19plus.R;
import kr.ac.yuhan.cs.yuhan19plus.main.adapter.MainProductPageAdapter;
import kr.ac.yuhan.cs.yuhan19plus.main.data.MainProductData;
/** 담당자 : 임성준, 오자현, 이정민
 * 초기 작성 : 임성준
 * 프론트 기능 : 이정민
 * 상품기능 : 오자현
 * */
public class MainProductActivity extends AppCompatActivity {

    private ImageView mainActivityProductSearchbtn;
    private EditText mainActivityProductEditTextFieldSearchProductName;
    private ArrayList<MainProductData> productDataList = new ArrayList<>(); // 상품 정보를 담을 리스트

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_product);

        mainActivityProductEditTextFieldSearchProductName = findViewById(R.id.mainActivityProductEditTextFieldSearchProductName);
        mainActivityProductSearchbtn = findViewById(R.id.mainActivityProductSearchBtn);
        mainActivityProductSearchbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadItemsFromFireStore();
            }
        });

        //상품 페이지 창 닫기 및 뷰페이저2 탭레이아웃 설정 <이정민>
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
                            spannableString = new SpannableString("주방 도구");
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

    // 파이어베이스에서 상품명으로 검색하고 데이터를 읽어오는 메서드 (오자현)
    void loadItemsFromFireStore() {
        // Firestore 데이터베이스 인스턴스를 가져옵니다.
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Task<QuerySnapshot> query;

        // 검색 텍스트가 비어있지 않으면, 해당 상품 이름으로 필터링된 쿼리를 실행합니다.
        if (mainActivityProductEditTextFieldSearchProductName != null) {
            // 검색 텍스트가 있을 경우, 해당 상품 이름으로 필터링된 쿼리 실행
            String text = mainActivityProductEditTextFieldSearchProductName.getText().toString();
            query = db.collection("products")
                    .whereEqualTo("productName", text)
                    .get();
            mainActivityProductEditTextFieldSearchProductName.setText("");
        } else {
            // 검색 텍스트가 없을 경우
            Toast.makeText(MainProductActivity.this, "검색창에 찾으시는 상품이름을 입력하세요", Toast.LENGTH_SHORT).show();
            query = db.collection("products").get();

        }

        // 쿼리가 완료된 후 실행될 리스너를 추가합니다.
        query.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().isEmpty()) {
                        // 검색 결과가 없을 경우
                        Toast.makeText(MainProductActivity.this, "loadItemsFromFireStore()없는 데이터입니다.", Toast.LENGTH_SHORT).show();
                    } else {
                        // 기존의 제품 리스트를 클리어합니다.
                        productDataList.clear();
                        // 쿼리 결과를 반복하여 각 문서를 처리합니다
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // 각 필드를 가져와서 변수에 저장합니다.
                            int code = document.getLong("productCode").intValue();
                            String productName = document.getString("productName");
                            String category = document.getString("productCategory");
                            String imageUrl = document.getString("productImage");
                            int price = document.getLong("productPrice").intValue();


                            // 이미지 URL이 null이거나 비어있으면 기본 이미지 URL을 사용합니다.
                            if (imageUrl == null || imageUrl.isEmpty()) {
                                imageUrl = "R.drawable.icon"; // 기본 이미지 URL 사용
                            }

                            // 로드된 이미지 URL을 로그에 출력합니다.
                            Log.d("DatabaseViewActivity", "Loaded imageUrl: " + imageUrl);
                            // 새로운 인텐트를 생성하고 데이터를 추가합니다.
                            Intent intent = new Intent(MainProductActivity.this, MainProductDetail.class);
                            intent.putExtra("productCode", code);
                            intent.putExtra("productName", productName);
                            intent.putExtra("productCategory", category);
                            intent.putExtra("productImage", imageUrl);
                            intent.putExtra("productPrice", price);

                            // 새로운 액티비티를 시작합니다.
                            startActivity(intent);
                            break; // 첫 번째 결과만 사용한다고 가정하고 루프를 종료합니다.
                        }

                    }
                } else {
                    // 쿼리 실행 중 오류가 발생하면 로그에 출력합니다.
                    Log.e("DatabaseViewActivity", "Error getting documents: ", task.getException());
                }
            }
        });
    }

}