package kr.ac.yuhan.cs.yuhan19plus.main;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import kr.ac.yuhan.cs.yuhan19plus.R;
import kr.ac.yuhan.cs.yuhan19plus.main.adapter.MainProductCustomAdapter;
import kr.ac.yuhan.cs.yuhan19plus.main.data.MainProductData;

public class MainProductPageList extends Fragment {

    private List<MainProductData> products = new ArrayList<>();
    private MainProductCustomAdapter adapter;
    private static final String ARG_CATEGORY = "category";
    private String category;

    public static MainProductPageList newInstance(String category) {
        MainProductPageList fragment = new MainProductPageList();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_CATEGORY, category);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 레이아웃 설정
        View rootView = inflater.inflate(R.layout.main_activity_product_list, container, false);

        // 인수로 받은 카테고리 설정
        if (getArguments() != null) {
            category = getArguments().getString(ARG_CATEGORY);
        }

        // ListView 객체 가져오기
        ListView listView = rootView.findViewById(R.id.listView);

        // 어댑터 생성 및 설정
        adapter = new MainProductCustomAdapter(getActivity(), products);
        listView.setAdapter(adapter);

        // 데이터 생성
        loadItemsFromFireStore(category);

        // ListView 아이템 클릭 시 상세페이지 이동
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 클릭된 아이템 가져오기
                MainProductData clickedItem = (MainProductData) parent.getItemAtPosition(position);

                // 인텐트 생성 및 데이터 전달
                Intent intent = new Intent(getActivity(), MainProductDetail.class);
                intent.putExtra("productImage", clickedItem.getImageResource());
                intent.putExtra("productName", clickedItem.getName());
                intent.putExtra("productCategory", clickedItem.getCategory());
                intent.putExtra("productPrice", clickedItem.getPrice());
                intent.putExtra("productCode", clickedItem.getProductCode());
                startActivity(intent);
            }
        });

        return rootView;
    }

    void loadItemsFromFireStore(String category) {
        Log.d("MainProductPageList", "Loading items from Firestore...");
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Task<QuerySnapshot> query = db.collection("products").whereEqualTo("category", category).get();

        query.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Log.d("MainProductPageList", "Successfully queried Firestore.");
                    products.clear();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        try {
                            int productCode = document.getLong("productCode").intValue();
                            String productName = document.getString("productName");
                            String imageUrl = document.getString("imageUrl");
                            String category = document.getString("category");
                            int productPrice = document.getLong("price").intValue();

                            if (imageUrl == null || imageUrl.isEmpty()) {
                                imageUrl = "R.drawable.default_image"; // 기본 이미지 URL 사용
                            }

                            Log.d("MainProductPageList", "Loaded product: " + productName + ", imageUrl: " + imageUrl);
                            products.add(new MainProductData(imageUrl, productName, productPrice, productCode, category));
                        } catch (Exception e) {
                            Log.e("MainProductPageList", "Error parsing document: ", e);
                        }
                    }
                    adapter.notifyDataSetChanged();
                    Log.d("MainProductPageList", "Adapter notified of data change.");
                } else {
                    Log.e("MainProductPageList", "Error getting documents: ", task.getException());
                }
            }
        });
    }
}
