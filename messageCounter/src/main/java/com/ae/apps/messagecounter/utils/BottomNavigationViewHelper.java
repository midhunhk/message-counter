package com.ae.apps.messagecounter.utils;

import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;

import java.lang.reflect.Field;

/**
 * Helper for BottomNavigationView
 */

public class BottomNavigationViewHelper {

    /**
     * Disable ShiftingMode for a BottomNavigationView
     *
     * @param view the BNV whose shiftingMode to be disabled
     */
    public static void disableShiftingMode(final BottomNavigationView view){
        BottomNavigationMenuView menu = (BottomNavigationMenuView) view.getChildAt(0);
        try {
            Field shiftMode = menu.getClass().getDeclaredField("mShiftingMode");
            shiftMode.setAccessible(true);
            shiftMode.setBoolean(menu, false);
            shiftMode.setAccessible(false);
            for(int i = 0; i< menu.getChildCount(); i++){
                BottomNavigationItemView item = (BottomNavigationItemView) menu.getChildAt(i);
                item.setShiftingMode(false);
                item.setSelected(item.getItemData().isChecked());
                item.setChecked(false);
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
