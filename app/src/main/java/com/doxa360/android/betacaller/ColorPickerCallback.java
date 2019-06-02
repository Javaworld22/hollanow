package com.doxa360.android.betacaller;

/**
 * Created by user on 10/20/2017.
 */


        import android.support.annotation.ColorInt;

/**
 * Created by Patrick Geselbracht on 2017-03-04
 *
 * @author Patrick Geselbracht
 */
public interface ColorPickerCallback {
    /**
     * Gets called whenever a user chooses a color from the ColorPicker, i.e., presses the
     * "Choose" button.
     *
     * @param color Color chosen
     */

    void onColorChosen(@ColorInt int color);
}