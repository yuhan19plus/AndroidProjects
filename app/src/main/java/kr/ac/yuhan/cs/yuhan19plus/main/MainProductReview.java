package kr.ac.yuhan.cs.yuhan19plus.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import kr.ac.yuhan.cs.yuhan19plus.R;

public class MainProductReview extends AppCompatActivity {
    private FirebaseFirestore db;
    private FirebaseAuth userDBFirebaseAuth;
    private FirebaseUser userDBFirebaseUser;
    private EditText reviewContentEditText;
    private RatingBar ratingBar;
    private TextView ratingTextView;
    private Button submitReviewButton;
    private ImageView backBtn;
    private int productCode2;
    private String currentMemberId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_product_review);

        // 파이어베이스 초기화
        FirebaseApp.initializeApp(this);
        db = FirebaseFirestore.getInstance();

        // 뷰 초기화
        backBtn = findViewById(R.id.backBtn);
        reviewContentEditText = findViewById(R.id.reviewContentEditText);
        ratingBar = findViewById(R.id.ratingBar);
//        ratingTextView = findViewById(R.id.ratingTextView);
        submitReviewButton = findViewById(R.id.submitReviewButton);

        // 인텐트에서 제품 코드 가져오기
        Intent intent = getIntent();
        productCode2 = intent.getIntExtra("productCode", 1);

        // 평점 변경 리스너 설정
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
//                ratingTextView.setText("Rating: " + rating);
            }
        });

        // 리뷰 제출 버튼 클릭 리스너 설정
        submitReviewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitReview();
            }
        });

        // 뒤로가기 버튼 클릭 리스너 설정
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void submitReview() {
        // 이전 리뷰의 마지막 ID를 조회하고 다음 번호를 계산
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("product_review")
                .orderBy("reviewId", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    int nextDocumentId;
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot lastDocument = queryDocumentSnapshots.getDocuments().get(0);
                        nextDocumentId = lastDocument.getLong("reviewId").intValue() + 1;
                    } else {
                        nextDocumentId = 1; // 문서가 없으면 첫 번째 문서로 시작
                    }

                    // 새로운 리뷰 데이터 생성
                    String reviewContent = reviewContentEditText.getText().toString().trim();
                    float ratingScore = ratingBar.getRating();
                    userDBFirebaseAuth = FirebaseAuth.getInstance();
                    userDBFirebaseUser = userDBFirebaseAuth.getCurrentUser();
                    currentMemberId = userDBFirebaseUser.getEmail(); // 실제 앱에서는 사용자 ID 사용
//                    currentMemberId = "MemberId";
                    if (reviewContent.isEmpty() || ratingScore == 0) {
                        Toast.makeText(MainProductReview.this, "Please provide a rating and review content", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    int productCode = productCode2;
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    String formattedDate = sdf.format(new Date());

                    Map<String, Object> review = new HashMap<>();
                    review.put("reviewId", nextDocumentId); // 새로운 문서 ID 설정
                    review.put("creationDate", formattedDate);
                    review.put("memberId", currentMemberId);
                    review.put("ratingScore", ratingScore);
                    review.put("reviewContent", reviewContent);
                    review.put("productCode", productCode);

                    // Firestore에 리뷰 추가
                    db.collection("product_review").add(review)
                            .addOnSuccessListener(documentReference -> {
                                Toast.makeText(MainProductReview.this, "리뷰 등록 완료", Toast.LENGTH_SHORT).show();
                                finish(); // 리뷰 등록이 완료되면 이전 화면으로 돌아감
                            })
                            .addOnFailureListener(e -> Toast.makeText(MainProductReview.this, "Error adding review: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(MainProductReview.this, "Error fetching document ID: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
