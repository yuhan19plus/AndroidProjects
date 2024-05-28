// MyPaymentAdapter.java

package kr.ac.yuhan.cs.yuhan19plus.main.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

import kr.ac.yuhan.cs.yuhan19plus.R;
/** 담당자 : 임성준
 * 개인결제내역 기능구현 : 임성준 */
public class MyPaymentAdapter extends ArrayAdapter<Map<String, Object>> {
    private Context mContext;

    public MyPaymentAdapter(Context context, int resource, List<Map<String, Object>> items) {
        super(context, resource, items);
        this.mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(mContext);
            view = vi.inflate(R.layout.main_mypayment_list_item, null);
        }

        final Map<String, Object> payment = getItem(position);

        if (payment != null) {
            TextView paymentDate = view.findViewById(R.id.paymentDate);
            TextView paymentReceiptId = view.findViewById(R.id.paymentReceiptId);

            if (paymentDate != null) {
                Object payDayObject = payment.get("payDay");
                String payDay = (payDayObject != null) ? payDayObject.toString() : "";
                paymentDate.setText(payDay);
            }

            if (paymentReceiptId != null) {
                Object receiptId = payment.get("receipt_id");
                String receiptIdString = (receiptId != null) ? receiptId.toString() : "";
                paymentReceiptId.setText(receiptIdString.substring(0,7));
            }

            // 아이템 클릭 이벤트 처리
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showPaymentDetailDialog(payment);
                }
            });
        }

        return view;
    }

    private void showPaymentDetailDialog(Map<String, Object> payment) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View dialogView = inflater.inflate(R.layout.main_payment_item_info, null);
        builder.setView(dialogView);

        TextView productNameTextView = dialogView.findViewById(R.id.productNameTextView);
        TextView amountTextView = dialogView.findViewById(R.id.amountTextView);
        TextView dateTextView = dialogView.findViewById(R.id.dateTextView);
        TextView receiptIdTextView = dialogView.findViewById(R.id.receiptIdTextView);
        TextView pointTextView = dialogView.findViewById(R.id.pointTextView);
        TextView isValidTextView = dialogView.findViewById(R.id.isValidTextView);

        // 데이터가 null인지 빈 문자열인지 확인하고 로그 메시지 출력
        Log.d("DEBUG", "ProductName: " + payment.get("productName"));
        Log.d("DEBUG", "Amount: " + payment.get("totalPrice"));
        Log.d("DEBUG", "Date: " + payment.get("payDay"));
        Log.d("DEBUG", "ReceiptId: " + payment.get("receipt_id"));
        Log.d("DEBUG", "Point: " + payment.get("userPoint"));
        Log.d("DEBUG", "isValid: " + payment.get("isValid"));

        Object receiptIdObject = payment.get("receipt_id");
        String receiptId = (receiptIdObject != null) ? receiptIdObject.toString() : "";
        receiptIdTextView.setText(receiptId);

        // 'payment' 객체에서 'productName' 키의 값을 가져오는 부분
        Object productMapObject = payment.get("products");

        // 로그를 추가하여 productMapObject가 올바르게 가져와지는지 확인
        if (productMapObject == null) {
            Log.w("Debug", "productMapObject is null");
        } else {
            Log.d("Debug", "productMapObject: " + productMapObject.toString());
        }

        Map<String, String> productMap = null;

        // productMapObject가 Map인지 확인하고 캐스팅
        if (productMapObject instanceof Map) {
            productMap = (Map<String, String>) productMapObject;
        } else {
            Log.w("Debug", "productMapObject is not a Map");
        }

        // productMap이 null이 아니고 비어 있지 않은지 확인
        if (productMap != null && !productMap.isEmpty()) {
            // 첫 번째 키 가져오기
            String productName = productMap.keySet().iterator().next();
            Log.d("Debug", "Extracted productName: " + productName);

            // productNameTextView에 설정
            productNameTextView.setText(productName);
        } else {
            // 맵이 비어있거나 null인 경우 기본 텍스트 설정
            productNameTextView.setText("상품명 없음");
            Log.w("Debug", "productMap is null or empty");
        }

        Object amountObject = payment.get("totalPrice");
        String amount = "";
        if (amountObject != null) {
            double doubleAmount = Double.parseDouble(amountObject.toString());
            DecimalFormat formatter = new DecimalFormat("#,###");
            amount = formatter.format(doubleAmount);
        }
        amountTextView.setText(amount + "원");

        Object dateObject = payment.get("payDay");
        String date = (dateObject != null) ? dateObject.toString() : "";
        dateTextView.setText(date);

        Object pointObject = payment.get("userPoint");
        String point = (pointObject != null) ? pointObject.toString() : "사용포인트 없음";
        pointTextView.setText(point);

        Object isValidObject = payment.get("isValid");
        String isValid = isValidObject.toString();
        if(isValid == "true") isValid = "환불하지 않았습니다.";
        else isValid = "환불하였습니다.";
        isValidTextView.setText(isValid);

        // 모달 창 표시
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}

