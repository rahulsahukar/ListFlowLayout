package flowlayout.rahul.com.flowlayout;

import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class FlowAdapter<T>
{
    private List<T> data;
    private OnDataChangedListener mOnDataChangedListener;

    public FlowAdapter(List<T> datas)
    {
        data = datas;
    }

    public FlowAdapter(T[] datas)
    {
        data = new ArrayList<T>(Arrays.asList(datas));
    }

    static interface OnDataChangedListener
    {
        void onChanged();
    }

    void setOnDataChangedListener(OnDataChangedListener listener)
    {
        mOnDataChangedListener = listener;
    }

    public int getCount()
    {
        return data == null ? 0 : data.size();
    }

    public void notifyDataChanged()
    {
        mOnDataChangedListener.onChanged();
    }

    public T getItem(int position)
    {
        return data.get(position);
    }

    public abstract View getView(FlowLayout parent, int position, T t);
}