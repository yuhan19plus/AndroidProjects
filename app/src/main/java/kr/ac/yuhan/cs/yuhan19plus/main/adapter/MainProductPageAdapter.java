package kr.ac.yuhan.cs.yuhan19plus.main.adapter;

import android.app.ListFragment;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import kr.ac.yuhan.cs.yuhan19plus.main.MainProductPageList;

public class MainProductPageAdapter extends FragmentStateAdapter {

    public MainProductPageAdapter (FragmentActivity fa) {
        super(fa);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // 인덱스에 따라 새로운 프래그먼트 생성
        return MainProductPageList.newInstance(position);
    }

    @Override
    public int getItemCount() {
        // 총 프래그먼트 수
        return 3;
    }
}