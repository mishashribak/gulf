package com.app.khaleeji.Adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Checkable;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ThemedSpinnerAdapter;

import com.squareup.picasso.Picasso;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@TargetApi(Build.VERSION_CODES.M)
public class ADSimpleAdapter extends BaseAdapter implements Filterable, ThemedSpinnerAdapter {
    private final LayoutInflater mInflater;

    private int[] mTo;
    private String[] mFrom;
    private ViewBinder mViewBinder;

    private List<? extends Map<String, String>> mData;

    private int mResource;
    private int mDropDownResource;

    /**
     * Layout inflater used for {@link #getDropDownView(int, View, ViewGroup)}.
     */
    private LayoutInflater mDropDownInflater;

    private SimpleFilter mFilter;
    private ArrayList<Map<String, String>> mUnfilteredData;

    /**
     * Constructor
     *
     * @param context The context where the View associated with this ADSimpleAdapter is running
     * @param data A List of Maps. Each entry in the List corresponds to one row in the list. The
     * Maps contain the data for each row, and should include all the entries specified in
     * "from"
     * @param resource Resource identifier of a view layout that defines the views for this list
     * item. The layout file should include at least those named views defined in "to"
     * @param from A list of column names that will be added to the Map associated with each
     * item.
     * @param to The views that should display column in the "from" parameter. These should all be
     * TextViews. The first N views in this list are given the values of the first N columns
     * in the from parameter.
     */

    Context context;
    String user_name;

