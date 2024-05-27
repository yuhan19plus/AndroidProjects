package kr.ac.yuhan.cs.yuhan19plus.admin.data;

/** 담당자 임성준
 * 임성준 작성
 * */
public class AdminData {
    // Admin Data Field
    private int adminNum;
    private String adminId;
    private String adminPw;
    private String adminPosition;

    // AdminData Constructor
    public AdminData(int adminNum, String adminId, String adminPw, String adminPosition) {
        this.adminNum = adminNum;
        this.adminId = adminId;
        this.adminPw = adminPw;
        this.adminPosition = adminPosition;
    }

    // Getter & Setter
    public void setAdminNum(int adminNum) {
        this.adminNum = adminNum;
    }
    public int getAdminNum() {
        return adminNum;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }
    public String getAdminId() {
        return adminId;
    }

    public void setAdminPw(String adminPw) {
        this.adminPw = adminPw;
    }
    public String getAdminPw() {
        return adminPw;
    }

    public void setAdminPosition(String adminPosition) {
        this.adminPosition = adminPosition;
    }
    public String getAdminPosition() {
        return adminPosition;
    }
}
