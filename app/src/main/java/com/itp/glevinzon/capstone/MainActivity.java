package com.itp.glevinzon.capstone;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.itp.glevinzon.capstone.api.CapstoneApi;
import com.itp.glevinzon.capstone.api.CapstoneService;
import com.itp.glevinzon.capstone.models.Datum;
import com.itp.glevinzon.capstone.models.Equations;
import com.itp.glevinzon.capstone.utils.PaginationAdapterCallback;
import com.itp.glevinzon.capstone.utils.PaginationScrollListener;

import java.util.List;
import java.util.concurrent.TimeoutException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements PaginationAdapterCallback {
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
    private boolean isLastPage = false;
    private int TOTAL_PAGES = 1;
    private int COUNT = 15;
    private int currentPage = PAGE_START;

    private CapstoneService equationService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFirstPage();
            }
        });

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

                List<Datum> data = fetchResults(response);
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

                List<Datum> data = fetchResults(response);
                adapter.addAll(data);

                if (currentPage != TOTAL_PAGES) adapter.addLoadingFooter();
                else isLastPage = true;
            }

            @Override
            public void onFailure(Call<Equations> call, Throwable t) {
                t.printStackTrace();
                adapter.showRetry(true);
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

    // Helpers -------------------------------------------------------------------------------------

    /**
     * @param throwable to determine and display appropriate error saying why call failed
     */
    private void showErrorView(Throwable throwable) {
        if (errorLayout.getVisibility() == View.GONE) {
            errorLayout.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);

            // display appropriate error message
            // Handling 3 generic fail cases.

            if (!isNetworkConnected()) {
                txtError.setText(R.string.error_msg_no_internet);
            } else {
                if (throwable instanceof TimeoutException) {
                    txtError.setText(R.string.error_msg_timeout);
                } else {
                    txtError.setText(R.string.error_msg_unknown);
                }
            }
        }
    }


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
}
