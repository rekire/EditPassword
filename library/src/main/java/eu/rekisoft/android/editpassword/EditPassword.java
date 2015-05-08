/**
 * @copyright
 * This code is licensed under the Rekisoft Public License.
 * See http://www.rekisoft.eu/licenses/rkspl.html for more information.
 */
/**
 * @package eu.rekisoft.android.editpassword
 * This package contains the EditPassword control by [rekisoft.eu](http://rekisoft.eu/).
 */
package eu.rekisoft.android.editpassword;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;

/**
 * Control for entering passwords, with the possibility to show the password on press.
 *
 * Created on 29.11.2014.
 * @author René Kilczan
 */
public class EditPassword extends AppCompatEditText implements View.OnTouchListener {
    private int mPreviousInputType;
    private View.OnTouchListener mTouchListener;
    private Drawable show;
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

    private void init() {
        super.setOnTouchListener(this);
        show = DrawableCompat.wrap(getResources().getDrawable(R.drawable.ep_ic_show));
        show.setBounds(0, 0, show.getIntrinsicWidth(), show.getIntrinsicHeight());
        DrawableCompat.setTint(show, getResources().getColor(R.color.ep_show_tint));
        setCompoundDrawables(null, null, show, null);
        setInputType(getInputType() | EditorInfo.TYPE_TEXT_VARIATION_PASSWORD);
    }

    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        if(event.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER) {
            updateState(android.R.attr.state_focused, android.R.attr.state_active);
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, @NonNull KeyEvent event) {
        if(event.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER) {
            updateState(android.R.attr.state_focused);
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
                    updateState(android.R.attr.state_focused, android.R.attr.state_pressed, android.R.attr.state_active);
                } else {
                    updateState(android.R.attr.state_pressed, android.R.attr.state_active);
                }
            }
            break;

        case MotionEvent.ACTION_UP:
        case MotionEvent.ACTION_CANCEL:
            if(mShowingPassword) {
                setInputType(mPreviousInputType, true);
                mPreviousInputType = -1;
                mShowingPassword = false;
                if(v.hasFocus()) {
                    updateState(android.R.attr.state_focused);
                } else {
                    updateState();
                }
                return true;
            }
            break;
        }

        return mShowingPassword || (mTouchListener != null && mTouchListener.onTouch(v, event));
    }

    private void updateState(int... states) {
        show.setState(states);
        //drawableStateChanged();
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
}