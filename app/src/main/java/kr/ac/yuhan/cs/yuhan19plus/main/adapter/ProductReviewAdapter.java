package kr.ac.yuhan.cs.yuhan19plus.main.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import kr.ac.yuhan.cs.yuhan19plus.R;
import kr.ac.yuhan.cs.yuhan19plus.main.data.ProductReviewData;

/** 담당자 : 임성준
 * 상품리뷰기능구현 관련 코드 */
public class ProductReviewAdapter extends ArrayAdapter<ProductReviewData> {
    private Activity context;
    private List<ProductReviewData> reviewList;

    public ProductReviewAdapter(Activity context, List<ProductReviewData> reviewList) {
        super(context, R.layout.main_product_review_item, reviewList);
        this.context = context;
        this.reviewList = reviewList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.main_product_review_item, null, true);

        TextView textViewMemberId = listViewItem.findViewById(R.id.textViewMemberId);
        TextView textViewRatingScore = listViewItem.findViewById(R.id.textViewRatingScore);
        TextView textViewCreationDate = listViewItem.findViewById(R.id.textViewCreationDate);

        ProductReviewData review = reviewList.get(position);

        textViewMemberId.setText(review.getMemberId() + "");
        textViewRatingScore.setText(review.getRatingScore() + "");
        textViewCreationDate.setText(review.getCreationDate() + "");

        return listViewItem;
    }
}
