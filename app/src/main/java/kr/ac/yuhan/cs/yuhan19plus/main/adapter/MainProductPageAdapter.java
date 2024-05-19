package kr.ac.yuhan.cs.yuhan19plus.main.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import kr.ac.yuhan.cs.yuhan19plus.main.MainProductPageList;

public class MainProductPageAdapter extends FragmentStateAdapter {

    private static final int NUM_PAGES = 3;

    public MainProductPageAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return MainProductPageList.newInstance("생필품");
            case 1:
                return MainProductPageList.newInstance("문구류");
            case 2:
                return MainProductPageList.newInstance("주방용품");
            default:
                return MainProductPageList.newInstance("생필품");
        }
    }
//1
    @Override
    public int getItemCount() {
        return NUM_PAGES;
    }
}
