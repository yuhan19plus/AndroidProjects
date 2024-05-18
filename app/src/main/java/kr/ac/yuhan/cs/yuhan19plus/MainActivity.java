package kr.ac.yuhan.cs.yuhan19plus;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
import kr.ac.yuhan.cs.yuhan19plus.main.MainLoginActivity;
import kr.ac.yuhan.cs.yuhan19plus.main.MainMyPageActivity;
import kr.ac.yuhan.cs.yuhan19plus.main.MainPaymentActivity;
import kr.ac.yuhan.cs.yuhan19plus.main.MainProductActivity;
import kr.ac.yuhan.cs.yuhan19plus.main.MainStoreLocationActivity;


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
                Intent intent = new Intent(MainActivity.this, AdminMainActivity.class);
                startActivity(intent);
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
                Intent intent = new Intent(MainActivity.this, MainPaymentActivity.class);
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

        //광고 배너 부분
        viewPager = findViewById(R.id.viewPager);

        // 광고 이미지 데이터 추후 데이터 베이스 추가 시 수정 가능
        imageList = new ArrayList<>();
        imageList.add(R.drawable.image01);
        imageList.add(R.drawable.image02);
        imageList.add(R.drawable.image03);

        // ViewPager2 어댑터 설정
        ImagePagerAdapter adapter = new ImagePagerAdapter(imageList);
        viewPager.setAdapter(adapter);

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
        ft.replace(R.id.main_View, new MainLoginActivity(), "one");
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

