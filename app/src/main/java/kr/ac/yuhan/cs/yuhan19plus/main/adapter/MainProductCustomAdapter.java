package kr.ac.yuhan.cs.yuhan19plus.main.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import kr.ac.yuhan.cs.yuhan19plus.R;
import kr.ac.yuhan.cs.yuhan19plus.main.data.MainProductData;

public class MainProductCustomAdapter extends BaseAdapter {
    private Context context;
    private List<MainProductData> products;
    private LayoutInflater inflater;

    public MainProductCustomAdapter(Context context, List<MainProductData> products) {
        this.context = context;
        this.products = products;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return products.size();
    }

    @Override
    public Object getItem(int position) {
        return products.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.main_activity_product_item_list, parent, false);
        }

        // 상품 데이터 가져오기
        MainProductData product = products.get(position);

        // 이미지 설정
        ImageView imageView = convertView.findViewById(R.id.item_image);
        try {
            Glide.with(context)
                    .load(product.getImageResource())
                    .placeholder(R.drawable.default_image)
                    .error(R.drawable.default_image) // 기본 이미지 설정
                    .into(imageView);
        } catch (Exception e) {
            Log.e("MainProductCustomAdapter", "Error loading image: ", e);
        }

        // 상품명 설정
        TextView nameView = convertView.findViewById(R.id.item_name);
        nameView.setText(product.getName());

        // 가격 설정
        TextView priceView = convertView.findViewById(R.id.item_price);
        priceView.setText(String.valueOf(product.getPrice())); // 가격을 문자열로 변환하여 설정

        return convertView;
    }
}
