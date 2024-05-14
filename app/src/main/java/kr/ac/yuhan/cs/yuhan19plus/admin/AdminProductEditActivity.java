package kr.ac.yuhan.cs.yuhan19plus.admin;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

import kr.ac.yuhan.cs.yuhan19plus.R;
import kr.ac.yuhan.cs.yuhan19plus.admin.util.ChangeMode;
import soup.neumorphism.NeumorphCardView;
import soup.neumorphism.NeumorphImageView;

public class AdminProductEditActivity extends AppCompatActivity {
    private FirebaseFirestore dbFirestore;
    private EditText editProductName2, editProductPrice2, editProductStock2;
    private Button buttonUpdateProduct2;
    private NeumorphImageView backBtn;
    private RadioGroup radioGroup2;
    private String ProductCategory2;
    private static final int PICK_FILE_REQUEST = 2; // 파일 선택을 위한 요청 코드
    private ImageView imageViewProduct2;
    private Uri fileUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_activity_product_edit_page);
        LinearLayout productEditPage = findViewById(R.id.productEditPage);

        int backgroundColor = Color.rgb(97, 97, 97);

        // Setting BackgroundColor
        View backgroundView = getWindow().getDecorView().getRootView();
        backgroundView.setBackgroundColor(backgroundColor);

        // Intent에서 데이터 추출
        Intent intent = getIntent();

        String categoryStr1 = getString(R.string.product_category1);
        String categoryStr2 = getString(R.string.product_category2);
        String categoryStr3 = getString(R.string.product_category3);

        // 추출한 데이터 사용가능하게 변수로 만들기
        int productCode = intent.getIntExtra("productCode", 0);
        String productName = intent.getStringExtra("productName");
        // 이미지는 특별한 처리가 필요
        String productImage = intent.getStringExtra("productImage");
        // 가격과 재고는 int, double 또는 다른 숫자 타입일 수 있습니다. 적절한 메서드 사용
        int productPrice = intent.getIntExtra("productPrice", 0); // 기본값을 설정
        int productStock = intent.getIntExtra("productStock", 0);
        String category = intent.getStringExtra("category");

        // UI 컴포넌트 초기화
        NeumorphCardView productEditCardView = findViewById(R.id.productEditCardView);
        NeumorphCardView editTextProductName = findViewById(R.id.editTextProductName);
        NeumorphCardView editTextProductImage = findViewById(R.id.editTextProductImage);
        NeumorphCardView editTextProductPrice = findViewById(R.id.editTextProductPrice);
        NeumorphCardView editTextProductStock = findViewById(R.id.editTextProductStock);
        NeumorphCardView editTextProductCategory = findViewById(R.id.editTextProductCategory);

        editProductName2 = findViewById(R.id.editProductName2);
        editProductPrice2 = findViewById(R.id.editProductPrice2);
        editProductStock2 = findViewById(R.id.editProductStock2);
        buttonUpdateProduct2 = findViewById(R.id.buttonUpdateProduct2);
        backBtn = findViewById(R.id.backBtn);
        imageViewProduct2 = findViewById(R.id.imageViewProduct2);
        radioGroup2 = findViewById(R.id.categroryRadioGroup2);
        editProductName2.setText(productName);
        editProductPrice2.setText(String.valueOf(productPrice)); // int 값을 String으로 변환
        editProductStock2.setText(String.valueOf(productStock)); // int 값을 String으로 변환

        // EditMode
        ChangeMode.applySubTheme(productEditPage, 1);

        // Product Edit Page Btn
        ChangeMode.setColorFilterDark(buttonUpdateProduct2);
        ChangeMode.setDarkShadowCardView(buttonUpdateProduct2);
        ChangeMode.setColorFilterDark(backBtn);
        ChangeMode.setDarkShadowCardView(backBtn);

        // Product Edit Page CardView content
        ChangeMode.setDarkShadowCardView(productEditCardView);
        ChangeMode.setDarkShadowCardView(editTextProductName);
        ChangeMode.setDarkShadowCardView(editTextProductImage);
        ChangeMode.setDarkShadowCardView(editTextProductPrice);
        ChangeMode.setDarkShadowCardView(editTextProductStock);
        ChangeMode.setDarkShadowCardView(editTextProductCategory);

        dbFirestore = FirebaseFirestore.getInstance();

        radioGroup2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.categoryRadioBtns1){
                    ProductCategory2 = categoryStr1;
                } else if (checkedId == R.id.categoryRadioBtns2) {
                    ProductCategory2 = categoryStr2;
                }else if (checkedId ==R.id.categoryRadioBtns3) {
                    ProductCategory2 = categoryStr3;
                }
            }
        });

        //카테고리를 클릭하지 않고 넘기는 경우 기본값으로 지정ㅇ
        if(ProductCategory2 == null){
            ProductCategory2 = categoryStr1;
        }

        // 읽어온 값으로 세팅
        if(category == categoryStr1){
            radioGroup2.check(R.id.categoryRadioBtns1);
        } else if( category == categoryStr2){
            radioGroup2.check(R.id.categoryRadioBtns2);
        }else if( category == categoryStr3){
            radioGroup2.check(R.id.categoryRadioBtns3);
        }

        // 이미지 세팅
        if (productImage != null && !productImage.isEmpty()) {
            imageViewProduct2.setImageURI(Uri.parse(productImage));
            Glide.with(this).load(productImage).into(imageViewProduct2);
        }

        //이미지를 누르면 파일선택기를 연다.
        imageViewProduct2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileManager();
            }
        });

        //업데이트버튼메서드
        buttonUpdateProduct2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // productCode를 이용하여 데이터베이스에 연동하고 업데이트하는 메서드
                updateProductByCode(productCode);

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

    private void openFileManager() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*"); // 모든 유형의 파일을 허용
        startActivityForResult(intent, PICK_FILE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri selectedFileUri = data.getData();
            fileUri = data.getData();
            // ImageView에 이미지 로드
            imageViewProduct2.setImageURI(selectedFileUri);
        }
    }


    private void updateProductInFirestore(String documentId) {
        String updatedName = editProductName2.getText().toString();
        String updatedPriceStr = editProductPrice2.getText().toString();
        String updatedStockStr = editProductStock2.getText().toString();
        String updatedCategory = ProductCategory2;

        int updatedPrice = Integer.parseInt(updatedPriceStr);
        int updatedStock = Integer.parseInt(updatedStockStr);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference fileRef = storageRef.child("files/" + System.currentTimeMillis());

        Map<String, Object> productUpdate = new HashMap<>();
        productUpdate.put("productName", updatedName); // 상품명 업데이트
        productUpdate.put("price", updatedPrice); // 가격 업데이트
        productUpdate.put("stock", updatedStock); // 재고 업데이트
        productUpdate.put("category", updatedCategory); // 카테고리 업데이트 추가


        // 파일이 선택되었는지 확인
        if (fileUri != null) {
            // 파일 업로드 로직
            fileRef.putFile(fileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            // 파일 URL을 productUpdate 맵에 추가하고 문서를 업데이트
                            productUpdate.put("imageUrl", uri.toString());
                            updateDocument(documentId, productUpdate);
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w("Upload File", "Error uploading file", e);
                    Toast.makeText(AdminProductEditActivity.this, "파일 업로드 실패", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // 파일 업로드 없이 문서 업데이트
            updateDocument(documentId, productUpdate);
        }

    }
    // Firestore 문서 업데이트를 처리하는 별도의 메소드
    private void updateDocument(String documentId, Map<String, Object> productUpdate) {
        dbFirestore.collection("products").document(documentId)
                .update(productUpdate)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Update Firestore", "DocumentSnapshot successfully updated.");
                        Toast.makeText(AdminProductEditActivity.this, "상품 정보가 성공적으로 업데이트되었습니다.", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Update Firestore", "Error updating document", e);
                        Toast.makeText(AdminProductEditActivity.this, "상품 정보 업데이트 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateProductByCode(final int Code) {
        dbFirestore.collection("products")
                .whereEqualTo("productCode", Code)// 여기에 들어가는 변수의 타입과 파이어베이스의 필드의 타입이 일치해야함
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if (querySnapshot != null && !querySnapshot.isEmpty()) {
                                for (QueryDocumentSnapshot document : querySnapshot) {
                                    Log.d("Firestore Success", "Document found with ID: " + document.getId());
                                    updateProductInFirestore(document.getId());
                                    Toast.makeText(AdminProductEditActivity.this, "서버와 연동으로 인해 약간의 딜레이가 발생할 수 있습니다.", Toast.LENGTH_SHORT).show();
                                    finishActivityWithResult();
                                }
                            } else {
                                Log.d("Firestore Empty", "No documents found at all");
                            }
                        } else {
                            Log.d("Firestore Error", "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

    private void finishActivityWithResult() {
        Intent database_viewIntent = new Intent(AdminProductEditActivity.this, AdminMainActivity.class);
        database_viewIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(database_viewIntent);
        finish();
        finish();
    }
}