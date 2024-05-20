package kr.ac.yuhan.cs.yuhan19plus.main;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import kr.ac.yuhan.cs.yuhan19plus.R;

public class MainStoreLocationActivity extends AppCompatActivity {
    WebView web;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_storelocation);
        
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getRealMetrics(displayMetrics);
        
        // Layout 설정
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setBackgroundColor(Color.rgb(238, 255, 65));
        
        // TextView 생성 및 설정
        TextView textView = new TextView(this);
        textView.setText(R.string.webView_title);
        textView.setTextColor(Color.BLACK);
        textView.setTypeface(null, Typeface.BOLD);       // 텍스트 스타일 설정
        textView.setTextSize(52);                           // 텍스트 사이즈 설정
        textView.setGravity(Gravity.CENTER_VERTICAL);       // 텍스트 정렬 설정
        textView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics()))); // 레이아웃 파라미터 설정
        layout.addView(textView);                           // LinearLayout에 TextView 추가

        web = new WebView(this);
        WebSettings webSet = web.getSettings();
        webSet.setJavaScriptEnabled(true); // JS 사용 허용
        // 웹페이지 로드 - 카카오맵 API URL로 변경
        web.loadUrl("https://developers.kakao.com/console/proj/maps/app");
        webSet.setUseWideViewPort(true);
        webSet.setBuiltInZoomControls(false);
        webSet.setAllowUniversalAccessFromFileURLs(true);
        webSet.setJavaScriptCanOpenWindowsAutomatically(true);
        webSet.setSupportMultipleWindows(true);
        webSet.setSaveFormData(false);
        webSet.setSavePassword(false);
        webSet.setLoadWithOverviewMode(true);
        webSet.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
        web.setWebChromeClient(new WebChromeClient());
        web.setWebViewClient(new WebViewClient());
        web.loadUrl("file:///android_asset/index.html");
        layout.addView(web);
        setContentView(layout);
    }

    // 웹뷰에서 뒤로가기 버튼을 눌렀을 때의 동작
    @Override
    public void onBackPressed() {
        if(web.canGoBack()) {
            web.goBack();
        } else {
            super.onBackPressed();
        }
    }
}