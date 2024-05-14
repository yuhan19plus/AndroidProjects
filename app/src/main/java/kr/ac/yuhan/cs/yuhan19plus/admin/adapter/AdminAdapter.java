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

import java.util.ArrayList;

import kr.ac.yuhan.cs.yuhan19plus.R;
import kr.ac.yuhan.cs.yuhan19plus.admin.data.AdminData;

public class AdminAdapter extends BaseAdapter {
    private ArrayList<AdminData> adminList;
    private LayoutInflater inflater;
    private Context context;

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
                        removeAdmin(position); // Delete data
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
        adminList.remove(position);
        notifyDataSetChanged();
    }

    static class ViewHolder {
        TextView numberTextView;
        TextView adminIdTextView;
        TextView userPostitionTextView;
        ImageView outBtn;
    }
}
