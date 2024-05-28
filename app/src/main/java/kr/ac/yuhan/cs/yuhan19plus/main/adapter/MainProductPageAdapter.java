package kr.ac.yuhan.cs.yuhan19plus.main.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import kr.ac.yuhan.cs.yuhan19plus.main.MainProductPageList;

/** 담당자 : 임성준, 오자현, 이정민
 * 초기작성 : 이정민
 * 프로젝트 병합 : 임성준
 * 수정 : 오자현 */
public class MainProductPageAdapter extends FragmentStateAdapter {
    private static final int NUM_PAGES = 3;
    public MainProductPageAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    // 상품 확인창 탭 레이아웃 띄우는 부분 <이정민>
    // 여기 데이터가 중요함 저기서 순서랑 이름을 잘못 입력하면 상품카테고리별로 보는 곳에서 꼬임
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return MainProductPageList.newInstance("문구류");
            case 1:
                return MainProductPageList.newInstance("생필품");
            case 2:
                return MainProductPageList.newInstance("주방 도구");
            default:
                return MainProductPageList.newInstance("생필품");
        }
    }

    @Override
    public int getItemCount() {
        return NUM_PAGES;
    }
}
