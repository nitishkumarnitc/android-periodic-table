package com.frozendevs.periodictable.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.frozendevs.periodictable.R;
import com.frozendevs.periodictable.helper.Database;
import com.frozendevs.periodictable.model.ElementListItem;
import com.frozendevs.periodictable.model.adapter.ElementsAdapter;

public class ElementsFragment extends Fragment {

    private ElementsAdapter mAdapter;
    private ListView mListView;
    private View mEmptyView;

    private String mSearchQuery;

    private class LoadData extends AsyncTask<Void, Void, ElementListItem[]> {

        @Override
        protected ElementListItem[] doInBackground(Void... params) {
            return Database.getInstance(getActivity()).getElementListItems();
        }

        @Override
        protected void onPostExecute(ElementListItem[] result) {
            mAdapter.setItems(result);

            mAdapter.notifyDataSetChanged();

            mListView.setEmptyView(mEmptyView);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
        setRetainInstance(true);

        mAdapter = new ElementsAdapter(getActivity());

        new LoadData().execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.elements_list_fragment, container, false);

        mEmptyView = rootView.findViewById(R.id.empty_elements_list);

        mListView = (ListView) rootView.findViewById(R.id.elements_list);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(mAdapter);
        mListView.setEmptyView(!mAdapter.isEmpty() ? mEmptyView :
                rootView.findViewById(R.id.progress_bar));

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.elements_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);

        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setQueryHint(getString(R.string.search_query_hint));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mSearchQuery = query;

                searchView.clearFocus();

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (ViewCompat.isLaidOut(searchView)) {
                    mSearchQuery = newText;
                }

                mAdapter.filter(newText);

                return true;
            }
        });

        MenuItemCompat.setOnActionExpandListener(searchItem,
                new MenuItemCompat.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionExpand(MenuItem item) {
                        if (mSearchQuery == null) {
                            mSearchQuery = "";
                        }

                        return true;
                    }

                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem item) {
                        mSearchQuery = null;

                        mAdapter.clearFilter();

                        return true;
                    }
                });

        if (mSearchQuery != null) {
            MenuItemCompat.expandActionView(searchItem);

            searchView.setQuery(mSearchQuery, false);
        }

        super.onCreateOptionsMenu(menu, inflater);
    }
}
