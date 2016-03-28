package flowlayout.rahul.com.flowlayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.Set;

public class ListFlowLayout extends FlowLayout implements FlowAdapter.OnDataChangedListener
{
    private FlowAdapter mFlowAdapter;
    private static final String TAG = "ListFlowLayout";
    private MotionEvent mMotionEvent;

    public ListFlowLayout(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ListFlowLayout);
        ta.recycle();

        setClickable(true);

    }

    public ListFlowLayout(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public ListFlowLayout(Context context)
    {
        this(context, null);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        int cCount = getChildCount();

        for (int i = 0; i < cCount; i++)
        {
            FlowView flowView = (FlowView) getChildAt(i);
            if (flowView.getVisibility() == View.GONE) continue;
            if (flowView.getListView().getVisibility() == View.GONE)
            {
                flowView.setVisibility(View.GONE);
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public interface OnSelectListener
    {
        void onSelected(Set<Integer> selectPosSet);
    }

    private OnSelectListener mOnSelectListener;

    public void setOnSelectListener(OnSelectListener onSelectListener)
    {
        mOnSelectListener = onSelectListener;
        if (mOnSelectListener != null) setClickable(true);
    }

    public interface OnListClickListener
    {
        boolean onListClick(View view, int position, FlowLayout parent);
    }

    private OnListClickListener mOnListClickListener;


    public void setOnListClickListener(OnListClickListener onListClickListener)
    {
        mOnListClickListener = onListClickListener;
        if (onListClickListener != null) setClickable(true);
    }


    public void setAdapter(FlowAdapter adapter)
    {
        mFlowAdapter = adapter;
        mFlowAdapter.setOnDataChangedListener(this);
        changeAdapter();

    }

    private void changeAdapter()
    {
        removeAllViews();
        FlowAdapter adapter = mFlowAdapter;
        FlowView flowViewContainer = null;
        for (int i = 0; i < adapter.getCount(); i++) {
            View tagView = adapter.getView(this, i, adapter.getItem(i));
            if (tagView == null) continue;

            flowViewContainer = new FlowView(getContext());
            tagView.setDuplicateParentStateEnabled(true);
            if (tagView.getLayoutParams() != null) {
                flowViewContainer.setLayoutParams(tagView.getLayoutParams());
            } else {
                MarginLayoutParams lp = new MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                lp.setMargins(dip2px(getContext(), 2),
                        dip2px(getContext(), 2),
                        dip2px(getContext(), 2),
                        dip2px(getContext(), 2));
                flowViewContainer.setLayoutParams(lp);
            }
            flowViewContainer.addView(tagView);
            addView(flowViewContainer);
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if (event.getAction() == MotionEvent.ACTION_UP)
        {
            mMotionEvent = MotionEvent.obtain(event);
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean performClick()
    {
        if (mMotionEvent == null) return super.performClick();

        int x = (int) mMotionEvent.getX();
        int y = (int) mMotionEvent.getY();
        mMotionEvent = null;

        FlowView child = findChild(x, y);
        int pos = findPosByView(child);
        if (child != null)
        {
            if (mOnListClickListener != null)
            {
                return mOnListClickListener.onListClick(child.getListView(), pos, this);
            }
        }
        return true;
    }

    private static final String KEY_CHOOSE_POS = "key_choose_pos";
    private static final String KEY_DEFAULT = "key_default";


    private int findPosByView(View child)
    {
        final int cCount = getChildCount();
        for (int i = 0; i < cCount; i++)
        {
            View v = getChildAt(i);
            if (v == child) return i;
        }
        return -1;
    }

    private FlowView findChild(int x, int y)
    {
        final int cCount = getChildCount();
        for (int i = 0; i < cCount; i++)
        {
            FlowView v = (FlowView) getChildAt(i);
            if (v.getVisibility() == View.GONE) continue;
            Rect outRect = new Rect();
            v.getHitRect(outRect);
            if (outRect.contains(x, y))
            {
                return v;
            }
        }
        return null;
    }

    @Override
    public void onChanged()
    {
        changeAdapter();
    }

    public static int dip2px(Context context, float dpValue)
    {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
