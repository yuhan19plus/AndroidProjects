package kr.ac.yuhan.cs.yuhan19plus.main.data;

/** 담당자 : 임성준 */
public class ProductReviewData {
    private String memberId;
    private int ratingScore;
    private int productCode;
    private String creationDate;
    private String reviewContent;

    public ProductReviewData(String memberId, int ratingScore, String creationDate, String reviewContent, int productCode) {
        this.memberId = memberId;
        this.ratingScore = ratingScore;
        this.creationDate = creationDate;
        this.reviewContent = reviewContent;
        this.productCode = productCode;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public int getProductCode() {
        return productCode;
    }

    public void setProductCode(int productCode) {
        this.productCode = productCode;
    }

    public int getRatingScore() {
        return ratingScore;
    }

    public void setRatingScore(int ratingScore) {
        this.ratingScore = ratingScore;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getReviewContent() {
        return reviewContent;
    }

    public void setReviewContent(String reviewContent) {
        this.reviewContent = reviewContent;
    }
}
