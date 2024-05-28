package kr.ac.yuhan.cs.yuhan19plus.admin.data;

import android.widget.Button;

import java.util.Date;
/** 담당자 : 임성준, 이석재
 * 초기 작성자 : 임성준
 * 수정 : 이석재 */
public class MemberData {
    // Member Data Field
    private int number;
    private String memberId;
    private String memberName;
    private Date joinDate;
    private String uid;
    private int point;
    private Button outButton;

    // MemberData Constructor
    public MemberData(int number, String memberId, String memberName, Date joinDate, String uid, int point) {
        this.number = number;
        this.memberId = memberId;
        this.memberName = memberName;
        this.joinDate = joinDate;
        this.uid = uid;
        this.point = point;
    }

    // Getter & Setter
    public int getNumber() {
        return number;
    }
    public void setNumber(int number) { this.number = number; }
    public String getMemberId() {
        return memberId;
    }
    public String getMemberName() {
        return memberName;
    }
    public Date getJoinDate() {
        return joinDate;
    }
    public String getUid() { return uid; }
    public int getPoint() {
        return point;
    }

    // 나가기 버튼 속성을 설정하는 메서드
    public void setOutButton(Button outButton) {
        this.outButton = outButton;
    }

    // 나가기 버튼 여부를 반환하는 메서드
    public boolean hasOutButton() {
        // 나가기 버튼이 있는지 여부에 따라 true 또는 false를 반환
        // 여기서는 임의로 true 또는 false를 반환하도록 설정
        // 실제로는 MemberData 객체가 가지고 있는 정보를 기반으로 판단해야 함
        return true; // 또는 false
    }
}
