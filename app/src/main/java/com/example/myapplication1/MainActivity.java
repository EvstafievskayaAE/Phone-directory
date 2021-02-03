package com.example.myapplication1;

import android.os.Bundle;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity
        implements SearchView.OnQueryTextListener, SwipeRefreshLayout.OnRefreshListener {

    private DBHelper dbHelper;

    private List<Person> personsList;

    private RecyclerView recyclerView;
    public SearchView searchView;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    public RVAdapter adapter;
    private AddPersonFragment addPersonFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(this);

        mSwipeRefreshLayout = findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_dark,
                android.R.color.holo_blue_light, android.R.color.holo_green_light,
                android.R.color.holo_green_light);

        dbHelper = new DBHelper(this);
        personsList = dbHelper.getAllData();

        adapter = new RVAdapter(this, personsList);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        FloatingActionButton fab= findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(v -> {
            addPersonFragment = new AddPersonFragment();
            addPersonFragment.show(getSupportFragmentManager(), "AddPerson");
        });
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    /**Фильтрация карточек по параметрам поиска*/
    @Override
    public boolean onQueryTextChange(String newText) {
        String text = newText;
        if (personsList.size()>0)
            adapter.searchFilter(text);
        return false;
    }

    /**Обновление списка контактов по swipe*/
    @Override
    public void onRefresh() {
        personsList = dbHelper.getAllData();
        adapter.updateData(personsList);
        mSwipeRefreshLayout.setRefreshing(false);
    }
}
