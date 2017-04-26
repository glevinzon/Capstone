package com.itp.glevinzon.capstone;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.itp.glevinzon.capstone.api.CapstoneApi;
import com.itp.glevinzon.capstone.api.CapstoneService;
import com.itp.glevinzon.capstone.models.Datum;
import com.itp.glevinzon.capstone.models.Equations;
import com.itp.glevinzon.capstone.utils.PaginationAdapterCallback;
import com.itp.glevinzon.capstone.utils.PaginationScrollListener;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import co.mobiwise.library.InteractivePlayerView;
import co.mobiwise.library.OnActionClickedListener;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements PaginationAdapterCallback, OnActionClickedListener, ItemClickListener, MediaPlayer.OnCompletionListener {
    private static final String TAG = "MainActivity";

    PaginationAdapter adapter;
    LinearLayoutManager linearLayoutManager;

    RecyclerView rv;
    ProgressBar progressBar;
    LinearLayout errorLayout;
    Button btnRetry;
    TextView txtError;

    private static final int PAGE_START = 1;
    private boolean isLoading = false;
    private boolean isLastPage = true;
    private int TOTAL_PAGES = 1;
    private int COUNT = 999;
    private int currentPage = PAGE_START;

    private CapstoneService equationService;

    //toolbar
    private CollapsingToolbarLayout collapsingToolbar;
    private AppBarLayout appBarLayout;

    private Menu collapsedMenu;
    private boolean appBarExpanded = true;

    private List<Datum> data;

    private String audioUrl = "";

    private String eqId = "999";

    MediaPlayer mediaPlayer = null;
    private int duration = 1;

    private FloatingActionButton fab;
    private InteractivePlayerView mInteractivePlayerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
//            String name = extras.getString("name");
//            String note = extras.getString("note");
            eqId = extras.getString("eqId");
            audioUrl = extras.getString("audioUrl");
        }
        Toast.makeText(this, "Please wait a while to process audio.", Toast.LENGTH_SHORT).show();
        Toast.makeText(this, "Source: " + audioUrl, Toast.LENGTH_SHORT).show();
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.reset();

        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource(audioUrl);
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, e.getMessage());
        }
        mediaPlayer.setOnPreparedListener(mp -> {
            int totalDuration = mp.getDuration();
            String s = String.format("%s", TimeUnit.MILLISECONDS.toSeconds(totalDuration));
            duration = Integer.parseInt(s);
            Log.d(TAG, "GLEVINZON WAS HERE! : " + duration);
            Toast.makeText(this, "Audio is ready. Press the play button.", Toast.LENGTH_SHORT).show();
        });
        mediaPlayer.prepareAsync();

        mInteractivePlayerView = (InteractivePlayerView) findViewById(R.id.interactivePlayerView);
        mInteractivePlayerView.setMax(duration);
        mInteractivePlayerView.setProgress(0);
        mInteractivePlayerView.setOnActionClickedListener(this);

        fab = (FloatingActionButton) findViewById(R.id.control);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mInteractivePlayerView.isPlaying()) {
                    mediaPlayer.start();
                    mInteractivePlayerView.setMax(duration);
                    mInteractivePlayerView.start();
                    fab.setImageResource(R.drawable.ic_action_pause);
                } else {
                    mInteractivePlayerView.stop();
                    mediaPlayer.pause();
                    fab.setImageResource(R.drawable.ic_action_play);
                }
            }
        });

        final Toolbar toolbar = (Toolbar) findViewById(R.id.anim_toolbar);
