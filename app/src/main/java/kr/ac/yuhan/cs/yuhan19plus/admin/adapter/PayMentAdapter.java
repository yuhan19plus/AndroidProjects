package kr.ac.yuhan.cs.yuhan19plus.admin.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import kr.ac.yuhan.cs.yuhan19plus.R;
import kr.ac.yuhan.cs.yuhan19plus.admin.data.PaymentData;

/** 담당자 : 이석재 */
public class PayMentAdapter extends BaseAdapter {
    private ArrayList<PaymentData> paymentList;
    private LayoutInflater inflater;
    private Context context;
    FirebaseFirestore paymentDBFireStore;
    FirebaseAuth userDBFirebaseAuth;
    FirebaseUser userDBFirebaseUser;

    // PaymentAdapter Constructor
    public PayMentAdapter(Context context, ArrayList<PaymentData> paymentList) {
        this.context = context;
        this.paymentList = paymentList;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() { return paymentList.size(); }

    @Override
    public Object getItem(int position) { return paymentList.get(position); }

    @Override
    public long getItemId(int position) { return position; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null){
            // ViewHolder Init
            convertView = inflater.inflate(R.layout.admin_payment_list_item, parent, false);
            viewHolder = new ViewHolder();
            // Id of payment_list.xml
            viewHolder.numberTextView = convertView.findViewById(R.id.number);
            viewHolder.userNameTextView = convertView.findViewById(R.id.userName);
            viewHolder.totalProceTextView = convertView.findViewById(R.id.totalPrice);
            viewHolder.outBtn = convertView.findViewById(R.id.outBtn);
            convertView.setTag(viewHolder);
        }
        else{
            // ViewHolder Recycle
            viewHolder = (ViewHolder) convertView.getTag();
        }

        PaymentData paymentData = paymentList.get(position);
        viewHolder.numberTextView.setText(String.valueOf(paymentData.getNumber()));
        viewHolder.userNameTextView.setText(String.valueOf(paymentData.getUserName()));
        viewHolder.totalProceTextView.setText(String.valueOf(paymentData.getTotalPrice()));

        viewHolder.outBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create AlertDialog
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("해당 결제내역을 환불처리합니다.");
                builder.setPositiveButton("환불", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // selected "환불"
                        String receiptId = paymentData.getReceiptId();
                        cancelPayment(receiptId);
                        removePayment(position);

                        int usePoint = paymentData.getUsePoint();
                        String uid = paymentData.getUid();
                        int totalPrice = paymentData.getTotalPrice();
                        Map<String, Number> products = paymentData.getProducts();

                        paymentDBFireStore = FirebaseFirestore.getInstance();

                        if(paymentData.getEmail() != null){
                            // Update user points
                            paymentDBFireStore.collection("users").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        long currentPoints = document.getLong("userPoint");
                                        long newPoints = currentPoints + usePoint - Math.round(totalPrice * 0.01);
                                        paymentDBFireStore.collection("users").document(uid).update("userPoint", newPoints);
                                    } else {
                                        Log.w("Firestore", "Error getting user document", task.getException());
                                    }
                                }
                            });
                        }
                        // Update product stocks
                        for (Map.Entry<String, Number> entry : products.entrySet()) {
                            String productName = entry.getKey();
                            long quantity = entry.getValue().longValue();

                            paymentDBFireStore.collection("products").whereEqualTo("productName", productName).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        QuerySnapshot querySnapshot = task.getResult();
                                        DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                                        long currentStock = document.getLong("productStock");
                                        long newStock = currentStock + quantity;
                                        document.getReference().update("productStock", newStock);
                                    } else {
                                        Log.w("Firestore", "Error getting product document: " + productName, task.getException());
                                    }
                                }
                            });
                        }
                        // Update payments isValid
                        paymentDBFireStore.collection("payments").whereEqualTo("receipt_id", receiptId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()){
                                    QuerySnapshot querySnapshot = task.getResult();
                                    DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                                    document.getReference().update("isValid", false);
                                }else{
                                    Log.w("Firestore", "Error getting payment document", task.getException());
                                }
                            }
                        });
                    }
                });
                builder.setNegativeButton("취소", null);
                builder.show();
            }
        });
        return convertView;
    }


    // 결제 취소 처리 함수
    private void cancelPayment(String receiptId) {
        // REST API 호출을 위한 데이터 설정
        Map<String, Object> data = new HashMap<>();
        data.put("receiptId", receiptId);
        // Firebase Cloud Functions를 호출하여 결제를 취소
        FirebaseFunctions.getInstance()
                .getHttpsCallable("cancelPayment")
                .call(data)
                .addOnSuccessListener(new OnSuccessListener<HttpsCallableResult>() {
                    @Override
                    // Firebase Cloud Functions 호출이 성공한 경우
                    public void onSuccess(HttpsCallableResult httpsCallableResult) {
                        Map<String, Object> result = (Map<String, Object>) httpsCallableResult.getData();
                        // 결제 취소가 성공한 경우
                        if (result.containsKey("success")) {
                            Toast.makeText(context, "결제가 취소되었습니다.", Toast.LENGTH_SHORT).show();
                        }
                        // 결제 취소 중에 오류가 발생한 경우
                        else if (result.containsKey("error")) {
                            String errorMessage = (String) result.get("error");
                            Toast.makeText(context, "결제 취소 실패: " + errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    // Firebase Cloud Functions 호출이 실패한 경우
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "결제 취소 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    // Delete PaymentData => Need to Update PaymentData
    public void removePayment(int position){
        // 리스트에서 해당하는 결제 데이터 ㅅ가제
        paymentList.remove(position);
        // 삭제된 아이템 이후의 모든 아이템들의 번호를 갱신
        for (int i = position; i < paymentList.size(); i++){
            PaymentData item = paymentList.get(i);
            item.setNumber(i + 1);
        }
        notifyDataSetChanged(); // 변경된 데이터셋을 알려 ListView를 갱신
    }

    public void updateData(ArrayList<PaymentData> data){
        this.paymentList = data;
        notifyDataSetChanged(); // 변경된 데이터셋을 알려 ListView를 갱신
    }

    static class ViewHolder {
        TextView numberTextView;
        TextView userNameTextView;
        TextView totalProceTextView;
        ImageView outBtn;
    }
}
