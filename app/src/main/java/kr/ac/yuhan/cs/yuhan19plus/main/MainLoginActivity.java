package kr.ac.yuhan.cs.yuhan19plus.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import kr.ac.yuhan.cs.yuhan19plus.R;

public class MainLoginActivity extends Fragment implements View.OnClickListener{

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

        // 회원가입 버튼 가져오기
        TextView register = view.findViewById(R.id.register_btn);

        // 버튼에 onClick 리스너 설정
        loginButton.setOnClickListener(this);
        register.setOnClickListener(this);

        return view;
    }

    // 버튼 클릭 이벤트 처리
    @Override
    public void onClick(View v) {
        //로그인 시 작동
        if (v.getId() == R.id.login_btn) {
        //로그인 구현 -> 석재애몸 도와줘요 ->>>>>>>>>>>>>>>이 페이지에서 처리 main_activity_login.xml 참조
        }

        //비회원 로그인 시 작동
        if (v.getId() == R.id.non_login_btn) {
        //비회원 로그인 구현-> 석재애몸 도와줘요 ->>>>>>>>>>>>>>>이 페이지에서 처리 main_activity_login.xml 참조
        }

        // 회원가입 창으로 이동
        if (v.getId() == R.id.register_btn) {
            Intent signUpIntent = new Intent(getActivity(), MainRegisterActivity.class);
            startActivity(signUpIntent);
        }
    }
}