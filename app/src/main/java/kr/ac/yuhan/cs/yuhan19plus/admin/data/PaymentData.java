package kr.ac.yuhan.cs.yuhan19plus.admin.data;

import android.widget.Button;

import java.util.Date;
import java.util.Map;

/** 담당자 : 이석재 */
public class PaymentData {
    // Payment Data Field
    private int number;
    private String email;
    private String userName;
    private String uid;
    private String receiptId;
    private Map<String, Number> products;
    private int totalPrice;
    private int usePoint;
    private Date payDay;

    private Button outButton;

    // PaymentData Constructor
    public PaymentData(int number, String receiptId, String userName, String uid, String email, Map<String, Number> products, int totalPrice, int usePoint, Date payDay) {
        this.number = number;
        this.receiptId = receiptId;
        this.userName =  userName;
        this.uid = uid;
        this.email = email;
        this.products = products;
        this.totalPrice = totalPrice;
        this.usePoint = usePoint;
        this.payDay = payDay;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getEmail() { return email; }

    public String getUserName() {
        return userName;
    }

    public String getUid() { return uid; }
    public String getReceiptId() { return receiptId; }

    public Map<String, Number> getProducts() { return products; }
    public int getUsePoint() { return usePoint; }

    public int getTotalPrice() { return totalPrice; }

    public Date getPayDay() { return payDay; }

    // 나가기 버튼 속성을 설정하는 메서드
    public void setOutButton(Button outButton) {
        this.outButton = outButton;
    }

    // 나가기 버튼 여부를 반환하는 메서드
    public boolean hasOutButton() {
        // 나가기 버튼이 있는지 여부에 따라 true 또는 false를 반환
        // 여기서는 임의로 true 또는 false를 반환하도록 설정
        // 실제로는 MemberData 객체가 가지고 있는 정보를 기반으로 판단해야 함
        return true; // 또는 false
    }
}
