package kr.ac.yuhan.cs.yuhan19plus.main;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import kr.ac.yuhan.cs.yuhan19plus.R;

/** 담당자 : 임성준, 이석재, 이정민
 * 초기작성 : 이정민
 * 메인 프로젝트 병합 : 임성준
 * 회원가입 기능구현 : 이석재 */
public class MainRegisterActivity extends AppCompatActivity {
    private FirebaseAuth userDBFirebaseAuth;
    private FirebaseUser userDBFirebaseUser;
    private FirebaseFirestore userDBFireStore;
    private ImageView close_register;
    private EditText input_email, input_pw, input_pwCk,
                input_name, input_phoneFirst, input_phoneSecond, input_phoneThird,
                input_birthdayYear, input_birthdayMonth, input_birthdayDay,
                input_address, input_detail_address;
    private Button mBtnRegister, mBtnIdCheck;

    private boolean idChecked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_register);

        close_register = findViewById(R.id.Close_Register_Btn);

        // EditText
        input_email = findViewById(R.id.Register_Id);
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
        mBtnIdCheck = findViewById(R.id.Register_Id_Check);
        mBtnRegister = findViewById(R.id.Register_Clear);

        // 회원가입 창 닫기 이동 <이정민>
        close_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 아이디를 재입력시 중복확인을 추가로 할 수 있도록 처리
        input_email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                idChecked = false;
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        // 입력한 이메일 체크
        mBtnIdCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userDBFirebaseAuth = FirebaseAuth.getInstance();
                String email = input_email.getText().toString();

                if(email.isEmpty()){
                    input_email.setError("아이디를 입력해주세요.");
                    input_email.requestFocus();
                }
                else{
                    userDBFirebaseAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                        @Override
                        public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                            if(task.isSuccessful()) {
                                boolean isNewUser = task.getResult().getSignInMethods().isEmpty();
                                if(isNewUser){
                                    Toast.makeText(MainRegisterActivity.this, "사용 가능한 아이디입니다.", Toast.LENGTH_SHORT).show();
                                    idChecked = true;
                                }
                                else{
                                    input_email.setError("사용할 수 없는 아이디입니다.");
                                    input_email.requestFocus();
                                }
                            }
                            else {
                                Toast.makeText(MainRegisterActivity.this, "아이디 확인에 실패했습니다.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
            }
        }});

        mBtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userDBFirebaseAuth = FirebaseAuth.getInstance();
                userDBFireStore = FirebaseFirestore.getInstance();

                String email = input_email.getText().toString();
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
                String detail_address = input_detail_address.getText().toString();

                if(email.isEmpty()){
                    input_email.setError("아이디를 입력해주세요.");
                    input_email.requestFocus();
                }
                else{
                    if(idChecked){
                        if(pw.isEmpty()){
                            input_pw.setError("비밀번호를 입력해주세요.");
                            input_pw.requestFocus();
                        }
                        else if(pwCk.isEmpty()) {
                            input_pwCk.setError("비밀번호 확인을 입력해주세요.");
                            input_pwCk.requestFocus();
                        }
                        else if(!pw.equals(pwCk)) {
                            input_pwCk.setError("비밀번호 확인에 입력한 비밀번호가 입력하신 비밀번호와 일치하지 않습니다.");
                            input_pwCk.requestFocus();
                        }
                        else if(name.isEmpty()){
                            input_name.setError("이름을 입력해주세요.");
                            input_name.requestFocus();
                        }
                        else if(phoneFirst.isEmpty() || phoneSecond.isEmpty() || phoneThird.isEmpty()){
                            if(phoneFirst.isEmpty()){
                                input_phoneFirst.setError("전화번호를 입력해주세요.");
                                input_phoneFirst.requestFocus();
                            }
                            else if(phoneSecond.isEmpty()){
                                input_phoneSecond.setError("전화번호를 입력해주세요.");
                                input_phoneSecond.requestFocus();
                            }
                            else{
                                input_phoneThird.setError("전화번호를 입력해주세요.");
                                input_phoneThird.requestFocus();
                            }
                        }
                        else if(!isInputPhoneValid()){
                            input_phoneThird.setError("올바른 전화번호를 입력해주세요.");
                        }
                        else if(birthdayYear.isEmpty() || birthdayMonth.isEmpty() || birthdayDay.isEmpty()){
                            if(birthdayYear.isEmpty()){
                                input_birthdayYear.setError("생년월일을 입력해주세요.");
                                input_birthdayYear.requestFocus();
                            }
                            else if(birthdayMonth.isEmpty()){
                                input_birthdayMonth.setError("생년월일을 입력해주세요.");
                                input_birthdayMonth.requestFocus();
                            }
                            else{
                                input_birthdayDay.setError("생년월일을 입력해주세요.");
                                input_birthdayDay.requestFocus();
                            }
                        }
                        else if(!isInputBirthDayValid()){
                            input_birthdayDay.setError("올바른 생년월일을 입력해주세요.");
                        }
                        else if(address.isEmpty()) {
                            input_address.setError("주소를 입력해주세요.");
                        }
                        else if(detail_address.isEmpty()) {
                            input_detail_address.setError("상세주소를 입력해주세요.");
                        }
                        else{
                            //FirebaseAuth 진행
                            userDBFirebaseAuth.createUserWithEmailAndPassword(email, pw).addOnCompleteListener(MainRegisterActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    // Firebase에 저장할 데이터 객체 생성
                                    if(task.isSuccessful()){
                                        // 사용자 생성 성공, 사용자의 UID 가져오기
                                        userDBFirebaseUser = task.getResult().getUser();
                                        String uid = userDBFirebaseUser.getUid();  // 이 UID를 문서 ID로 사용

                                        // Firebase에 저장할 데이터 객체 생성
                                        Map<String, Object> userInfo = new HashMap<>();
                                        userInfo.put("userEmail", email);
                                        userInfo.put("uid", uid);
                                        userInfo.put("userName", name);
                                        userInfo.put("userPhone", phoneFirst+'-'+phoneSecond+'-'+phoneThird);
                                        userInfo.put("userBirthday", birthdayYear+'.'+birthdayMonth+'.'+birthdayDay);
                                        userInfo.put("userAddress", address);
                                        userInfo.put("userDetail_address", detail_address);
                                        userInfo.put("userJoindate", new Date());
                                        userInfo.put("userPoint", 50);
                                        userInfo.put("isValid", true);

                                        // "User" 컬렉션에 데이터 추가
                                        userDBFireStore.collection("users").document(uid).set(userInfo).addOnCompleteListener(MainRegisterActivity.this, new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    Toast.makeText(MainRegisterActivity.this, "회원가입 성공", Toast.LENGTH_SHORT).show();
                                                    finish();
                                                }
                                                else{
                                                    Toast.makeText(MainRegisterActivity.this, "회원가입 중 오류가 발생하였습니다.", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });

                                    }
                                }
                            });

                        }
                    }
                    else{
                        input_email.setError("아이디 중복검사를 진행해주세요.");
                        input_email.requestFocus();
                    }
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
