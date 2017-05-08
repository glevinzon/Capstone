package com.itp.glevinzon.capstone;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.itp.glevinzon.capstone.models.Datum;
import com.itp.glevinzon.capstone.utils.PaginationAdapterCallback;

import java.util.ArrayList;
import java.util.List;

import katex.hourglass.in.mathlib.MathView;

/**
 * Created by glen on 3/31/17.
 */

public class PaginationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int ITEM = 0;
    private static final int LOADING = 1;
    private static final String TAG = "ADAPTER";
    private String BASE_URL_IMG = "http://apicapstone.herokuapp.com/images/extension-icons/not-applicable.png";
    private List<Datum> equationResults;
    private Context context;

    private ItemClickListener clickListener;

    private boolean isLoadingAdded = false;

    private boolean retryPageLoad = false;

    private PaginationAdapterCallback mCallback;

    private String errorMsg;

    public PaginationAdapter(Context context) {
        this.context = context;
        this.mCallback = (PaginationAdapterCallback) context;
        equationResults = new ArrayList<>();
    }

    public List<Datum> getEquations() {
        return equationResults;
    }

    public void setMovies(List<Datum> equationResults) {
        this.equationResults = equationResults;
    }

//
//    public PaginationAdapter(List<Datum> equationResults, int rowLayout, Context context) {
//        this.equationResults = equationResults;
//        this.rowLayout = rowLayout;
//        this.context = context;
//    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case ITEM:
                viewHolder = getViewHolder(parent, inflater);
                break;
            case LOADING:
                View v2 = inflater.inflate(R.layout.item_progress, parent, false);
                viewHolder = new LoadingVH(v2);
                break;
        }
        return viewHolder;
    }

    @NonNull
    private RecyclerView.ViewHolder getViewHolder(ViewGroup parent, LayoutInflater inflater) {
        RecyclerView.ViewHolder viewHolder;
        View v1 = inflater.inflate(R.layout.item_list, parent, false);
        viewHolder = new ViewHolder(v1);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        Datum data = equationResults.get(position);
        if (data.getAudioUrl() != null) {
            try {
                String s = data.getAudioUrl();
                String file = s.substring(s.lastIndexOf("."));
                String extension = file.substring(file.indexOf("."));

                if (extension.equals(".m4a")) {
                    BASE_URL_IMG = "http://apicapstone.herokuapp.com/images/extension-icons/041-mov.png";
                } else if (extension.equals(".3gp")) {
                    BASE_URL_IMG = "http://apicapstone.herokuapp.com/images/extension-icons/041-avi.png";
                } else if (extension.equals(".mp4")) {
                    BASE_URL_IMG = "http://apicapstone.herokuapp.com/images/extension-icons/041-mpg.png";
                } else if (extension.equals(".mp3")) {
                    BASE_URL_IMG = "http://apicapstone.herokuapp.com/images/extension-icons/041-mp3.png";
                } else if (extension.equals(".wav") ) {
                    BASE_URL_IMG = "http://apicapstone.herokuapp.com/images/extension-icons/041-wav.png";
                } else if (extension.equals(".mkv")) {
                    BASE_URL_IMG = "http://apicapstone.herokuapp.com/images/extension-icons/041-wma.png";
                } else if (extension.equals(".ogg") ) {
                    BASE_URL_IMG = "http://apicapstone.herokuapp.com/images/extension-icons/041-wav.png";
                }
            } catch (Exception e) {
                Log.d(TAG, "Glevinzon Dapal" + e.getMessage());
            }
        }

        switch (getItemViewType(position)) {
            case ITEM:
                final ViewHolder viewHolder = (ViewHolder) holder;

                viewHolder.mMovieTitle.setText(data.getName());


                viewHolder.mYear.setText(
                        data.getCreatedAt().substring(0, 4)  // we want the year only
                                + " | "
                                + "EN"
                );
//                viewHolder.mMovieDesc.setText(data.getNote());
                String laTex = "";
                String tex = "$ "+ laTex +" $";
                if(!laTex.isEmpty()){
                    viewHolder.mathView.setDisplayText(tex);
                }

                /**
                 * Using Glide to handle image loading.
                 * Learn more about Glide here:
                 * <a href="http://blog.grafixartist.com/image-gallery-app-android-studio-1-4-glide/" />
                 */
                Log.d(TAG, "Glide " + BASE_URL_IMG);
//                Glide
//                        .with(context)
//                        .load(BASE_URL_IMG)
//                        .listener(new RequestListener<String, GlideDrawable>() {
//                            @Override
//                            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
//                                // TODO: 08/11/16 handle failure
//                                viewHolder.mProgress.setVisibility(View.GONE);
//                                return false;
//                            }
//
//                            @Override
//                            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
//                                // image ready, hide progress now
//                                viewHolder.mProgress.setVisibility(View.GONE);
//                                return false;   // return false if you want Glide to handle everything else.
//                            }
//                        })
//                        .diskCacheStrategy(DiskCacheStrategy.ALL)   // cache both original & resized image
////                        .centerCrop()
//                        .crossFade()
//                        .into(viewHolder.mPosterImg);
                BASE_URL_IMG = "http://apicapstone.herokuapp.com/images/extension-icons/not-applicable.png";
                break;

            case LOADING:
                LoadingVH loadingVH = (LoadingVH) holder;

                if (retryPageLoad) {
                    loadingVH.mErrorLayout.setVisibility(View.VISIBLE);
                    loadingVH.mProgressBar.setVisibility(View.GONE);
                    loadingVH.mErrorTxt.setText(
                            errorMsg != null ?
                                    errorMsg :
                                    context.getString(R.string.error_msg_unknown));
                } else {
                    loadingVH.mErrorLayout.setVisibility(View.GONE);
                    loadingVH.mProgressBar.setVisibility(View.VISIBLE);
                }

                break;
        }

    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "GLEVINZON WAS HERE : " + equationResults);

        return equationResults == null ? 0 : equationResults.size();
    }


    public void setClickListener(ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        return (position == equationResults.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }


    /*
   Helpers
   _________________________________________________________________________________________________
    */

    public void add(Datum r) {
        equationResults.add(r);
        notifyItemInserted(equationResults.size() - 1);
    }

    public void addAll(List<Datum> moveResults) {
        for (Datum data : moveResults) {
            add(data);
        }
    }

    public void remove(Datum r) {
        int position = equationResults.indexOf(r);
        if (position > -1) {
            equationResults.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        isLoadingAdded = false;
        while (getItemCount() > 0) {
            remove(getItem(0));
        }
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }


    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new Datum());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;

        int position = equationResults.size() - 1;
        Datum data = getItem(position);

        if (data != null) {
            equationResults.remove(position);
            notifyItemRemoved(position);
        }
    }

    public Datum getItem(int position) {
        return equationResults.get(position);
    }

    /**
     * Displays Pagination retry footer view along with appropriate errorMsg
     *
     * @param show
     * @param errorMsg to display if page load fails
     */
    public void showRetry(boolean show, @Nullable String errorMsg) {
        retryPageLoad = show;
        notifyItemChanged(equationResults.size() - 1);

        if (errorMsg != null) this.errorMsg = errorMsg;
    }


   /*
   View Holders
   _________________________________________________________________________________________________
    */

    private int getMatColor(String typeColor) {
        int returnColor = Color.BLACK;
        int arrayId = this.context.getResources().getIdentifier("mdcolor_" + typeColor, "array", this.context.getPackageName());

        if (arrayId != 0) {
            TypedArray colors = this.context.getResources().obtainTypedArray(arrayId);
            int index = (int) (Math.random() * colors.length());
            returnColor = colors.getColor(index, Color.BLACK);
            colors.recycle();
        }
        return returnColor;
    }

    /**
     * Main list's content ViewHolder
     */
    protected class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mMovieTitle;
