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

import com.bumptech.glide.Glide;

import java.util.List;

import kr.ac.yuhan.cs.yuhan19plus.R;
import kr.ac.yuhan.cs.yuhan19plus.main.MainProductDetail;
import kr.ac.yuhan.cs.yuhan19plus.main.data.MainProductData;

/** 담당자 : 이정민, 오자현
 * 초기 작성자 : 이정민
 * 기능구현 : 오자현 */
public class MainPopularProductPagerAdapter extends RecyclerView.Adapter<MainPopularProductPagerAdapter.ProductViewHolder> {

    private List<MainProductData> products; // MainProductData 타입의 제품 목록
    private Context context;

    // 생성자: 어댑터 생성 시 컨텍스트와 제품 데이터 리스트를 받음
    public MainPopularProductPagerAdapter(Context context, List<MainProductData> products) {
        this.context = context;
        this.products = products;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 뷰홀더 생성: 제품 정보를 표시할 레이아웃을 인플레이트함
        View view = LayoutInflater.from(context).inflate(R.layout.main_popular_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        // 제품 데이터 바인딩: 제품 정보를 뷰홀더의 뷰들과 연결
        MainProductData product = products.get(position);
        // 이미지 로드: Glide 라이브러리를 사용하여 네트워크 이미지 또는 기본 이미지를 로드
        if (!product.getImageResource().equals("R.drawable.icon")) {
            Glide.with(context)
                    .load(product.getImageResource()) // 이미지 URL
                    .placeholder(R.drawable.icon) // 로딩 중 표시할 기본 이미지
                    .into(holder.productImage); // 이미지를 표시할 뷰
        } else {
            // URL이 없을 경우 기본 이미지 표시
            holder.productImage.setImageResource(R.drawable.icon);
        }
        holder.productName.setText(product.getName()); // 제품 이름 설정
        holder.productPrice.setText(String.valueOf(product.getPrice()+" 원")); // 제품 가격 설정

        // 클릭 이벤트 처리: 제품을 클릭하면 제품 상세 화면으로 이동
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, MainProductDetail.class);
            intent.putExtra("productImage", product.getImageResource());
            intent.putExtra("productName", product.getName());
            intent.putExtra("productPrice", product.getPrice());
            intent.putExtra("productCode", product.getProductCode());
            intent.putExtra("productCategory", product.getCategory());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        // 아이템 개수 반환: 제품 리스트의 크기
        return products.size();
    }

    // 내부 클래스: 뷰홀더
    static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productName;
        TextView productPrice;

        // 뷰홀더 생성자: 뷰홀더가 관리할 뷰들을 초기화
        ProductViewHolder(View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.product_image);
            productName = itemView.findViewById(R.id.product_name);
            productPrice = itemView.findViewById(R.id.product_price);
        }
    }
}
