package kr.ac.yuhan.cs.yuhan19plus.admin;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import kr.ac.yuhan.cs.yuhan19plus.R;
import kr.ac.yuhan.cs.yuhan19plus.admin.adapter.MemberAdapter;
import kr.ac.yuhan.cs.yuhan19plus.admin.adapter.PayMentAdapter;
import kr.ac.yuhan.cs.yuhan19plus.admin.adapter.ProductAdapter;
import kr.ac.yuhan.cs.yuhan19plus.admin.data.MemberData;
import kr.ac.yuhan.cs.yuhan19plus.admin.data.PaymentData;
import kr.ac.yuhan.cs.yuhan19plus.admin.data.ProductData;
import kr.ac.yuhan.cs.yuhan19plus.admin.util.ChangeMode;
import soup.neumorphism.NeumorphButton;
import soup.neumorphism.NeumorphCardView;
import soup.neumorphism.NeumorphImageView;
public class AdminMainActivity extends AppCompatActivity {
    // 다크/라이트 모드 초기값
    public int mode = 0;

    // Product Firebase
    private FirebaseFirestore productDBFireStore; // 제품 Firebase
    private Uri productFileUri = null; // 제품 파일 Uri

    // Member Firebase and Adapter
    private FirebaseFirestore memberDBFireStore; // 회원 Firebase 및 어댑터
    private MemberAdapter memberAdapter; // 회원 어댑터

    // Payment Firebase and Adapter
    private FirebaseFirestore paymentDBFireStore; // 결제 Firebase 및 어댑터
    private PayMentAdapter paymentAdapter; // 결제 어댑터

    // 세션 객체
    SharedPreferences sharedPreferences;

    private static final int PICK_FILE_REQUEST = 2; // 이미지 파일 선택을 위한 요청 코드

    // ViewFlipper
    private ViewFlipper vFlipper; // 뷰 플리퍼

    // 기본 색상
    private int backgroundColor; // 배경 색상
    private int mainBackgroundColor = Color.rgb(236, 240, 243); // 기본 메인 배경색
    private final int darkModeBackgroundColor = Color.rgb(97, 97, 97); // 다크 모드 메인 배경색
    private final int btnColor = Color.rgb(0, 174, 142); // 기본 버튼 색상
    private final int radioButtonTextColor = Color.rgb(0, 105, 97); // 라디오 버튼 텍스트 색상

    // 헤더 버튼 이미지
    private NeumorphImageView admin_setting, changeMode; // 관리자 설정, 모드 변경

    // 메인 액티비티 카드뷰
    private NeumorphCardView mainCardView, footer_menu; // 메인 카드뷰, 푸터 메뉴

    // 푸터 바 메뉴
    private NeumorphImageView
            homeBtn, // 홈 버튼
            memberBtn, // 회원 버튼
            productBtn, // 제품 버튼
            payHistoryBtn, // 결제 내역 버튼
            productPushBtn; // 제품 푸시 버튼

    // 회원 관리 페이지 메뉴
    private ListView memberListView; // 회원 리스트 뷰
    private NeumorphCardView
            memberListCardView, // 회원 리스트 카드뷰
            input_searchIdCardView; // ID 검색 카드뷰
    private NeumorphButton memberSearchBtn; // 회원 검색 버튼
    private EditText editTextFieldSearchMemberName; // 회원 이름 검색 입력란

    // 제품 관리 페이지 메뉴
    private ListView productListView; // 제품 리스트 뷰
    private NeumorphCardView
            input_searchProductIdCardView, // 제품 ID 검색 카드뷰
            productListCardView; // 제품 리스트 카드뷰
    private ImageView imageViewProduct; // 제품 이미지
    private NeumorphButton productSearchBtn; // 제품 검색 버튼
    private EditText editTextFieldSearchProductName; // 제품 이름 검색 입력란

    private String currentSearchText = ""; // 검색 창 초기값 설정

    // HOMEManagement Page Menu
    private NeumorphCardView
            adminListBtnCardView, // 관리자 목록 카드뷰
            adminScheduleBtnCardView, // 관리자 일정 카드뷰
            adminCallBtnCardView, // 관리자 전화 카드뷰
            adminLoginBtnCardView, // 관리자 로그인 카드뷰
            adminCreateAppQRBtnCardView, // 앱 QR코드 생성 카드뷰
            adminExitCardView; // 관리자 종료 카드뷰
    private ImageView
            adminListImage, // 관리자 목록 이미지
            adminScheduleImage, // 관리자 일정 이미지
            adminCallImage, // 관리자 전화 이미지
            adminLoginImage, // 관리자 로그인 이미지
            adminCreateAppQRImage, // 앱 QR코드 생성 이미지
            adminExitImage; // 관리자 종료 이미지

    private TextView
            adminLoginTextView; // 관리자 로그인 텍스트뷰

    // PaymentList Page Menu
    private ListView paymentListView; // 결제 리스트 뷰
    private NeumorphCardView
            payListCardView, // 결제 리스트 카드뷰
            input_searchPayIdCardView; // 결제 ID 검색 카드뷰
    private NeumorphButton paySearchBtn; // 결제 검색 버튼
    private EditText editTextFieldPaymentSearchMemberName; // 결제 회원 이름 검색 입력란

    // ProductRegister Page Menu
    private NeumorphCardView
            input_productImageCardView, // 제품 이미지 카드뷰
            input_productNameCardView, // 제품 이름 카드뷰
            input_productStockCardView, // 제품 재고 카드뷰
            input_productCategoryCardView, // 제품 카테고리 카드뷰
            input_productPriceCardView; // 제품 가격 카드뷰
    private EditText
            editTextFieldProductName, // 제품 이름 입력란
            editTextFieldProductPrice, // 제품 가격 입력란
            editTextFieldProductStock; // 제품 재고 입력란
    private RadioGroup radioGroup; // 라디오 그룹
    private RadioButton
            categoryRadioBtn1, // 카테고리 라디오 버튼1
            categoryRadioBtn2, // 카테고리 라디오 버튼2
            categoryRadioBtn3; // 카테고리 라디오 버튼3
    private NeumorphButton
            createQRBtn, // QR코드 생성 버튼
            createProductBtn; // 제품 생성 버튼

