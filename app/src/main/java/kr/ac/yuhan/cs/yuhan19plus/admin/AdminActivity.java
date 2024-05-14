package kr.ac.yuhan.cs.yuhan19plus.admin;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import kr.ac.yuhan.cs.yuhan19plus.R;
import kr.ac.yuhan.cs.yuhan19plus.admin.adapter.AdminAdapter;
import kr.ac.yuhan.cs.yuhan19plus.admin.data.AdminData;
import kr.ac.yuhan.cs.yuhan19plus.admin.util.ChangeMode;
import soup.neumorphism.NeumorphButton;
import soup.neumorphism.NeumorphCardView;
import soup.neumorphism.NeumorphImageView;

public class AdminActivity extends AppCompatActivity {
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

        // Create Fake Date
        ArrayList<AdminData> fakeDataList = createFakeData();
        // Setting Adapter
        AdminAdapter adapter = new AdminAdapter(this, fakeDataList);

        // Receives current mode value
        int modeValue = getIntent().getIntExtra("mode", 1);

        // Receives background color value passed from MainActivity
        int backgroundColor = getIntent().getIntExtra("background_color", Color.rgb(236, 240, 243));

        // Setting BackgroundColor
        View backgroundView = getWindow().getDecorView().getRootView();
        backgroundView.setBackgroundColor(backgroundColor);

        adminListPage = findViewById(R.id.adminListPage);
        listView = findViewById(R.id.listView);

        listView.setAdapter(adapter);

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
                AdminData selectedItem = fakeDataList.get(position);
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
    }
    // Create Fake Data
    ArrayList<AdminData> createFakeData() {
        ArrayList<AdminData> dataList = new ArrayList<>();
        for (int i = 1; i <= 20; i++) {
            // Create Fake Data & Add AdminList
            AdminData adminData = new AdminData(i, "Admin" + i, "1234", "position" + i);
            dataList.add(adminData);
        }
        return dataList;
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
        textViewAdminNum.setText("Num : " + selectedItem.getAdminNum());

        TextView textViewAdminId = dialog.findViewById(R.id.textViewAdminId);
        textViewAdminId.setText("관리자 ID : " + selectedItem.getAdminId());

        TextView textViewAdminPosition = dialog.findViewById(R.id.textViewAdminPosition);
        textViewAdminPosition.setText("직책 : " + selectedItem.getAdminPosition());

        // Show Dialog
        dialog.show();
    }
}