//        private TextView mMovieDesc;
        private TextView mYear; // displays "year | language"
//        private ImageView mPosterImg;
//        private ProgressBar mProgress;

        private MathView mathView;

        public ViewHolder(View itemView) {
            super(itemView);

            mMovieTitle = (TextView) itemView.findViewById(R.id.movie_title);
//            mMovieDesc = (TextView) itemView.findViewById(R.id.movie_desc);
            mathView = (MathView) itemView.findViewById(R.id.equationView);
            mYear = (TextView) itemView.findViewById(R.id.movie_year);
//            mPosterImg = (ImageView) itemView.findViewById(R.id.movie_poster);
//            mProgress = (ProgressBar) itemView.findViewById(R.id.movie_progress);

//            mPosterImg.setBackgroundColor(getMatColor("A100"));
            itemView.setTag(itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (clickListener != null) clickListener.onClick(view, getAdapterPosition());
        }
    }

    protected class LoadingVH extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ProgressBar mProgressBar;
        private ImageButton mRetryBtn;
        private TextView mErrorTxt;
        private LinearLayout mErrorLayout;

        public LoadingVH(View itemView) {
            super(itemView);

            mProgressBar = (ProgressBar) itemView.findViewById(R.id.loadmore_progress);
            mRetryBtn = (ImageButton) itemView.findViewById(R.id.loadmore_retry);
            mErrorTxt = (TextView) itemView.findViewById(R.id.loadmore_errortxt);
            mErrorLayout = (LinearLayout) itemView.findViewById(R.id.loadmore_errorlayout);

            mRetryBtn.setOnClickListener(this);
            mErrorLayout.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.loadmore_retry:
                case R.id.loadmore_errorlayout:

                    showRetry(false, null);
                    mCallback.retryPageLoad();

                    break;
            }
        }
    }
}
