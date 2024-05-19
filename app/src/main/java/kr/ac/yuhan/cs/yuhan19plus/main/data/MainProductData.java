package kr.ac.yuhan.cs.yuhan19plus.main.data;

public class MainProductData {
    private String  imageResource;
    private String name;
    private int price;
    private int productCode;

    public MainProductData(String  imageResource, String name, int price, int productCode) {
        this.imageResource = imageResource;
        this.name = name;
        this.price = price;
        this.productCode = productCode;
    }

    public String  getImageResource() {return imageResource;}
    public int getProductCode() {return productCode;}

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }
}

