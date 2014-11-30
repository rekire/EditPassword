/**
 * @copyright
 * This code is licensed under the Rekisoft Public License.
 * See http://www.rekisoft.eu/licenses/rkspl.html for more informations.
 */
/**
 * @package eu.rekisoft.android.controls
 * This package contains controls provided by [rekisoft.eu](http://rekisoft.eu/).
 */
package eu.rekisoft.android.editpassword;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Picture;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableContainer;
import android.graphics.drawable.InsetDrawable;
import android.graphics.drawable.PictureDrawable;
import android.os.Build;
import android.util.AttributeSet;
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
        show = new TintedDrawable(R.drawable.ic_show);
        setCompoundDrawables(null, null, show, null);
        setInputType(getInputType() | EditorInfo.TYPE_TEXT_VARIATION_PASSWORD);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(event.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER) {
            show.onStateChange(new int[]{android.R.attr.state_active, android.R.attr.state_pressed});
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if(event.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER) {
            show.onStateChange(new int[]{android.R.attr.state_active});
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public void setOnTouchListener(OnTouchListener l) {
        mTouchListener = l;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        final int action = event.getAction();
        switch(action) {
            case MotionEvent.ACTION_DOWN:
                mPreviousInputType = getInputType();
                setInputType(EditorInfo.TYPE_CLASS_TEXT | EditorInfo.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD, true);
                show.onStateChange(new int[]{android.R.attr.state_active, android.R.attr.state_pressed});
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                setInputType(mPreviousInputType, true);
                mPreviousInputType = -1;
                show.onStateChange(new int[]{android.R.attr.state_active});
                break;
        }

        return mTouchListener != null && mTouchListener.onTouch(v, event);
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

    private class TintedDrawable extends InsetDrawable {
        public TintedDrawable(int image) {
            super(getResources().getDrawable(image), 0);
            Drawable d = getResources().getDrawable(image);
            setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
        }

        @Override
        protected boolean onStateChange(int[] states) {
            for(int state : states) {
                if(state == android.R.attr.state_pressed) {
                    TypedValue typedValue = new TypedValue();
                    Resources.Theme theme = getContext().getTheme();
                    if(theme.resolveAttribute(R.attr.colorAccent, typedValue, true))
                        super.setColorFilter(typedValue.data, PorterDuff.Mode.SRC_ATOP);
                    else
                        Log.d(getClass().getSimpleName(), "BUG!");
                } else {
                    TypedValue typedValue = new TypedValue();
                    Resources.Theme theme = getContext().getTheme();
                    super.setColorFilter(Color.BLUE, android.graphics.PorterDuff.Mode.MULTIPLY);
                    if(theme.resolveAttribute(R.attr.colorControlNormal, typedValue, true))
                        super.setColorFilter(typedValue.data, PorterDuff.Mode.SRC_ATOP);
                    else
                        Log.d(getClass().getSimpleName(), "BUG!");
                }
            }
            return super.onStateChange(states);
        }

        @Override
        public boolean isStateful() {
            return true;
        }
    }
}