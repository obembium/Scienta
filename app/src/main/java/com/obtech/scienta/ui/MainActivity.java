package com.obtech.scienta.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.obtech.scienta.model.Movie.Movie;
import com.obtech.scienta.ui.adapter.MovieAdapter;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;
import com.obtech.scienta.R;
import com.obtech.scienta.network.ApiConstants;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements com.obtech.scienta.ui.adapter.MovieAdapter.ListClickListener, SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String PREF = "pref";
    @BindView(R.id.swipyrefreshlayout)
    SwipyRefreshLayout swipyrefreshlayout;
    @BindView(R.id.bottomNavigationView)
    BottomNavigationView bottomNavigationView;
    private com.obtech.scienta.ui.adapter.MovieAdapter movieAdapter;
    private SharedPreferences preferences;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    private String popular = ApiConstants.POPULAR_MOVIES;
    private String topRated = ApiConstants.TOP_RATED;
    @BindView(R.id.movieRecyclerView)
    RecyclerView movieRecyclerView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    private String apikey = ApiConstants.API_KEY;
    @BindView(R.id.coordinator_layout)
    CoordinatorLayout coordinatorLayout;
    private MainViewModel mainViewModel;
    public final static String LIST_STATE_KEY = "recycler_list_state";
    private Parcelable listState;
    private GridLayoutManager gridLayoutManager;
    boolean doubleBackToExitPressedOnce = false;
    int page = 0;
    SearchView searchView;
    ArrayList<com.obtech.scienta.model.Movie.Movie> movies = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);
        initViews(savedInstanceState);
        setNavigation();
        checkOrientation();
    }

    private void setNavigation() {
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.nav_popular:
                        setKey(getString(R.string.popular));
                        refresh();
                        return true;
                    case R.id.nav_search:
                        setKey(getString(R.string.top_rated));
                        refresh();
                        return true;
                    case R.id.nav_favourites:
                        setKey(getString(R.string.favorites));
                        refresh();
                        return true;
                }
                return false;
            }
        });
    }

    private void checkOrientation() {
        if (this.movieRecyclerView.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            gridLayoutManager = new GridLayoutManager(this, 2);
            movieRecyclerView.setLayoutManager(gridLayoutManager);
        } else if (this.movieRecyclerView.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            gridLayoutManager = new GridLayoutManager(this, 4);
            movieRecyclerView.setLayoutManager(gridLayoutManager);
        }
    }

    private void initViews(Bundle s) {
        gridLayoutManager = new GridLayoutManager(this, 2);
        movieRecyclerView.setLayoutManager(gridLayoutManager);
        movieRecyclerView.setHasFixedSize(true);
        movieAdapter = new MovieAdapter(getApplicationContext(), this);
        setSupportActionBar(toolbar);
        movieRecyclerView.setAdapter(movieAdapter);
        preferences = getApplicationContext().getSharedPreferences(PREF, Context.MODE_PRIVATE);
        preferences.registerOnSharedPreferenceChangeListener(this);
        if (isConnected()) {
            //  onSharedPreferenceChanged(preferences, getString(R.string.sort_by));
        } else {
            Snackbar.make(coordinatorLayout, "Check your network connection", Snackbar.LENGTH_LONG).show();
            toolbar.setTitle("Favorite movies");
        }

        swipyrefreshlayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {
                swipyrefreshlayout.setRefreshing(false);
            }
        });

        movieRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    Toast.makeText(MainActivity.this, "Loading more Content", Toast.LENGTH_SHORT);

                    Log.d("LOADINGSS", "onScrollStateChanged: Loading" + page);

                    onSharedPreferenceChanged(preferences, getString(R.string.sort_by));
                }
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(LIST_STATE_KEY, gridLayoutManager.onSaveInstanceState());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null)
            listState = savedInstanceState.getParcelable(LIST_STATE_KEY);
    }

    // press back twice to exit
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        setKey(getString(R.string.popular));
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, R.string.double_press_back, Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    private boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (connectivityManager != null) {
            networkInfo = connectivityManager.getActiveNetworkInfo();
        }
        return networkInfo != null && networkInfo.isConnected();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }

    //TODO: Add your Api key in the ApiConstants class
    private void loadMovies(String sort, String apiKey) {
        page = page + 1;
        mainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        mainViewModel.loadAllMovies(sort, apiKey, page).observe(this, new Observer<List<com.obtech.scienta.model.Movie.Movie>>() {
            @Override
            public void onChanged(@Nullable List<com.obtech.scienta.model.Movie.Movie> moviesList) {
                movies.addAll(moviesList);
                movieAdapter.setMovieItem(movies);
                if (listState != null) {
                    gridLayoutManager.onRestoreInstanceState(listState);
                }
            }
        });
    }

    //TODO: Add your Api key in the ApiConstants class
    private void loadSearchedMovies(String query, String apiKey) {
        page = page + 1;
        mainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        mainViewModel.loadSearchMovies(query, apiKey, page).observe(this, new Observer<List<com.obtech.scienta.model.Movie.Movie>>() {
            @Override
            public void onChanged(@Nullable List<com.obtech.scienta.model.Movie.Movie> moviesList) {
                movies.addAll(moviesList);
                movieAdapter.setMovieItem(movies);
                if (listState != null) {
                    gridLayoutManager.onRestoreInstanceState(listState);
                }
            }
        });
    }



    private void loadFavorites() {
        mainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        mainViewModel.getFavMovies().observe(this, new Observer<List<com.obtech.scienta.model.Movie.Movie>>() {
            @Override
            public void onChanged(@Nullable List<com.obtech.scienta.model.Movie.Movie> movies) {
                movieAdapter.setMovieItem(movies);
                progressBar.setVisibility(View.GONE);
                if (listState != null) {
                    gridLayoutManager.onRestoreInstanceState(listState);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.movie_menu, menu);

        getMenuInflater().inflate(R.menu.search_menu, menu);
        MenuItem search = menu.findItem(R.id.search);
        searchView = (SearchView) search.getActionView();


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override public boolean onQueryTextSubmit(String query) {
                movies = new ArrayList<>();
                page = 0;
                loadSearchedMovies(query, apikey);
                return query != null;
            }
            @Override public boolean onQueryTextChange(String query) {
                Log.d("onQueryTextChange", query);
                if (query.length() >= 4) {
                    movies = new ArrayList<>();
                    page = 0;
                    loadSearchedMovies(query, apikey);
                }else if(query.length()==0){
                    setKey(getString(R.string.popular));
                    refresh();
                }
                return query != null;
            }

        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                setKey(getString(R.string.popular));
                refresh();
                return false;
            }
        });

        searchView.clearFocus();

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_about:
               Intent intent = new Intent(this, AboutActivity.class);
                startActivity(intent);

                return true;

            case R.id.refresh:
                refresh();
                getPreference();
                progressBar.setVisibility(View.VISIBLE);
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void refresh() {
        movies = new ArrayList<>();
        page = 0;
        getPreference();
    }

    private void getPreference() {
        String key = getOrderValue();
        if (key.equals(getString(R.string.popular))) {
            loadMovies(popular, apikey);
            toolbar.setTitle("Popular movies");
        } else if (key.equals(getString(R.string.top_rated))) {
            loadMovies(topRated, apikey);
            toolbar.setTitle("Top rated movies");
        } else {
            toolbar.setTitle("Favorite movies");
            loadFavorites();
        }
    }

    private String getOrderValue() {
        String key = getString(R.string.sort_by);
        String value = getString(R.string.popular);
        return preferences.getString(key, value);
    }

    private void setKey(String value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(getString(R.string.sort_by), value);
        editor.apply();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.sort_by))) getPreference();
    }

    @Override
    public void onListClick(Movie movie) {
        Intent movieIntent = new Intent(this.getApplicationContext(), DetailActivity.class);
        movieIntent.putExtra(DetailActivity.EXTRA_VALUE, movie);
        startActivity(movieIntent);
    }

    @Override
    protected void onResume() {
        if (!isConnected()) {
            loadFavorites();
        } else {
            onSharedPreferenceChanged(preferences, getString(R.string.sort_by));
        }
        super.onResume();
    }
}
