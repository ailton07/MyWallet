package br.edu.ufam.ceteli.mywallet.classes;

import android.content.Context;
import android.content.res.TypedArray;

/**
 * Created by rodrigo on 04/11/15.
 */
public class DesignUtils {
    public static int getToolbarHeight(Context context, int offset) {
        final TypedArray styledAttributes = context.getTheme().obtainStyledAttributes(new int[]{android.support.design.R.attr.actionBarSize});
        int toolbarHeight = (int) styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();
        return toolbarHeight + offset;
    }
}
