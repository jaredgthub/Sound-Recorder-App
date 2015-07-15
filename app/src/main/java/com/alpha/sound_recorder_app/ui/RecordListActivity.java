package com.alpha.sound_recorder_app.ui;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;

import com.alpha.sound_recorder_app.R;
import com.alpha.sound_recorder_app.dao.Db;
import com.alpha.sound_recorder_app.dao.UserDao;
import com.alpha.sound_recorder_app.model.User;

public class RecordListActivity extends ListActivity {

    private SimpleCursorAdapter adapter;
    private EditText addUsername,addPassword;
    private Button addUserBtn;
    private Db db;
    private UserDao userDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_list);
        db = new Db(this);
        userDao = new UserDao(db);
        addUsername = (EditText) findViewById(R.id.addUsername);
        addPassword = (EditText) findViewById(R.id.addPassword);
        addUserBtn = (Button) findViewById(R.id.addUserBtn);
        //add user (save)
        addUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = new User();
                user.setUsername(addUsername.getText().toString());
                user.setPassword(addPassword.getText().toString());
                userDao.addUser(user);
                refreshListView();
            }
        });
        adapter = new SimpleCursorAdapter(this,R.layout.record_list_item,null,new String[]{"username","password"},new int[]{R.id.usernameItem,R.id.passwordItem});
        setListAdapter(adapter);
        //长按
        getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                new AlertDialog.Builder(RecordListActivity.this).setTitle("alert").setMessage("are you sure?")
                        .setPositiveButton("sure", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Cursor c = adapter.getCursor();
                        c.moveToPosition(position);
                        int itemId = c.getInt(c.getColumnIndex("_id"));
                        userDao.delUser(itemId);
                        refreshListView();
                    }
                }).setNegativeButton("cancel",null).show();
                //此次长按有效，返回true
                return true;
            }
        });
    }

    public void refreshListView(){
        adapter.changeCursor(userDao.getAllUser());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_record_list, menu);
        return true;
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
