package kr.ac.yuhan.cs.yuhan19plus.admin.func;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
/** 담당자 : 임성준 */
public class ChangeTextColor {
    public static void changeDarkTextColor(View view, int color) {
        if (view instanceof TextView) {
            // view가 TextView일 경우 글자 색상 변경
            ((TextView) view).setTextColor(color);
        } else if (view instanceof ViewGroup) {
            // view가 ViewGroup일 경우, ViewGroup 내 모든 뷰에 대해 재귀적으로 색상 변경
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                changeDarkTextColor(viewGroup.getChildAt(i), color);
            }
        }
    }
    public static void changeLightTextColor(View view, int color) {
        if (view instanceof TextView) {
            // view가 TextView일 경우 글자 색상 변경
            ((TextView) view).setTextColor(color);
        } else if (view instanceof ViewGroup) {
            // view가 ViewGroup일 경우, ViewGroup 내 모든 뷰에 대해 재귀적으로 색상 변경
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                changeLightTextColor(viewGroup.getChildAt(i), color);
            }
        }
    }
}
