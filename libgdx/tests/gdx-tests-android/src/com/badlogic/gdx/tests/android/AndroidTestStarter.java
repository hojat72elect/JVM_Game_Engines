package com.badlogic.gdx.tests.android;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.badlogic.gdx.tests.BackTest;
import com.badlogic.gdx.tests.utils.GdxTests;

import java.util.List;

public class AndroidTestStarter extends ListActivity {
    SharedPreferences prefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!GdxTests.tests.contains(MatrixTest.class)) GdxTests.tests.add(MatrixTest.class);
        if (!GdxTests.tests.contains(APKExpansionTest.class))
            GdxTests.tests.add(APKExpansionTest.class);
        if (!GdxTests.tests.contains(BackTest.class)) GdxTests.tests.add(BackTest.class);
        List<String> testNames = GdxTests.getNames();
        setListAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, testNames));

        prefs = getSharedPreferences("libgdx-tests", Context.MODE_PRIVATE);
        getListView().setSelectionFromTop(prefs.getInt("index", 0), prefs.getInt("top", 0));
    }

    protected void onListItemClick(ListView listView, View view, int position, long id) {
        super.onListItemClick(listView, view, position, id);

        Editor editor = prefs.edit();
        editor.putInt("index", listView.getFirstVisiblePosition());
        editor.putInt("top", listView.getChildAt(0) == null ? 0 : listView.getChildAt(0).getTop());
        editor.apply();

        Object o = this.getListAdapter().getItem(position);
        String testName = o.toString();

        Bundle bundle = new Bundle();
        bundle.putString("test", testName);
        Intent intent = new Intent(this, GdxTestActivity.class);
        intent.putExtras(bundle);

        startActivity(intent);
    }
}
