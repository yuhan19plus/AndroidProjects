package kr.ac.yuhan.cs.yuhan19plus.admin.data;

import com.google.firebase.firestore.PropertyName;

// 여기서 카테고리는 준비완료
public class ProductData {
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

    @PropertyName("productCode")
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

    public void setProductCategory(String productCategory) {this.productCategory = productCategory;}

    // 게터 메서드
    @PropertyName("productCode")
    public int getProductCode() { return productCode; }

    public String getProductName() {
        return productName;
    }

    public String getProductImage() {
        return productImage;
    } // 메서드 이름 변경

    public int getProductPrice() {
        return productPrice;
    }

    public int getProductStock() {
        return productStock;
    }
    @PropertyName("category") // Firestore 필드 이름과 일치하도록 설정
    public String getProductCategory() {
        return productCategory;
    }
}
