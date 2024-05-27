package kr.ac.yuhan.cs.yuhan19plus.admin;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import kr.ac.yuhan.cs.yuhan19plus.R;
import kr.ac.yuhan.cs.yuhan19plus.admin.adapter.AdminAdapter;
import kr.ac.yuhan.cs.yuhan19plus.admin.data.AdminData;
import kr.ac.yuhan.cs.yuhan19plus.admin.util.ChangeMode;
import soup.neumorphism.NeumorphButton;
import soup.neumorphism.NeumorphCardView;
import soup.neumorphism.NeumorphImageView;

/** 담당자 임성준, 이석재
 * 초기 작성 및 관리자 전체 UI담당 및 다크모드 기능, 모달창 구현 임성준.
 * 관리자 관리 기능 구현 이석재
 * */
public class AdminActivity extends AppCompatActivity {
    // Admin Firebase
    private FirebaseFirestore adminDBFireStore;
    // Session Object
    private SharedPreferences sharedPreferences;
    // Adapter
    private AdminAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_activity_admin_page);

        NeumorphCardView adminListCardView;
        NeumorphCardView editTextSearchAdminField;
        NeumorphButton adminSearchBtn;
        NeumorphImageView backBtn;
        LinearLayout adminListPage;
        ListView listView;

        // Receives current mode value
        int modeValue = getIntent().getIntExtra("mode", 1);

        // Receives background color value passed from MainActivity
        int backgroundColor = getIntent().getIntExtra("background_color", Color.rgb(236, 240, 243));

        // Setting BackgroundColor
        View backgroundView = getWindow().getDecorView().getRootView();
        backgroundView.setBackgroundColor(backgroundColor);

        adminListPage = findViewById(R.id.adminListPage);
        listView = findViewById(R.id.listView);

        // Setting Adapter
        adapter = new AdminAdapter(this, new ArrayList<AdminData>());
        listView.setAdapter(adapter);

        // fetchAdminData
        fetchAdminData();

        // Admin Page Id
        backBtn = findViewById(R.id.backBtn);
        adminListCardView = findViewById(R.id.adminListCardView);
        editTextSearchAdminField = findViewById(R.id.editTextSearchAdminField);
        adminSearchBtn = findViewById(R.id.adminSearchBtn);

        // listView onItemClickListener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get information about the clicked item
                AdminData selectedItem= (AdminData) adapter.getItem(position);
                showAdminInfoDialog(selectedItem);
            }
        });

        if(modeValue == 1) {
            // DarkMode
            ChangeMode.applySubTheme(adminListPage, modeValue);

            // Admin Page Btn
            ChangeMode.setColorFilterDark(backBtn);
            ChangeMode.setDarkShadowCardView(backBtn);

            // Admin Page CardView content
            ChangeMode.setDarkShadowCardView(adminListCardView);
            ChangeMode.setDarkShadowCardView(editTextSearchAdminField);
            ChangeMode.setDarkShadowCardView(adminSearchBtn);
        }
        else {
            // LightMode
            adminSearchBtn.setBackgroundColor(Color.rgb(0, 174, 142));
            ChangeMode.setLightShadowCardView(adminSearchBtn);
        }

        // BackBtn onClickListener
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Change ShapeType to 'pressed' when clicked
                backBtn.setShapeType(1);
                // After clicked, it changes back to 'flat'
                v.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        backBtn.setShapeType(0);
                    }
                }, 200);
                finish();
            }
        });


        // AdminSearchBtn onClickListener
        adminSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadAdminFromFireStore();
            }
        });



    }
    // fetchAdminData
    private void fetchAdminData() {
        // 현재 로그인한 관리자 ID 가져오기
        sharedPreferences = getSharedPreferences("AdminSession", MODE_PRIVATE);
        String currentAdminId = sharedPreferences.getString("admin_id", null);

        // 인스턴스 가져오기, 관리자 목록 배열 생성
        adminDBFireStore = FirebaseFirestore.getInstance();
        ArrayList<AdminData> adminList = new ArrayList<>();
        //관리자 목록을 DB에서 가져오기
        adminDBFireStore.collection("admins").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                // DB에서 데이터를 성공적으로 가져온 경우
                if (task.isSuccessful()) {
                    int i = 1; // num을 위한 카운터 시작 값
                    // 가져온 데이터의 수만큼 반복
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        // 현재 로그인한 관리자가 아닌경우
                        if (!document.getId().equals(currentAdminId)) {
                            String adminId = document.getString("adminId");
                            String password = document.getString("adminPassword");
                            String position = document.getString("adminPosition");
                            // 관리자 목록 배열에 추가
                            adminList.add(new AdminData(i, adminId, password, position));
                            i++;  // 다음 num 값 증가
                        }
                    }
                    // 결과가 없는 경우
                    if (adminList.isEmpty()) {
                        Toast.makeText(getApplicationContext(), "결과가 없습니다.", Toast.LENGTH_SHORT).show();
                    }
                    // 결과가 있는 경우 데이터 업데이트
                    else {
                        adapter.updateData(adminList);
                    }

                }
                // DB에서 데이터를 가져오는 중 오류가 발생한경우
                else {
                    Log.w("AdminActivity", "Error getting documents: ", task.getException());
                }
            }
        });

    }
    public static void showErrorDialog(Context context, String message) {
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
    void showAdminInfoDialog(AdminData selectedItem) {
        // Create Dialog & Layout Setting
        Dialog dialog = new Dialog(AdminActivity.this);
        dialog.setContentView(R.layout.admin_dialog_admin_item_info);

        // Get TextView ID in Dialog
        TextView textViewAdminNum = dialog.findViewById(R.id.textViewAdminNum);
        textViewAdminNum.setText(selectedItem.getAdminNum() + "");

        TextView textViewAdminId = dialog.findViewById(R.id.textViewAdminId);
        textViewAdminId.setText(selectedItem.getAdminId());

        TextView textViewAdminPosition = dialog.findViewById(R.id.textViewAdminPosition);
        textViewAdminPosition.setText(selectedItem.getAdminPosition());

        // Show Dialog
        dialog.show();
    }

    // 파이어베이스에서 관리자 아이디로 검색하고 관리자 데이터를 읽어오는 메서드 (이석재)
    private void loadAdminFromFireStore() {
        // 현재 로그인한 관리자 ID 가져오기
        sharedPreferences = getSharedPreferences("AdminSession", MODE_PRIVATE);
        String currentAdminId = sharedPreferences.getString("admin_id", null);

        // 인스턴스 가져오기, 관리자 목록 배열 생성
        adminDBFireStore = FirebaseFirestore.getInstance();
        ArrayList<AdminData> adminList = new ArrayList<>();

        // 검색창에 입력한 값을 가져오기
        EditText input_searchId = findViewById(R.id.input_searchId);
        String searchId = input_searchId.getText().toString().trim();

        // 쿼리문 작성
        Query query;
        if (searchId.isEmpty()) {
            // 검색어가 없을 경우 전체 문서를 조회
            query = adminDBFireStore.collection("admins");
        } else {
            // 입력된 검색어로 시작하는 adminId를 가진 문서를 조회
            query = adminDBFireStore.collection("admins").orderBy("adminId").startAt(searchId).endAt(searchId + '\uf8ff');
        }

        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            // DB에서 검색이 완료된 경우
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    int i = 1; // num을 위한 카운터 시작 값
                    // 검색한 관리자 수 만큼 반복
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        // 현재 로그인한 관리자가 아닌 경우 리스트에 추가
                        if (!document.getId().equals(currentAdminId)) {
                            String adminId = document.getId();
                            String password = document.getString("adminPassword");
                            String position = document.getString("adminPosition");
                            adminList.add(new AdminData(i, adminId, password, position));
                            i++;  // 다음 num 값 증가
                        }
                    }
                    // 검색 결과가 없는 경우
                    if (adminList.isEmpty()) {
                        adapter.updateData(adminList);
                        Toast.makeText(getApplicationContext(), "검색 결과가 없습니다.", Toast.LENGTH_SHORT).show();
                    }
                    // 검색결과가 있는 경우
                    else {
                        adapter.updateData(adminList);
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

}