package com.alpha.sound_recorder_app.ui;

import android.app.ActionBar;
import android.app.ListActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.alpha.sound_recorder_app.R;
import com.alpha.sound_recorder_app.dao.RecordDao;

public class SearchActivity extends ListActivity {

    private ActionBar bar;
    TextView resultTV;
    RecordDao recordDao;
    private SimpleCursorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        bar = getActionBar();
        bar.setDisplayHomeAsUpEnabled(true);

        resultTV = (TextView) findViewById(R.id.resultTV);

        recordDao = new RecordDao(this);

        adapter = new SimpleCursorAdapter(this,R.layout.record_list_item,null,new String[]{"_id","name","createTime","length"},new int[]{R.id._idItem,R.id.nameItem,R.id.createTimeItem,R.id.lengthItem});
        setListAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);
        // 搜索
        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search)
                .getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                //TODO 暂时显示到本界面
                resultTV.setText(query);
                adapter.changeCursor(recordDao.findRecordByName(query));
                // 跳转到搜索结果界面
//                Intent intent = new Intent(SearchActivity.this, SearchActivity.class);
//                intent.putExtra("str", query);
//                startActivity(intent);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                resultTV.setText(newText);
                adapter.changeCursor(recordDao.findRecordByName(newText));
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
