package kr.ac.yuhan.cs.yuhan19plus.main;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

import kr.ac.yuhan.cs.yuhan19plus.R;
import kr.ac.yuhan.cs.yuhan19plus.admin.data.ProductData;
import kr.ac.yuhan.cs.yuhan19plus.main.adapter.MainScanAdpter;

public class MainActivityProductScan extends AppCompatActivity {
    private DecoratedBarcodeView barcodeScannerView;
    private CaptureManager capture;
    private FirebaseFirestore userDBFireStore;
    private FirebaseAuth userDBFirebaseAuth;
    private FirebaseUser userDBFirebaseUser;
    private int pointsUsed = 0; // 포인트 사용 정보를 저장할 변수

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_product_scan);

        Log.d("ScanQR", "ListView set with dummy adapter");
        barcodeScannerView = (DecoratedBarcodeView) findViewById(R.id.decorated_bar_code_view);

        capture = new CaptureManager(this, barcodeScannerView);
        capture.initializeFromIntent(this.getIntent(), savedInstanceState);
        capture.decode();
        barcodeScannerView.decodeContinuous(new BarcodeCallback() {
            @Override
            public void barcodeResult(BarcodeResult result) {
                Log.d("ScanQR", "Scanned QR Data: " + result.getText());
                capture.onPause();  // Properly pause decoding
                processScannedData(result.getText());
            }
        });
        Button payButton = findViewById(R.id.paybutton);
        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processPayment();
            }
        });
        Button pointButton = findViewById(R.id.pointBtn);
        pointButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usePoints();
            }
        });
    }

    // 밑에는 파이어베이스와 연결하여 전역변수로 설정한 2개의 변수 필드명, 데이터를 이용하여 조회하고 값을 가져온다.
    // 이후 재고를 수정하고 업데이트한다. 버튼을 눌러서 보낼 때 기존 값 -(선택수량)하게 한다.
    private void processScannedData(String scannedData) {
        if (scannedData != null && scannedData.contains(":")) {
            String[] keyValues = scannedData.split(":");
            if (keyValues.length == 2) {
                String field = keyValues[0];  // 이 부분은 상품 코드를 나타낼 필드, 예를 들어 "productCode"
                String value = keyValues[1];  // 실제 상품 코드
                Log.d("ScanQR", "Field: " + field + ", Value: " + value); // 필드와 값 로그

                fetchDataFromFirestore(field, value, new FirestoreCallback() {
                    @Override
                    public void onCallback(ArrayList<ProductData> products) {
                        ListView listView = findViewById(R.id.scannedlist);
                        MainScanAdpter adapter = (MainScanAdpter) listView.getAdapter();
                        if (adapter == null) {
                            adapter = new MainScanAdpter(MainActivityProductScan.this, new ArrayList<>());
                            listView.setAdapter(adapter);
                        }

                        int parsedValue = Integer.parseInt(value);  // 파싱된 상품 코드
                        boolean isNewProduct = true;

                        for (int i = 0; i < adapter.getCount(); i++) {
                            ProductData existingProduct = adapter.getItem(i);
                            if (existingProduct.getProductCode() == parsedValue) {
                                existingProduct.setProductStock(existingProduct.getProductStock() + 1);
                                isNewProduct = false;
                                break;
                            }
                        }
                        //코드의 진행순서상 여기가 먼저고 뒤에서 매핑하는 바람에 product클래스에서 productCode를 함께 인식하도록   @PropertyName("productCode")를 추가함
                        //나중에 그냥 code를 productcode로 변경해야함
                        if (isNewProduct) {
                            for (ProductData newProduct : products) {
                                if (newProduct.getProductCode() == parsedValue) {
                                    newProduct.setProductStock(1);  // 새 상품의 초기 수량 설정
                                    adapter.add(newProduct);
                                    break;
                                }
                            }
                        }

                        adapter.notifyDataSetChanged();  // 데이터 변경 알림
                        updateTotalPrice();  // 총 금액 업데이트
                    }


                });
            } else {
                Log.d("ScanQR", "Scanned data format is incorrect"); // 데이터 형식 오류 로그
            }
        } else {
            Log.d("ScanQR", "Scanned data is null or does not contain ':'"); // 스캔 데이터 문제 로그
        }

        capture.onResume();
    }

    public interface FirestoreCallback {
        void onCallback(ArrayList<ProductData> products);
    }

    private void fetchDataFromFirestore(String field, String value, FirestoreCallback callback) {
        Log.d("ScanQR", "Starting to fetch data from Firestore. Field: " + field + ", Value: " + value);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        ArrayList<ProductData> products = new ArrayList<>(); // 여기서 선언

        long value1;
        try {
            value1 = Long.parseLong(value); //여기 롱은 파이어베이스에서 검색할
            Log.d("ScanQR", "Value parsed to long successfully.");
        } catch (NumberFormatException e) {
            Log.d("ScanQR", "Error parsing value to long: " + e.getMessage());
            callback.onCallback(new ArrayList<>());
            return;
        }

        Log.d("ScanQR", "Preparing to query Firestore...");

        db.collection("products").whereEqualTo(field, value1).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            ProductData product = document.toObject(ProductData.class);
                            String productName = document.getString("productName");
                            String productImage = document.getString("productImage");
                            String productCategory = document.getString("productCategory");
                            Number productPriceNumber = document.getLong("productPrice"); // Number 타입으로 가져오기

                            long productPrice = productPriceNumber.longValue(); // Number를 long으로 변환

                            product.setProductName(productName);
                            product.setProductImage(productImage);
                            product.setProductCategory(productCategory);
                            product.setProductPrice((int) productPrice);
                            products.add(product);
                            Log.d("ScanQR", "Product code: " + product.getProductCode() + ", Name: " + product.getProductName());

                        }
                    } else {
                        Log.d("ScanQR", "No products matched the query");
                    }

                    callback.onCallback(products);
                })
                .addOnFailureListener(e -> {
                    Log.e("ScanQR", "Error fetching data: " + e.getMessage());
                    callback.onCallback(null);
                });

        Log.d("ScanQR", "Firestore query dispatched.");

    }

    public void updateTotalPrice() {
        ListView listView = findViewById(R.id.scannedlist);
        MainScanAdpter adapter = (MainScanAdpter) listView.getAdapter();
        if (adapter == null) {
            return; // 어댑터가 없으면 종료
        }

        double total = 0;
        for (int i = 0; i < adapter.getCount(); i++) {
            ProductData product = adapter.getItem(i);
            total += product.getProductPrice() * product.getProductStock();
        }

        total -= pointsUsed; // 포인트 적용

        TextView totalTextView = findViewById(R.id.itemtotal);
        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.getDefault());
        totalTextView.setText(numberFormat.format(total));
    }

    private void processPayment() {
        ListView listView = findViewById(R.id.scannedlist);
        EditText usePoint = findViewById(R.id.editTextFieldPoint);
        MainScanAdpter adapter = (MainScanAdpter) listView.getAdapter();
        if (adapter == null) return;

        ArrayList<ProductData> products = new ArrayList<>();
        for (int i = 0; i < adapter.getCount(); i++) {
            products.add(adapter.getItem(i));
        }
        EditText pointEditText = findViewById(R.id.editTextFieldPoint);
        String pointInput = pointEditText.getText().toString();
        int pointsToUse = pointInput.isEmpty() ? 0 : Integer.parseInt(pointInput);

        TextView totalPrice = findViewById(R.id.itemtotal);
        String totalPriceStr = totalPrice.getText().toString().replace(",", "");
        double itemTotal = Double.parseDouble(totalPriceStr);
        if(itemTotal == 0){
            Toast.makeText(MainActivityProductScan.this, "결제할 상품이 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(MainActivityProductScan.this, MainPaymentActivity.class);
        intent.putParcelableArrayListExtra("products", products);
        intent.putExtra("pointsToUse", pointsToUse);
        intent.putExtra("totalPrice", itemTotal);
        startActivity(intent);

    }

    private void runTransactionToUpdateStock(FirebaseFirestore db, DocumentReference docRef, ProductData product) {
        db.runTransaction(transaction -> {
            DocumentSnapshot snapshot = transaction.get(docRef);
            if (!snapshot.exists()) {
                throw new FirebaseFirestoreException("Product not found",
                        FirebaseFirestoreException.Code.ABORTED);
            }
            long newStock = snapshot.getLong("productStock") - product.getProductStock();
            if (newStock < 0) {
                throw new FirebaseFirestoreException("Stock cannot be negative",
                        FirebaseFirestoreException.Code.ABORTED);
            }
            transaction.update(docRef, "productStock", newStock);
            return null;
        }).addOnSuccessListener(aVoid -> {
            Log.d("ScanQR", "Stock updated successfully for product code: " + product.getProductCode());
        }).addOnFailureListener(e -> {
            Log.e("ScanQR", "Error updating stock: " + e.getMessage());
        });
    }

    private void usePoints() {
        EditText pointEditText = findViewById(R.id.editTextFieldPoint);
        String pointInput = pointEditText.getText().toString();

        if (pointInput.isEmpty()) {
            updateTotalPrice();
            return;
        }

        int pointsToUse;
        try {
            pointsToUse = Integer.parseInt(pointInput);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "올바른 포인트 값을 입력해주세요.", Toast.LENGTH_SHORT).show();
            return;
        }

        userDBFirebaseAuth = FirebaseAuth.getInstance();
        userDBFirebaseUser = userDBFirebaseAuth.getCurrentUser();
        String uid = userDBFirebaseUser.getUid();
        userDBFireStore = FirebaseFirestore.getInstance();
        userDBFireStore.collection("users").document(uid).get().addOnSuccessListener(documentSnapshot -> {
            int availablePoints;
            try {
                availablePoints = documentSnapshot.getLong("userPoint").intValue();
            } catch (NullPointerException | NumberFormatException e) {
                Toast.makeText(this, "사용자 포인트를 불러오는 데 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (pointsToUse > availablePoints) {
                Toast.makeText(MainActivityProductScan.this, "보유 포인트보다 많이 입력했습니다.", Toast.LENGTH_SHORT).show();
                updateTotalPrice();
            } else {
                applyPoints(pointsToUse);
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(MainActivityProductScan.this, "포인트 조회 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void applyPoints(int pointsToUse) {
        pointsUsed = pointsToUse; // 포인트 사용 정보 저장
        updateTotalPrice(); // 총 금액 업데이트
    }


    @Override
    protected void onResume() {
        super.onResume();
        capture.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        capture.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        capture.onDestroy();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        capture.onSaveInstanceState(outState);
    }

    public void readBarcode(String barcode) {
        Toast.makeText(getApplicationContext(), barcode, Toast.LENGTH_LONG).show();
    }
}
