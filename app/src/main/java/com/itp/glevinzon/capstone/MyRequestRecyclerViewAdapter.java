package com.itp.glevinzon.capstone;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.curioustechizen.ago.RelativeTimeTextView;
import com.itp.glevinzon.capstone.RequestFragment.OnListFragmentInteractionListener;
import com.itp.glevinzon.capstone.models.Requests.Datum;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Datum} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyRequestRecyclerViewAdapter extends RecyclerView.Adapter<MyRequestRecyclerViewAdapter.ViewHolder> {

    private final List<Datum> mValues;
    private final OnListFragmentInteractionListener mListener;

    public MyRequestRecyclerViewAdapter(List<Datum> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_request, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mIdView.setText(mValues.get(position).getCode().toUpperCase());
        holder.mContentView.setText(mValues.get(position).getName());

        Date localTime = null;
        try {
            localTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(mValues.get(position).getCreatedAt());
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }

        holder.v.setReferenceTime(localTime.getTime());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public final RelativeTimeTextView v;
        public Datum mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.id);
            mContentView = (TextView) view.findViewById(R.id.content);
            v = (RelativeTimeTextView )view.findViewById(R.id.timestamp);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }

    public void add(Datum r) {
        mValues.add(r);
        notifyItemInserted(mValues.size() - 1);
    }

    public void addAll(List<Datum> moveResults) {
        for (Datum data : moveResults) {
            add(data);
        }
    }

    public void remove(Datum r) {
        int position = mValues.indexOf(r);
        if (position > -1) {
            mValues.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        while (getItemCount() > 0) {
            remove(getItem(0));
        }
    }

    public Datum getItem(int position) {
        return mValues.get(position);
    }
}