    // 오자현 추가 부분
    private String productCategory; // 제품 카테고리
    private ProductAdapter productAdapter; // 제품 어댑터
    private Handler handler = new Handler(); // 핸들러
    private Runnable runnable; // 실행 가능한 객체
    private ArrayList<ProductData> productDataList = new ArrayList<>(); // 상품 정보를 담을 리스트

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_activity_main_page);

        String product_categoryDefault = getString(R.string.product_categoryDefault); // 카테고리 기본값 (성준 추가 부분)
        String categoryStr1 = getString(R.string.product_category1);
        String categoryStr2 = getString(R.string.product_category2);
        String categoryStr3 = getString(R.string.product_category3);

        // 리스트 뷰 설정
        memberListView = findViewById(R.id.memberListView);
        productListView = findViewById(R.id.productListView);
        paymentListView = findViewById(R.id.payListView);

        // 카테고리를 클릭하지 않고 넘기는 경우 기본값으로 지정
        if(productCategory == null){
            productCategory = product_categoryDefault;
        }

        // 회원 어댑터 설정
        memberAdapter = new MemberAdapter(this, new ArrayList<MemberData>());
        memberListView.setAdapter(memberAdapter);

        // 제품 어댑터 설정
        productAdapter = new ProductAdapter(this, productDataList);
        productListView.setAdapter(productAdapter); // 리스트 뷰에 어댑터 설정

        // 결제 어댑터 설정
        paymentAdapter = new PayMentAdapter(this, new ArrayList<PaymentData>());
        paymentListView.setAdapter(paymentAdapter);

        startAutoRefresh(); // 상품 데이터 자동 새로고침
        loadItemsFromFireStore(); // 파이어베이스에서 상품명으로 검색하고 데이터를 읽어오는 메서드 호출
        productDBFireStore = FirebaseFirestore.getInstance();

        // ViewFlipper 설정
        vFlipper = findViewById(R.id.viewFlipper1);

        // 메인 레이아웃 설정
        LinearLayout main = findViewById(R.id.main);

        // 기본 배경 색상 설정
        backgroundColor = mainBackgroundColor;
        main.setBackgroundColor(backgroundColor);

        Drawable backgroundDrawable = main.getBackground();
        mainBackgroundColor = ((ColorDrawable) backgroundDrawable).getColor();

        // 메인 액티비티 헤더 ID
        admin_setting = findViewById(R.id.admin_setting);
        changeMode = findViewById(R.id.darkMode);

        // 관리자 메인 페이지 카드뷰 ID
        adminListBtnCardView = findViewById(R.id.adminListBtnCardView);
        adminScheduleBtnCardView = findViewById(R.id.adminScheduleBtnCardView);
        adminCallBtnCardView = findViewById(R.id.adminCallBtnCardView);
        adminLoginBtnCardView = findViewById(R.id.adminLoginBtnCardView);
        adminCreateAppQRBtnCardView = findViewById(R.id.adminCreateAppQRBtnCardView);
        adminExitCardView = findViewById(R.id.adminExitCardView);

        // 관리자 메인 페이지 이미지뷰 ID
        adminListImage = findViewById(R.id.adminListImage);
        adminScheduleImage = findViewById(R.id.adminScheduleImage);
        adminCallImage = findViewById(R.id.adminCallImage);
        adminLoginImage = findViewById(R.id.adminLoginImage);
        adminCreateAppQRImage = findViewById(R.id.adminCreateAppQRImage);
        adminExitImage = findViewById(R.id.adminExitImage);

        // 로그인/로그아웃 텍스트뷰
        adminLoginTextView = (TextView) findViewById(R.id.adminLoginTextView);

        // 메인 액티비티 카드뷰 & 푸터 ID
        mainCardView = findViewById(R.id.mainCardView);
        footer_menu = findViewById(R.id.footer_menu);

        // 회원 관리 페이지 ID
        input_searchIdCardView = findViewById(R.id.input_searchIdCardView);
        editTextFieldSearchMemberName = findViewById(R.id.editTextFieldSearchMemberName);
        memberSearchBtn = findViewById(R.id.memberSearchBtn);
        memberListCardView = findViewById(R.id.memberListCardView);

        // 제품 관리 페이지 ID
        input_searchProductIdCardView = findViewById(R.id.input_searchProductIdCardView);
        productSearchBtn = findViewById(R.id.productSearchBtn);
        productListCardView = findViewById(R.id.productListCardView);
        imageViewProduct = findViewById(R.id.imageViewProduct);
        editTextFieldSearchProductName = findViewById(R.id.editTextFieldSearchProductName);

        // 결제 목록 페이지 ID
        input_searchPayIdCardView =findViewById(R.id.input_searchPayIdCardView);
        editTextFieldPaymentSearchMemberName = findViewById(R.id.editTextFieldPaymentSearchMemberName);
        paySearchBtn = findViewById(R.id.paySearchBtn);
        payListCardView = findViewById(R.id.payListCardView);

        // 제품 등록 페이지 ID
        input_productImageCardView = findViewById(R.id.input_productImageCardView);
        input_productNameCardView = findViewById(R.id.input_productNameCardView);
        input_productStockCardView = findViewById(R.id.input_productStockCardView);
        input_productCategoryCardView = findViewById(R.id.input_productCategoryCardView);
        input_productPriceCardView = findViewById(R.id.input_productPriceCardView);
        editTextFieldProductName = findViewById(R.id.editTextFieldProductName);
        editTextFieldProductPrice = findViewById(R.id.editTextFieldProductPrice);
        editTextFieldProductStock = findViewById(R.id.editTextFieldProductStock);
        categoryRadioBtn1 = findViewById(R.id.categoryRadioBtn1);
        categoryRadioBtn2 = findViewById(R.id.categoryRadioBtn2);
        categoryRadioBtn3 = findViewById(R.id.categoryRadioBtn3);
        radioGroup = findViewById(R.id.categoryRadioGroup);
        createQRBtn = findViewById(R.id.createQRBtn);
        createProductBtn = findViewById(R.id.createProductBtn);
        memberSearchBtn = findViewById(R.id.memberSearchBtn);

        // Footer Menu Icon Id
        memberBtn = findViewById(R.id.memberBtn);
        productBtn = findViewById(R.id.productBtn);
        homeBtn = findViewById(R.id.homeBtn);
        payHistoryBtn = findViewById(R.id.payHistoryBtn);
        productPushBtn = findViewById(R.id.productPushBtn);

        // MainActivity Button BackgroundColor Setting
        memberSearchBtn.setBackgroundColor(btnColor);
        productSearchBtn.setBackgroundColor(btnColor);
        paySearchBtn.setBackgroundColor(btnColor);
        createQRBtn.setBackgroundColor(btnColor);
        createProductBtn.setBackgroundColor(btnColor);

        // 라디오 버튼 글자 색상 설정
        categoryRadioBtn1.setTextColor(radioButtonTextColor);
        categoryRadioBtn2.setTextColor(radioButtonTextColor);
        categoryRadioBtn3.setTextColor(radioButtonTextColor);

        // 로그인 여부에 따라 관리자 전용 버튼 숨기기 및 로그인 카드뷰 텍스트 변경
        sharedPreferences = getSharedPreferences("AdminSession", MODE_PRIVATE);
        String adminId = sharedPreferences.getString("admin_id", null);
        if(adminId != null){
            adminLoginTextView.setText("로그아웃");
            adminListBtnCardView.setVisibility(View.VISIBLE);
            adminScheduleBtnCardView.setVisibility(View.VISIBLE);
            adminCallBtnCardView.setVisibility(View.VISIBLE);
            adminCreateAppQRBtnCardView.setVisibility(View.VISIBLE);

        }
        else{
            adminLoginTextView.setText("로그인");
            adminListBtnCardView.setVisibility(View.GONE);
            adminScheduleBtnCardView.setVisibility(View.GONE);
            adminCallBtnCardView.setVisibility(View.GONE);
            adminCreateAppQRBtnCardView.setVisibility(View.GONE);

        }

        // SettingBtn onClickListener
        admin_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                admin_setting.setShapeType(1);
                v.postDelayed(new Runnable() {
                    @Override
                    public void run() {admin_setting.setShapeType(0);}
                }, 200);

                // 세션 객체를 이용하여 현재 로그인한 관리자의 아이디 값을 가져온다.
                sharedPreferences = getSharedPreferences("AdminSession", MODE_PRIVATE);
                String adminId = sharedPreferences.getString("admin_id", null);

                // 관리자가 로그인하지 않았을 경우 토스트 메시지를 표시하고 추가 실행을 중단한다.
                if (adminId == null) {
                    Toast.makeText(AdminMainActivity.this, "관리자 로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Setting 페이지로 이동 및 메인페이지 배경색상 전달
                Intent intent = new Intent(getApplicationContext(), AdminSettingActivity.class);
                intent.putExtra("background_color", backgroundColor);
                intent.putExtra("mode", mode);
                startActivity(intent);
            }
        });

        // Dark/Light 모드 적용 리스너
        changeMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeMode.setShapeType(1);
                v.postDelayed(new Runnable() {
                    @Override
                    public void run() {changeMode.setShapeType(0);}
                }, 200);

                if(mode == 0) {
                    // DarkMode
                    backgroundColor = darkModeBackgroundColor;
                    main.setBackgroundColor(backgroundColor);

                    // Change FontColor
                    // Find and Change The Color Of All TextViews In Every Root view
                    ChangeMode.applyMainTheme(main, mode);

                    ChangeMode.setDarkShadowCardView(mainCardView);
                    ChangeMode.setDarkShadowCardView(footer_menu);

                    // MemberManagement Page CardView
                    ChangeMode.setDarkShadowCardView(input_searchIdCardView);
                    ChangeMode.setDarkShadowCardView(memberSearchBtn);
                    ChangeMode.setDarkShadowCardView(memberListCardView);

                    // 상품 관리 페이지 CardView
                    ChangeMode.setDarkShadowCardView(input_searchProductIdCardView);
                    ChangeMode.setDarkShadowCardView(productSearchBtn);
                    ChangeMode.setDarkShadowCardView(productListCardView);

                    // AdminMain Page CardView
                    ChangeMode.setDarkShadowCardView(adminListBtnCardView);
                    ChangeMode.setDarkShadowCardView(adminScheduleBtnCardView);
                    ChangeMode.setDarkShadowCardView(adminCallBtnCardView);
                    ChangeMode.setDarkShadowCardView(adminLoginBtnCardView);
                    ChangeMode.setDarkShadowCardView(adminCreateAppQRBtnCardView);
                    ChangeMode.setDarkShadowCardView(adminExitCardView);

                    // PaymentList Page CardView
                    ChangeMode.setColorFilterLight(paySearchBtn);
                    ChangeMode.setDarkShadowCardView(paySearchBtn);
                    ChangeMode.setDarkShadowCardView(payListCardView);
                    ChangeMode.setDarkShadowCardView(input_searchPayIdCardView);

                    // ProductRegister Page CardView
                    ChangeMode.setDarkShadowCardView(input_productImageCardView);
                    ChangeMode.setDarkShadowCardView(input_productNameCardView);
                    ChangeMode.setDarkShadowCardView(input_productStockCardView);
                    ChangeMode.setDarkShadowCardView(input_productCategoryCardView);
                    ChangeMode.setDarkShadowCardView(input_productPriceCardView);
                    ChangeMode.setColorFilterDark(categoryRadioBtn1);
                    ChangeMode.setColorFilterDark(categoryRadioBtn2);
                    ChangeMode.setColorFilterDark(categoryRadioBtn3);
                    ChangeMode.setDarkShadowCardView(createQRBtn);
                    ChangeMode.setDarkShadowCardView(createProductBtn);

                    // Change ImageView Color
                    ChangeMode.setColorFilterDark(adminListImage);
                    ChangeMode.setColorFilterDark(adminScheduleImage);
                    ChangeMode.setColorFilterDark(adminCallImage);
                    ChangeMode.setColorFilterDark(adminLoginImage);
                    ChangeMode.setColorFilterDark(adminCreateAppQRImage);
                    ChangeMode.setColorFilterDark(adminExitImage);

                    // Footer Menu
                    ChangeMode.setColorFilterDark(memberBtn);
                    ChangeMode.setDarkShadowCardView(memberBtn);
                    ChangeMode.setColorFilterDark(productBtn);
                    ChangeMode.setDarkShadowCardView(productBtn);
                    ChangeMode.setColorFilterDark(homeBtn);
                    ChangeMode.setDarkShadowCardView(homeBtn);
                    ChangeMode.setColorFilterDark(payHistoryBtn);
                    ChangeMode.setDarkShadowCardView(payHistoryBtn);
                    ChangeMode.setColorFilterDark(productPushBtn);
                    ChangeMode.setDarkShadowCardView(productPushBtn);
                    ChangeMode.setColorFilterDark(admin_setting);
                    ChangeMode.setDarkShadowCardView(admin_setting);

                    // Change ChangeMode Image & Color
                    changeMode.setImageResource(R.drawable.light);
                    ChangeMode.setColorFilterDark(changeMode);
                    ChangeMode.setDarkShadowCardView(changeMode);

                    // Change Mode Value
                    mode++;
                }
                else if(mode == 1) {
                    // LightMode
                    backgroundColor = mainBackgroundColor;
                    main.setBackgroundColor(backgroundColor);

                    // Change FontColor
                    // Find and Change The Color Of All TextViews In Every Root view
                    ChangeMode.applyMainTheme(main, mode);

                    ChangeMode.setLightShadowCardView(footer_menu);
                    ChangeMode.setLightShadowCardView(mainCardView);

                    // MemberManagement Page CardView
                    ChangeMode.setLightShadowCardView(input_searchIdCardView);
                    ChangeMode.setLightShadowCardView(memberSearchBtn);
                    ChangeMode.setLightShadowCardView(memberListCardView);

                    // 상품 관리 페이지 CardView
                    ChangeMode.setLightShadowCardView(input_searchProductIdCardView);
                    ChangeMode.setLightShadowCardView(productSearchBtn);
                    ChangeMode.setLightShadowCardView(productListCardView);

                    // AdminMain Page CardView
                    ChangeMode.setLightShadowCardView(adminListBtnCardView);
                    ChangeMode.setLightShadowCardView(adminScheduleBtnCardView);
                    ChangeMode.setLightShadowCardView(adminCallBtnCardView);
                    ChangeMode.setLightShadowCardView(adminLoginBtnCardView);
                    ChangeMode.setLightShadowCardView(adminCreateAppQRBtnCardView);
                    ChangeMode.setLightShadowCardView(adminExitCardView);

                    // PaymentList Page CardView
                    ChangeMode.setColorFilterLight(paySearchBtn);
                    ChangeMode.setLightShadowCardView(paySearchBtn);
                    ChangeMode.setLightShadowCardView(payListCardView);
                    ChangeMode.setLightShadowCardView(input_searchPayIdCardView);

                    // ProductRegister Page CardView
                    ChangeMode.setLightShadowCardView(input_productImageCardView);
                    ChangeMode.setLightShadowCardView(input_productNameCardView);
                    ChangeMode.setLightShadowCardView(input_productStockCardView);
                    ChangeMode.setLightShadowCardView(input_productCategoryCardView);
                    ChangeMode.setLightShadowCardView(input_productPriceCardView);
                    ChangeMode.setColorFilterLight(categoryRadioBtn1);
                    ChangeMode.setColorFilterLight(categoryRadioBtn2);
                    ChangeMode.setColorFilterLight(categoryRadioBtn3);
                    ChangeMode.setLightShadowCardView(createQRBtn);
                    ChangeMode.setLightShadowCardView(createProductBtn);

                    // Change ImageView Color
                    ChangeMode.setColorFilterLight(adminListImage);
                    ChangeMode.setColorFilterLight(adminScheduleImage);
                    ChangeMode.setColorFilterLight(adminCallImage);
                    ChangeMode.setColorFilterLight(adminLoginImage);
                    ChangeMode.setColorFilterLight(adminCreateAppQRImage);
                    ChangeMode.setColorFilterLight(adminExitImage);

                    // Footer Menu
                    ChangeMode.setLightShadowCardView(memberBtn);
                    ChangeMode.setColorFilterLight(memberBtn);
                    ChangeMode.setLightShadowCardView(productBtn);
                    ChangeMode.setColorFilterLight(productBtn);
                    ChangeMode.setLightShadowCardView(homeBtn);
                    ChangeMode.setColorFilterLight(homeBtn);
                    ChangeMode.setLightShadowCardView(payHistoryBtn);
                    ChangeMode.setColorFilterLight(payHistoryBtn);
                    ChangeMode.setLightShadowCardView(productPushBtn);
                    ChangeMode.setColorFilterLight(productPushBtn);
                    ChangeMode.setLightShadowCardView(admin_setting);
                    ChangeMode.setColorFilterLight(admin_setting);

                    // Change ChangeMode Image & Color
                    changeMode.setImageResource(R.drawable.dark);
                    ChangeMode.setLightShadowCardView(changeMode);
                    ChangeMode.setColorFilterLight(changeMode);

                    // Change Mode Value
                    mode--;
                }
                else {
                    showErrorDialog(AdminMainActivity.this, "임성준");
                }
            }
        });

        // AdminBtn onClickListener
        adminListBtnCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Change ShapeType to 'pressed' when clicked
                adminListBtnCardView.setShapeType(1);
                // After clicked, it changes back to 'flat'
                v.postDelayed(new Runnable() {
                    @Override
                    public void run() {adminListBtnCardView.setShapeType(0);}
                }, 200);
                // Move to AdminList Page & transfer main page background color
                Intent intent = new Intent(getApplicationContext(), AdminActivity.class);
                intent.putExtra("background_color", backgroundColor);
                intent.putExtra("mode", mode);
                startActivity(intent);
            }
        });

        // AdminScheduleBtn onClickListener
        adminScheduleBtnCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Change ShapeType to 'pressed' when clicked
                adminScheduleBtnCardView.setShapeType(1);
                // After clicked, it changes back to 'flat'
                v.postDelayed(new Runnable() {
                    @Override
                    public void run() {adminScheduleBtnCardView.setShapeType(0);}
                }, 200);
                // Move to AdminSchedule Page & transfer main page background color
                Intent intent = new Intent(getApplicationContext(), AdminScheduleActivity.class);
                intent.putExtra("background_color", backgroundColor);
                intent.putExtra("mode", mode);
                startActivity(intent);
            }
        });

        // CallBtn onClickListener
        adminCallBtnCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Change ShapeType to 'pressed' when clicked
                adminCallBtnCardView.setShapeType(1);
                // After clicked, it changes back to 'flat'
                v.postDelayed(new Runnable() {
                    @Override
                    public void run() {adminCallBtnCardView.setShapeType(0);}
                }, 200);

                // Move to Dial
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:/010-1234-1234"));
                startActivity(intent);
            }
        });

        // LoginBtn onClickListener
        adminLoginBtnCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Change ShapeType to 'pressed' when clicked
                adminLoginBtnCardView.setShapeType(1);
                // After clicked, it changes back to 'flat'
                v.postDelayed(new Runnable() {
                    @Override
                    public void run() {adminLoginBtnCardView.setShapeType(0);}
                }, 200);

                String loginStatus = adminLoginTextView.getText().toString();

                // 로그아웃 상태인 경우 로그인 액티비티로 이동
                if(loginStatus.equals("로그인")){
                    // Move to Login Page & transfer main page background color
                    Intent intent = new Intent(getApplicationContext(), AdminLoginActivity.class);
                    intent.putExtra("background_color", backgroundColor);
                    intent.putExtra("mode", mode);
                    startActivity(intent);
                }
                // 로그인 상태인 경우 로그아웃 처리
                else{
                    // 로그아웃 처리
                    sharedPreferences = getSharedPreferences("AdminSession", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.remove("admin_id");
                    editor.apply();
                    adminLoginTextView.setText("로그인");
                    adminListBtnCardView.setVisibility(View.GONE);
                    adminScheduleBtnCardView.setVisibility(View.GONE);
                    adminCallBtnCardView.setVisibility(View.GONE);
                    adminCreateAppQRBtnCardView.setVisibility(View.GONE);
                    Toast.makeText(AdminMainActivity.this, "관리자 로그아웃을 완료했습니다.", Toast.LENGTH_SHORT).show();
                }

            }
        });

        //adminCreateAppQRBtnCardView onClickListener
        adminCreateAppQRBtnCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentCreateAppQR = new Intent(AdminMainActivity.this, AdminCreateAppQR.class);
                startActivity(intentCreateAppQR); // AdminCreateQR 액티비티를 시작합니다.
            }
        });

        // AdminExitBtn onClickListener
        adminExitCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toast.makeText(AdminSettingActivity.this, "adminExitCardView가 클릭되었습니다!", Toast.LENGTH_SHORT).show();
                // AlertDialog를 통해 사용자에게 종료 여부를 물음
                AlertDialog.Builder builder = new AlertDialog.Builder(AdminMainActivity.this);
                builder.setMessage("정말 종료 하시겠습니까?");
                builder.setPositiveButton("종료", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 로그아웃 처리
                        sharedPreferences = getSharedPreferences("AdminSession", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.remove("admin_id");
                        editor.apply();
                        // 사용자가 종료를 선택한 경우 액티비티 종료
                        finish();
                    }
                });
                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 사용자가 취소를 선택한 경우 아무 동작도 하지 않음
                        dialog.dismiss();
                    }
                });
                // AlertDialog 표시
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        // HomeBtn onClickListener
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Change ShapeType to 'pressed' when clicked
                homeBtn.setShapeType(1);
                // After clicked, it changes back to 'flat'
                v.postDelayed(new Runnable() {
                    @Override
                    public void run() {homeBtn.setShapeType(0);}
                }, 200);
                vFlipper.setDisplayedChild(0);
            }
        });

        // MemberBtn onClickListener
        memberBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Change ShapeType to 'pressed' when clicked
                memberBtn.setShapeType(1);
                // After clicked, it changes back to 'flat'
                v.postDelayed(new Runnable() {
                    @Override
                    public void run() {memberBtn.setShapeType(0);}
                }, 200);
                // 세션 객체를 이용하여 현재 로그인한 관리자의 아이디 값을 가져온다.
                sharedPreferences = getSharedPreferences("AdminSession", MODE_PRIVATE);
                String adminId = sharedPreferences.getString("admin_id", null);

                // 관리자가 로그인하지 않았을 경우 토스트 메시지를 표시하고 추가 실행을 중단한다.
                if (adminId == null) {
                    Toast.makeText(AdminMainActivity.this, "관리자 로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
                fetchMemberData();
                vFlipper.setDisplayedChild(1);
            }
        });
        // Member ListView Item onClickListener
        memberListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get Information of Clicked Member Item
                MemberData selectedItem = (MemberData) memberAdapter.getItem(position);
                showMemberInfoDialog(selectedItem);
            }
        });

        // memberSearchBtn onClickListener
        memberSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadeMemberFromFireStore();
            }
        });

        // ProductBtn onClickListener
        productBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Change ShapeType to 'pressed' when clicked
                productBtn.setShapeType(1);
                // After clicked, it changes back to 'flat'
                v.postDelayed(new Runnable() {
                    @Override
                    public void run() {productBtn.setShapeType(0);}
                }, 200);

                // 세션 객체를 이용하여 현재 로그인한 관리자의 아이디 값을 가져온다.
                sharedPreferences = getSharedPreferences("AdminSession", MODE_PRIVATE);
                String adminId = sharedPreferences.getString("admin_id", null);

                // 관리자가 로그인하지 않았을 경우 토스트 메시지를 표시하고 추가 실행을 중단한다.
                if (adminId == null) {
                    Toast.makeText(AdminMainActivity.this, "관리자 로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
                vFlipper.setDisplayedChild(2);
            }
        });



        // Product ListView Item onClickListener
        productListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get Information of Clicked Member Item
                ProductData selectedItem = productDataList.get(position);
                showProductInfoDialog(selectedItem);
            }
        });

        productSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchText = editTextFieldSearchProductName.getText().toString();
                editTextFieldSearchProductName.setText("");
                currentSearchText = searchText; // 현재 검색 텍스트 업데이트
                loadItemsFromFireStore(); // 필터링된 상품 새로고침
                currentSearchText="";
            }
        });

        imageViewProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileManager();
            }
        });

        // PayHistoryBtn onClickListener
        payHistoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Change ShapeType to 'pressed' when clicked
                payHistoryBtn.setShapeType(1);
                // After clicked, it changes back to 'flat'
                v.postDelayed(new Runnable() {
                    @Override
                    public void run() {payHistoryBtn.setShapeType(0);}
                }, 200);

                // 세션 객체를 이용하여 현재 로그인한 관리자의 아이디 값을 가져온다.
                sharedPreferences = getSharedPreferences("AdminSession", MODE_PRIVATE);
                String adminId = sharedPreferences.getString("admin_id", null);

                // 관리자가 로그인하지 않았을 경우 토스트 메시지를 표시하고 추가 실행을 중단한다.
                if (adminId == null) {
                    Toast.makeText(AdminMainActivity.this, "관리자 로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
                vFlipper.setDisplayedChild(3);
                fetchPaymentData();
            }
        });
        paymentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get Information of Clicked Payment Item
                PaymentData selectedItem = (PaymentData) paymentAdapter.getItem(position);
                showPaymentInfoDialog(selectedItem);
            }
        });

        paySearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadePaymentFromFireStore();
            }
        });

        // CreateQRBtn OnClickListener
        createQRBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminMainActivity.this, AdminCreateQR.class);
                startActivity(intent); // AdminCreateQR 액티비티를 시작합니다.
            }
        });

        createProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(AdminMainActivity.this, "서버와 연동으로 인해 약간의 딜레이가 발생할 수 있습니다.", Toast.LENGTH_SHORT).show();
                uploadFileAndSaveProductInfo();

            }
        });

        // RadioGroup OnCheckedChangeListener
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.categoryRadioBtn1) {
                    productCategory = categoryStr1;
                } else if (checkedId == R.id.categoryRadioBtn2) {
                    productCategory = categoryStr2;
                } else if (checkedId == R.id.categoryRadioBtn3) {
                    productCategory = categoryStr3;
                }
            }
        });

        // ProductPushBtn onClickListener
        productPushBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Change ShapeType to 'pressed' when clicked
                productPushBtn.setShapeType(1);
                // After clicked, it changes back to 'flat'
                v.postDelayed(new Runnable() {
                    @Override
                    public void run() {productPushBtn.setShapeType(0);}
                }, 200);

                // 세션 객체를 이용하여 현재 로그인한 관리자의 아이디 값을 가져온다.
                sharedPreferences = getSharedPreferences("AdminSession", MODE_PRIVATE);
                String adminId = sharedPreferences.getString("admin_id", null);

                // 관리자가 로그인하지 않았을 경우 토스트 메시지를 표시하고 추가 실행을 중단한다.
                if (adminId == null) {
                    Toast.makeText(AdminMainActivity.this, "관리자 로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
                vFlipper.setDisplayedChild(4);
            }
        });
    }

    // 회원 데이터를 가져오는 메서드 (이석재)
    private void fetchMemberData() {
        memberDBFireStore = FirebaseFirestore.getInstance();
        ArrayList<MemberData> memberDataList = new ArrayList<>();

        memberDBFireStore.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                int i = 1;  // num을 위한 카운터 시작 값
                if (task.isSuccessful()){
                    for(QueryDocumentSnapshot document : task.getResult()){
                        String id = document.getString("userEmail");
                        String name = document.getString("userName");
                        Date joindate = document.getTimestamp("userJoindate").toDate();
                        String uid = document.getString("uid");
                        int point = document.getLong("userPoint").intValue();
                        boolean isValid = document.getBoolean("isValid");

                        if(isValid){
                            memberDataList.add(new MemberData(i, id, name, joindate, uid, point));
                            i++;
                        }
                    }
                    if (memberDataList.isEmpty()){
                        Toast.makeText(getApplicationContext(), "결과가 없습니다.", Toast.LENGTH_SHORT).show();
                        memberAdapter.updateData(memberDataList);
                    }
                    else{
                        memberAdapter.updateData(memberDataList);
                    }
                }
            }
        });
    }



    // 결제 데이터를 가져오는 메서드 (이석재)
    private void fetchPaymentData(){
        paymentDBFireStore = FirebaseFirestore.getInstance();
        ArrayList<PaymentData> paymentDataList = new ArrayList<>();

        paymentDBFireStore.collection("payments").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                int i = 1;  // num을 위한 카운터 시작 값
                if (task.isSuccessful()){
                    for(QueryDocumentSnapshot document : task.getResult()){
                        String receiptId = document.getString("receipt_id");
                        String name = document.getString("userName");
                        String uid = document.getString("uid");
                        String email = document.getString("userEmail");
                        Map<String, Number> products = (Map<String, Number>) document.get("products");
                        int totalPrice = document.getLong("totalPrice").intValue();
                        int usePoint = document.getLong("usePoint").intValue();
                        Date payDay = document.getTimestamp("payDay").toDate();
                        boolean isValid = document.getBoolean("isValid");

                        if(isValid){
                            paymentDataList.add(new PaymentData(i, receiptId, name, uid, email, products, totalPrice, usePoint, payDay));
                            i++;
                        }
                    }

                }
                if (paymentDataList.isEmpty()){
                    Toast.makeText(getApplicationContext(), "결과가 없습니다.", Toast.LENGTH_SHORT).show();
                    paymentAdapter.updateData(paymentDataList);
                }
                else{
                    paymentAdapter.updateData(paymentDataList);
                }
            }
        });
    }

    // Error Dialog Method
    public static void showErrorDialog(Context context, String message) {
        // Create Dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("오류 발생")
                .setMessage(message)
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Pressed "확인" Btn
                        dialog.dismiss(); // Close Dialog
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // ProductData Info Dialog
    private void showProductInfoDialog(ProductData selectedItem) {
        // Create Dialog & Layout Setting
        Dialog dialog = new Dialog(AdminMainActivity.this);
        dialog.setContentView(R.layout.admin_dialog_product_item_info);

        // Get TextView ID in Dialog
        ImageView imageViewProductImage = dialog.findViewById(R.id.imageViewProductImage);
        Glide.with(AdminMainActivity.this)
                .load(selectedItem.getProductImage())
                .placeholder(R.drawable.default_image) // 로드 중 이미지
                .into(imageViewProductImage);

        TextView textViewProductCode = dialog.findViewById(R.id.textViewProductCode);
        textViewProductCode.setText("상품코드: " + selectedItem.getProductCode());

        TextView textViewProductName = dialog.findViewById(R.id.textViewProductName);
        textViewProductName.setText("상품명: " + selectedItem.getProductName());

        TextView textViewProductCategory = dialog.findViewById(R.id.textViewProductCategory);
        textViewProductCategory.setText("카테고리: " + selectedItem.getProductCategory());

        TextView textViewProductStock = dialog.findViewById(R.id.textViewProductStock);
        textViewProductStock.setText("상품재고: " + selectedItem.getProductStock());

        TextView textViewProductPrice = dialog.findViewById(R.id.textViewProductPrice);
        textViewProductPrice.setText("상품가격: " + selectedItem.getProductPrice() + "원");

        // Show Dialog
        dialog.show();
    }

    // Member Info Dialog
    private void showMemberInfoDialog(MemberData selectedItem) {
        // Create Dialog & Layout Setting
        Dialog dialog = new Dialog(AdminMainActivity.this);
        dialog.setContentView(R.layout.admin_dialog_member_item_info);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String formattedDate = sdf.format(selectedItem.getJoinDate());

        // Get TextView ID in Dialog
        TextView textViewMemberId = dialog.findViewById(R.id.textViewMemberNum);
        textViewMemberId.setText("Num : " + selectedItem.getNumber());

        TextView textViewMemberName = dialog.findViewById(R.id.textViewMemberId);
        textViewMemberName.setText("회원아이디 : " + selectedItem.getMemberId());

        TextView textViewMemberDate = dialog.findViewById(R.id.textViewMemberDate);
        textViewMemberDate.setText("가입날짜 : " + formattedDate);

        TextView textViewMemberAmount = dialog.findViewById(R.id.textViewMemberPoint);
        textViewMemberAmount.setText("Point : " + selectedItem.getPoint() + "점");

        // Show Dialog
        dialog.show();
    }



    // Member Info Dialog
    private void showPaymentInfoDialog(PaymentData selectedItem) {
        // Create Dialog & Layout Setting
        Dialog dialog = new Dialog(AdminMainActivity.this);
        dialog.setContentView(R.layout.admin_dialog_payment_item_info);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String formattedDate = sdf.format(selectedItem.getPayDay());

        // Get TextView ID in Dialog
        TextView textViewPaymentNum = dialog.findViewById(R.id.textViewPaymentNum);
        textViewPaymentNum.setText("Num : " + selectedItem.getNumber());

        TextView textViewReceiptId = dialog.findViewById(R.id.textViewReceiptId);
        textViewReceiptId.setText("주문ID : " + selectedItem.getReceiptId());

        TextView textViewUserName = dialog.findViewById(R.id.textViewUserName);
        textViewUserName.setText("회원이름 : " + selectedItem.getUserName());

        TextView textViewUid = dialog.findViewById(R.id.textViewUid);
        textViewUid.setText("UID : " + selectedItem.getUid());

        TextView textViewUserEmail = dialog.findViewById(R.id.textViewUserEmail);
        textViewUserEmail.setText("회원아이디 : " + selectedItem.getEmail());

        TextView textViewProducts = dialog.findViewById(R.id.textViewProducts);
        textViewProducts.setText("상품 : " + selectedItem.getProducts());

        TextView textViewTotalPrice = dialog.findViewById(R.id.textViewTotalPrice);
        textViewTotalPrice.setText("결제금액 : " + selectedItem.getTotalPrice());

        TextView textViewUsePoint = dialog.findViewById(R.id.textViewUsePoint);
        textViewUsePoint.setText("사용포인트 : " + selectedItem.getUsePoint());

        TextView textViewPayDay = dialog.findViewById(R.id.textViewPayDay);
        textViewPayDay.setText("결제일 : " + selectedItem.getPayDay());

        // Show Dialog
        dialog.show();
    }





    // 자동으로 새로고침을 하게 만들어주는 메서드 (made by 오자현)
    private void startAutoRefresh() {
        // Runnable 인터페이스를 구현한 익명 클래스 객체를 생성합니다.
        // Runnable은 실행할 작업을 정의하는 인터페이스로, run() 메서드를 구현해야 합니다.
        runnable = new Runnable() {
            @Override
            public void run() {
                // Firestore에서 데이터를 로드하는 메서드를 호출합니다.
                loadItemsFromFireStore();

                // 5초 후에 이 Runnable 객체를 다시 실행하도록 스케줄링합니다.
                // Handler는 다른 스레드에서 실행된 작업을 메인 스레드에서 처리하거나,
                // 일정 시간 후에 코드를 실행할 때 사용됩니다.
                // postDelayed(this, 5000)은 5초 후에 현재 Runnable 객체의 run() 메서드를 다시 실행합니다.
                handler.postDelayed(this, 5000);
            }
        };
        // 처음에 5초 후에 이 Runnable 객체를 실행하도록 스케줄링합니다.
        // 이 줄은 startAutoRefresh() 메서드가 처음 호출될 때 실행됩니다.
        handler.postDelayed(runnable, 5000);  // 처음 시작
    }


    // 파이어베이스에서 상품명으로 검색하고 데이터를 읽어오는 메서드 (오자현)
    void loadItemsFromFireStore() {
        // Firestore 데이터베이스 인스턴스를 가져옵니다.
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Task<QuerySnapshot> query;

        // 검색 텍스트가 비어있지 않으면, 해당 상품 이름으로 필터링된 쿼리를 실행합니다.
        if (!currentSearchText.isEmpty()) {
            // 검색 텍스트가 있을 경우, 해당 상품 이름으로 필터링된 쿼리 실행
            query = db.collection("products")
                    .whereEqualTo("productName", currentSearchText)
                    .get();
        } else {
            // 검색 텍스트가 없을 경우, 전체 상품 로드
            query = db.collection("products").get();
        }

        // 쿼리가 완료된 후 실행될 리스너를 추가합니다.
        query.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
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
                        int stock = document.getLong("productStock").intValue();

                        // 이미지 URL이 null이거나 비어있으면 기본 이미지 URL을 사용합니다.
                        if (imageUrl == null || imageUrl.isEmpty()) {
                            imageUrl = "R.drawable.default_image"; // 기본 이미지 URL 사용
                        }

                        // 로드된 이미지 URL을 로그에 출력합니다.
                        Log.d("DatabaseViewActivity", "Loaded imageUrl: " + imageUrl);
                        // 새로운 제품 데이터를 리스트에 추가합니다.
                        productDataList.add(new ProductData(code, productName, category, imageUrl, price, stock)); // 리스트에 제품 추가
                    }
                    // 어댑터에 데이터 변경을 알립니다.
                    productAdapter.notifyDataSetChanged(); // 데이터 변경을 어댑터에 알림
                } else {
                    // 쿼리 실행 중 오류가 발생하면 로그에 출력합니다.
                    Log.e("DatabaseViewActivity", "Error getting documents: ", task.getException());
                }
            }
        });
    }

    // 파이어베이스에 데이터를 저장하는 메서드 (오자현)
    private void uploadFileAndSaveProductInfo() {
        Log.d("UploadFile", "uploadFileAndSaveProductInfo started");

        // 입력 필드에서 값을 가져와서 문자열 변수에 저장합니다.
        String name = editTextFieldProductName.getText().toString().trim();
        String priceStr = editTextFieldProductPrice.getText().toString().trim();
        String stockStr = editTextFieldProductStock.getText().toString().trim();
        String category = productCategory;

        // 필수 입력 필드가 비어있는지 확인하고, 비어있으면 경고 메시지를 표시하고 메서드를 종료합니다.
        if (name.isEmpty() || priceStr.isEmpty() || stockStr.isEmpty() || productFileUri == null || category.isEmpty()) {
            Toast.makeText(this, "모든 필드를 채워주세요", Toast.LENGTH_SHORT).show();
            return;
        }
        // 문자열로 된 가격과 재고 수량을 정수로 변환합니다.
        int price = Integer.parseInt(priceStr);
        int stock = Integer.parseInt(stockStr);

        // FirebaseStorage 인스턴스를 가져옵니다.
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        // 파일을 저장할 경로를 설정합니다.
        StorageReference fileRef = storageRef.child("files/" + System.currentTimeMillis());


        // 파일 업로드 작업을 시작합니다.

        UploadTask uploadTask = fileRef.putFile(productFileUri);
        uploadTask.addOnSuccessListener(taskSnapshot -> taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(uri -> {
            Log.d("UploadFile", "파이어베이스업로드리스너 진입");
            String fileUrl = uri.toString();

            // Firestore에서 productCounter 문서를 업데이트하고 새 productCode를 가져온다

            // 중요! lastProductCode 필드에 초기값(0)을 설정. 데이터 타입은 number 이 문서가 없으면 프로그램이 진행 안됨 중요!
            // 중요! 만약에 상품 컬렉션을 지웠으면 이거도 관리해서 0으로 만들것(수동이라 반드시 해야함 ) 중요!
            DocumentReference counterRef = productDBFireStore.collection("counters").document("productCounter");
            productDBFireStore.runTransaction(transaction -> {
                DocumentSnapshot counterSnapshot = transaction.get(counterRef);
                Long lastProductCode = counterSnapshot.getLong("lastProductCode");
                if (lastProductCode == null) lastProductCode = 0L; // 초기값 설정
                Long newProductCode = lastProductCode + 1;
                transaction.update(counterRef, "lastProductCode", newProductCode);// 새로운 productCode 사용

                // 상품 정보와 파일 URL을 Firestore에 저장합니다.
                Map<String, Object> product = new HashMap<>();
                product.put("productName", name);
                product.put("productImage", fileUrl);
                product.put("productPrice", price);
                product.put("productStock", stock);
                product.put("productCategory", category);
                product.put("productCode", newProductCode); // 새로운 productCode 사용

                // Firestore에 새로운 상품 정보를 추가합니다.
                productDBFireStore.collection("products").add(product).addOnSuccessListener(documentReference -> {
                    // 성공적으로 저장되면 사용자에게 알림을 표시하고, 액티비티를 종료합니다.
                    Toast.makeText(AdminMainActivity.this, "상품 정보와 파일 URL 파이어베이스에 저장 성공", Toast.LENGTH_SHORT).show();
                    finishActivityWithResult();
                }).addOnFailureListener(e -> {
                    // 저장 실패 시 사용자에게 오류 메시지를 표시합니다.
                    Toast.makeText(AdminMainActivity.this, "파이어베이스 저장 실패: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });

                return null;
            }).addOnFailureListener(e -> {
                // 상품 코드 업데이트 실패 시 사용자에게 오류 메시지를 표시합니다.
                Toast.makeText(AdminMainActivity.this, "상품 코드 업데이트 실패: " + e.getMessage(), Toast.LENGTH_LONG).show();
            });
        })).addOnFailureListener(e -> {
            // 파일 업로드 실패 시 사용자에게 오류 메시지를 표시합니다.
            Toast.makeText(AdminMainActivity.this, "파일 업로드 실패: " + e.getMessage(), Toast.LENGTH_LONG).show();
        });
    }

    // 파이어베이스에 업로드 후처리를 위한 메서드(여기선 홈으로 보내고 내용을 초기화함) (오자현)
    private void finishActivityWithResult() {
        // vFlipper의 첫 번째 화면을 표시합니다.
        vFlipper.setDisplayedChild(0);
        // imageViewProduct를 기본 이미지로 초기화합니다.
        imageViewProduct.setImageResource(android.R.drawable.ic_menu_camera);
        // 제품 이름 입력 필드를 비웁니다.
        editTextFieldProductName.setText("");
        // 제품 가격 입력 필드를 비웁니다.
        editTextFieldProductPrice.setText("");
        // 제품 재고 입력 필드를 비웁니다.
        editTextFieldProductStock.setText("");
        // 카테고리 라디오 버튼을 첫 번째 버튼으로 초기화합니다.
        radioGroup.check(R.id.categoryRadioBtn1);
    }

    // 파일 관리자를 여는 메서드 (오자현)
    private void openFileManager() {
        // Intent.ACTION_OPEN_DOCUMENT 액션을 사용하여 파일을 열기 위한 인텐트를 생성합니다.
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        // CATEGORY_OPENABLE 카테고리를 추가하여 파일을 열 수 있는 앱만 표시되도록 합니다.
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        // 모든 유형의 파일을 허용합니다.
        intent.setType("*/*");
        // 인텐트를 시작하여 파일 선택 화면을 엽니다.
        startActivityForResult(intent, PICK_FILE_REQUEST);
        // 모든 유형의 파일을 허용하지만, 실제로 선택할 수 있는 파일은 이미지 파일뿐입니다.
    }

    // 결과를 반환하는 메서드 (오자현)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 파일 선택 요청인지 확인하고, 결과가 성공적이며, 인텐트 데이터가 null이 아닌지 확인합니다.
        if (requestCode == PICK_FILE_REQUEST && resultCode == RESULT_OK && data != null) {
            // 선택한 파일의 URI를 가져옵니다.
            Uri selectedFileUri = data.getData();
            // productFileUri 변수에 선택한 파일의 URI를 저장합니다.
            productFileUri = data.getData();
            // 선택한 파일의 URI를 ImageView에 로드하여 이미지를 표시합니다.
            imageViewProduct.setImageURI(selectedFileUri);
        }
    }



    // 파이어베이스에서 회원 이름으로 검색하고 회원 데이터를 읽어오는 메서드 (이석재)
    private void loadeMemberFromFireStore() {
        // 인스턴스 가져오기, 회원 목록 배열 생성
        memberDBFireStore = FirebaseFirestore.getInstance();
        ArrayList<MemberData> memberList = new ArrayList<>();

        // 검색창에 입력한 값을 가져오기
        String searchId = editTextFieldSearchMemberName.getText().toString().trim();

        // 쿼리문 작성
        Query query;
        if (searchId.isEmpty()) {
            // 검색어가 없을 경우 전체 문서를 조회
            query = memberDBFireStore.collection("users");
        } else {
            // 입력된 검색어로 시작하는 adminId를 가진 문서를 조회
            query = memberDBFireStore.collection("users").orderBy("userName").startAt(searchId).endAt(searchId + '\uf8ff');
        }

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            // DB에서 검색이 완료된 경우
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    int i = 1; // num을 위한 카운터 시작 값
                    // 검색한 회원 수 만큼 반복
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String id = document.getString("userEmail");
                        String name = document.getString("userName");
                        Date joindate = document.getTimestamp("userJoindate").toDate();
                        String uid = document.getString("uid");
                        int point = document.getLong("userPoint").intValue();
                        boolean isValid = document.getBoolean("isValid");

                        // 탈퇴하지 않은 회원인 경우 리스트에 추가
                        if(isValid){
                            memberList.add(new MemberData(i, id, name, joindate, uid, point));
                            i++;
                        }
                    }
                    // 검색 결과가 없는 경우
                    if (memberList.isEmpty()) {
                        memberAdapter.updateData(memberList);
                        Toast.makeText(getApplicationContext(), "검색 결과가 없습니다.", Toast.LENGTH_SHORT).show();
                    }
                    // 검색결과가 있는 경우
                    else {
                        memberAdapter.updateData(memberList);
                        Toast.makeText(getApplicationContext(), "검색 완료", Toast.LENGTH_SHORT).show();
                    }
                }
                // DB에서 데이터를 가져오는 중 오류가 발생한 경우
                else {
                    Log.d("Firestore Search", "Error getting documents: ", task.getException());
                    Toast.makeText(getApplicationContext(), "검색 중 오류 발생", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // 파이어베이스에서 회원 이름으로 검색하고 결제 데이터를 읽어오는 메서드 (이석재)
    private void loadePaymentFromFireStore(){
        // 인스턴스 가져오기, 회원 목록 배열 생성
        paymentDBFireStore = FirebaseFirestore.getInstance();
        ArrayList<PaymentData> paymentList = new ArrayList<>();

        // 검색창에 입력한 값을 가져오기
        String searchId = editTextFieldPaymentSearchMemberName.getText().toString().trim();

        // 쿼리문 작성
        Query query;
        if (searchId.isEmpty()) {
            // 검색어가 없을 경우 전체 문서를 조회
            query = paymentDBFireStore.collection("payments");
        } else {
            // 입력된 검색어로 시작하는 adminId를 가진 문서를 조회
            query = paymentDBFireStore.collection("payments").orderBy("userName").startAt(searchId).endAt(searchId + '\uf8ff');
        }

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            // DB에서 검색이 완료된 경우
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    int i = 1; // num을 위한 카운터 시작 값
                    // 검색한 회원 수 만큼 반복
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String receiptId = document.getString("receipt_id");
                        String name = document.getString("userName");
                        String uid = document.getString("uid");
                        String email = document.getString("userEmail");
                        Map<String, Number> products = (Map<String, Number>) document.get("products");
                        int totalPrice = document.getLong("totalPrice").intValue();
                        int usePoint = document.getLong("usePoint").intValue();
                        Date payDay = document.getTimestamp("payDay").toDate();
                        boolean isValid = document.getBoolean("isValid");
                        if(isValid){
                            paymentList.add(new PaymentData(i, receiptId, name, uid, email, products, totalPrice, usePoint, payDay));
                            i++;
                        }
                    }
                    // 검색 결과가 없는 경우
                    if (paymentList.isEmpty()) {
                        paymentAdapter.updateData(paymentList);
                        Toast.makeText(getApplicationContext(), "검색 결과가 없습니다.", Toast.LENGTH_SHORT).show();
                    }
                    // 검색결과가 있는 경우
                    else {
                        paymentAdapter.updateData(paymentList);
                        Toast.makeText(getApplicationContext(), "검색 완료", Toast.LENGTH_SHORT).show();
                    }
                }
                // DB에서 데이터를 가져오는 중 오류가 발생한 경우
                else {
                    Log.d("Firestore Search", "Error getting documents: ", task.getException());
                    Toast.makeText(getApplicationContext(), "검색 중 오류 발생", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        // AlertDialog를 통해 사용자에게 종료 여부를 물음
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("정말 종료 하시겠습니까?");
        builder.setPositiveButton("종료", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 로그아웃 처리
                sharedPreferences = getSharedPreferences("AdminSession", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove("admin_id");
                editor.apply();
                // 사용자가 종료를 선택한 경우 액티비티 종료
                // 상위 클래스의 구현도 호출
                finish();
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 사용자가 취소를 선택한 경우 아무 동작도 하지 않음
                dialog.dismiss();
            }
        });
        // AlertDialog 표시
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = getSharedPreferences("AdminSession", MODE_PRIVATE);
        // 로그인 상태에 따라 로그인 카드뷰의 텍스트 변경
        String adminId = sharedPreferences.getString("admin_id", null);
        if(adminId != null){
            adminLoginTextView.setText("로그아웃");
            adminListBtnCardView.setVisibility(View.VISIBLE);
            adminScheduleBtnCardView.setVisibility(View.VISIBLE);
            adminCallBtnCardView.setVisibility(View.VISIBLE);
            adminCreateAppQRBtnCardView.setVisibility(View.VISIBLE);
        }
        else{
            adminLoginTextView.setText("로그인");
            adminListBtnCardView.setVisibility(View.GONE);
            adminScheduleBtnCardView.setVisibility(View.GONE);
            adminCallBtnCardView.setVisibility(View.GONE);
            adminCreateAppQRBtnCardView.setVisibility(View.GONE);
        }
    }
}