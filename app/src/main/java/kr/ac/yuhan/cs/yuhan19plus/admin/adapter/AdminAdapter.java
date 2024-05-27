package kr.ac.yuhan.cs.yuhan19plus.admin.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

import java.util.ArrayList;

import kr.ac.yuhan.cs.yuhan19plus.R;
import kr.ac.yuhan.cs.yuhan19plus.admin.data.AdminData;

/** 담당자 임성준, 이석재
 * 초기 작성 임성준.
 * 이석재 관리자 삭제 기능 추가
 * */
public class AdminAdapter extends BaseAdapter {
    private ArrayList<AdminData> adminList;
    private LayoutInflater inflater;
    private Context context;
    // Admin Firebase
    FirebaseFirestore adminDBFireStore;

    // AdminAdapter Constructor
    public AdminAdapter(Context context, ArrayList<AdminData> adminList) {
        this.context = context;
        this.adminList = adminList;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return adminList.size();
    }

    @Override
    public Object getItem(int position) {
        return adminList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            // ViewHolder Init
            convertView = inflater.inflate(R.layout.admin_admin_list_item, parent, false);
            viewHolder = new ViewHolder();
            // Id of admin_list.xml
            viewHolder.numberTextView = convertView.findViewById(R.id.number);
            viewHolder.adminIdTextView = convertView.findViewById(R.id.adminId);
            viewHolder.userPostitionTextView = convertView.findViewById(R.id.adminPosition);
            viewHolder.outBtn = convertView.findViewById(R.id.outBtn);
            convertView.setTag(viewHolder);
        } else {
            // ViewHolder Recycle
            viewHolder = (ViewHolder) convertView.getTag();
        }

        AdminData adminData = adminList.get(position);
        viewHolder.numberTextView.setText(String.valueOf(adminData.getAdminNum()));
        viewHolder.adminIdTextView.setText(adminData.getAdminId());
        viewHolder.userPostitionTextView.setText(String.valueOf(adminData.getAdminPosition()));

        // outBtn onClickListener
        viewHolder.outBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create AlertDialog
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("해당 관리자를 삭제 합니다.");
                builder.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // selected "삭제"
                        // 관리자 아이디 가져오기, 인스턴스 가져오기
                        String adminId = adminData.getAdminId();
                        adminDBFireStore = FirebaseFirestore.getInstance();

                        // admins 컬렉션에서 관리자 데이터 삭제
                        adminDBFireStore.collection("admins").document(adminId).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            // 리스트뷰에서 해당 데이터 삭제 및 토스트 메시지 출력
                            public void onSuccess(Void unused) {
                                removeAdmin(position); // Delete data
                                Toast.makeText(context, "관리자 데이터가 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            // 오류 발생 시 토스트 메시지 출력
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(context, "데이터 삭제 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                builder.setNegativeButton("취소", null);
                builder.show(); // Show AlertDialog
            }
        });
        return convertView;
    }

    // Delete AdminData => Need to Update AdminData
    public void removeAdmin(int position) {
        // 리스트에서 해당하는 관리자 데이터 삭제
        adminList.remove(position);
        // 삭제된 아이템 이후의 모든 아이템들의 번호를 갱신
        for (int i = position; i < adminList.size(); i++) {
            AdminData item = adminList.get(i);
            item.setAdminNum(i + 1);  // 번호를 현재 인덱스에 맞게 재설정
        }
        notifyDataSetChanged(); // 변경된 데이터셋을 알려 ListView를 갱신
    }


    public void updateData(ArrayList<AdminData> data) {
        this.adminList = data;
        notifyDataSetChanged(); // 변경된 데이터셋을 알려 ListView를 갱신
    }

    static class ViewHolder {
        TextView numberTextView;
        TextView adminIdTextView;
        TextView userPostitionTextView;
        ImageView outBtn;
    }
}
