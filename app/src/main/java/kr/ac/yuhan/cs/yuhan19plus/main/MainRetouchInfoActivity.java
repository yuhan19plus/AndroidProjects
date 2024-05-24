package kr.ac.yuhan.cs.yuhan19plus.main;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import kr.ac.yuhan.cs.yuhan19plus.MainActivity;
import kr.ac.yuhan.cs.yuhan19plus.R;

public class MainRetouchInfoActivity extends AppCompatActivity {
    //석재애몽 도와줘요-->>>>>>>>>>>>>>> 회원정보 수정 페이지 main_activity_my_info.xml 참조

    private FirebaseAuth userDBFirebaseAuth;
    private FirebaseUser userDBFirebaseUser;
    private FirebaseFirestore userDBFireStore;
    private Button mBtnUpdate;
    private ImageView closeButton;
    private TextView email, id_label, pw_label, pwCk_label;
    private EditText input_pw, input_pwCk,
            input_name, input_phoneFirst, input_phoneSecond, input_phoneThird,
            input_birthdayYear, input_birthdayMonth, input_birthdayDay,
            input_address, input_detail_address;
    private boolean isAnonymousUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_my_info);

        
        // 인스턴스 가져오기
        userDBFirebaseAuth = FirebaseAuth.getInstance();
        userDBFireStore = FirebaseFirestore.getInstance();
        userDBFirebaseUser = userDBFirebaseAuth.getCurrentUser();;

        closeButton = findViewById(R.id.Close_info_Btn);

        isAnonymousUser = userDBFirebaseUser.isAnonymous();


        // TextView
        email = findViewById(R.id.User_ID);
        id_label = findViewById(R.id.id_label);
        pw_label = findViewById(R.id.pw_label);
        pwCk_label = findViewById(R.id.pwCk_label);

        // EditText
        input_pw = findViewById(R.id.Register_Pwd);
        input_pwCk = findViewById(R.id.Register_Pwd_Re);
        input_name = findViewById(R.id.Register_name);
        input_phoneFirst = findViewById(R.id.First_Phone_Number);
        input_phoneSecond = findViewById(R.id.Second_Phone_Number);
        input_phoneThird = findViewById(R.id.Third_Phone_Number);
        input_birthdayYear = findViewById(R.id.Year);
        input_birthdayMonth = findViewById(R.id.Month);
        input_birthdayDay = findViewById(R.id.Day);
        input_address = findViewById(R.id.address);
        input_detail_address = findViewById(R.id.detail_address);

        // Button
        mBtnUpdate = findViewById(R.id.Finish_Info_Btn);

        if (isAnonymousUser) {
            id_label.setVisibility(View.GONE);
            email.setVisibility(View.GONE);
            pw_label.setVisibility(View.GONE);
            input_pw.setVisibility(View.GONE);
            pwCk_label.setVisibility(View.GONE);
            input_pwCk.setVisibility(View.GONE);

        }

        loadUserInfo();

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mBtnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUserInfo();
            }
        });

    }

    private void loadUserInfo() {
        String uid = userDBFirebaseUser.getUid();
        userDBFireStore.collection("users").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    Map<String, Object> userInfo = document.getData();
                    updateUI(userInfo);
                }
                else {
                    Toast.makeText(MainRetouchInfoActivity.this, "사용자 정보 로드에 실패했습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }




    private void updateUI(Map<String, Object> userInfo) {
        email.setText(userDBFirebaseUser.getEmail());

        input_name.setText(userInfo.get("userName").toString());
        String[] phoneParts = userInfo.get("userPhone").toString().split("-");
        if (phoneParts.length == 3) {
            input_phoneFirst.setText(phoneParts[0]);
            input_phoneSecond.setText(phoneParts[1]);
            input_phoneThird.setText(phoneParts[2]);
        }
        String[] birthParts = userInfo.get("userBirthday").toString().split("\\.");
        if (birthParts.length == 3) {
            input_birthdayYear.setText(birthParts[0]);
            input_birthdayMonth.setText(birthParts[1]);
            input_birthdayDay.setText(birthParts[2]);
        }
        input_address.setText(userInfo.get("userAddress").toString());
        input_detail_address.setText(userInfo.get("userDetail_address").toString());
    }


    private void saveUserInfo() {
        String uid = userDBFirebaseAuth.getUid();
        String pw = input_pw.getText().toString();
        String pwCk = input_pwCk.getText().toString();
        String name = input_name.getText().toString();
        String phoneFirst = input_phoneFirst.getText().toString();
        String phoneSecond = input_phoneSecond.getText().toString();
        String phoneThird = input_phoneThird.getText().toString();
        String birthdayYear = input_birthdayYear.getText().toString();
        String birthdayMonth = input_birthdayMonth.getText().toString();
        String birthdayDay = input_birthdayDay.getText().toString();
        String address = input_address.getText().toString();
        String detail_Address = input_detail_address.getText().toString();

        if (!isAnonymousUser) {
            if (pw.isEmpty()) {
                input_pw.setError("비밀번호를 입력해주세요.");
                input_pw.requestFocus();
                return;
            }
            if (pwCk.isEmpty()) {
                input_pwCk.setError("비밀번호 확인을 입력해주세요.");
                input_pwCk.requestFocus();
                return;
            }
            if (!pw.equals(pwCk)) {
                input_pwCk.setError("비밀번호 확인에 입력한 비밀번호가 입력하신 비밀번호와 일치하지 않습니다.");
                input_pwCk.requestFocus();
                return;
            }
        }
        if(name.isEmpty()){
            input_name.setError("이름을 입력해주세요.");
            input_name.requestFocus();
            return;
        }
        else if(phoneFirst.isEmpty() || phoneSecond.isEmpty() || phoneThird.isEmpty()){
            if(phoneFirst.isEmpty()){
                input_phoneFirst.setError("전화번호를 입력해주세요.");
                input_phoneFirst.requestFocus();
                return;
            }
            else if(phoneSecond.isEmpty()){
                input_phoneSecond.setError("전화번호를 입력해주세요.");
                input_phoneSecond.requestFocus();
                return;
            }
            else{
                input_phoneThird.setError("전화번호를 입력해주세요.");
                input_phoneThird.requestFocus();
                return;
            }
        }
        else if(!isInputPhoneValid()){
            input_phoneThird.setError("올바른 전화번호를 입력해주세요.");
            return;
        }
        else if(birthdayYear.isEmpty() || birthdayMonth.isEmpty() || birthdayDay.isEmpty()){
            if(birthdayYear.isEmpty()){
                input_birthdayYear.setError("생년월일을 입력해주세요.");
                input_birthdayYear.requestFocus();
                return;
            }
            else if(birthdayMonth.isEmpty()){
                input_birthdayMonth.setError("생년월일을 입력해주세요.");
                input_birthdayMonth.requestFocus();
                return;
            }
            else{
                input_birthdayDay.setError("생년월일을 입력해주세요.");
                input_birthdayDay.requestFocus();
                return;
            }
        }
        else if(!isInputBirthDayValid()){
            input_birthdayDay.setError("올바른 생년월일을 입력해주세요.");
            return;
        }
        else if(address.isEmpty()) {
            input_address.setError("주소를 입력해주세요.");
            return;
        }
        else if(detail_Address.isEmpty()) {
            input_detail_address.setError("상세주소를 입력해주세요.");
            return;
        }

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("userName", name);
        userInfo.put("userPhone", phoneFirst+'-'+phoneSecond+'-'+phoneThird);
        userInfo.put("userBirthday", birthdayYear+'.'+birthdayMonth+'.'+birthdayDay);
        userInfo.put("userAddress", address);
        userInfo.put("userDetail_address", detail_Address);

        userDBFireStore.collection("users").document(uid).update(userInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(MainRetouchInfoActivity.this, "회원 정보가 성공적으로 수정되었습니다.", Toast.LENGTH_SHORT).show();
                    if(!isAnonymousUser){
                        userDBFirebaseUser.updatePassword(pw);
                    }
                    finish();
                } else {
                    Toast.makeText(MainRetouchInfoActivity.this, "회원 정보 수정에 실패했습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private boolean isInputPhoneValid() {
        String part1 = input_phoneFirst.getText().toString().trim();
        String part2 = input_phoneSecond.getText().toString().trim();
        String part3 = input_phoneThird.getText().toString().trim();

        return isNumeric(part1) && isNumeric(part2) && isNumeric(part3)
                && part1.length() == 3 && part2.length() == 4 && part3.length() == 4;
    }

    private boolean isInputBirthDayValid() {
        String yearStr = input_birthdayYear.getText().toString().trim();
        String monthStr = input_birthdayMonth.getText().toString().trim();
        String dayStr = input_birthdayDay.getText().toString().trim();

        if (!isNumeric(yearStr) || !isNumeric(monthStr) || !isNumeric(dayStr)) {
            return false;
        }

        int year = Integer.parseInt(yearStr);
        int month = Integer.parseInt(monthStr);
        int day = Integer.parseInt(dayStr);

        if (month < 1 || month > 12) {
            return false;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setLenient(false);
        try {
            calendar.set(year, month - 1, day); // 월은 0부터 시작하므로 -1 해줍니다.
            calendar.getTime(); // 유효하지 않은 날짜는 여기서 예외가 발생합니다.
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    private boolean isNumeric(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        try {
            Long.parseLong(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }




}