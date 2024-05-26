package kr.ac.yuhan.cs.yuhan19plus;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import kr.ac.yuhan.cs.yuhan19plus.admin.AdminMainActivity;
import kr.ac.yuhan.cs.yuhan19plus.admin.adapter.ProductAdapter;
import kr.ac.yuhan.cs.yuhan19plus.admin.data.ProductData;
import kr.ac.yuhan.cs.yuhan19plus.main.MainActivityProductScan;
import kr.ac.yuhan.cs.yuhan19plus.main.MainLoginActivity;
import kr.ac.yuhan.cs.yuhan19plus.main.MainMyPageActivity;
import kr.ac.yuhan.cs.yuhan19plus.main.MainProductActivity;
import kr.ac.yuhan.cs.yuhan19plus.main.MainProductDetail;
import kr.ac.yuhan.cs.yuhan19plus.main.MainStoreLocationActivity;
import kr.ac.yuhan.cs.yuhan19plus.main.adapter.MainPopularProductPagerAdapter;
import kr.ac.yuhan.cs.yuhan19plus.main.adapter.MainProductCustomAdapter;
import kr.ac.yuhan.cs.yuhan19plus.main.data.MainProductData;

/**
 * 메인 액티비티 클래스로, UI 컴포넌트 초기화 및 상호작용 관리를 담당합니다.
 */
public class MainActivity extends AppCompatActivity {

    private ImageView setting;
    private ViewPager2 viewPager, product_viewPager;
    private List<Integer> imageList;
    private int currentPage = 0;
    private Timer timer;
    private ImageView mainProductSearchBtn;
    private EditText mainEditTextFieldSearchProductName;
    private ArrayList<MainProductData> productDataList = new ArrayList<>(); // 상품 정보를 담을 리스트
    private FirebaseAuth userDBFirebaseAuth;
    private FirebaseUser userDBFirebaseUser;


