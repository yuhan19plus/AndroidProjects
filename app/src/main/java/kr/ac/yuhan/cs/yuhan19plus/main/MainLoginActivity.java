package kr.ac.yuhan.cs.yuhan19plus.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import kr.ac.yuhan.cs.yuhan19plus.R;

public class MainLoginActivity extends Fragment implements View.OnClickListener{
    private FirebaseAuth userDBFirebaseAuth;
    private FirebaseFirestore userDBFirestore;
    private EditText input_id;
    private EditText input_pw;





    public MainLoginActivity() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_activity_login, container, false);

        // 로그인 버튼 가져오기
        Button loginButton = view.findViewById(R.id.login_btn);

        // 비회원 로그인 버튼 가져오기
        TextView nonloginButton = view.findViewById(R.id.non_login_btn);

        // 회원가입 버튼 가져오기
        TextView register = view.findViewById(R.id.register_btn);

        // EditText 초기화
        input_id = view.findViewById(R.id.login_id);
        input_pw = view.findViewById(R.id.login_pwd);

        // 버튼에 onClick 리스너 설정
        loginButton.setOnClickListener(this);
        nonloginButton.setOnClickListener(this);
        register.setOnClickListener(this);

        return view;
    }

    // 버튼 클릭 이벤트 처리
    @Override
    public void onClick(View v) {
        //로그인 시 작동
        if (v.getId() == R.id.login_btn) {
        //로그인 구현 -> 석재애몸 도와줘요 ->>>>>>>>>>>>>>>이 페이지에서 처리 main_activity_login.xml 참조
            // 인스턴스 가져오기
            userDBFirebaseAuth = FirebaseAuth.getInstance();

            // 아이디와 비밀번호 가져오기
            String user_id = input_id.getText().toString();
            String user_pw = input_pw.getText().toString();

            // 입력 필드가 비어 있는지 확인
            if (user_id.isEmpty()) {
                input_id.setError("아이디를 입력하세요.");
                input_id.requestFocus();
                return;
            }
            if (user_pw.isEmpty()) {
                input_pw.setError("비밀번호를 입력하세요.");
                input_pw.requestFocus();
                return;
            }

            userDBFirebaseAuth.signInWithEmailAndPassword(user_id, user_pw).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        // 로그인 성공 토스트 메시지 출력 및 login_text를 로그아웃 텍스트로 변경
                        Toast.makeText(getActivity(), "로그인에 성공했습니다.", Toast.LENGTH_SHORT).show();
                        TextView loginTextView = getActivity().findViewById(R.id.login_text);
                        loginTextView.setText("로그아웃");

                        // 로그인 프래그먼트 닫기
                        FragmentManager manager = getActivity().getSupportFragmentManager();
                        FragmentTransaction ft = manager.beginTransaction();
                        Fragment fragment = manager.findFragmentByTag("one");
                        if (fragment != null) {
                            ft.remove(fragment);
                            ft.commit();
                        }
                    }
                    else{
                        // 로그인 실패 시 처리
                        Toast.makeText(getActivity(), "아이디 또는 비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        //비회원 로그인 시 작동
        if (v.getId() == R.id.non_login_btn) {
        //비회원 로그인 구현-> 석재애몸 도와줘요 ->>>>>>>>>>>>>>>이 페이지에서 처리 main_activity_login.xml 참조
            // 인스턴스 가져오기
            userDBFirebaseAuth = FirebaseAuth.getInstance();
            userDBFirebaseAuth.signInAnonymously().addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // 로그인 성공, Firestore에 기본 사용자 정보 저장
                        FirebaseUser user = userDBFirebaseAuth.getCurrentUser();
                        if (user != null) {
                            String uid = user.getUid();
                            checkAndSaveAnonymousUserInfo(uid);
                        }
                    } else {
                        Toast.makeText(getActivity(), "비회원 로그인에 실패했습니다.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        // 회원가입 창으로 이동
        if (v.getId() == R.id.register_btn) {
            Intent signUpIntent = new Intent(getActivity(), MainRegisterActivity.class);
            startActivity(signUpIntent);
        }
    }

    private void checkAndSaveAnonymousUserInfo(final String uid) {
        userDBFirestore = FirebaseFirestore.getInstance();
        userDBFirestore.collection("users").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // 로그인 성공 토스트 메시지 출력 및 login_text를 로그아웃 텍스트로 변경
                        Toast.makeText(getActivity(), "로그인에 성공했습니다.", Toast.LENGTH_SHORT).show();
                        TextView loginTextView = getActivity().findViewById(R.id.login_text);
                        loginTextView.setText("로그아웃");

                        // 로그인 프래그먼트 닫기
                        FragmentManager manager = getActivity().getSupportFragmentManager();
                        FragmentTransaction ft = manager.beginTransaction();
                        Fragment fragment = manager.findFragmentByTag("one");
                        if (fragment != null) {
                            ft.remove(fragment);
                            ft.commit();
                        }

                    }
                    else {
                        // 사용자 문서가 존재하지 않을 경우, 기본 정보 저장
                        saveAnonymousUserInfo(uid);
                    }
                } else {
                    Toast.makeText(getActivity(), "사용자 정보 확인 실패", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void saveAnonymousUserInfo(String uid) {
        userDBFirestore = FirebaseFirestore.getInstance();
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("userEmail", "Anonymous");
        userInfo.put("uid", uid);
        userInfo.put("userName", "Anonymous");
        userInfo.put("userPhone", "010-0000-0000");
        userInfo.put("userBirthday", "2000.01.01");
        userInfo.put("userAddress", "NULL");
        userInfo.put("userDetail_address", "NULL");
        userInfo.put("userJoindate", new Date());
        userInfo.put("userPoint", 0);
        userInfo.put("isValid", true);

        userDBFirestore.collection("users").document(uid).set(userInfo).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    // 로그인 성공 토스트 메시지 출력 및 login_text를 로그아웃 텍스트로 변경
                    Toast.makeText(getActivity(), "로그인에 성공했습니다.", Toast.LENGTH_SHORT).show();
                    TextView loginTextView = getActivity().findViewById(R.id.login_text);
                    loginTextView.setText("로그아웃");

                    // 로그인 프래그먼트 닫기
                    FragmentManager manager = getActivity().getSupportFragmentManager();
                    FragmentTransaction ft = manager.beginTransaction();
                    Fragment fragment = manager.findFragmentByTag("one");
                    if (fragment != null) {
                        ft.remove(fragment);
                        ft.commit();
                    }

                } else {
                    // 사용자 정보 저장 실패
                    Toast.makeText(getActivity(), "비회원 로그인에 실패했습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}