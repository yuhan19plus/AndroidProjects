package kr.ac.yuhan.cs.yuhan19plus.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;

import kr.ac.yuhan.cs.yuhan19plus.R;

public class MainLoginActivity extends Fragment {

    public MainLoginActivity() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {super.onCreate(savedInstanceState);}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup
            container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.main_activity_login, container, false);

    }
}