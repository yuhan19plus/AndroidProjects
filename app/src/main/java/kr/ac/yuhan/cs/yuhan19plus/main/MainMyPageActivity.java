package kr.ac.yuhan.cs.yuhan19plus.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.util.Map;

import kr.ac.yuhan.cs.yuhan19plus.R;

public class MainMyPageActivity extends AppCompatActivity {
    private FirebaseAuth userDBFirebaseAuth;
    private FirebaseUser userDBFirebaseUser;
    private FirebaseFirestore userDBFireStore;
    private TextView id_label;
    private boolean isAnonymousUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity_mypage);

        ImageView closeButton = findViewById(R.id.Close_Mypage_Btn);

        id_label = findViewById(R.id.id_label);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Button updateButton = findViewById(R.id.Update_Info_Btn);
        updateButton .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainMyPageActivity.this, MainRetouchInfoActivity.class);
                startActivity(intent);
            }
        });
        //석재애몽 도와줘요->>>>>>>>>>> 회원 정보 보여주게 하기

        loadUserInfo();

    }

    @Override
    protected void onResume() {
        super.onResume();
        // Activity가 다시 활성화될 때 사용자 정보 다시 로드
        loadUserInfo();
    }

    private void loadUserInfo() {
        userDBFirebaseAuth = FirebaseAuth.getInstance();
        userDBFirebaseUser = userDBFirebaseAuth.getCurrentUser();
        String uid = userDBFirebaseUser.getUid();
        getUserInfoFromFirestore(uid);
    }

    private void getUserInfoFromFirestore(String uid) {
        userDBFireStore = FirebaseFirestore.getInstance();
        userDBFireStore.collection("users").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    // DocumentSnapshot에서 데이터를 가져와서 UI에 설정
                    Map<String, Object> userInfo = document.getData();
                    updateUserInfoUI(userInfo);
                }
                else {
                    Toast.makeText(MainMyPageActivity.this, "사용자 정보 로드에 실패했습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void updateUserInfoUI(Map<String, Object> userInfo) {
        FirebaseUser currentUser = userDBFirebaseAuth.getCurrentUser();
        TextView uid = findViewById(R.id.UserCode);
        TextView userPoint = findViewById(R.id.UserPoint);
        TextView userID = findViewById(R.id.UserID);
        TextView userName = findViewById(R.id.UserName);
        TextView userFirstNum = findViewById(R.id.UserFirstNum);
        TextView userSecondNum = findViewById(R.id.UserSecondNum);
        TextView userThirdNum = findViewById(R.id.UserThirdNum);
        TextView userYear = findViewById(R.id.UserYear);
        TextView userMonth = findViewById(R.id.UserMonth);
        TextView userDay = findViewById(R.id.UserDay);

        isAnonymousUser = userDBFirebaseUser.isAnonymous();

        if (isAnonymousUser) {
            id_label.setVisibility(View.GONE);
            userID.setVisibility(View.GONE);
        }

        // Firestore에서 가져온 데이터를 UI에 설정
        uid.setText(userInfo.get("uid").toString());
        userPoint.setText(userInfo.get("userPoint").toString());
        userID.setText(currentUser.getEmail());
        userName.setText(userInfo.get("userName").toString());
        String[] phone = userInfo.get("userPhone").toString().split("-");
        userFirstNum.setText(phone[0]);
        userSecondNum.setText(phone[1]);
        userThirdNum.setText(phone[2]);
        String[] birthday = userInfo.get("userBirthday").toString().split("\\.");
        userYear.setText(birthday[0]);
        userMonth.setText(birthday[1]);
        userDay.setText(birthday[2]);
    }



}