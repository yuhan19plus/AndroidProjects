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

/** 담당자 임성준, 이석재, 오자현
 * 초기 작성 및 관리자 전체 UI담당 및 다크모드 기능 구현 임성준.
 * 회원관리 기능, 결제내역 기능 구현 이석재
 * 상품관리 기능, 상품등록 기능 구현 오자현
 * 리팩토링 : 오자현, 임성준
 * */
public class AdminMainActivity extends AppCompatActivity {
    // 상수 선언 - 이석재 오자현
    private static final int PICK_FILE_REQUEST = 2;
    private static final int MAIN_CHILD_INDEX = 0;
    private static final int MEMBER_CHILD_INDEX = 1;
    private static final int PRODUCT_CHILD_INDEX = 2;
    private static final int PAYMENT_CHILD_INDEX = 3;
    private static final int PRODUCT_PUSH_CHILD_INDEX = 4;

    // 다크/라이트 모드 초기값 & 기본색상 설정 - 임성준
    private int mode = 0;
    private int backgroundColor;
    private int mainBackgroundColor = Color.rgb(236, 240, 243);
    private final int darkModeBackgroundColor = Color.rgb(97, 97, 97);
    private final int btnColor = Color.rgb(0, 174, 142);
    private final int radioButtonTextColor = Color.rgb(0, 105, 97);

    // Firebase 인스턴스 및 어댑터 - 임성준 오자현 이석재
    private FirebaseFirestore productDBFireStore;
    private FirebaseFirestore memberDBFireStore;
    private FirebaseFirestore paymentDBFireStore;
    private Uri productFileUri = null;
    private MemberAdapter memberAdapter;
    private PayMentAdapter paymentAdapter;
    private ProductAdapter productAdapter;

    // UI 요소 - 임성준
    // 뷰 플리퍼 (여러 화면 전환을 위한 뷰 그룹)
    private ViewFlipper vFlipper;
    // 관리자 설정 및 모드 변경을 위한 이미지 뷰
    private NeumorphImageView admin_setting, changeMode;
    // 메인 카드뷰 및 푸터 메뉴
    private NeumorphCardView mainCardView, footer_menu;
    // 이미지 뷰 - 홈, 회원, 제품, 결제 내역, 제품 푸시 버튼
    private NeumorphImageView homeBtn, memberBtn, productBtn, payHistoryBtn, productPushBtn;
    // 회원, 제품, 결제 목록을 표시하는 리스트 뷰
    private ListView memberListView, productListView, paymentListView;
    // 회원 리스트 카드뷰, ID 검색 카드뷰, 회원 검색 버튼
    private NeumorphCardView memberListCardView, input_searchIdCardView;
    private NeumorphButton memberSearchBtn;
    // 회원 이름 검색 입력란
    private EditText editTextFieldSearchMemberName;
    // 제품 ID 검색 카드뷰, 제품 리스트 카드뷰, 제품 이미지 뷰, 제품 검색 버튼
    private NeumorphCardView input_searchProductIdCardView, productListCardView;
    private ImageView imageViewProduct;
    private NeumorphButton productSearchBtn;
    // 제품 이름 검색 입력란
    private EditText editTextFieldSearchProductName;
    // 관리자 버튼 카드뷰들 (관리자 목록, 일정, 전화, 로그인, QR코드 생성, 종료)
    private NeumorphCardView adminListBtnCardView, adminScheduleBtnCardView, adminCallBtnCardView, adminLoginBtnCardView, adminCreateAppQRBtnCardView, adminExitCardView;
    // 관리자 이미지 뷰들 (관리자 목록, 일정, 전화, 로그인, QR코드 생성, 종료)
    private ImageView adminListImage, adminScheduleImage, adminCallImage, adminLoginImage, adminCreateAppQRImage, adminExitImage;
    // 관리자 로그인 텍스트뷰
    private TextView adminLoginTextView;
    // 결제 목록 카드뷰, 결제 ID 검색 카드뷰, 결제 검색 버튼
    private NeumorphCardView payListCardView, input_searchPayIdCardView;
    private NeumorphButton paySearchBtn;
    // 결제 회원 이름 검색 입력란
    private EditText editTextFieldPaymentSearchMemberName;
    // 제품 이미지 카드뷰, 제품 이름 카드뷰, 제품 재고 카드뷰, 제품 카테고리 카드뷰, 제품 가격 카드뷰
    private NeumorphCardView input_productImageCardView, input_productNameCardView, input_productStockCardView, input_productCategoryCardView, input_productPriceCardView;
    // 제품 이름, 가격, 재고 입력란
    private EditText editTextFieldProductName, editTextFieldProductPrice, editTextFieldProductStock;
    // 라디오 그룹 및 카테고리 라디오 버튼
    private RadioGroup radioGroup;
    private RadioButton categoryRadioBtn1, categoryRadioBtn2, categoryRadioBtn3;
    // QR코드 생성 버튼 및 제품 생성 버튼
    private NeumorphButton createQRBtn, createProductBtn;

    // 데이터 및 핸들러 - 오자현
    private String productCategory;
    private String currentSearchText = "";
    private Handler handler = new Handler();
    private ArrayList<ProductData> productDataList = new ArrayList<>();

