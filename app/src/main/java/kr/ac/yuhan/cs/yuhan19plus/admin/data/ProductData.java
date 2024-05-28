package kr.ac.yuhan.cs.yuhan19plus.admin.data;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.PropertyName;

/** 담당자 : 임성준, 오자현, 이석재
 * 초기 작성자 : 임성준
 * 수정 : 이석재, 오자현 */
public class ProductData implements Parcelable {
    private int productCode;
    private String productName;
    private String productImage; // 상품 이미지 또는 3D 파일 데이터
    private int productPrice; // 가격
    private int productStock; // 재고량
    private String productCategory;//카테고리

    // 생성자
    public ProductData(int productCode, String productName, String productCategory, String productImage, int productPrice, int productStock) {
        this.productCode = productCode;
        this.productName = productName;
        this.productCategory = productCategory;
        this.productImage = productImage;
        this.productPrice = productPrice;
        this.productStock = productStock;
    }

    // 기본 생성자 추가
    public ProductData() {
        // Firestore 역직렬화를 위해 필요 파이어베이스에서 읽은 정보를 임시로 담아두는 역할을 함
    }

    // Setter 메서드
    public void setProductCode(int productCode) {
        this.productCode = productCode;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public void setProductPrice(int productPrice) {
        this.productPrice = productPrice;
    }

    public void setProductStock(int productStock) {
        this.productStock = productStock;
    }

    public void setProductCategory(String productCategory) {
        this.productCategory = productCategory;
    }

    // Getter 메서드
    public int getProductCode() {
        return productCode;
    }

    public String getProductName() {
        return productName;
    }

    public String getProductImage() {
        return productImage;
    }

    public int getProductPrice() {
        return productPrice;
    }

    public int getProductStock() {
        return productStock;
    }

    public String getProductCategory() {
        return productCategory;
    }

    // 결제 액티비티에서 객체를 넘길때 사용함
    protected ProductData(Parcel in) {
        productCode = in.readInt();
        productName = in.readString();
        productImage = in.readString();
        productPrice = in.readInt();
        productStock = in.readInt();
        productCategory = in.readString();
    }

    public static final Creator<ProductData> CREATOR = new Creator<ProductData>() {
        @Override
        public ProductData createFromParcel(Parcel in) {
            return new ProductData(in);
        }

        @Override
        public ProductData[] newArray(int size) {
            return new ProductData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(productCode);
        dest.writeString(productName);
        dest.writeString(productImage);
        dest.writeInt(productPrice);
        dest.writeInt(productStock);
        dest.writeString(productCategory);
    }
}