package kr.ac.yuhan.cs.yuhan19plus.main.data;

public class MainProductData {
    private String  imageResource;
    private String name;
    private String category;
    private int price;
    private int productCode;

    public MainProductData(String  imageResource, String name, int price, int productCode, String category) {
        this.imageResource = imageResource;
        this.name = name;
        this.price = price;
        this.productCode = productCode;
        this.category = category;
    }

    public String  getImageResource() {return imageResource;}
    public String  getCategory() {return category;}
    public int getProductCode() {return productCode;}

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }
}

