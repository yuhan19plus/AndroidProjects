package kr.ac.yuhan.cs.yuhan19plus.main.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import kr.ac.yuhan.cs.yuhan19plus.R;
import kr.ac.yuhan.cs.yuhan19plus.main.MainProductDetail;

public class MainPopularProductPagerAdapter extends RecyclerView.Adapter<MainPopularProductPagerAdapter.ProductViewHolder> {

    // Product 클래스: 이미지 리소스 ID, 상품명, 가격을 담는 클래스
    public static class Product {
        int imageResId;
        String name;
        String price;

        public Product(int imageResId, String name, String price) {
            this.imageResId = imageResId;
            this.name = name;
            this.price = price;
        }
    }

    private List<Product> products; // 상품 목록을 담는 리스트
    private Context context;
    // ViewHolder 클래스: 아이템 뷰 내의 뷰들을 바인딩
    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productName;
        TextView productPrice;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.product_image);
            productName = itemView.findViewById(R.id.product_name);
            productPrice = itemView.findViewById(R.id.product_price);

        }
    }

    public MainPopularProductPagerAdapter(List<Product> products) {
        this.products = products;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // XML 레이아웃을 inflate 하여 ViewHolder 객체를 생성하여 반환
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_popular_product, parent, false);
        return new ProductViewHolder(view);
    }
    //더미데이터 부분
    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        // ViewHolder에 데이터를 바인딩
        Product product = products.get(position);
        holder.productImage.setImageResource(product.imageResId);
        holder.productName.setText(product.name);
        holder.productPrice.setText(product.price);

        // 아이템 클릭 리스너 설정
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 새로운 액티비티로 이동
                Intent intent = new Intent(context, MainProductDetail.class);
                intent.putExtra("imageResId", product.imageResId);
                intent.putExtra("name", product.name);
                intent.putExtra("price", product.price);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }
}
