package kr.ac.yuhan.cs.yuhan19plus.main;

import static android.content.ContentValues.TAG;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

import kr.ac.yuhan.cs.yuhan19plus.R;
import kr.ac.yuhan.cs.yuhan19plus.main.adapter.ProductReviewAdapter;
import kr.ac.yuhan.cs.yuhan19plus.main.data.ProductReviewData;
/** 담당자 : 임성준, 이석재, 임성준
 * 초기작성 및 상품평점계산, 상품평점목록 및 알림창 기능 : 임성준
 * 해당 상품코드의 상품정보 가져오기 : 오자현
 * 회원처리기능 : 이석재
 * */
public class MainProductDetail extends AppCompatActivity {
    private ProductReviewAdapter adapter; // 리뷰 어댑터
    private ArrayList<ProductReviewData> reviewList; // 리뷰 리스트
    private TextView productAverage; // 평균 평점 텍스트뷰
    private String currentMemberId; // 현재 사용자 ID
    private FirebaseFirestore db; // Firestore 인스턴스
    private FirebaseAuth userDBFirebaseAuth;
    private FirebaseUser userDBFirebaseUser;
    private int productCode; // 상품 코드

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_product_item_detail);

        reviewList = new ArrayList<>();
        adapter = new ProductReviewAdapter(this, reviewList);

        ListView reviewListView = findViewById(R.id.reviewListView);
        reviewListView.setAdapter(adapter);

        // Firebase Firestore 초기화
        db = FirebaseFirestore.getInstance();
        Button productReviewBtn = findViewById(R.id.productReviewBtn);

        //상세 상품 페이지 창 닫기 <이정민>
        ImageView backlist = findViewById(R.id.Back_ListView_Btn);
        backlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 이미지, 상품명, 가격 객체 가져오기
        ImageView productImageView = findViewById(R.id.Item_Image_Detail);
        TextView productCategoryTextView = findViewById(R.id.Item_Category_Detail);
        TextView productNameTextView = findViewById(R.id.Item_Name_Detail);
        TextView productPriceTextView = findViewById(R.id.Item_Price_Detail);
        productAverage = findViewById(R.id.productAverage); // 상품 평점

        // 인텐트에서 데이터 가져오기
        String productImage = getIntent().getStringExtra("productImage");
        String productCategory = getIntent().getStringExtra("productCategory");
        String productName = getIntent().getStringExtra("productName");
        int productPrice = getIntent().getIntExtra("productPrice", 0);
        productCode = getIntent().getIntExtra("productCode", 1);
        Log.d("productDetailCode", productCode + " " + productCategory + " " + productImage + " " + productPrice);

        // 각 객체에 데이터 설정
        if (productImage != null) {
            Glide.with(this)
                    .load(productImage)
                    .placeholder(R.drawable.icon)
                    .error(R.drawable.icon) // 기본 이미지 설정
                    .into(productImageView);
        }
        if (productName != null) {
            productNameTextView.setText(productName);
        }
        // int 타입은 null 값을 가질 수 없으므로 null 체크가 필요하지 않습니다. // int 타입을 String으로 변환하여 설정
        productPriceTextView.setText(String.valueOf(productPrice)+" 원");
        if (productCategory != null) {
            productCategoryTextView.setText(productCategory);
        }

        // 리뷰 작성 버튼 클릭 리스너 설정
        productReviewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userDBFirebaseAuth = FirebaseAuth.getInstance();
                userDBFirebaseUser = userDBFirebaseAuth.getCurrentUser();
                if(userDBFirebaseUser == null){
                    Toast.makeText(MainProductDetail.this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(userDBFirebaseUser.isAnonymous()) {
                    Toast.makeText(MainProductDetail.this, "비회원은 리뷰 작성이 불가합니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
                String uid = userDBFirebaseUser.getUid();
                db.collection("payments").whereEqualTo("uid", uid).get().addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        boolean productFound = false;
                        for(QueryDocumentSnapshot document : task.getResult()){
                            Map<String, Number> products = (Map<String, Number>) document.get("products");

                            if(products != null){
                                for(String key : products.keySet()){
                                    if (key.equals(productName)){
                                        productFound = true;
                                        break;
                                    }
                                }
                            }
                            if(productFound){
                                break;
                            }
                        }
                        if(!productFound){
                            Toast.makeText(MainProductDetail.this, "상품 구매 이력이 없어 리뷰 작성이 불가합니다.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Intent intent = new Intent(MainProductDetail.this, MainProductReview.class);
                        intent.putExtra("productCode", productCode);
                        Log.d("pro", productCode+"");
                        startActivity(intent);
                    }
                    else{
                        Log.w(TAG, "Error getting documents.", task.getException());
                    }
                });
            }
        });

        // ListView 아이템 클릭 리스너 설정
        reviewListView.setOnItemClickListener((parent, view, position, id) -> {
            ProductReviewData selectedReview = reviewList.get(position);
            showReviewDetailsDialog(selectedReview);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProductReviews();
    }

    // Firestore에서 리뷰 로드
    private void loadProductReviews() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference productReviewsRef = db.collection("product_review");

        productReviewsRef.whereEqualTo("productCode", productCode)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            reviewList.clear();
                            int totalRatingScore = 0;
                            int reviewCount = 0;

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                try {
                                    // 파이어베이스에서 가져온 문서의 필드 값을 사용하여 ProductReviewData 객체를 생성하고 리스트에 추가합니다.
                                    String memberId = document.getString("memberId");
                                    Long ratingScoreLong = document.getLong("ratingScore");
                                    String creationDate = document.getString("creationDate");
                                    String reviewContent = document.getString("reviewContent");

                                    // 로그로 각 필드 값을 출력하여 확인합니다.
                                    Log.d(TAG, "문서 data: " + document.getData());

                                    if (memberId == null || ratingScoreLong == null || creationDate == null || reviewContent == null) {
                                        Log.e(TAG, "Null 데이터: " + document.getId());
                                        continue;
                                    }

                                    int ratingScore = ratingScoreLong.intValue();

                                    ProductReviewData reviewData = new ProductReviewData(memberId, ratingScore, creationDate, reviewContent, productCode);
                                    reviewList.add(reviewData);

                                    // 총 평점과 리뷰 수를 증가시킵니다.
                                    totalRatingScore += ratingScore;
                                    reviewCount++;

                                    Log.d(TAG, "Review added: " + reviewData.toString());
                                    Log.d(TAG, "Total reviews loaded: " + reviewList.size());
                                } catch (Exception e) {
                                    Log.e(TAG, "Error parsing document: " + document.getId(), e);
                                }
                            }
                            adapter.notifyDataSetChanged();
                            Log.d(TAG, "Total reviews loaded: " + reviewList.size());

                            // 평균 평점을 계산하고 업데이트합니다.
                            updateProductAverage(totalRatingScore, reviewCount);
                        } else {
                            Log.e(TAG, "Error getting documents: ", task.getException());
                            Toast.makeText(MainProductDetail.this, "Error fetching product reviews", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    // 평균 평점 업데이트
    private void updateProductAverage(int totalRatingScore, int reviewCount) {
        if (reviewCount == 0) {
            productAverage.setText("0");
        } else {
            float averageRating = (float) totalRatingScore / reviewCount;
            productAverage.setText(String.format("%.1f", averageRating));
        }
    }

    // 리뷰 상세보기 다이얼로그 표시
    private void showReviewDetailsDialog(ProductReviewData reviewData) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("상품리뷰");

        String message = "<html>"
                + "<body>"
                + "<p><b>작성자</b>\t\t\t\t" + reviewData.getMemberId() + "</p>"
                + "<p><b>평점</b>\t\t\t\t\t\t" + reviewData.getRatingScore() + "</p>"
                + "<p><b>작성날짜</b>\t\t" + reviewData.getCreationDate() + "</p>"
                + "<p><b>리뷰 내용</b><br>" + reviewData.getReviewContent() + "</p>"
                + "</body>"
                + "</html>";

        builder.setMessage(Html.fromHtml(message, Html.FROM_HTML_MODE_LEGACY));

        userDBFirebaseAuth = FirebaseAuth.getInstance();
        currentMemberId = userDBFirebaseAuth.getCurrentUser().getEmail(); // 예시, 실제로는 현재 로그인된 사용자 ID를 가져와야 함
//        currentMemberId = "MemberId"; // 예시, 실제로는 현재 로그인된 사용자 ID를 가져와야 함

        // 현재 사용자가 작성한 리뷰일 경우 삭제 버튼 추가
        if (reviewData.getMemberId().equals(currentMemberId)) {
            builder.setNegativeButton("삭제", (dialog, which) -> {
                deleteReview(reviewData);
            });
        }

        builder.setPositiveButton("닫기", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // 리뷰 삭제
    private void deleteReview(ProductReviewData reviewData) {
        db.collection("product_review")
                .whereEqualTo("productCode", reviewData.getProductCode())
                .whereEqualTo("memberId", reviewData.getMemberId())
                .whereEqualTo("creationDate", reviewData.getCreationDate())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                db.collection("product_review").document(document.getId())
                                        .delete()
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(MainProductDetail.this, "리뷰가 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                                            reviewList.remove(reviewData); // 선택한 리뷰를 목록에서 제거
                                            adapter.notifyDataSetChanged(); // 어댑터에 변경 사항을 알림
                                        })
                                        .addOnFailureListener(e -> {
                                            Log.e(TAG, "리뷰 삭제 오류", e);
                                            Toast.makeText(MainProductDetail.this, "리뷰 삭제 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                                        });
                            }
                        } else {
                            Log.e(TAG, "리뷰 검색 오류", task.getException());
                            Toast.makeText(MainProductDetail.this, "리뷰를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}