package kr.ac.yuhan.cs.yuhan19plus.admin.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import kr.ac.yuhan.cs.yuhan19plus.R;
import kr.ac.yuhan.cs.yuhan19plus.admin.data.TodoData;
import kr.ac.yuhan.cs.yuhan19plus.admin.db.DatabaseHelper;

/** 담당자 : 임성준, 이석재
 * 관리자 일정 기능 : 임성준
 * 관리자 세션 처리 : 이석재 */
public class TodoListAdapter extends ArrayAdapter<TodoData> {
    private final Context mContext;
    private final List<TodoData> todoList;

    // 관리자 TestID
    String adminId = "SeongJun1";

    // TodoListAdapter Constructor
    public TodoListAdapter(Context context, ArrayList<TodoData> list) {
        super(context, 0, list);
        mContext = context;
        todoList = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null) {
            listItem = LayoutInflater.from(mContext).inflate(R.layout.admin_todo_list_item, parent, false);
        }

        final TodoData currentItem = todoList.get(position);

        TextView tododateTextView = listItem.findViewById(R.id.tododate);
        TextView adminIdTextView = listItem.findViewById(R.id.adminId);
        TextView todoContentTextView = listItem.findViewById(R.id.todoContent);

        tododateTextView.setText(String.valueOf(currentItem.getSelectedDate()));
        adminIdTextView.setText(currentItem.getAdminId());
        todoContentTextView.setText(currentItem.getTodoContent());

        ImageView deleteButton = listItem.findViewById(R.id.outBtn);

        // deleteButton onClickListener
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // delete TodoData
                deleteItem(adminId, currentItem);
            }
        });

        return listItem;
    }

    // 할일 데이터 삭제
    private void deleteItem(String adminId, TodoData item) {
        // Create AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage("할일을 삭제 합니다.")
                .setCancelable(false)
                .setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Delete the corresponding Todo item from DB
                        DatabaseHelper dbHelper = new DatabaseHelper(mContext);
                        dbHelper.deleteTodo(item.getId(), adminId);
                        // Delete the item from the List
                        todoList.remove(item);
                        notifyDataSetChanged();
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
