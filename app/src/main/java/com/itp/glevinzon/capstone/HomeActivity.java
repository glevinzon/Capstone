package com.itp.glevinzon.capstone;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
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
import com.tbruyelle.rxpermissions.RxPermissions;

import net.gotev.speech.GoogleVoiceTypingDisabledException;
import net.gotev.speech.Speech;
import net.gotev.speech.SpeechDelegate;
import net.gotev.speech.SpeechRecognitionNotAvailable;
import net.gotev.speech.SpeechUtil;
import net.gotev.speech.TextToSpeechCallback;
import net.gotev.speech.ui.SpeechProgressView;

import java.util.List;
import java.util.concurrent.TimeoutException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity implements PaginationAdapterCallback, SpeechDelegate, SearchView.OnQueryTextListener, ItemClickListener {
    private SpeechProgressView progress;
    private LinearLayout speechLayout;
    private FloatingActionButton fab;
    private SearchView searchView;

    //pagination
    private static final String TAG = "HomeActivity";

    PaginationAdapter adapter;
    LinearLayoutManager linearLayoutManager;

    RecyclerView rv;
    ProgressBar progressBar;
    LinearLayout errorLayout;
    Button btnRetry;
    TextView txtError;

    private static final int PAGE_START = 1;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int TOTAL_PAGES = 1;
    private int currentPage = PAGE_START;

    private CapstoneService equationService;

    private List<Datum> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //pagination
        rv = (RecyclerView) findViewById(R.id.home_recycler);
        progressBar = (ProgressBar) findViewById(R.id.home_progress);
        errorLayout = (LinearLayout) findViewById(R.id.home_error_layout);
        btnRetry = (Button) findViewById(R.id.home_error_btn_retry);
        txtError = (TextView) findViewById(R.id.home_error_txt_cause);

        adapter = new PaginationAdapter(this);

        adapter.setClickListener(this);

        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rv.setLayoutManager(linearLayoutManager);

        rv.setItemAnimator(new DefaultItemAnimator());

        rv.setAdapter(adapter);

        rv.addOnScrollListener(new PaginationScrollListener(linearLayoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage += 1;

                loadNextPage();
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

        btnRetry.setOnClickListener(view -> loadFirstPage());

        //speech
        progress = (SpeechProgressView) findViewById(R.id.progress);
        speechLayout = (LinearLayout) findViewById(R.id.speech_layout);;

        int[] colors = {
                ContextCompat.getColor(this, R.color.pink),
                ContextCompat.getColor(this, R.color.black_trans80),
                ContextCompat.getColor(this, R.color.red),
                ContextCompat.getColor(this, R.color.yellow),
                ContextCompat.getColor(this, R.color.blue)
        };
        progress.setColors(colors);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(view -> onButtonClick());

        //onClick
}

    @Override
    public void onClick(View view, int position) {
        final Datum result = data.get(position);
        Intent i = new Intent(this, MainActivity.class);
        i.putExtra("name", result.getName());
        i.putExtra("note", result.getNote());
        i.putExtra("audioUrl", result.getAudioUrl());
        Log.d(TAG, result.getName());
        startActivity(i);
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
                progressBar.setVisibility(View.GONE);
                adapter.addAll(data);

                TOTAL_PAGES = fetchLastPage(response);

                if (currentPage <= TOTAL_PAGES) adapter.addLoadingFooter();
                else isLastPage = true;
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
        int COUNT = 15;
        return equationService.getEquations(
                "paginate",
                currentPage,
                COUNT
        );
    }

    @Override
    public void retryPageLoad() {
        loadNextPage();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        final MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);

        return true;
    }

    @Override
    public boolean onQueryTextChange(String query) {
//        final List<WordModel> filteredModelList = filter(mModels, query);
//        mAdapter.edit()
//                .replaceAll(filteredModelList)
//                .commit();
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    protected void onDestroy() {
        Speech.getInstance().unregisterDelegate();
    }

    private void onButtonClick() {
        if (Speech.getInstance().isListening()) {
            Speech.getInstance().stopListening();
        } else {
            RxPermissions.getInstance(this)
                    .request(Manifest.permission.RECORD_AUDIO)
                    .subscribe(granted -> {
                        if (granted) { // Always true pre-M
                            onRecordAudioPermissionGranted();
                        } else {
                            Toast.makeText(HomeActivity.this, R.string.permission_required, Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }

    private void onRecordAudioPermissionGranted() {
        fab.setVisibility(View.GONE);
        speechLayout.setVisibility(View.VISIBLE);
        errorLayout.setVisibility(View.GONE);
        try {
            Speech.getInstance().stopTextToSpeech();
            Speech.getInstance().startListening(progress, HomeActivity.this);

        } catch (SpeechRecognitionNotAvailable exc) {
            showSpeechNotSupportedDialog();

        } catch (GoogleVoiceTypingDisabledException exc) {
            showEnableGoogleVoiceTyping();
        }
    }

    private void onSpeakClick() {
        if (searchView.getQuery().toString().trim().isEmpty()) {
            Toast.makeText(this, R.string.input_something, Toast.LENGTH_LONG).show();
            return;
        }

        Speech.getInstance().say(searchView.getQuery().toString().trim(), new TextToSpeechCallback() {
            @Override
            public void onStart() {
                Toast.makeText(HomeActivity.this, "TTS onStart", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCompleted() {
                Toast.makeText(HomeActivity.this, "TTS onCompleted", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError() {
                Toast.makeText(HomeActivity.this, "TTS onError", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onStartOfSpeech() {
    }

    @Override
    public void onSpeechRmsChanged(float value) {
        //Log.d(getClass().getSimpleName(), "Speech recognition rms is now " + value +  "dB");
    }

    @Override
    public void onSpeechResult(String result) {

        fab.setVisibility(View.VISIBLE);
        speechLayout.setVisibility(View.GONE);
        searchView.setIconified(false);
        searchView.setQuery(result, false);

        if (result.isEmpty()) {
            Speech.getInstance().say(getString(R.string.repeat));
            searchView.setIconified(true);
        } else {
            Speech.getInstance().say(result);
        }
    }

    @Override
    public void onSpeechPartialResults(List<String> results) {
        searchView.setQuery("", false);
        for (String partial : results) {
            searchView.setQuery(searchView.getQuery() + partial + " ", false);
        }
    }

    private void showSpeechNotSupportedDialog() {
        DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    SpeechUtil.redirectUserToGoogleAppOnPlayStore(HomeActivity.this);
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    break;
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.speech_not_available)
                .setCancelable(false)
                .setPositiveButton(R.string.yes, dialogClickListener)
                .setNegativeButton(R.string.no, dialogClickListener)
                .show();
    }

    private void showEnableGoogleVoiceTyping() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.enable_google_voice_typing)
                .setCancelable(false)
                .setPositiveButton(R.string.yes, (dialogInterface, i) -> {
                    // do nothing
                })
                .show();
    }

}
