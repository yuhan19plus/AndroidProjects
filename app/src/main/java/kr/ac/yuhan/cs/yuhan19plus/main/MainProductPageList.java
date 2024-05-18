package kr.ac.yuhan.cs.yuhan19plus.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import kr.ac.yuhan.cs.yuhan19plus.R;
import kr.ac.yuhan.cs.yuhan19plus.main.adapter.MainProductCustomAdapter;
import kr.ac.yuhan.cs.yuhan19plus.main.data.MainProductData;

public class MainProductPageList extends Fragment {

    //프래그먼트 인스턴스 상수 정의 --> 값 저장
    private static final String ARG_SECTION_NUMBER = "section_number";

    // 새로운 인스턴스를 생성하기 위한 메서드
    public static MainProductPageList newInstance(int index) {
        MainProductPageList fragment = new MainProductPageList();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_NUMBER, index);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 레이아웃 설정
        View rootView = inflater.inflate(R.layout.main_activity_product_list, container, false);

        // ListView 객체 가져오기
        ListView listView = rootView.findViewById(R.id.listView);

        // 데이터 생성 --> 추후 데이터베이스 연결시 변경해야 할 부분
        List<MainProductData> products = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            products.add(new MainProductData(R.drawable.main_home, "상품 " + (i + 1),(i + 1) * 1000+"원", (long) i + 1));
        }

        // 커스텀 어댑터 생성 및 설정
        MainProductCustomAdapter adapter = new MainProductCustomAdapter (getActivity(), products);
        listView.setAdapter(adapter);

        // ListView 아이템 클릭 시 상세페이지 이동
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 클릭된 아이템 가져오기
                MainProductData clickedItem = (MainProductData) parent.getItemAtPosition(position);

                // 인텐트 생성 및 데이터 전달
                Intent intent = new Intent(getActivity(), MainProductDetail.class);
                intent.putExtra("productImage", clickedItem.getImageResource());
                intent.putExtra("productName", clickedItem.getName().toString());
                intent.putExtra("productPrice", clickedItem.getPrice());
                intent.putExtra("productCode", clickedItem.getProductCode());
                startActivity(intent);
            }
        });

        return rootView;
    }

}