    public ADSimpleAdapter(Context context, ArrayList<TreeMap<String, String>> data,
                         int resource, String[] from,  int[] to) {
        mData = data;
        mResource = mDropDownResource = resource;
        mFrom = from;
        mTo = to;
        this.context = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    /**
     * @see android.widget.Adapter#getCount()
     */
    public int getCount() {
        return mData.size();
    }

    /**
     * @see android.widget.Adapter#getItem(int)
     */
    public Object getItem(int position) {
        return mData.get(position).get("name");
    }

    /**
     * @see android.widget.Adapter#getItemId(int)
     */
    public long getItemId(int position) {
        return position;
    }

    /**
     * @see android.widget.Adapter#getView(int, View, ViewGroup)
     */
    public View getView(int position, View convertView, ViewGroup parent) {
        return createViewFromResource(mInflater, position, convertView, parent, mResource);
    }

    private View createViewFromResource(LayoutInflater inflater, int position, View convertView,
                                        ViewGroup parent, int resource) {
        View v;
        if (convertView == null) {
            v = inflater.inflate(resource, parent, false);
        } else {
            v = convertView;
        }

        bindView(position, v);

        return v;
    }

    /**
     * <p>Sets the layout resource to create the drop down views.</p>
     *
     * @param resource the layout resource defining the drop down views
     * @see #getDropDownView(int, View, ViewGroup)
     */
    public void setDropDownViewResource(int resource) {
        mDropDownResource = resource;
    }

    /**
     * Sets the {@link Resources.Theme} against which drop-down views are
     * inflated.
     * <p/>
     * By default_img, drop-down views are inflated against the theme of the
     * {@link Context} passed to the adapter's constructor.
     *
     * @param theme the theme against which to inflate drop-down views or
     *              {@code null} to use the theme from the adapter's context
     * @see #getDropDownView(int, View, ViewGroup)
     */
    @Override
    public void setDropDownViewTheme(Resources.Theme theme) {
        if (theme == null) {
            mDropDownInflater = null;
        } else if (theme == mInflater.getContext().getTheme()) {
            mDropDownInflater = mInflater;
        } else {
            final Context context = new ContextThemeWrapper(mInflater.getContext(), theme);
            mDropDownInflater = LayoutInflater.from(context);
        }
    }

    @Override
    public Resources.Theme getDropDownViewTheme() {
        return mDropDownInflater == null ? null : mDropDownInflater.getContext().getTheme();
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        final LayoutInflater inflater = mDropDownInflater == null ? mInflater : mDropDownInflater;
        return createViewFromResource(inflater, position, convertView, parent, mDropDownResource);
    }

    private void bindView(int position, View view) {
        final Map dataSet = mData.get(position);
        if (dataSet == null) {
            return;
        }

        final ViewBinder binder = mViewBinder;
        final String[] from = mFrom;
        final int[] to = mTo;
        final int count = to.length;

        for (int i = 0; i < count; i++) {
            final View v = view.findViewById(to[i]);
            if (v != null) {
                final Object data = dataSet.get(from[i]);
                String text = data == null ? "" : data.toString();
                if (text == null) {
                    text = "";
                }

                boolean bound = false;
                if (binder != null) {
                    bound = binder.setViewValue(v, data, text);
                }

                if (!bound) {
                    if (v instanceof Checkable) {
                        if (data instanceof Boolean) {
                            ((Checkable) v).setChecked((Boolean) data);
                        } else if (v instanceof TextView) {
                            // Note: keep the instanceof TextView check at the bottom of these
                            // ifs since a lot of views are TextViews (e.g. CheckBoxes).
                            setViewText((TextView) v, text, i);
                        } else {
                            throw new IllegalStateException(v.getClass().getName() +
                                    " should be bound to a Boolean, not a " +
                                    (data == null ? "<unknown type>" : data.getClass()));
                        }
                    } else if (v instanceof TextView) {
                        // Note: keep the instanceof TextView check at the bottom of these
                        // ifs since a lot of views are TextViews (e.g. CheckBoxes).
                        setViewText((TextView) v, text, i);
                    } else if (v instanceof ImageView) {
                        if (data instanceof String) {
//                            setViewImage((ImageView) v, (Integer) data);
//                        } else {
                            setViewImage((ImageView) v, text);
                        }
                    } else {
                        throw new IllegalStateException(v.getClass().getName() + " is not a " +
                                " view that can be bounds by this ADSimpleAdapter");
                    }
                }
            }
        }
    }

    /**
     * Returns the {@link ViewBinder} used to bind data to views.
     *
     * @return a ViewBinder or null if the binder does not exist
     */
    public ViewBinder getViewBinder() {
        return mViewBinder;
    }

    /**
     * Sets the binder used to bind data to views.
     *
     * @param viewBinder the binder used to bind data to views, can be null to
     *                   remove the existing binder
     * @see #getViewBinder()
     */
    public void setViewBinder(ViewBinder viewBinder) {
        mViewBinder = viewBinder;
    }

    /**
     * Called by bindView() to set the image for an ImageView but only if
     * there is no existing ViewBinder or if the existing ViewBinder cannot
     * handle binding to an ImageView.
     * <p/>
     * This method is called instead of {@link #setViewImage(ImageView, String)}
     * if the supplied data is an int or Integer.
     *
     * @param v     ImageView to receive an image
     * @param value the value retrieved from the data set
     * @see #setViewImage(ImageView, String)
     */
    public void setViewImage(ImageView v, int value) {
        v.setImageResource(value);
    }

    /**
     * Called by bindView() to set the image for an ImageView but only if
     * there is no existing ViewBinder or if the existing ViewBinder cannot
     * handle binding to an ImageView.
     * <p/>
     * By default_img, the value will be treated as an image resource. If the
     * value cannot be used as an image resource, the value is used as an
     * image Uri.
     * <p/>
     * This method is called instead of {@link #setViewImage(ImageView, int)}
     * if the supplied data is not an int or Integer.
     *
     * @param v     ImageView to receive an image
     * @param value the value retrieved from the data set
     * @see #setViewImage(ImageView, int)
     */
    public void setViewImage(ImageView v, String value) {
        try {
//            v.setImageResource(Integer.parseInt(value));

            Picasso.with(context).load(value).into(v);

        } catch (NumberFormatException nfe) {
            v.setImageURI(Uri.parse(value));
        }
    }

    /**
     * Called by bindView() to set the text for a TextView but only if
     * there is no existing ViewBinder or if the existing ViewBinder cannot
     * handle binding to a TextView.
     *
     * @param v    TextView to receive text
     * @param text the text to be set for the TextView
     */
    public void setViewText(TextView v, String text, int pos) {
        if (pos == 1) {
            text = text.substring(1, text.length());
        }
        this.user_name = text;
        Log.e("text filterfdfdf" , text);
        v.setText(text);
    }


    public String getText()
    {
        return user_name;
    }

    public Filter getFilter() {
        if (mFilter == null) {
            mFilter = new SimpleFilter();
        }
        return mFilter;
    }

    public void setData(ArrayList<Map<String, String>> mPeopleList) {
        mData=mPeopleList;
        notifyDataSetChanged();
    }

    /**
     * This class can be used by external clients of ADSimpleAdapter to bind
     * values to views.
     * <p/>
     * You should use this class to bind values to views that are not
     * directly supported by ADSimpleAdapter or to change the way binding
     * occurs for views supported by ADSimpleAdapter.
     *
     * @see ADSimpleAdapter#setViewImage(ImageView, int)
     * @see ADSimpleAdapter#setViewImage(ImageView, String)
     */
    public static interface ViewBinder {
        /**
         * Binds the specified data to the specified view.
         * <p/>
         * When binding is handled by this ViewBinder, this method must return true.
         * If this method returns false, ADSimpleAdapter will attempts to handle
         * the binding on its own.
         *
         * @param view               the view to bind the data to
         * @param data               the data to bind to the view
         * @param textRepresentation a safe String representation of the supplied data:
         *                           it is either the result of data.toString() or an empty String but it
         *                           is never null
         * @return true if the data was bound to the view, false otherwise
         */
        boolean setViewValue(View view, Object data, String textRepresentation);
    }

    /**
     * <p>An array filters constrains the content of the array adapter with
     * a prefix. Each item that does not start with the supplied prefix
     * is removed from the list.</p>
     */
    private class SimpleFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();

            if (mUnfilteredData == null) {
                mUnfilteredData = new ArrayList<Map<String, String>>(mData);
            }

            if (prefix == null || prefix.length() == 0) {
                ArrayList<Map<String, String >> list = mUnfilteredData;
                results.values = list;
                results.count = list.size();
            } else {
                String prefixString = prefix.toString().toLowerCase();

                ArrayList<Map<String, String>> unfilteredValues = mUnfilteredData;
                int count = unfilteredValues.size();

                ArrayList<Map<String, String>> newValues = new ArrayList<Map<String, String>>(count);

                for (int i = 0; i < count; i++) {
//                    Map<String, ?> h = unfilteredValues.get(i);
//                    if (h != null) {
//
//                        int len = mTo.length;
//
////                        for (int j = 0; j < len; j++) {
//                        String str = (String) h.get(mFrom[1]);
//
//                        String[] words = str.split(" ");
//                        int wordCount = words.length;
//
//
//
//
//                        for (int k = 0; k < wordCount; k++) {
//                            String word = words[k];
//
//                            if (word.toLowerCase().startsWith(prefixString)) {
//                                newValues.add(h);
//                                break;
//                            }
//                        }
////                        }
//                    }

                    if (mUnfilteredData.get(i).get("name").toLowerCase().contains(prefixString)) {
                        newValues.add(mUnfilteredData.get(i));

                    }

                }

                results.values = newValues;
                results.count = newValues.size();
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            //noinspection unchecked
            mData = (List<Map<String, String>>) results.values;
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }
}