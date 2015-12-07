package han.recyclerviewdemo;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ns.developer.tagview.entity.Tag;
import com.ns.developer.tagview.widget.TagCloudLinkView;

import java.util.ArrayList;
import java.util.List;

public class TagSelect extends AppCompatActivity {
    TagCloudLinkView view;
    int count;
    DataBaseHelper dataBaseHelper;
    ArrayList<String> tag_name = new ArrayList<>();

    @Override
    public void onBackPressed() {
        Intent intent1 = new Intent(TagSelect.this, MainActivity.class);
        startActivity(intent1);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_select);
        dataBaseHelper = new DataBaseHelper(this);
        Cursor cursor = dataBaseHelper.getAllNames();

        while (cursor.moveToNext()) {
            tag_name.add(cursor.getString(0));
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_tag);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        view = (TagCloudLinkView) findViewById(R.id.tag_view);
        Button button = (Button) findViewById(R.id.tag_add);
        Button button_cancel = (Button) findViewById(R.id.btn_cancel);
        Button button_confirm = (Button) findViewById(R.id.btn_confirm);
        final List<Tag> addlist = new ArrayList<>();
        final List<Tag> dellist = new ArrayList<>();
        final EditText editText = (EditText) findViewById(R.id.et_tag);
        for (int i = 0; i < tag_name.size(); i++) {
            view.add(new Tag(i, tag_name.get(i)));
        }
        count = tag_name.size();
        view.drawTags();
        editText.setOnKeyListener(new View.OnKeyListener() {

            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (keyCode == KeyEvent.KEYCODE_ENTER) {

                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

                    if (imm.isActive()) {

                        imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);

                    }

                    return true;

                }

                return false;

            }

        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String data = editText.getText().toString().trim();
                if (data.equals("")) {
                    Toast.makeText(getApplicationContext(), "You can not add a empty tag!!", Toast.LENGTH_SHORT).show();
                } else if (dataBaseHelper.getName(data).moveToNext()) {
                    Toast.makeText(getApplicationContext(), "You have already add this tag!!", Toast.LENGTH_SHORT).show();
                } else {
                    Tag tag = new Tag(count, data);
                    view.add(tag);
                    addlist.add(tag);
                    count++;
                    view.drawTags();
                }
                editText.setText("");
            }
        });

        view.setOnTagDeleteListener(new TagCloudLinkView.OnTagDeleteListener() {
            @Override
            public void onTagDeleted(Tag tag, int position) {
                dellist.add(tag);
            }
        });

        button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addlist.clear();
                Intent intent1 = new Intent(TagSelect.this, MainActivity.class);
                startActivity(intent1);
                finish();
            }
        });
        button_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dellist.size() > 0) {
                    for (int i = 0; i < dellist.size(); i++) {
                        dataBaseHelper.deleteName(dellist.get(i).getText());
                    }
                }
                if (addlist.size() > 0) {
                    for (int i = 0; i < addlist.size(); i++) {
                        dataBaseHelper.insertName(addlist.get(i).getText());
                    }
                }
                if (!dataBaseHelper.getAllNames().moveToNext()) {
                    Toast.makeText(getApplicationContext(), "You must add at least one tag", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent1 = new Intent(TagSelect.this, MainActivity.class);
                    startActivity(intent1);
                    addlist.clear();
                    dellist.clear();
                    finish();
                }
            }
        });


    }
}