    /**
     * 액티비티를 초기화하고 사용자 인터페이스를 설정합니다.
     *
     * @param savedInstanceState 액티비티가 이전에 종료된 후 다시 초기화되는 경우,
     *                           이번들에 가장 최근에 제공된 데이터가 포함됩니다.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        initializeViews();
        setupViewListeners();
        setupViewPager();

        userDBFirebaseAuth = FirebaseAuth.getInstance();
        TextView login_text = findViewById(R.id.login_text);
        if (userDBFirebaseAuth.getCurrentUser() != null) {
            // User is signed in
            login_text.setText("로그아웃");
        } else {
            // No user is signed in
            login_text.setText("로그인");
        }
    }

    /**
     * ID를 통해 뷰를 찾아 초기화합니다.
     */
    private void initializeViews() {
        mainEditTextFieldSearchProductName = findViewById(R.id.mainEditTextFieldSearchProductName);
        mainProductSearchBtn = findViewById(R.id.mainProductSearchBtn);
        setting = findViewById(R.id.setting);
        viewPager = findViewById(R.id.viewPager);
        product_viewPager = findViewById(R.id.Product_viewPager);
        loadItemsFromFireStoreToProductViewPager();
        mainProductSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadItemsFromFireStore();
            }
        });
    }

    //로그인 페이지 활성화 및 비활성화
    public void handleTextViewClick(View v) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        int id = v.getId();
        if (id == R.id.login_text) {
            TextView login_text = (TextView) v;
            if("로그인".equals(login_text.getText().toString())){
                // 로그인 텍스트뷰가 클릭된 경우 로그인 화면을 표시합니다.
                ft.replace(R.id.login_view, new MainLoginActivity(), "one");
                ft.commitAllowingStateLoss();
            }
            else if("로그아웃".equals(login_text.getText().toString())){
                // 로그아웃 텍스트뷰가 클릭된 경우 로그아웃 처리합니다.
                userDBFirebaseAuth = FirebaseAuth.getInstance();
                userDBFirebaseAuth.signOut();
                login_text.setText("로그인");
                Toast.makeText(MainActivity.this, "로그아웃에 성공했습니다.", Toast.LENGTH_SHORT).show();
            }

        } else if (id == R.id.close_text) {
            // 홈 텍스트뷰가 클릭된 경우 로그인 화면을 제거합니다.
            Fragment fragment = manager.findFragmentByTag("one");
            if (fragment != null) {
                ft.remove(fragment);
                ft.commit();
            }
        }
    }

    /**
     * 버튼 및 이미지 뷰와 같은 다양한 UI 컴포넌트에 대한 리스너를 설정합니다.
     */
    private void setupViewListeners() {
        setting.setOnClickListener(v -> showAdminDialog());
        findViewById(R.id.store_location_Btn).setOnClickListener(v -> launchActivity(MainStoreLocationActivity.class));
        findViewById(R.id.product_Btn).setOnClickListener(v -> launchActivity(MainProductActivity.class));
        findViewById(R.id.payment_Btn).setOnClickListener(v -> launchActivity(MainActivityProductScan.class));
        findViewById(R.id.mypage_Btn).setOnClickListener(v -> launchActivity(MainMyPageActivity.class));
    }

    /**
     * 뷰페이저를 설정하고 이미지 리스트를 초기화합니다.
     */
    private void setupViewPager() {
        imageList = new ArrayList<>();
        imageList.add(R.drawable.image01);
        imageList.add(R.drawable.image02);
        imageList.add(R.drawable.image03);

        ImagePagerAdapter adapter = new ImagePagerAdapter(imageList);
        viewPager.setAdapter(adapter);
        setupAutoPageTimer();
    }

    /**
     * 관리자 모드 대화 상자를 표시합니다.
     */
    private void showAdminDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.main_dialog_admin_code, null);
        final EditText editText = view.findViewById(R.id.editText);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("관리자 모드")
                .setView(view)
                .setPositiveButton("접속", (dialog, which) -> {
                    String enteredValue = editText.getText().toString();
                    if ("201907012".equals(enteredValue)) {
                        launchActivity(AdminMainActivity.class);
                    } else {
                        Toast.makeText(MainActivity.this, "코드가 일치 하지 않습니다.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("취소", (dialog, which) -> dialog.cancel())
                .show();
    }

    /**
     * 자동 페이지 타이머를 설정합니다. 7초마다 페이지를 자동으로 넘깁니다.
     */
    private void setupAutoPageTimer() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new AutoPagerTask(), 0, 7000);
    }

    /**
     * 지정된 액티비티를 시작합니다.
     *
     * @param cls 시작할 액티비티의 클래스 객체
     */
    private void launchActivity(Class<?> cls) {
        userDBFirebaseAuth = FirebaseAuth.getInstance();
        userDBFirebaseUser = userDBFirebaseAuth.getCurrentUser();
        if ((cls == MainMyPageActivity.class || cls == MainActivityProductScan.class) && userDBFirebaseUser == null) {
            Toast.makeText(MainActivity.this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
        }
        else{
            Intent intent = new Intent(MainActivity.this, cls);
            startActivity(intent);
        }
    }

    /**
     * 자동으로 페이지를 넘기는 타이머 작업을 정의합니다.
     */
    class AutoPagerTask extends TimerTask {
        @Override
        public void run() {
            new Handler(Looper.getMainLooper()).post(() -> {
                if (currentPage == imageList.size()) {
                    currentPage = 0;
                }
                viewPager.setCurrentItem(currentPage++, true);
            });
        }
    }

    /**
     * 이미지 뷰페이저 어댑터를 정의합니다. 이미지 자원을 받아 화면에 표시합니다.
     */
    class ImagePagerAdapter extends RecyclerView.Adapter<ImageViewHolder> {

        private List<Integer> imageList;

        ImagePagerAdapter(List<Integer> imageList) {
            this.imageList = imageList;
        }

        @NonNull
        @Override
        public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adv_image, parent, false);
            return new ImageViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
            holder.bind(imageList.get(position));
        }

        @Override
        public int getItemCount() {
            return imageList.size();
        }
    }


    /**
     * 뷰홀더 클래스로, 이미지 뷰에 이미지를 바인딩합니다.
     */
    class ImageViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;

        ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
        }

        void bind(int imageResourceId) {
            imageView.setImageResource(imageResourceId);
        }
    }

    // 파이어베이스에서 상품명으로 검색하고 데이터를 읽어오는 메서드 (오자현)
    void loadItemsFromFireStore() {
        // Firestore 데이터베이스 인스턴스를 가져옵니다.
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Task<QuerySnapshot> query;

        // 검색 텍스트가 비어있지 않으면, 해당 상품 이름으로 필터링된 쿼리를 실행합니다.
        if (mainEditTextFieldSearchProductName != null) {
            // 검색 텍스트가 있을 경우, 해당 상품 이름으로 필터링된 쿼리 실행
            String text = mainEditTextFieldSearchProductName.getText().toString();
            query = db.collection("products")
                    .whereEqualTo("productName", text)
                    .get();
            mainEditTextFieldSearchProductName.setText("");
        } else {
            // 검색 텍스트가 없을 경우
            Toast.makeText(MainActivity.this, "검색창에 찾으시는 상품이름을 입력하세요", Toast.LENGTH_SHORT).show();
            query = db.collection("products").get();

        }

        // 쿼리가 완료된 후 실행될 리스너를 추가합니다.
        query.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().isEmpty()) {
                        // 검색 결과가 없을 경우
                        Toast.makeText(MainActivity.this, "loadItemsFromFireStore()없는 데이터입니다.", Toast.LENGTH_SHORT).show();
                    } else {
                        // 기존의 제품 리스트를 클리어합니다.
                        productDataList.clear();
                        // 쿼리 결과를 반복하여 각 문서를 처리합니다
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // 각 필드를 가져와서 변수에 저장합니다.
                            int code = document.getLong("productCode").intValue();
                            String productName = document.getString("productName");
                            String category = document.getString("productCategory");
                            String imageUrl = document.getString("productImage");
                            int price = document.getLong("productPrice").intValue();

                            // 이미지 URL이 null이거나 비어있으면 기본 이미지 URL을 사용합니다.
                            if (imageUrl == null || imageUrl.isEmpty()) {
                                imageUrl = "R.drawable.default_image"; // 기본 이미지 URL 사용
                            }

                            // 로드된 이미지 URL을 로그에 출력합니다.
                            Log.d("DatabaseViewActivity", "Loaded imageUrl: " + imageUrl);
                            // 새로운 인텐트를 생성하고 데이터를 추가합니다.
                            Intent intent = new Intent(MainActivity.this, MainProductDetail.class);
                            intent.putExtra("productCode", code);
                            intent.putExtra("productName", productName);
                            intent.putExtra("productCategory", category);
                            intent.putExtra("productImage", imageUrl);
                            intent.putExtra("productPrice", price);

                            // 새로운 액티비티를 시작합니다.
                            startActivity(intent);
                            break; // 첫 번째 결과만 사용한다고 가정하고 루프를 종료합니다.
                        }
                       
                    }
                } else {
                    // 쿼리 실행 중 오류가 발생하면 로그에 출력합니다.
                    Log.e("DatabaseViewActivity", "Error getting documents: ", task.getException());
                }
            }
        });
    }

    void loadItemsFromFireStoreToProductViewPager() {
        // Firestore 데이터베이스 인스턴스를 가져옵니다.
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Task<QuerySnapshot> query;

        query = db.collection("products")
                .limit(3)
                .get();


        // 쿼리가 완료된 후 실행될 리스너를 추가합니다.
        query.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().isEmpty()) {
                        // 검색 결과가 없을 경우
                        Toast.makeText(MainActivity.this, "없는 데이터입니다.", Toast.LENGTH_SHORT).show();
                    } else {
                        // 기존의 제품 리스트를 클리어합니다.
                        productDataList.clear();
                        // 쿼리 결과를 반복하여 각 문서를 처리합니다
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            int code = document.getLong("productCode").intValue();
                            String productName = document.getString("productName");
                            String category = document.getString("productCategory");
                            String imageUrl = document.getString("productImage");
                            int price = document.getLong("productPrice").intValue();

                            // 이미지 URL이 null이거나 비어있으면 기본 이미지 URL을 사용합니다.
                            if (imageUrl == null || imageUrl.isEmpty()) {
                                imageUrl = "R.drawable.default_image"; // 기본 이미지 URL 사용
                            }
                            //productImage, String productName, int productPrice, int productCode, String productCategory) {
                            // 제품 데이터 객체를 생성하고 리스트에 추가합니다.
                            MainProductData productData = new MainProductData(imageUrl, productName, price, code, category);
                            productDataList.add(productData);
                        }

                        // ViewPager 어댑터에 데이터 설정
                        MainPopularProductPagerAdapter adapter = new MainPopularProductPagerAdapter(MainActivity.this, productDataList);
                        product_viewPager.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    }
                } else {
                    // 쿼리 실행 중 오류가 발생하면 로그에 출력합니다.
                    Log.e("DatabaseViewActivity", "Error getting documents: ", task.getException());
                }
            }
        });
    }
}