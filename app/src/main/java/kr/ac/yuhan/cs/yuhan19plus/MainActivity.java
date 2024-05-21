package kr.ac.yuhan.cs.yuhan19plus;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import kr.ac.yuhan.cs.yuhan19plus.admin.AdminMainActivity;
import kr.ac.yuhan.cs.yuhan19plus.main.MainActivityProductScan;
import kr.ac.yuhan.cs.yuhan19plus.main.MainLoginActivity;
import kr.ac.yuhan.cs.yuhan19plus.main.MainMyPageActivity;
import kr.ac.yuhan.cs.yuhan19plus.main.MainProductActivity;
import kr.ac.yuhan.cs.yuhan19plus.main.MainStoreLocationActivity;
import kr.ac.yuhan.cs.yuhan19plus.main.adapter.MainPopularProductPagerAdapter;


public class MainActivity extends AppCompatActivity {

    private ImageView setting;

    //뷰페이저2
    private ViewPager2 viewPager;

    //광고 이미지 리스트
    private List<Integer> imageList;

    //광고 페이지를 임시 저장하는 부분
    private int currentPage = 0;

    //타이머
    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        setting = findViewById(R.id.setting);
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // AlertDialog Builder 생성

                // LayoutInflater를 사용하여 custom_dialog.xml을 인플레이트함
                LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
                View view = inflater.inflate(R.layout.main_dialog_admin_code, null);

                // EditText 가져오기
                final EditText editText = view.findViewById(R.id.editText);

                // AlertDialog.Builder 생성 및 설정
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("관리자 모드");
                builder.setView(view);
                builder.setPositiveButton("접속", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String enteredValue = editText.getText().toString();
                        if ("201907012".equals(enteredValue)) {
                            Intent intent = new Intent(MainActivity.this, AdminMainActivity.class);
                            startActivity(intent);
                        } else {
                            // 유효하지 않은 값 처리
                            Toast.makeText(MainActivity.this, "코드가 일치 하지 않습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                // Negative 버튼 설정
                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                // 다이얼로그 표시
                builder.show();
            }
        });

        //매장 위치 이동 부분
        ImageView map = findViewById(R.id.store_location_Btn);
        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MainStoreLocationActivity.class);
                startActivity(intent);
            }
        });

        //상품 페이지 이동 부분
        ImageView product = findViewById(R.id.product_Btn);
        product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MainProductActivity.class);
                startActivity(intent);
            }
        });

        //결제(QR) 페이지 이동 부분
        ImageView payment = findViewById(R.id.payment_Btn);
        payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MainActivityProductScan.class);
                startActivity(intent);
            }
        });

        //나의 정보 이동 부분
        ImageView mypage = findViewById(R.id.mypage_Btn);
        mypage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MainMyPageActivity.class);
                startActivity(intent);
            }
        });

        ViewPager2 viewPager2 = findViewById(R.id.Product_viewPager);

        //더미 상품 데이터 생성
        List<MainPopularProductPagerAdapter.Product> products = new ArrayList<>();
        products.add(new MainPopularProductPagerAdapter.Product(R.drawable.image01, "Product 1", "$10.00"));
        products.add(new MainPopularProductPagerAdapter.Product(R.drawable.image02, "Product 2", "$20.00"));
        products.add(new MainPopularProductPagerAdapter.Product(R.drawable.image03, "Product 3", "$30.00"));

        // 어댑터 생성 및 뷰페이저에 설정
        MainPopularProductPagerAdapter adapter = new MainPopularProductPagerAdapter(products);
        viewPager2.setAdapter(adapter);

        //광고 배너 부분
        viewPager = findViewById(R.id.viewPager);

        // 광고 이미지 데이터 추후 데이터 베이스 추가 시 수정 가능
        imageList = new ArrayList<>();
        imageList.add(R.drawable.image01);
        imageList.add(R.drawable.image02);
        imageList.add(R.drawable.image03);

        // ViewPager2 어댑터 설정
        ImagePagerAdapter adapter1 = new ImagePagerAdapter(imageList);
        viewPager.setAdapter(adapter1);

        // 페이지 자동 넘김 설정
        timer = new Timer();
        timer.scheduleAtFixedRate(new AutoPagerTask(), 0, 7000); // 5초마다 실행
    }

    // 타이머 클래스 부분
    class AutoPagerTask extends TimerTask {
        @Override
        public void run() {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    //광고 페이지 안에 이미지 리스트 저장
                    if (currentPage == imageList.size()) {
                        currentPage = 0;
                    }
                    viewPager.setCurrentItem(currentPage++, true);
                }
            });
        }
    }

    //로그인 페이지 활성화
    public void set_login(View v) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        ft.replace(R.id.login_view, new MainLoginActivity(), "one");
        ft.commitAllowingStateLoss();
    }

    public void set_home(View v) {
        FragmentManager manager = getSupportFragmentManager();
        Fragment fragment = manager.findFragmentByTag("one"); // "one" 태그로 등록된 프래그먼트를 찾습니다.
        if (fragment != null) {
            FragmentTransaction ft = manager.beginTransaction();
            ft.remove(fragment); // 프래그먼트를 제거합니다.
            ft.commit();
        }
    }

    //광고 이미지 어댑터 부분
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

    //광고 이미지 붙잡아 두는 부분
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
}

