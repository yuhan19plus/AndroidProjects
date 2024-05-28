package kr.ac.yuhan.cs.yuhan19plus.main.data;

/** 담당자 : 임성준, 오자현
 * 초기 작성 : 임성준
 * 수정 : 오자현 */
public class MainProductData {
    private String  productImage;
    private String productName;
    private String productCategory;
    private int productPrice;
    private int productCode;

    public MainProductData(String  productImage, String productName, int productPrice, int productCode, String productCategory) {
        this.productImage = productImage;
        this.productName = productName;
        this.productPrice = productPrice;
        this.productCode = productCode;
        this.productCategory = productCategory;
    }

    public String  getImageResource() {return productImage;}
    public String  getCategory() {return productCategory;}
    public int getProductCode() {return productCode;}

    public String getName() {
        return productName;
    }

    public int getPrice() {
        return productPrice;
    }
}