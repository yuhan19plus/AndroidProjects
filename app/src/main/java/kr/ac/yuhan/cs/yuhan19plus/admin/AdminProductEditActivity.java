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
    // Firestore 인스턴스와 UI 요소 선언
    private FirebaseFirestore dbFirestore;
    private EditText editProductName2, editProductPrice2, editProductStock2;
    private Button buttonUpdateProduct2;
    private NeumorphImageView backBtn;
    private RadioGroup radioGroup2;
    private String ProductCategory2;
    private static final int PICK_FILE_REQUEST = 2; // 파일 선택을 위한 요청 코드: startActivityForResult() 메서드에서 파일 선택 작업을 시작할 때 이 요청 코드를 사용하여 결과를 식별합니다.
    private ImageView imageViewProduct2;
    private Uri fileUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_activity_product_edit_page);
        LinearLayout productEditPage = findViewById(R.id.productEditPage);

        int backgroundColor = Color.rgb(97, 97, 97);

        // 배경 색 설정
        View backgroundView = getWindow().getDecorView().getRootView();
        backgroundView.setBackgroundColor(backgroundColor);

        // Intent에서 데이터 추출
        Intent intent = getIntent();

        // 카테고리 문자열을 리소스에서 가져오기
        String categoryStr1 = getString(R.string.product_category1);
        String categoryStr2 = getString(R.string.product_category2);
        String categoryStr3 = getString(R.string.product_category3);

        // 추출한 데이터 사용가능하게 변수로 만들기
        int productCode = intent.getIntExtra("productCode", 0);
        String productName = intent.getStringExtra("productName");
        String productImage = intent.getStringExtra("productImage");
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

        // 제품 정보를 UI에 세팅
        editProductName2.setText(productName);
        editProductPrice2.setText(String.valueOf(productPrice)); // int 값을 String으로 변환
        editProductStock2.setText(String.valueOf(productStock)); // int 값을 String으로 변환

        // EditMode 설정
        ChangeMode.applySubTheme(productEditPage, 1);

        // Product Edit Page Btn 설정
        ChangeMode.setColorFilterDark(buttonUpdateProduct2);
        ChangeMode.setDarkShadowCardView(buttonUpdateProduct2);
        ChangeMode.setColorFilterDark(backBtn);
        ChangeMode.setDarkShadowCardView(backBtn);

        // Product Edit Page CardView content 설정
        ChangeMode.setDarkShadowCardView(productEditCardView);
        ChangeMode.setDarkShadowCardView(editTextProductName);
        ChangeMode.setDarkShadowCardView(editTextProductImage);
        ChangeMode.setDarkShadowCardView(editTextProductPrice);
        ChangeMode.setDarkShadowCardView(editTextProductStock);
        ChangeMode.setDarkShadowCardView(editTextProductCategory);

        // Firestore 인스턴스 초기화
        dbFirestore = FirebaseFirestore.getInstance();

        // 라디오 그룹의 체크 변경 리스너 설정
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

        // 카테고리를 클릭하지 않고 넘기는 경우 기본값으로 설정
        if(ProductCategory2 == null){
            ProductCategory2 = categoryStr1;
        }

        // 읽어온 값으로 라디오 버튼 세팅
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

        // 이미지를 누르면 파일 선택기를 엽니다.
        imageViewProduct2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileManager();
            }
        });

        // 업데이트 버튼 클릭 리스너 설정
        buttonUpdateProduct2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // productCode를 이용하여 데이터베이스에 연동하고 업데이트하는 메서드 호출
                updateProductByCode(productCode);

            }
        });

        // 뒤로 가기 버튼 클릭 리스너 설정
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 클릭 시 버튼 모양을 변경
                backBtn.setShapeType(1);
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

    // 파일 관리자를 여는 메서드 (오자현)
    private void openFileManager() {
        // ACTION_OPEN_DOCUMENT 액션을 사용하여 파일을 열기 위한 인텐트를 생성합니다.
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        // CATEGORY_OPENABLE 카테고리를 추가하여 파일을 열 수 있는 앱만 표시되도록 합니다.
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        // 모든 유형의 파일을 허용하도록 파일 타입을 설정합니다.
        intent.setType("*/*");
        // 파일 선택 작업을 시작하고, 결과를 식별하기 위해 PICK_FILE_REQUEST 요청 코드를 사용합니다.
        startActivityForResult(intent, PICK_FILE_REQUEST);
    }

    // 파일 선택 결과를 처리하는 메서드 (오자현)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 파일 선택 요청 코드와 결과가 성공인지 확인하고, 인텐트 데이터가 null이 아닌지 확인합니다.
        if (requestCode == PICK_FILE_REQUEST && resultCode == RESULT_OK && data != null) {
            // 선택한 파일의 URI를 가져옵니다.
            Uri selectedFileUri = data.getData();
            // fileUri 변수에 선택한 파일의 URI를 저장합니다.
            fileUri = data.getData();
            // 선택한 파일의 URI를 ImageView에 로드하여 이미지를 표시합니다.
            imageViewProduct2.setImageURI(selectedFileUri);
        }
    }


    // 파이어베이스의 데이터를 업데이트하는 메서드 (오자현)
    private void updateProductInFirestore(String documentId) {
        // UI 입력 필드에서 업데이트할 값을 가져옵니다.
        String updatedName = editProductName2.getText().toString();
        String updatedPriceStr = editProductPrice2.getText().toString();
        String updatedStockStr = editProductStock2.getText().toString();
        String updatedCategory = ProductCategory2;

        // 문자열로 입력된 가격과 재고를 정수로 변환합니다.
        int updatedPrice = Integer.parseInt(updatedPriceStr);
        int updatedStock = Integer.parseInt(updatedStockStr);

        // FirebaseStorage 인스턴스를 가져옵니다.
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference fileRef = storageRef.child("files/" + System.currentTimeMillis());

        // 업데이트할 제품 정보를 맵에 저장합니다.
        Map<String, Object> productUpdate = new HashMap<>();
        productUpdate.put("productName", updatedName); // 상품명 업데이트
        productUpdate.put("price", updatedPrice); // 가격 업데이트
        productUpdate.put("stock", updatedStock); // 재고 업데이트
        productUpdate.put("category", updatedCategory); // 카테고리 업데이트 추가

        // 파일이 선택되었는지 확인합니다.
        if (fileUri != null) {
            // 파일을 업로드합니다.
            fileRef.putFile(fileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // 파일 업로드가 성공하면 파일 URL을 가져와서 업데이트할 맵에 추가합니다.
                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            // 파일 URL을 productUpdate 맵에 추가하고 문서를 업데이트
                            productUpdate.put("imageUrl", uri.toString());
                            // Firestore 문서를 업데이트합니다.
                            updateDocument(documentId, productUpdate);
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    // 파일 업로드에 실패하면 로그를 출력하고 사용자에게 알림을 표시합니다.
                    Log.w("Upload File", "Error uploading file", e);
                    Toast.makeText(AdminProductEditActivity.this, "파일 업로드 실패", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // 파일 업로드 없이 문서를 업데이트합니다.
            updateDocument(documentId, productUpdate);
        }

    }

    // Firestore 문서 업데이트를 처리하는 별도의 메소드 (오자현)
    private void updateDocument(String documentId, Map<String, Object> productUpdate) {
        // Firestore에서 'products' 컬렉션의 지정된 문서를 업데이트합니다.
        dbFirestore.collection("products").document(documentId)
                .update(productUpdate)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // 문서 업데이트가 성공했을 때 실행됩니다.
                        Log.d("Update Firestore", "DocumentSnapshot successfully updated.");
                        Toast.makeText(AdminProductEditActivity.this, "상품 정보가 성공적으로 업데이트되었습니다.", Toast.LENGTH_SHORT).show();
                        // 액티비티를 종료합니다.
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // 문서 업데이트가 실패했을 때 실행됩니다.
                        Log.w("Update Firestore", "Error updating document", e);
                        Toast.makeText(AdminProductEditActivity.this, "상품 정보 업데이트 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // 상품코드를 이용하여 상품데이터를 업데이트하는 메소드 (오자현)
    private void updateProductByCode(final int Code) {
        // Firestore에서 'products' 컬렉션에서 productCode가 일치하는 문서를 조회합니다.
        dbFirestore.collection("products")
                .whereEqualTo("productCode", Code)// 변수의 타입과 Firestore 필드의 타입이 일치해야 합니다.
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if (querySnapshot != null && !querySnapshot.isEmpty()) {
                                // 일치하는 문서가 하나 이상 존재할 경우
                                for (QueryDocumentSnapshot document : querySnapshot) {
                                    Log.d("Firestore Success", "Document found with ID: " + document.getId());
                                    // 문서 ID를 이용하여 Firestore에서 상품 정보를 업데이트합니다.
                                    updateProductInFirestore(document.getId());
                                    Toast.makeText(AdminProductEditActivity.this, "서버와 연동으로 인해 약간의 딜레이가 발생할 수 있습니다.", Toast.LENGTH_SHORT).show();
                                    // 업데이트 후 결과 처리를 위해 액티비티를 종료합니다.
                                    finishActivityWithResult();
                                }
                            } else {
                                // 일치하는 문서가 없을 경우 로그를 출력합니다.
                                Log.d("Firestore Empty", "No documents found at all");
                            }
                        } else {
                            // 문서 조회 중 오류가 발생할 경우 로그를 출력합니다.
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