//        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        appBarLayout = (AppBarLayout) findViewById(R.id.appbar);

        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(getString(R.string.android_relevant_results));

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
                R.drawable.header);

        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            @SuppressWarnings("ResourceType")
            @Override
            public void onGenerated(Palette palette) {
                int vibrantColor = palette.getVibrantColor(R.color.primary_500);
                collapsingToolbar.setContentScrimColor(vibrantColor);
                collapsingToolbar.setStatusBarScrimColor(R.color.black_trans80);
            }
        });


        rv = (RecyclerView) findViewById(R.id.main_recycler);
        progressBar = (ProgressBar) findViewById(R.id.main_progress);
        errorLayout = (LinearLayout) findViewById(R.id.error_layout);
        btnRetry = (Button) findViewById(R.id.error_btn_retry);
        txtError = (TextView) findViewById(R.id.error_txt_cause);

        adapter = new PaginationAdapter(this);

        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rv.setLayoutManager(linearLayoutManager);

        rv.setItemAnimator(new DefaultItemAnimator());

        rv.setAdapter(adapter);

        adapter.setClickListener(this);

        rv.addOnScrollListener(new PaginationScrollListener(linearLayoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage += 1;

//                loadNextPage();
            }

            @Override
            public int getTotalPageCount() {
                return TOTAL_PAGES;
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });

        equationService = CapstoneApi.getClient().create(CapstoneService.class);

        loadFirstPage();

        btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFirstPage();
            }
        });

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                Log.d(MainActivity.class.getSimpleName(), "onOffsetChanged: verticalOffset: " + verticalOffset);

                //  Vertical offset == 0 indicates appBar is fully expanded.
                if (Math.abs(verticalOffset) > 200) {
                    appBarExpanded = false;
                    invalidateOptionsMenu();
                } else {
                    appBarExpanded = true;
                    invalidateOptionsMenu();
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }

    }

    @Override
    public void onClick(View view, int position) {
        final Datum result = data.get(position);
        Intent i = new Intent(this, MainActivity.class);
//        i.putExtra("name", result.getName());
        i.putExtra("eqId", result.getId() + "");
        i.putExtra("audioUrl", result.getAudioUrl());
        Log.d(TAG, result.getName());
        startActivity(i);
    }

    @Override
    public void onActionClicked(int id) {
        switch (id) {
            case 1:
                break;
            case 2:
                break;
            case 3:
                mediaPlayer.isLooping();
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (collapsedMenu != null
                && (!appBarExpanded || collapsedMenu.size() != 1)) {
            //collapsed
            collapsedMenu.add("Add")
                    .setIcon(R.drawable.ic_action_play)
                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        } else {
            //expanded
        }
        return super.onPrepareOptionsMenu(collapsedMenu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        collapsedMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_settings:
                return true;
        }
        if (item.getTitle() == "Add") {
            if (!mInteractivePlayerView.isPlaying()) {
                mediaPlayer.start();
                mInteractivePlayerView.setMax(duration);
                mInteractivePlayerView.start();
                fab.setImageResource(R.drawable.ic_action_pause);
            } else {
                mInteractivePlayerView.stop();
                mediaPlayer.pause();
                fab.setImageResource(R.drawable.ic_action_play);
            }
//            Toast.makeText(this, "clicked add", Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }

    private void loadFirstPage() {
        Log.d(TAG, "loadFirstPage: ");

        // To ensure list is visible when retry button in error view is clicked
        hideErrorView();

        callEquationsApi().enqueue(new Callback<Equations>() {
            @Override
            public void onResponse(Call<Equations> call, Response<Equations> response) {
                // Got data. Send it to adapter

                hideErrorView();

                data = fetchResults(response);
                Log.d(TAG, data + " Glevinzon");
                progressBar.setVisibility(View.GONE);
                adapter.addAll(data);

                TOTAL_PAGES = fetchLastPage(response);

                if (currentPage != TOTAL_PAGES || currentPage < TOTAL_PAGES) {
                    adapter.addLoadingFooter();
                } else {
                    isLastPage = true;
                    adapter.removeLoadingFooter();
                }
            }

            @Override
            public void onFailure(Call<Equations> call, Throwable t) {
                t.printStackTrace();
                showErrorView(t);
            }
        });

    }

    /**
     * @param response extracts List<{@link Datum>} from response
     * @return
     */
    private List<Datum> fetchResults(Response<Equations> response) {
        Equations equations = response.body();
        return equations.getData();
    }

    private Integer fetchLastPage(Response<Equations> response) {
        Equations equations = response.body();
        return equations.getLastPage();
    }

    private void loadNextPage() {
        Log.d(TAG, "loadNextPage: " + currentPage);

        callEquationsApi().enqueue(new Callback<Equations>() {
            @Override
            public void onResponse(Call<Equations> call, Response<Equations> response) {
                adapter.removeLoadingFooter();
                isLoading = false;

                data = fetchResults(response);
                adapter.addAll(data);

                if (currentPage != TOTAL_PAGES) adapter.addLoadingFooter();
                else isLastPage = true;
            }

            @Override
            public void onFailure(Call<Equations> call, Throwable t) {
                t.printStackTrace();
                adapter.showRetry(true, fetchErrorMessage(t));
            }
        });
    }

    /**
     * Performs a Retrofit call to the top rated movies API.
     * Same API call for Pagination.
     * As {@link #currentPage} will be incremented automatically
     * by @{@link PaginationScrollListener} to load next page.
     */
    private Call<Equations> callEquationsApi() {
        return equationService.getRelated(
                eqId,
                1,
                COUNT
        );
    }

    @Override
    public void retryPageLoad() {
//        loadNextPage();
    }

    /**
     * @param throwable required for {@link #fetchErrorMessage(Throwable)}
     * @return
     */
    private void showErrorView(Throwable throwable) {

        if (errorLayout.getVisibility() == View.GONE) {
            errorLayout.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);

            txtError.setText(fetchErrorMessage(throwable));
        }
    }

    /**
     * @param throwable to identify the type of error
     * @return appropriate error message
     */
    private String fetchErrorMessage(Throwable throwable) {
        String errorMsg = getResources().getString(R.string.error_msg_unknown);

        if (!isNetworkConnected()) {
            errorMsg = getResources().getString(R.string.error_msg_no_internet);
        } else if (throwable instanceof TimeoutException) {
            errorMsg = getResources().getString(R.string.error_msg_timeout);
        }

        return errorMsg;
    }

    // Helpers -------------------------------------------------------------------------------------

    private void hideErrorView() {
        if (errorLayout.getVisibility() == View.VISIBLE) {
            errorLayout.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Remember to add android.permission.ACCESS_NETWORK_STATE permission.
     *
     * @return
     */
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        //Invoked when playback of a media source has completed.
        fab.setImageResource(R.drawable.ic_action_play);
    }

}