    // 세션 객체
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_activity_main_page);
        // UI 초기화
        initUI();
        // 어댑터 초기화
        initAdapters();
        // Firebase 초기화
        initFirebase();
        // 리스너 설정
        setupListeners();
        // 자동 새로고침 시작
        startAutoRefresh();
        // Firestore에서 아이템 로드
        loadItemsFromFireStore();
    }

    // UI 요소 초기화 메서드 - 임성준 이석재 작성.
    private void initUI() {
        vFlipper = findViewById(R.id.viewFlipper1);
        LinearLayout main = findViewById(R.id.main);
        backgroundColor = mainBackgroundColor;
        main.setBackgroundColor(backgroundColor);

        Drawable backgroundDrawable = main.getBackground();
        mainBackgroundColor = ((ColorDrawable) backgroundDrawable).getColor();

        // 상단
        admin_setting = findViewById(R.id.admin_setting);
        changeMode = findViewById(R.id.darkMode);

        // admin main
        adminListBtnCardView = findViewById(R.id.adminListBtnCardView);
        adminScheduleBtnCardView = findViewById(R.id.adminScheduleBtnCardView);
        adminCallBtnCardView = findViewById(R.id.adminCallBtnCardView);
        adminLoginBtnCardView = findViewById(R.id.adminLoginBtnCardView);
        adminCreateAppQRBtnCardView = findViewById(R.id.adminCreateAppQRBtnCardView);
        adminExitCardView = findViewById(R.id.adminExitCardView);

        // admin main 이미지
        adminListImage = findViewById(R.id.adminListImage);
        adminScheduleImage = findViewById(R.id.adminScheduleImage);
        adminCallImage = findViewById(R.id.adminCallImage);
        adminLoginImage = findViewById(R.id.adminLoginImage);
        adminCreateAppQRImage = findViewById(R.id.adminCreateAppQRImage);
        adminExitImage = findViewById(R.id.adminExitImage);

        adminLoginTextView = findViewById(R.id.adminLoginTextView);

        mainCardView = findViewById(R.id.mainCardView);
        footer_menu = findViewById(R.id.footer_menu);

        // 회원관리
        input_searchIdCardView = findViewById(R.id.input_searchIdCardView);
        editTextFieldSearchMemberName = findViewById(R.id.editTextFieldSearchMemberName);
        memberSearchBtn = findViewById(R.id.memberSearchBtn);
        memberListCardView = findViewById(R.id.memberListCardView);

        // 상품관리
        input_searchProductIdCardView = findViewById(R.id.input_searchProductIdCardView);
        productSearchBtn = findViewById(R.id.productSearchBtn);
        productListCardView = findViewById(R.id.productListCardView);
        imageViewProduct = findViewById(R.id.imageViewProduct);
        editTextFieldSearchProductName = findViewById(R.id.editTextFieldSearchProductName);

        // 결제내역
        input_searchPayIdCardView = findViewById(R.id.input_searchPayIdCardView);
        editTextFieldPaymentSearchMemberName = findViewById(R.id.editTextFieldPaymentSearchMemberName);
        paySearchBtn = findViewById(R.id.paySearchBtn);
        payListCardView = findViewById(R.id.payListCardView);

        // 상품등록
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

        // 하단 바 메뉴
        memberBtn = findViewById(R.id.memberBtn);
        productBtn = findViewById(R.id.productBtn);
        homeBtn = findViewById(R.id.homeBtn);
        payHistoryBtn = findViewById(R.id.payHistoryBtn);
        productPushBtn = findViewById(R.id.productPushBtn);

        // 버튼 색상 설정
        setButtonColors();
        // 라디오 버튼 색상 설정
        setRadioButtonColors();

        // 로그인 상태 업데이트
        sharedPreferences = getSharedPreferences("AdminSession", MODE_PRIVATE);
        updateLoginStatus();
    }

    // 어댑터 초기화 메서드 - 임성준 이석재 오자현
    private void initAdapters() {
        memberAdapter = new MemberAdapter(this, new ArrayList<MemberData>());
        memberListView = findViewById(R.id.memberListView);
        memberListView.setAdapter(memberAdapter);

        productAdapter = new ProductAdapter(this, productDataList);
        productListView = findViewById(R.id.productListView);
        productListView.setAdapter(productAdapter);

        paymentAdapter = new PayMentAdapter(this, new ArrayList<PaymentData>());
        paymentListView = findViewById(R.id.payListView);
        paymentListView.setAdapter(paymentAdapter);
    }

    // Firebase 초기화 메서드 - 이석재 오자현
    private void initFirebase() {
        productDBFireStore = FirebaseFirestore.getInstance();
        memberDBFireStore = FirebaseFirestore.getInstance();
        paymentDBFireStore = FirebaseFirestore.getInstance();
    }

    // 리스너 설정 메서드 - 임성준 작성.
    private void setupListeners() {
        admin_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleSettingClick();
            }
        });

        changeMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleChangeModeClick();
            }
        });

        adminListBtnCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleAdminListClick();
            }
        });

        adminScheduleBtnCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleAdminScheduleClick();
            }
        });

        adminCallBtnCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleAdminCallClick();
            }
        });

        adminLoginBtnCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleAdminLoginClick();
            }
        });

        adminCreateAppQRBtnCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleCreateAppQRClick();
            }
        });

        adminExitCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleAdminExitClick();
            }
        });

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleHomeClick();
            }
        });

        memberBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleMemberClick();
            }
        });

        memberListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                handleMemberItemClick(position);
            }
        });

        memberSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadMembersFromFireStore();
            }
        });

        productBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleProductClick();
            }
        });

        productListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                handleProductItemClick(position);
            }
        });

        productSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleProductSearch();
            }
        });

        imageViewProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileManager();
            }
        });

        payHistoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handlePayHistoryClick();
            }
        });

        paymentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                handlePaymentItemClick(position);
            }
        });

        paySearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadPaymentsFromFireStore();
            }
        });

        createQRBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminMainActivity.this, AdminCreateQR.class));
            }
        });

        createProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleCreateProduct();
            }
        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                handleRadioGroupCheck(checkedId);
            }
        });

        productPushBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleProductPushClick();
            }
        });
    }

    // 버튼 색상 설정 메서드 - 임성준 작성.
    private void setButtonColors() {
        memberSearchBtn.setBackgroundColor(btnColor);
        productSearchBtn.setBackgroundColor(btnColor);
        paySearchBtn.setBackgroundColor(btnColor);
        createQRBtn.setBackgroundColor(btnColor);
        createProductBtn.setBackgroundColor(btnColor);
    }

    // 라디오 버튼 색상 설정 메서드 - 임성준 작성.
    private void setRadioButtonColors() {
        categoryRadioBtn1.setTextColor(radioButtonTextColor);
        categoryRadioBtn2.setTextColor(radioButtonTextColor);
        categoryRadioBtn3.setTextColor(radioButtonTextColor);
    }

    // 로그인 상태 업데이트 메서드 - 이석재 작성.
    private void updateLoginStatus() {
        String adminId = sharedPreferences.getString("admin_id", null);
        if (adminId != null) {
            adminLoginTextView.setText("로그아웃");
            showAdminButtons(true);
        } else {
            adminLoginTextView.setText("로그인");
            showAdminButtons(false);
        }
    }

    // 관리자 버튼 표시 설정 메서드 - 임성준 이석재 작성.
    private void showAdminButtons(boolean isVisible) {
        int visibility = isVisible ? View.VISIBLE : View.GONE;
        adminListBtnCardView.setVisibility(visibility);
        adminScheduleBtnCardView.setVisibility(visibility);
        adminCallBtnCardView.setVisibility(visibility);
        adminCreateAppQRBtnCardView.setVisibility(visibility);
    }

    // 설정 버튼 클릭 핸들러 - 임성준 작성.
    private void handleSettingClick() {
        admin_setting.setShapeType(1);
        postDelayedShapeChange(admin_setting);
        if (!isAdminLoggedIn()) return;
        Intent intent = new Intent(getApplicationContext(), AdminSettingActivity.class);
        intent.putExtra("background_color", backgroundColor);
        intent.putExtra("mode", mode);
        startActivity(intent);
    }

    // 모드 변경 버튼 클릭 핸들러 - 임성준 작성.
    private void handleChangeModeClick() {
        changeMode.setShapeType(1);
        postDelayedShapeChange(changeMode);
        if (mode == 0) {
            applyDarkMode();
        } else if (mode == 1) {
            applyLightMode();
        } else {
            showErrorDialog(this, "임성준");
        }
    }

    // 다크 모드 적용 메서드 - 임성준 작성.
    private void applyDarkMode() {
        backgroundColor = darkModeBackgroundColor;
        LinearLayout main = findViewById(R.id.main);
        main.setBackgroundColor(backgroundColor);
        ChangeMode.applyMainTheme(main, mode);
        applyDarkModeStyles();
        changeMode.setImageResource(R.drawable.light);
        mode++;
    }

    // 라이트 모드 적용 메서드 - 임성준 작성.
    private void applyLightMode() {
        backgroundColor = mainBackgroundColor;
        LinearLayout main = findViewById(R.id.main);
        main.setBackgroundColor(backgroundColor);
        ChangeMode.applyMainTheme(main, mode);
        applyLightModeStyles();
        changeMode.setImageResource(R.drawable.dark);
        mode--;
    }

    // 다크 모드 스타일 적용 메서드 - 임성준 작성.
    private void applyDarkModeStyles() {
        applyCardViewDarkShadow(mainCardView, footer_menu);
        applyCardViewDarkShadow(input_searchIdCardView, memberSearchBtn, memberListCardView);
        applyCardViewDarkShadow(input_searchProductIdCardView, productSearchBtn, productListCardView);
        applyCardViewDarkShadow(adminListBtnCardView, adminScheduleBtnCardView, adminCallBtnCardView, adminLoginBtnCardView, adminCreateAppQRBtnCardView, adminExitCardView);
        applyCardViewDarkShadow(paySearchBtn, payListCardView, input_searchPayIdCardView);
        applyCardViewDarkShadow(input_productImageCardView, input_productNameCardView, input_productStockCardView, input_productCategoryCardView, input_productPriceCardView, createQRBtn, createProductBtn);
        applyColorFilterDark(categoryRadioBtn1, categoryRadioBtn2, categoryRadioBtn3);
        applyColorFilterDark(adminListImage, adminScheduleImage, adminCallImage, adminLoginImage, adminCreateAppQRImage, adminExitImage);
        applyFooterMenuDarkFilter(memberBtn, productBtn, homeBtn, payHistoryBtn, productPushBtn, admin_setting, changeMode);
        applyColorFilterDark(changeMode);
    }

    // 라이트 모드 스타일 적용 메서드 - 임성준 작성.
    private void applyLightModeStyles() {
        applyCardViewLightShadow(mainCardView, footer_menu);
        applyCardViewLightShadow(input_searchIdCardView, memberSearchBtn, memberListCardView);
        applyCardViewLightShadow(input_searchProductIdCardView, productSearchBtn, productListCardView);
        applyCardViewLightShadow(adminListBtnCardView, adminScheduleBtnCardView, adminCallBtnCardView, adminLoginBtnCardView, adminCreateAppQRBtnCardView, adminExitCardView);
        applyCardViewLightShadow(paySearchBtn, payListCardView, input_searchPayIdCardView);
        applyCardViewLightShadow(input_productImageCardView, input_productNameCardView, input_productStockCardView, input_productCategoryCardView, input_productPriceCardView, createQRBtn, createProductBtn);
        applyColorFilterLight(categoryRadioBtn1, categoryRadioBtn2, categoryRadioBtn3);
        applyColorFilterLight(adminListImage, adminScheduleImage, adminCallImage, adminLoginImage, adminCreateAppQRImage, adminExitImage);
        applyFooterMenuLightFilter(memberBtn, productBtn, homeBtn, payHistoryBtn, productPushBtn, admin_setting, changeMode);
        applyColorFilterLight(changeMode);
    }

    // 관리자 목록 버튼 클릭 핸들러 - 임성준 작성.
    private void handleAdminListClick() {
        adminListBtnCardView.setShapeType(1);
        postDelayedShapeChange(adminListBtnCardView);
        if (!isFirstAdmin()) return;
        Intent intent = new Intent(getApplicationContext(), AdminActivity.class);
        intent.putExtra("background_color", backgroundColor);
        intent.putExtra("mode", mode);
        startActivity(intent);
    }

    // 관리자 일정 버튼 클릭 핸들러 - 임성준 작성.
    private void handleAdminScheduleClick() {
        adminScheduleBtnCardView.setShapeType(1);
        postDelayedShapeChange(adminScheduleBtnCardView);
        Intent intent = new Intent(getApplicationContext(), AdminScheduleActivity.class);
        intent.putExtra("background_color", backgroundColor);
        intent.putExtra("mode", mode);
        startActivity(intent);
    }

    // 관리자 전화 버튼 클릭 핸들러 - 임성준 작성.
    private void handleAdminCallClick() {
        adminCallBtnCardView.setShapeType(1);
        postDelayedShapeChange(adminCallBtnCardView);
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:/010-1234-1234"));
        startActivity(intent);
    }

    // 관리자 로그인 버튼 클릭 핸들러 - 임성준 작성.
    private void handleAdminLoginClick() {
        adminLoginBtnCardView.setShapeType(1);
        postDelayedShapeChange(adminLoginBtnCardView);
        String loginStatus = adminLoginTextView.getText().toString();
        if ("로그인".equals(loginStatus)) {
            navigateToAdminLogin();
        } else {
            performLogout();
        }
    }

    // 앱 QR 생성 버튼 클릭 핸들러 - 오자현 작성
    private void handleCreateAppQRClick() {
        Intent intentCreateAppQR = new Intent(AdminMainActivity.this, AdminCreateAppQR.class);
        startActivity(intentCreateAppQR);
    }

    // 관리자 종료 버튼 클릭 핸들러 - 임성준 작성.
    private void handleAdminExitClick() {
        showExitDialog();
    }

    // 홈 버튼 클릭 핸들러 - 임성준 작성.
    private void handleHomeClick() {
        homeBtn.setShapeType(1);
        postDelayedShapeChange(homeBtn);
        vFlipper.setDisplayedChild(MAIN_CHILD_INDEX);
    }

    // 회원 버튼 클릭 핸들러 - 임성준 작성.
    private void handleMemberClick() {
        memberBtn.setShapeType(1);
        postDelayedShapeChange(memberBtn);
        if (!isAdminLoggedIn()) return;
        fetchMemberData();
        vFlipper.setDisplayedChild(MEMBER_CHILD_INDEX);
    }

    // 회원 리스트 아이템 클릭 핸들러 - 임성준 작성.
    private void handleMemberItemClick(int position) {
        MemberData selectedItem = (MemberData) memberAdapter.getItem(position);
        showMemberInfoDialog(selectedItem);
    }

    // 제품 버튼 클릭 핸들러 - 임성준 작성.
    private void handleProductClick() {
        productBtn.setShapeType(1);
        postDelayedShapeChange(productBtn);
        if (!isAdminLoggedIn()) return;
        vFlipper.setDisplayedChild(PRODUCT_CHILD_INDEX);
    }

    // 제품 리스트 아이템 클릭 핸들러 - 임성준 작성.
    private void handleProductItemClick(int position) {
        ProductData selectedItem = productDataList.get(position);
        showProductInfoDialog(selectedItem);
    }

    // 제품 검색 버튼 클릭 핸들러 - 임성준 작성.
    private void handleProductSearch() {
        String searchText = editTextFieldSearchProductName.getText().toString();
        editTextFieldSearchProductName.setText("");
        currentSearchText = searchText;
        loadItemsFromFireStore();
        currentSearchText = "";
    }

    // 결제 내역 버튼 클릭 핸들러 - 임성준 작성.
    private void handlePayHistoryClick() {
        payHistoryBtn.setShapeType(1);
        postDelayedShapeChange(payHistoryBtn);
        if (!isAdminLoggedIn()) return;
        vFlipper.setDisplayedChild(PAYMENT_CHILD_INDEX);
        fetchPaymentData();
    }

    // 결제 리스트 아이템 클릭 핸들러 - 임성준 작성.
    private void handlePaymentItemClick(int position) {
        PaymentData selectedItem = (PaymentData) paymentAdapter.getItem(position);
        showPaymentInfoDialog(selectedItem);
    }

    // 제품 생성 버튼 클릭 핸들러 - 오자현 작성
    private void handleCreateProduct() {
        Toast.makeText(this, "서버와 연동으로 인해 약간의 딜레이가 발생할 수 있습니다.", Toast.LENGTH_SHORT).show();
        uploadFileAndSaveProductInfo();
    }

    // 라디오 그룹 체크 변경 핸들러 - 오자현 작성
    private void handleRadioGroupCheck(int checkedId) {
        if (checkedId == R.id.categoryRadioBtn1) {
            productCategory = getString(R.string.product_category1);
        } else if (checkedId == R.id.categoryRadioBtn2) {
            productCategory = getString(R.string.product_category2);
        } else if (checkedId == R.id.categoryRadioBtn3) {
            productCategory = getString(R.string.product_category3);
        }
    }

    // 제품 푸시 버튼 클릭 핸들러 - 오자현 작성
    private void handleProductPushClick() {
        productPushBtn.setShapeType(1);
        postDelayedShapeChange(productPushBtn);
        if (!isAdminLoggedIn()) return;
        vFlipper.setDisplayedChild(PRODUCT_PUSH_CHILD_INDEX);
    }

    // 관리자 로그인 여부 확인 메서드 - 이석재 작성.
    private boolean isAdminLoggedIn() {
        sharedPreferences = getSharedPreferences("AdminSession", MODE_PRIVATE);
        String adminId = sharedPreferences.getString("admin_id", null);
        if (adminId == null) {
            Toast.makeText(this, "관리자 로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    // 최초 관리자 여부 확인 메서드 - 이석재 작성
    private boolean isFirstAdmin() {
        sharedPreferences = getSharedPreferences("AdminSession", MODE_PRIVATE);
        String adminId = sharedPreferences.getString("admin_id", null);
        if (!adminId.equals("jun")) {
            Toast.makeText(this, "최초 관리자만 접근 가능합니다.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    // 관리자 로그아웃 메서드 - 이석재 작성.
    private void performLogout() {
        sharedPreferences = getSharedPreferences("AdminSession", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("admin_id");
        editor.apply();
        adminLoginTextView.setText("로그인");
        showAdminButtons(false);
        Toast.makeText(this, "관리자 로그아웃을 완료했습니다.", Toast.LENGTH_SHORT).show();
    }

    // 관리자 로그인 페이지로 이동 메서드 - 임성준 작성.
    private void navigateToAdminLogin() {
        Intent intent = new Intent(getApplicationContext(), AdminLoginActivity.class);
        intent.putExtra("background_color", backgroundColor);
        intent.putExtra("mode", mode);
        startActivity(intent);
    }

    // 뷰의 형태 변화를 지연시키는 메서드 - 임성준 작성.
    private void postDelayedShapeChange(final View view) {
        if (view instanceof NeumorphImageView) {
            ((NeumorphImageView) view).setShapeType(1);
            view.postDelayed(new Runnable() {
                @Override
                public void run() {
                    ((NeumorphImageView) view).setShapeType(0);
                }
            }, 200);
        } else if (view instanceof NeumorphCardView) {
            ((NeumorphCardView) view).setShapeType(1);
            view.postDelayed(new Runnable() {
                @Override
                public void run() {
                    ((NeumorphCardView) view).setShapeType(0);
                }
            }, 200);
        } else if (view instanceof NeumorphButton) {
            ((NeumorphButton) view).setShapeType(1);
            view.postDelayed(new Runnable() {
                @Override
                public void run() {
                    ((NeumorphButton) view).setShapeType(0);
                }
            }, 200);
        }
    }

    // 종료 다이얼로그 표시 메서드 - 임성준 작성.
    private void showExitDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("정말 종료 하시겠습니까?")
                .setPositiveButton("종료", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        performLogout();
                        finish();
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }

    // 자동 새로고침 시작 메서드 - 임성준 오자현
    private void startAutoRefresh() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadItemsFromFireStore();
                handler.postDelayed(this, 5000);
            }
        }, 5000);
    }

    // Firestore에서 아이템 로드 메서드 - 오자현 작성
    private void loadItemsFromFireStore() {
        Query query = currentSearchText.isEmpty()
                ? productDBFireStore.collection("products")
                : productDBFireStore.collection("products").whereEqualTo("productName", currentSearchText);

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    productDataList.clear();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        int code = document.getLong("productCode").intValue();
                        String productName = document.getString("productName");
                        String category = document.getString("productCategory");
                        String imageUrl = document.getString("productImage");
                        int price = document.getLong("productPrice").intValue();
                        int stock = document.getLong("productStock").intValue();
                        imageUrl = (imageUrl == null || imageUrl.isEmpty()) ? "R.drawable.icon" : imageUrl;
                        productDataList.add(new ProductData(code, productName, category, imageUrl, price, stock));
                    }
                    productAdapter.notifyDataSetChanged();
                } else {
                    Log.e("DatabaseViewActivity", "Error getting documents: ", task.getException());
                }
            }
        });
    }

    // 파일 업로드 및 제품 정보 저장 메서드 - 오자현 작성.
    private void uploadFileAndSaveProductInfo() {
        String name = editTextFieldProductName.getText().toString().trim();
        String priceStr = editTextFieldProductPrice.getText().toString().trim();
        String stockStr = editTextFieldProductStock.getText().toString().trim();
        String category = productCategory;

        if (name.isEmpty() || priceStr.isEmpty() || stockStr.isEmpty() || productFileUri == null || category.isEmpty()) {
            Toast.makeText(this, "모든 필드를 채워주세요", Toast.LENGTH_SHORT).show();
            return;
        }

        int price = Integer.parseInt(priceStr);
        int stock = Integer.parseInt(stockStr);
        StorageReference fileRef = FirebaseStorage.getInstance().getReference().child("files/" + System.currentTimeMillis());

        fileRef.putFile(productFileUri).addOnSuccessListener(taskSnapshot -> taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(uri -> {
            String fileUrl = uri.toString();
            saveProductInfoToFirestore(name, fileUrl, price, stock, category);
        })).addOnFailureListener(e -> {
            Toast.makeText(this, "파일 업로드 실패: " + e.getMessage(), Toast.LENGTH_LONG).show();
        });
    }

    // 제품 정보를 Firestore에 저장 메서드 - 오자현 작성.
    private void saveProductInfoToFirestore(String name, String fileUrl, int price, int stock, String category) {
        DocumentReference counterRef = productDBFireStore.collection("counters").document("productCounter");
        productDBFireStore.runTransaction(transaction -> {
            DocumentSnapshot counterSnapshot = transaction.get(counterRef);
            Long lastProductCode = counterSnapshot.getLong("lastProductCode");
            Long newProductCode = (lastProductCode == null ? 0 : lastProductCode) + 1;
            transaction.update(counterRef, "lastProductCode", newProductCode);

            Map<String, Object> product = new HashMap<>();
            product.put("productName", name);
            product.put("productImage", fileUrl);
            product.put("productPrice", price);
            product.put("productStock", stock);
            product.put("productCategory", category);
            product.put("productCode", newProductCode);

            productDBFireStore.collection("products").add(product).addOnSuccessListener(documentReference -> {
                Toast.makeText(this, "상품 정보와 파일 URL 파이어베이스에 저장 성공", Toast.LENGTH_SHORT).show();
                finishActivityWithResult();
            }).addOnFailureListener(e -> {
                Toast.makeText(this, "파이어베이스 저장 실패: " + e.getMessage(), Toast.LENGTH_LONG).show();
            });
            return null;
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "상품 코드 업데이트 실패: " + e.getMessage(), Toast.LENGTH_LONG).show();
        });
    }

    // 액티비티 종료 후 결과 반환 메서드 - 오자현 작성.
    private void finishActivityWithResult() {
        vFlipper.setDisplayedChild(MAIN_CHILD_INDEX);
        imageViewProduct.setImageResource(android.R.drawable.ic_menu_camera);
        editTextFieldProductName.setText("");
        editTextFieldProductPrice.setText("");
        editTextFieldProductStock.setText("");
        radioGroup.check(R.id.categoryRadioBtn1);
    }

    // 파일 매니저 열기 메서드 - 오자현
    private void openFileManager() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        startActivityForResult(intent, PICK_FILE_REQUEST);
    }

    // 임성준 오자현
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILE_REQUEST && resultCode == RESULT_OK && data != null) {
            productFileUri = data.getData();
            imageViewProduct.setImageURI(productFileUri);
        }
    }

    // Firestore에서 회원 데이터 로드 메서드 - 이석재 작성.
    private void loadMembersFromFireStore() {
        String searchId = editTextFieldSearchMemberName.getText().toString().trim();
        Query query = searchId.isEmpty()
                ? memberDBFireStore.collection("users")
                : memberDBFireStore.collection("users").orderBy("userName").startAt(searchId).endAt(searchId + '\uf8ff');

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                ArrayList<MemberData> memberList = new ArrayList<>();
                if (task.isSuccessful()) {
                    int i = 1;
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String id = document.getString("userEmail");
                        String name = document.getString("userName");
                        Date joindate = document.getTimestamp("userJoindate").toDate();
                        String uid = document.getString("uid");
                        int point = document.getLong("userPoint").intValue();
                        boolean isValid = document.getBoolean("isValid");
                        if (isValid) {
                            memberList.add(new MemberData(i++, id, name, joindate, uid, point));
                        }
                    }
                    memberAdapter.updateData(memberList);
                    showToastMessage(memberList.isEmpty() ? "검색 결과가 없습니다." : "검색 완료");
                } else {
                    Log.d("Firestore Search", "Error getting documents: ", task.getException());
                    showToastMessage("검색 중 오류 발생");
                }
            }
        });
    }

    // Firestore에서 결제 데이터 로드 메서드 - 이석재 작성.
    private void loadPaymentsFromFireStore() {
        String searchId = editTextFieldPaymentSearchMemberName.getText().toString().trim();
        Query query = searchId.isEmpty()
                ? paymentDBFireStore.collection("payments")
                : paymentDBFireStore.collection("payments").orderBy("userName").startAt(searchId).endAt(searchId + '\uf8ff');

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                ArrayList<PaymentData> paymentList = new ArrayList<>();
                if (task.isSuccessful()) {
                    int i = 1;
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
                        if (isValid) {
                            paymentList.add(new PaymentData(i++, receiptId, name, uid, email, products, totalPrice, usePoint, payDay));
                        }
                    }
                    paymentAdapter.updateData(paymentList);
                    showToastMessage(paymentList.isEmpty() ? "검색 결과가 없습니다." : "검색 완료");
                } else {
                    Log.d("Firestore Search", "Error getting documents: ", task.getException());
                    showToastMessage("검색 중 오류 발생");
                }
            }
        });
    }

    // 토스트 메시지 표시 메서드 - 이석재 작성.
    private void showToastMessage(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    // 다크 모드 카드뷰 그림자 적용 메서드 - 임성준 작성.
    private void applyCardViewDarkShadow(View... views) {
        for (View view : views) {
            if (view instanceof NeumorphCardView) {
                ChangeMode.setDarkShadowCardView((NeumorphCardView) view);
            } else if (view instanceof NeumorphButton) {
                ChangeMode.setDarkShadowCardView((NeumorphButton) view);
            }
        }
    }

    // 라이트 모드 카드뷰 그림자 적용 메서드 - 임성준 작성.
    private void applyCardViewLightShadow(View... views) {
        for (View view : views) {
            if (view instanceof NeumorphCardView) {
                ChangeMode.setLightShadowCardView((NeumorphCardView) view);
            } else if (view instanceof NeumorphButton) {
                ChangeMode.setLightShadowCardView((NeumorphButton) view);
            }
        }
    }

    // 다크 모드 색상 필터 적용 메서드 - 임성준 작성.
    private void applyColorFilterDark(View... views) {
        for (View view : views) {
            ChangeMode.setColorFilterDark(view);
        }
    }

    // 라이트 모드 색상 필터 적용 메서드 - 임성준 작성.
    private void applyColorFilterLight(View... views) {
        for (View view : views) {
            ChangeMode.setColorFilterLight(view);
        }
    }

    // 다크 모드 푸터 메뉴 필터 적용 메서드 - 임성준 작성.
    private void applyFooterMenuDarkFilter(NeumorphImageView... buttons) {
        for (NeumorphImageView button : buttons) {
            ChangeMode.setColorFilterDark(button);
            ChangeMode.setDarkShadowCardView(button);
        }
    }

    // 라이트 모드 푸터 메뉴 필터 적용 메서드 - 임성준 작성.
    private void applyFooterMenuLightFilter(NeumorphImageView... buttons) {
        for (NeumorphImageView button : buttons) {
            ChangeMode.setLightShadowCardView(button);
            ChangeMode.setColorFilterLight(button);
        }
    }

    // 회원 정보 다이얼로그 표시 메서드 - 임성준 작성.
    private void showMemberInfoDialog(MemberData selectedItem) {
        Dialog dialog = createInfoDialog(R.layout.admin_dialog_member_item_info);
        setTextInDialog(dialog, R.id.textViewMemberNum, selectedItem.getNumber() + "");
        setTextInDialog(dialog, R.id.textViewMemberId, selectedItem.getMemberId());
        setTextInDialog(dialog, R.id.textViewMemberDate, formatDate(selectedItem.getJoinDate()));
        setTextInDialog(dialog, R.id.textViewMemberPoint, selectedItem.getPoint() + "점");
        dialog.show();
    }

    // 제품 정보 다이얼로그 표시 메서드 - 임성준 작성.
    private void showProductInfoDialog(ProductData selectedItem) {
        Dialog dialog = createInfoDialog(R.layout.admin_dialog_product_item_info);
        ImageView imageViewProductImage = dialog.findViewById(R.id.imageViewProductImage);
        Glide.with(this).load(selectedItem.getProductImage()).placeholder(R.drawable.icon).into(imageViewProductImage);
        setTextInDialog(dialog, R.id.textViewProductCode, selectedItem.getProductCode()+"");
        setTextInDialog(dialog, R.id.textViewProductName, selectedItem.getProductName());
        setTextInDialog(dialog, R.id.textViewProductCategory, selectedItem.getProductCategory());
        setTextInDialog(dialog, R.id.textViewProductStock, selectedItem.getProductStock()+"개");
        setTextInDialog(dialog, R.id.textViewProductPrice, selectedItem.getProductPrice() + "원");
        dialog.show();
    }

    // 결제 정보 다이얼로그 표시 메서드 - 임성준 작성.
    private void showPaymentInfoDialog(PaymentData selectedItem) {
        Dialog dialog = createInfoDialog(R.layout.admin_dialog_payment_item_info);
        setTextInDialog(dialog, R.id.textViewPaymentNum, selectedItem.getNumber()+"");
        setTextInDialog(dialog, R.id.textViewReceiptId, selectedItem.getReceiptId());
        setTextInDialog(dialog, R.id.textViewUserName, selectedItem.getUserName());
        setTextInDialog(dialog, R.id.textViewUid, selectedItem.getUid());
        setTextInDialog(dialog, R.id.textViewUserEmail, selectedItem.getEmail());
        setTextInDialog(dialog, R.id.textViewProducts, selectedItem.getProducts()+"");
        setTextInDialog(dialog, R.id.textViewTotalPrice, selectedItem.getTotalPrice()+"");
        setTextInDialog(dialog, R.id.textViewUsePoint, selectedItem.getUsePoint()+"");
        setTextInDialog(dialog, R.id.textViewPayDay, formatDate(selectedItem.getPayDay()));
        dialog.show();
    }

    // 정보 다이얼로그 생성 메서드 - 임성준 작성.
    private Dialog createInfoDialog(int layoutId) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(layoutId);
        return dialog;
    }

    // 다이얼로그 내 텍스트 설정 메서드 - 임성준 작성.
    private void setTextInDialog(Dialog dialog, int textViewId, String text) {
        TextView textView = dialog.findViewById(textViewId);
        textView.setText(text);
    }

    // 날짜 형식 변환 메서드 - 임성준 작성.
    private String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(date);
    }

    @Override
    public void onBackPressed() {
        showExitDialog();
    } //  - 임성준

    @Override
    protected void onResume() {
        super.onResume();
        updateLoginStatus();
    }

    // 오류 다이얼로그 표시 메서드 - 임성준 작성.
    public static void showErrorDialog(Context context, String message) {
        // 다이얼로그 생성
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("오류 발생")
                .setMessage(message)
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // "확인" 버튼 클릭 시
                        dialog.dismiss(); // 다이얼로그 닫기
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // 회원 데이터를 가져오는 메서드  - 이석재 작성.
    private void fetchMemberData() {
        memberDBFireStore = FirebaseFirestore.getInstance();
        ArrayList<MemberData> memberDataList = new ArrayList<>();

        memberDBFireStore.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                int i = 1;  // num을 위한 카운터 시작 값
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String id = document.getString("userEmail");
                        String name = document.getString("userName");
                        Date joindate = document.getTimestamp("userJoindate").toDate();
                        String uid = document.getString("uid");
                        int point = document.getLong("userPoint").intValue();
                        boolean isValid = document.getBoolean("isValid");

                        if (isValid) {
                            memberDataList.add(new MemberData(i, id, name, joindate, uid, point));
                            i++;
                        }
                    }
                    if (memberDataList.isEmpty()) {
                        Toast.makeText(getApplicationContext(), "결과가 없습니다.", Toast.LENGTH_SHORT).show();
                        memberAdapter.updateData(memberDataList);
                    } else {
                        memberAdapter.updateData(memberDataList);
                    }
                }
            }
        });
    }

    // 결제 데이터를 가져오는 메서드  - 이석재 작성.
    private void fetchPaymentData() {
        paymentDBFireStore = FirebaseFirestore.getInstance();
        ArrayList<PaymentData> paymentDataList = new ArrayList<>();

        paymentDBFireStore.collection("payments").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                int i = 1;  // num을 위한 카운터 시작 값
                if (task.isSuccessful()) {
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

                        if (isValid) {
                            paymentDataList.add(new PaymentData(i, receiptId, name, uid, email, products, totalPrice, usePoint, payDay));
                            i++;
                        }
                    }
                    if (paymentDataList.isEmpty()) {
                        Toast.makeText(getApplicationContext(), "결과가 없습니다.", Toast.LENGTH_SHORT).show();
                        paymentAdapter.updateData(paymentDataList);
                    } else {
                        paymentAdapter.updateData(paymentDataList);
                    }
                }
            }
        });
    }
}