/**
 * @copyright
 * This code is licensed under the Rekisoft Public License.
 * See http://www.rekisoft.eu/licenses/rkspl.html for more informations.
 */
/**
 * @package eu.rekisoft.android.editpassword
 * This package contains the EditPassword control by [rekisoft.eu](http://rekisoft.eu/).
 */
package eu.rekisoft.android.editpassword;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

/**
 * Control for entering passwords, with the possibility to show the password on press.
 *
 * Created on 29.11.2014.
 * @author René Kilczan
 */
public class EditPassword extends EditText implements View.OnTouchListener {
    private int mPreviousInputType;
    private OnTouchListener mTouchListener;
    private TintedDrawable show;
    private boolean mShowingPassword;

    public EditPassword(Context context) {
        super(context);
        init();
    }

    public EditPassword(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public EditPassword(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public EditPassword(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        super.setOnTouchListener(this);
        show = new TintedDrawable(R.drawable.ic_show) {
            @Override
            protected int getColor(boolean isPressed, boolean isFocused, boolean isActive) {
                TypedValue typedValue = new TypedValue();
                Resources.Theme theme = getContext().getTheme();
                if(isActive) {
                    Log.d(TAG, "using active color");
                    if(theme.resolveAttribute(R.attr.colorControlActivated, typedValue, true)) {
                        return typedValue.data;
                    } else {
                        Log.e(TAG, "Could not apply colorControlActivated.");
                    }
                } else if(isFocused) {
                    Log.d(TAG, "using focus color");
                    if(theme.resolveAttribute(R.attr.colorControlNormal, typedValue, true)) {
                        return typedValue.data;
                    } else {
                        Log.e(TAG, "Could not apply colorControlNormal.");
                    }
                }
                if(theme.resolveAttribute(android.R.attr.textColorPrimaryDisableOnly, typedValue, true)) {
                    Log.d(TAG, "using fallback color");
                    return typedValue.data;
                } else {
                    Log.e(TAG, "Could not apply textColorPrimaryDisableOnly.");
                    return Color.GRAY;
                }
            }
        };
        setCompoundDrawables(null, null, show, null);
        setInputType(getInputType() | EditorInfo.TYPE_TEXT_VARIATION_PASSWORD);
        setBackgroundDrawable(new TintedDrawable(R.drawable.abc_edit_text_material) {
            @Override
            protected int getColor(boolean isPressed, boolean isFocused, boolean isActive) {
                TypedValue typedValue = new TypedValue();
                Resources.Theme theme = getContext().getTheme();
                if(isActive) {
                    Log.d(TAG, "using active color");
                    if(theme.resolveAttribute(R.attr.colorControlActivated, typedValue, true)) {
                        return typedValue.data;
                    } else {
                        Log.e(TAG, "Could not apply colorControlActivated.");
                    }
                } else if(isFocused) {
                    Log.d(TAG, "using focus color");
                    if(theme.resolveAttribute(R.attr.colorControlNormal, typedValue, true)) {
                        return typedValue.data;
                    } else {
                        Log.e(TAG, "Could not apply colorControlNormal.");
                    }
                }
                if(theme.resolveAttribute(android.R.attr.textColorPrimaryDisableOnly, typedValue, true)) {
                    Log.d(TAG, "using fallback color");
                    return typedValue.data;
                } else {
                    Log.e(TAG, "Could not apply textColorPrimaryDisableOnly.");
                    return Color.GRAY;
                }
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        if(event.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER) {
            show.onStateChange(new int[]{android.R.attr.state_active, android.R.attr.state_pressed});
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, @NonNull KeyEvent event) {
        if(event.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER) {
            show.onStateChange(new int[]{android.R.attr.state_active});
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public void setOnTouchListener(OnTouchListener l) {
        mTouchListener = l;
    }

    public int getPx(int dp) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, metrics);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        final int action = event.getAction();
        switch(action) {
            case MotionEvent.ACTION_DOWN:
                mPreviousInputType = getInputType();
                if(event.getRawX() >= v.getWidth() - getPx(20)) {
                    setInputType(EditorInfo.TYPE_CLASS_TEXT | EditorInfo.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD, true);
                    mShowingPassword = true;
                    if(v.hasFocus()) {
                        show.onStateChange(new int[] {android.R.attr.state_focused, android.R.attr.state_pressed, android.R.attr.state_active});
                    } else {
                        show.onStateChange(new int[] {android.R.attr.state_pressed, android.R.attr.state_active});
                    }
                }
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if(mShowingPassword) {
                    setInputType(mPreviousInputType, true);
                    mPreviousInputType = -1;
                    mShowingPassword = false;
                    return true;
                }
                break;
        }

        return mShowingPassword || (mTouchListener != null && mTouchListener.onTouch(v, event));
    }

    private void setInputType(int inputType, boolean keepState) {
        int selectionStart = -1;
        int selectionEnd = -1;
        if(keepState) {
            selectionStart = getSelectionStart();
            selectionEnd = getSelectionEnd();
        }
        setInputType(inputType);
        if(keepState) {
            setSelection(selectionStart, selectionEnd);
        }
    }

    private abstract class TintedDrawable extends InsetDrawable {
        protected final String TAG = TintedDrawable.class.getSimpleName();

        public TintedDrawable(int image) {
            super(getResources().getDrawable(image), 0);
            Drawable d = getResources().getDrawable(image);
            setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
        }

        @Override
        protected final boolean onStateChange(int[] states) {
            boolean isPressed = false;
            boolean isFocused = false;
            boolean isActive = false;
            for(int state : states) {
                if(state == android.R.attr.state_pressed) {
                    isPressed = true;
                } else if(state == android.R.attr.state_focused) {
                    isFocused = true;
                } else if(state == android.R.attr.state_active) {
                    isActive = true;
                }
            }
            super.setColorFilter(getColor(isPressed, isFocused, isActive), PorterDuff.Mode.SRC_ATOP);
            return super.onStateChange(states);
        }

        protected abstract int getColor(boolean isPressed, boolean isFocused, boolean isActive);

        @Override
        public boolean isStateful() {
            return true;
        }
    }
}