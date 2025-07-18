package com.badlogic.gdx.backends.android.keyboardheight;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

/**
 * The keyboard height provider, this class uses a PopupWindow to calculate the window height when the floating keyboard is
 * opened and closed.
 */
public class StandardKeyboardHeightProvider extends PopupWindow implements KeyboardHeightProvider {

    /**
     * The tag for logging purposes
     */
    private final static String TAG = "sample_KeyboardHeightProvider";
    /**
     * The cached landscape height of the keyboard
     */
    private static int keyboardLandscapeHeight;
    /**
     * The cached portrait height of the keyboard
     */
    private static int keyboardPortraitHeight;
    /**
     * The keyboard height observer
     */
    private KeyboardHeightObserver observer;
    /**
     * The view that is used to calculate the keyboard height
     */
    private final View popupView;

    /**
     * The parent view
     */
    private final View parentView;

    /**
     * The root activity that uses this KeyboardHeightProvider
     */
    private final Activity activity;

    /**
     * Construct a new KeyboardHeightProvider
     *
     * @param activity The parent activity
     */
    public StandardKeyboardHeightProvider(Activity activity) {
        super(activity);
        this.activity = activity;

        LayoutInflater inflator = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout linearLayout = new LinearLayout(inflator.getContext());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        linearLayout.setLayoutParams(layoutParams);
        this.popupView = linearLayout;
        setContentView(popupView);

        setSoftInputMode(LayoutParams.SOFT_INPUT_ADJUST_RESIZE | LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);

        parentView = activity.findViewById(android.R.id.content);

        setWidth(0);
        setHeight(android.view.ViewGroup.LayoutParams.MATCH_PARENT);

        popupView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            if (popupView != null) {
                handleOnGlobalLayout();
            }
        });
    }

    /**
     * Start the KeyboardHeightProvider, this must be called after the onResume of the Activity. PopupWindows are not allowed to
     * be registered before the onResume has finished of the Activity.
     */
    @Override
    public void start() {

        if (!isShowing() && parentView.getWindowToken() != null) {
            setBackgroundDrawable(new ColorDrawable(0));
            showAtLocation(parentView, Gravity.NO_GRAVITY, 0, 0);
        }
    }

    /**
     * Close the keyboard height provider, this provider will not be used anymore.
     */
    @Override
    public void close() {
        this.observer = null;
        dismiss();
    }

    /**
     * Set the keyboard height observer to this provider. The observer will be notified when the keyboard height has changed. For
     * example when the keyboard is opened or closed.
     *
     * @param observer The observer to be added to this provider.
     */
    @Override
    public void setKeyboardHeightObserver(KeyboardHeightObserver observer) {
        this.observer = observer;
    }

    /**
     * Get the screen orientation
     *
     * @return the screen orientation
     */
    private int getScreenOrientation() {
        return activity.getResources().getConfiguration().orientation;
    }

    /**
     * Popup window itself is as big as the window of the Activity. The keyboard can then be calculated by extracting the popup
     * view bottom from the activity window height.
     */
    private void handleOnGlobalLayout() {

        Point screenSize = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(screenSize);

        Rect rect = new Rect();
        popupView.getWindowVisibleDisplayFrame(rect);

        // REMIND, you may like to change this using the fullscreen size of the phone
        // and also using the status bar and navigation bar heights of the phone to calculate
        // the keyboard height. But this worked fine on a Nexus.
        int orientation = getScreenOrientation();
        int keyboardHeight = screenSize.y - rect.bottom;
        int leftInset = rect.left;
        int rightInset = Math.abs(screenSize.x - rect.right + rect.left);

        if (keyboardHeight == 0) {
            notifyKeyboardHeightChanged(0, leftInset, rightInset, orientation);
        } else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            keyboardPortraitHeight = keyboardHeight;
            notifyKeyboardHeightChanged(keyboardPortraitHeight, leftInset, rightInset, orientation);
        } else {
            keyboardLandscapeHeight = keyboardHeight;
            notifyKeyboardHeightChanged(keyboardLandscapeHeight, leftInset, rightInset, orientation);
        }
    }

    /**
     *
     */
    private void notifyKeyboardHeightChanged(int height, int leftInset, int rightInset, int orientation) {
        if (observer != null) {
            observer.onKeyboardHeightChanged(height, leftInset, rightInset, orientation);
        }
    }

    @Override
    public int getKeyboardLandscapeHeight() {
        return keyboardLandscapeHeight;
    }

    @Override
    public int getKeyboardPortraitHeight() {
        return keyboardPortraitHeight;
    }
}
