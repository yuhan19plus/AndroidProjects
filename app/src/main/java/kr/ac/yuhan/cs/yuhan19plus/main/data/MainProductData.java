package kr.ac.yuhan.cs.yuhan19plus.main.data;

public class MainProductData {
    private int imageResource;
    private String name;
    private String price;
    private Long productCode;

    public MainProductData(int imageResource, String name, String price, Long productCode) {
        this.imageResource = imageResource;
        this.name = name;
        this.price = price;
        this.productCode = productCode;
    }

    public int getImageResource() {return imageResource;}
    public Long getProductCode() {return productCode;}

    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }
}

