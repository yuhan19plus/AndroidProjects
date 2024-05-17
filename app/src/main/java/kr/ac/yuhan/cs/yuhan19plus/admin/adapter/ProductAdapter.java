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


// made by 오자현
public class ProductAdapter extends ArrayAdapter<ProductData> {

    //생성자
    public ProductAdapter(Context context, ArrayList<ProductData> productData) {
        super(context, 0, productData);
    }

    //리스트 뷰의 각 아이템 뷰를 생성하고 데이터를 바인딩하는 메서드 (오자현)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        // convertView가 null인지 확인합니다. null이면 새로운 View를 생성합니다.
        if (convertView == null) {
            // LayoutInflater를 사용하여 admin_product_list_item 레이아웃을 inflate합니다.
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.admin_product_list_item, parent, false);

            // ViewHolder 객체를 생성하고, convertView의 각 뷰를 초기화합니다.
            holder = new ViewHolder();
            holder.textViewProductName = convertView.findViewById(R.id.textViewProductName);
            holder.textViewCategory = convertView.findViewById(R.id.textViewCategory);
            holder.imageViewProduct = convertView.findViewById(R.id.imageViewProduct);
            holder.productUpdateBtn = convertView.findViewById(R.id.productUpdateBtn);
            holder.productDeleteBtn = convertView.findViewById(R.id.productDeleteBtn);

            // ViewHolder 객체를 convertView의 태그로 설정합니다.
            convertView.setTag(holder);
            // 새로운 convertView가 생성되었음을 로그로 출력합니다.
            Log.d("ProductAdapter", "New convertView created for position: " + position);
        } else {
            // convertView가 null이 아니면, 기존의 ViewHolder 객체를 가져옵니다.
            holder = (ViewHolder) convertView.getTag();
        }

        // 현재 위치의 ProductData 객체를 가져옵니다.
        ProductData productData = getItem(position);

        // productData가 null인지 확인하고 로그를 출력합니다.
        if (productData == null) {
            Log.e("ProductAdapter", "ProductData at position " + position + " is null.");
        } else {
            // productData가 null이 아니면, 각 뷰에 데이터를 설정합니다.
            Log.d("ProductAdapter", "Displaying productData at position " + position + ": " + productData.getProductName());
            holder.textViewProductName.setText(productData.getProductName());
            holder.textViewCategory.setText(productData.getProductCategory());

            // 제품 이미지 URL이 null이 아니고 비어 있지 않으면 이미지를 로드합니다.
            if (productData.getProductImage() != null && !productData.getProductImage().isEmpty()) {
                Glide.with(getContext())
                        .load(productData.getProductImage())
                        .placeholder(R.drawable.default_image)
                        .into(holder.imageViewProduct);
            } else {
                // 이미지 URL이 없으면 기본 이미지를 설정합니다.
                holder.imageViewProduct.setImageResource(R.drawable.default_image);
                Log.d("ProductAdapter", "No image URL provided for productData at position " + position);
            }
        }

        // 수정 버튼 클릭 리스너 설정
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

        // 삭제 버튼 클릭 리스너 설정
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

    // ViewHolder 클래스: 리스트 아이템의 뷰들을 캐시하여 성능을 향상시킵니다.
    static class ViewHolder {
        TextView textViewProductName;
        ImageView imageViewProduct;
        TextView textViewCategory;
        ImageView productUpdateBtn;
        ImageView productDeleteBtn;
    }

    // 상품코드로 찾은 상품을 삭제하는 메서드 (오자현)
    private void deleteProductByProductCode(int productCode) {
        // Firestore 데이터베이스 인스턴스를 가져옵니다.
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
                                                // 상품이 성공적으로 삭제되었음을 사용자에게 알림.
                                                Toast.makeText(getContext().getApplicationContext(), "상품이 성공적으로 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                                                // 리스트에서 해당 상품을 제거하고 변경사항을 반영합니다.
                                                removeProductByCode(productCode);
                                                notifyDataSetChanged(); // 변경사항 반영
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                // 상품 삭제에 실패했음을 사용자에게 알림.
                                                Toast.makeText(getContext().getApplicationContext(), "상품 삭제에 실패했습니다: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        } else {
                            // 문서를 가져오는 도중 오류가 발생했음을 로그로 출력합니다.
                            Log.d("deleteProduct", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }


    // 주어진 상품코드를 기준으로 리스트에서 상품을 제거하는 메서드 (오자현)
    public void removeProductByCode(int productCode) {
        // 리스트의 모든 항목을 순회합니다.
        for (int i = 0; i < getCount(); i++) {
            // 현재 위치의 상품 데이터를 가져옵니다.
            ProductData p = getItem(i);

            // 현재 상품의 코드가 주어진 코드와 일치하는지 확인합니다.
            if (p.getProductCode() == productCode) {
                // 일치하는 상품을 리스트에서 제거합니다.
                remove(p);
                // 일치하는 상품을 찾았으므로 반복문을 종료합니다.
                break;
            }
        }
    }
}