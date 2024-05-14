package kr.ac.yuhan.cs.yuhan19plus.admin.func;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ChangeTextColor {
    public static void changeDarkTextColor(View view, int color) {
        if (view instanceof TextView) {
            // Change FontColor if view is TextView
            ((TextView) view).setTextColor(color);
        } else if (view instanceof ViewGroup) {
            // If the view is a ViewGroup, recursively change the color for all views in that ViewGroup
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                changeDarkTextColor(viewGroup.getChildAt(i), color);
            }
        }
    }
    public static void changeLightTextColor(View view, int color) {
        if (view instanceof TextView) {
            // Change FontColor if view is TextView
            ((TextView) view).setTextColor(color);
        } else if (view instanceof ViewGroup) {
            // If the view is a ViewGroup, recursively change the color for all views in that ViewGroup
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                changeLightTextColor(viewGroup.getChildAt(i), color);
            }
        }
    }
}
