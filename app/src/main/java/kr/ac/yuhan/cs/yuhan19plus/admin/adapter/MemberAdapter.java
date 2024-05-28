package kr.ac.yuhan.cs.yuhan19plus.admin.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import kr.ac.yuhan.cs.yuhan19plus.R;
import kr.ac.yuhan.cs.yuhan19plus.admin.data.MemberData;
/** 담당자 : 임성준, 이석재
 * 초기 작성 : 임성준
 * 수정 : 이석재
 *  */
public class MemberAdapter extends BaseAdapter {
    private ArrayList<MemberData> memberList;
    private LayoutInflater inflater;
    private Context context;

    // MemberAdapter Constructor
    public MemberAdapter(Context context, ArrayList<MemberData> memberList) {
        this.context = context;
        this.memberList = memberList;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return memberList.size();
    }

    @Override
    public Object getItem(int position) {
        return memberList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            // ViewHolder 초기화
            convertView = inflater.inflate(R.layout.admin_member_list_item, parent, false);
            viewHolder = new ViewHolder();

            viewHolder.numberTextView = convertView.findViewById(R.id.number);
            viewHolder.memberNameTextView = convertView.findViewById(R.id.memberName);
            viewHolder.memberPointTextView = convertView.findViewById(R.id.memberPoint);
            viewHolder.outBtn = convertView.findViewById(R.id.outBtn);
            convertView.setTag(viewHolder);
        } else {
            // ViewHolder 재사용
            viewHolder = (ViewHolder) convertView.getTag();
        }

        MemberData memberData = memberList.get(position);

        viewHolder.numberTextView.setText(String.valueOf(memberData.getNumber()));
        viewHolder.memberNameTextView.setText(memberData.getMemberName());
        viewHolder.memberPointTextView.setText(String.valueOf(memberData.getPoint()));

        // outBtn onClickListener
        viewHolder.outBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 회원 데이터 삭제 전 묻는 알림창
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("해당 회원을 삭제 합니다.");
                builder.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // selected "삭제"
                        String uid = memberData.getUid();// 사용자의 UID
                        FirebaseFirestore memberDBFireStore = FirebaseFirestore.getInstance();

                        // Firestore에서 사용자 데이터 비활성화
                        memberDBFireStore.collection("users").document(uid)
                                .update("isValid", false)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // 성공적으로 Firestore에서 사용자 데이터가 비활성화된 경우,
                                        // Firebase Cloud Functions를 호출하여 Firebase Authentication에서도 사용자를 삭제
                                        deleteFirebaseUser(uid);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(context, "데이터 삭제 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                        removeMember(position); // Delete data
                    }
                });
                builder.setNegativeButton("취소", null);
                builder.show(); // Show AlertDialog
            }
        });

        return convertView;
    }

    // 회원 탈퇴 처리 함수
    private void deleteFirebaseUser(String uid) {
        // Rest API 호출을 위한 데이터 설정
        Map<String, Object> data = new HashMap<>();
        data.put("uid", uid);
        // Firebase Cloud Functions를 호출하여 Firebase Authentication에서 회원을 탈퇴
        FirebaseFunctions.getInstance()
                .getHttpsCallable("deleteOrDeactivateUser")
                .call(data)
                .addOnSuccessListener(new OnSuccessListener<HttpsCallableResult>() {
                    @Override
                    // Firebase Cloud Functions 호출이 성공한 경우
                    public void onSuccess(HttpsCallableResult httpsCallableResult) {
                        Map<String, Object> result = (Map<String, Object>) httpsCallableResult.getData();
                        // 회원 탈퇴가 성공한 경우
                        if (result.containsKey("success")) {
                            Toast.makeText(context, "회원 삭제가 완료되었습니다.", Toast.LENGTH_SHORT).show();
                        }
                        // 회원 탈퇴 중에 오류가 발생한 경우
                        else if (result.containsKey("error")) {
                            String errorMessage = (String) result.get("error");
                            Toast.makeText(context, "회원 삭제 실패: " + errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    // Firebase Cloud Functions 호출이 실패한 경우
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "사용자 삭제 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // 회원 데이터 삭제
    public void removeMember(int position) {
        // 리스트에서 해당하는 회원 데이터 삭제
        memberList.remove(position);
        // 삭제된 아이템 이후의 모든 아이템들의 번호를 갱신
        for (int i = position; i < memberList.size(); i++) {
            MemberData item = memberList.get(i);
            item.setNumber(i + 1);  // 번호를 현재 인덱스에 맞게 재설정
        }
        notifyDataSetChanged(); // 변경된 데이터셋을 알려 ListView를 갱신
    }

    public void updateData(ArrayList<MemberData> data) {
        this.memberList = data;
        notifyDataSetChanged(); // 변경된 데이터셋을 알려 ListView를 갱신
    }

    static class ViewHolder {
        // ViewHolder 변수 선언
        TextView numberTextView;
        TextView memberNameTextView;
        TextView memberPointTextView;
        ImageView outBtn;
    }
}