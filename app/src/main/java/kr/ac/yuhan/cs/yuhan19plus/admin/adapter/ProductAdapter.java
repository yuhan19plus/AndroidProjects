package kr.ac.yuhan.cs.yuhan19plus.admin.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import kr.ac.yuhan.cs.yuhan19plus.R;
import kr.ac.yuhan.cs.yuhan19plus.admin.AdminProductEditActivity;
import kr.ac.yuhan.cs.yuhan19plus.admin.data.ProductData;


public class ProductAdapter extends ArrayAdapter<ProductData> {

    public ProductAdapter(Context context, ArrayList<ProductData> productData) {
        super(context, 0, productData);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.admin_product_list_item, parent, false);
            holder = new ViewHolder();
            holder.textViewProductName = convertView.findViewById(R.id.textViewProductName);
            holder.textViewCategory = convertView.findViewById(R.id.textViewCategory);
            holder.imageViewProduct = convertView.findViewById(R.id.imageViewProduct);
            holder.productUpdateBtn = convertView.findViewById(R.id.productUpdateBtn);
            holder.productDeleteBtn = convertView.findViewById(R.id.productDeleteBtn);
            convertView.setTag(holder);
            Log.d("ProductAdapter", "New convertView created for position: " + position);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ProductData productData = getItem(position);
        if (productData == null) {
            Log.e("ProductAdapter", "ProductData at position " + position + " is null.");
        } else {
            Log.d("ProductAdapter", "Displaying productData at position " + position + ": " + productData.getProductName());
            holder.textViewProductName.setText(productData.getProductName());
            holder.textViewCategory.setText(productData.getProductCategory());
            if (productData.getProductImage() != null && !productData.getProductImage().isEmpty()) {
                Glide.with(getContext())
                        .load(productData.getProductImage())
                        .placeholder(R.drawable.default_image)
                        .into(holder.imageViewProduct);
            } else {
                holder.imageViewProduct.setImageResource(R.drawable.default_image);
                Log.d("ProductAdapter", "No image URL provided for productData at position " + position);
            }
        }

        // Add click listeners for your buttons
        holder.productUpdateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 현재 position에서 ProductData 객체 가져오기
                ProductData productData = getItem(position);

                // Intent를 사용하여 수정 액티비티로 ProductData 정보 전달
                Intent intent = new Intent(getContext(), AdminProductEditActivity.class);
                intent.putExtra("productCode", productData.getProductCode());
                intent.putExtra("productName", productData.getProductName());
                intent.putExtra("productImage", productData.getProductImage());
                intent.putExtra("productPrice", productData.getProductPrice());
                intent.putExtra("productStock", productData.getProductStock());
                intent.putExtra("category", productData.getProductCategory());
                getContext().startActivity(intent);
            }
        });

        holder.productDeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // AlertDialog 생성해서 삭제 하기 전 삭제할 건지 묻는 알리창 구현 - 성준
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("상품을 삭제 하시겠습니까?");
                builder.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 사용자가 "삭제"를 선택한 경우 상품 삭제 로직 호출
                        deleteProductByProductCode(productData.getProductCode());
                    }
                });
                builder.setNegativeButton("취소", null);
                builder.show(); // AlertDialog 표시
            }
        });

        return convertView;
    }

    // ViewHolder class
    static class ViewHolder {
        TextView textViewProductName;
        ImageView imageViewProduct;
        TextView textViewCategory;
        ImageView productUpdateBtn;
        ImageView productDeleteBtn;
    }

    private void deleteProductByProductCode(int productCode) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // productCode를 기준으로 상품 문서를 조회
        db.collection("products")
                .whereEqualTo("productCode", productCode)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // 일치하는 각 문서(상품) 삭제
                                db.collection("products").document(document.getId()).delete()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(getContext().getApplicationContext(), "상품이 성공적으로 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                                                // 화면을 새로고침하는 코드
                                                removeProductByCode(productCode); // 여기서 리스트에서 제거
                                                notifyDataSetChanged(); // 변경사항 반영
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(getContext().getApplicationContext(), "상품 삭제에 실패했습니다: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        } else {
                            Log.d("deleteProduct", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public void removeProductByCode(int productCode) {
        for (int i = 0; i < getCount(); i++) {
            ProductData p = getItem(i);
            if (p.getProductCode() == productCode) {
                remove(p); // 상품 제거
                break;
            }
        }
    }
}