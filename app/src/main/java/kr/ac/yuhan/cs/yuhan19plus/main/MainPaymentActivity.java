package kr.ac.yuhan.cs.yuhan19plus.main;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import kr.ac.yuhan.cs.yuhan19plus.MainActivity;
import kr.ac.yuhan.cs.yuhan19plus.R;
import kr.ac.yuhan.cs.yuhan19plus.admin.data.ProductData;
import kr.co.bootpay.android.Bootpay;
import kr.co.bootpay.android.events.BootpayEventListener;
import kr.co.bootpay.android.models.BootExtra;
import kr.co.bootpay.android.models.BootUser;
import kr.co.bootpay.android.models.Payload;

public class MainPaymentActivity extends AppCompatActivity {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth userDBFirebaseAuth = FirebaseAuth.getInstance();
    private FirebaseUser userDBFirebaseUser = userDBFirebaseAuth.getCurrentUser();

    private double totalPrice = 0;
    private int usePoint = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_payment);

        String uid = userDBFirebaseUser.getUid();
        String email = userDBFirebaseUser.getEmail() != null ? userDBFirebaseUser.getEmail() : "";

        db.collection("users").document(uid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    String userName = documentSnapshot.getString("userName");
                    String userPhone = documentSnapshot.getString("userPhone");

                    BootUser user = new BootUser()
                            .setId(uid)
                            .setEmail(email)
                            .setUsername(userName)
                            .setPhone(userPhone);

                    processPayment(user); // BootUser 객체를 결제 처리 함수로 전달
                })
                .addOnFailureListener(e -> {
                    Log.e("setupBootUser", "Error fetching user data: " + e.getMessage());
                });
    }

    // 결제 요청 메소드
    private void processPayment(BootUser user) {

        BootExtra extra = new BootExtra().setCardQuota("0,2,3"); // 일시불, 2개월, 3개월 할부 허용

        ArrayList<ProductData> products = getIntent().getParcelableArrayListExtra("products");

        int pointsToUse = getIntent().getIntExtra("pointsToUse", 0);
        double totalPrice = getIntent().getDoubleExtra("totalPrice", 0);


        Payload payload = new Payload();
        payload.setApplicationId("660c0ed6d7005fd6c24ec046")
                .setOrderName("유한대학교 안드로이드 프로젝트")
                .setOrderId("1234")
                .setPrice(totalPrice)
                .setUser(user)
                .setExtra(extra);


        Bootpay.init(getSupportFragmentManager(), getApplicationContext())
                .setPayload(payload)
                .setEventListener(new BootpayEventListener() {
                    @Override
                    public void onCancel(String data) {
                        Log.d("bootpay", "cancel: " + data);
                    }

                    @Override
                    public void onError(String data) {
                        Toast.makeText(MainPaymentActivity.this, "결제 오류: " + data, Toast.LENGTH_LONG).show();
                        Log.d("결제 오류: ", data);
                        finish();
                    }

                    @Override
                    public void onClose() {
                        Bootpay.removePaymentWindow();
                        Toast.makeText(MainPaymentActivity.this, "결제 창 닫힘", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void onIssued(String data) {
                        Toast.makeText(MainPaymentActivity.this, "이슈 발생: " + data, Toast.LENGTH_LONG).show();
                        finish();
                    }

                    @Override
                    public boolean onConfirm(String data) {
                        Log.d("bootpay", "confirm: " + data);
                        return true;
                    }

                    @Override
                    public void onDone(String data) {
                        Log.d("done", data);
                        savePaymentData(userDBFirebaseUser.getUid(), totalPrice, pointsToUse, products, data, userDBFirebaseUser);
                        updateUserPoints(userDBFirebaseUser.getUid(), pointsToUse, totalPrice); // 포인트 업데이트 함수 호출
                        updateProductStocks(products);
                        Toast.makeText(MainPaymentActivity.this, "결제가 완료되었습니다.", Toast.LENGTH_LONG).show();

                        // MainActivity로 이동하고 현재 액티비티를 종료
                        Intent intent = new Intent(MainPaymentActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish(); // MainPaymentActivity 종료
                    }
                }).requestPayment();
    }


    // 결제 데이터 저장 메소드
    private void savePaymentData(String uid, double totalPrice, int pointsUsed, ArrayList<ProductData> products, String data, FirebaseUser userDBFirebaseUser) {
        Map<String, Object> paymentData = new HashMap<>();
        paymentData.put("uid", uid);
        paymentData.put("totalPrice", totalPrice);
        paymentData.put("usePoint", pointsUsed);

        try {
            JSONObject jsonObject = new JSONObject(data);
            JSONObject dataObject = jsonObject.getJSONObject("data");
            String receiptId = dataObject.getString("receipt_id");

            paymentData.put("receipt_id", receiptId);
        } catch (JSONException e) {
            Log.e("JSON", "Error parsing JSON data", e);
        }

        Map<String, Number> productMap = new HashMap<>();
        for (ProductData product : products) {
            productMap.put(String.valueOf(product.getProductName()), product.getProductStock());
        }
        paymentData.put("products", productMap);
        paymentData.put("payDay", new Date());
        paymentData.put("userEmail", userDBFirebaseUser.getEmail());
        paymentData.put("isValid", true);

        // Firestore에서 사용자 이름을 가져와서 paymentData에 추가한 후 payments 컬렉션에 저장합니다.
        db.collection("users").document(uid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    String userName = documentSnapshot.getString("userName");
                    paymentData.put("userName", userName);

                    // userName을 추가한 후 paymentData를 payments 컬렉션에 저장합니다.
                    db.collection("payments").add(paymentData)
                            .addOnSuccessListener(documentReference -> Log.d("Firestore", "Payment data saved with ID: " + documentReference.getId()))
                            .addOnFailureListener(e -> Log.e("Firestore", "Error saving payment data", e));
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error getting user data", e));
    }


    // 포인트 업데이트 메소드
    private void updateUserPoints(String uid, int pointsUsed, double totalPrice) {
        db.collection("users").document(uid).get().addOnSuccessListener(documentSnapshot -> {
                long currentPoints = documentSnapshot.getLong("userPoint");
                long newPoints = currentPoints - pointsUsed + Math.round(totalPrice * 0.01);

                db.collection("users").document(uid).update("userPoint", newPoints)
                        .addOnSuccessListener(aVoid -> Log.d("Firestore", "User points updated successfully"))
                        .addOnFailureListener(e -> Log.e("Firestore", "Error updating user points", e));
        }).addOnFailureListener(e -> Log.e("Firestore", "Error fetching user document", e));
    }
    private void updateProductStocks(ArrayList<ProductData> products) {
        for (ProductData product : products) {
            int productCode = product.getProductCode();
            int quantityToDeduct = product.getProductStock();

            db.collection("products").whereEqualTo("productCode", productCode).get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                DocumentReference productRef = document.getReference();
                                db.runTransaction(transaction -> {
                                    DocumentSnapshot snapshot = transaction.get(productRef);
                                    long currentStock = snapshot.getLong("productStock");
                                    long newStock = currentStock - quantityToDeduct;
                                    if (newStock < 0) {
                                        throw new FirebaseFirestoreException("Stock cannot be negative", FirebaseFirestoreException.Code.ABORTED);
                                    }
                                    transaction.update(productRef, "productStock", newStock);
                                    return null;
                                }).addOnSuccessListener(aVoid -> {
                                    Log.d("Firestore", "Product stock updated successfully for product code: " + productCode);
                                }).addOnFailureListener(e -> {
                                    Log.e("Firestore", "Error updating product stock", e);
                                });
                            }
                        } else {
                            Log.e("Firestore", "No product found with code: " + productCode);
                        }
                    }).addOnFailureListener(e -> {
                        Log.e("Firestore", "Error fetching product", e);
                    });
        }
    }


    private String extractFieldFromJson(String jsonData, String fieldName) throws JSONException {
        JSONObject jsonObject = new JSONObject(jsonData);
        return jsonObject.getString(fieldName);
    }

}
