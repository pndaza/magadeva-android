package mm.pndaza.maghadeva.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import java.util.ArrayList;

import mm.pndaza.maghadeva.R;
import mm.pndaza.maghadeva.adapter.PageAdapter;
import mm.pndaza.maghadeva.db.DBOpenHelper;
import mm.pndaza.maghadeva.fragment.SettingDialogFragment;
import mm.pndaza.maghadeva.model.Verse;
import mm.pndaza.maghadeva.utils.MDetect;
import mm.pndaza.maghadeva.utils.Rabbit;
import mm.pndaza.maghadeva.utils.SharePref;


public class ReadBookActivity extends AppCompatActivity {

    private ArrayList<Verse> verses = new ArrayList<>();
    private PageAdapter pageAdapter;
    private Context context = null;

    private static ViewPager viewPager;
    private static LinearLayout control_bar;
    int currentVerseNumber = 1;
    private static DiscreteSeekBar seekBar;

    private static final int LAST_VERSE = 625;
    private static final String TAG = "ReadBook";

    private String queryWord;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_readbook);

        setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        MDetect.init(this);
        setTitle(MDetect.getDeviceEncodedText(getString(R.string.app_name_mm)));

        Intent intent = getIntent();
        int verseNumber = intent.getIntExtra("verse_number", 0);
        queryWord = intent.getStringExtra("query_word");
        currentVerseNumber = verseNumber;

        if( savedInstanceState != null){
            currentVerseNumber = savedInstanceState.getInt("current_verse_number");
        }


        initView();
        new LoadVerses().execute();

        setupSeek();
        setupSeekSync();


    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt("currentPage", viewPager.getCurrentItem() + 1 );

    }

    private void initView() {

        context = this;
        viewPager = findViewById(R.id.vpPager);
        control_bar = findViewById(R.id.control_bar);
        seekBar = findViewById(R.id.seedbar);
    }

    private void setupSeek() {

        seekBar.setMin(1);
        seekBar.setMax(LAST_VERSE);
        //if min value is not 1, something wrong with seekbar_progess_indicator
        if(currentVerseNumber != 0){
            seekBar.setProgress(currentVerseNumber - 1);
        } else {
            seekBar.setProgress(1 + 1);
            seekBar.setProgress(1);
        }

        seekBar.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {

            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {
                viewPager.setCurrentItem(seekBar.getProgress()-1);

            }
        });
    }


    private void setupSeekSync(){
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                seekBar.setProgress( 1 + i);
                SharePref.getInstance(context).setRecentVerseNumber( i + 1);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_reading, menu);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (item.getItemId()){
            case R.id.menu_addBookmark:
                addToBookmark(viewPager.getCurrentItem() + 1);
                break;
            case R.id.menu_setting:
                showSettingDialog();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    public class LoadVerses extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

                verses = DBOpenHelper.getInstance(context).getAllVerse();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            //highlight page
            if (queryWord != null) {
                if (queryWord.length() > 0) {
                    String verseContent = verses.get(currentVerseNumber - 1).getVerseContent();
                    String catName = verses.get(currentVerseNumber - 1).getFullCatName();
                    verseContent = verseContent.replaceAll(
                            queryWord, "<span class=\"highlight\">" + queryWord + "</span>");
                    verses.set(currentVerseNumber - 1, new Verse(currentVerseNumber , verseContent, catName));
                }
            }

            pageAdapter = new PageAdapter(context,verses);
            viewPager.setAdapter(pageAdapter);

            if( currentVerseNumber != 0) {
                viewPager.setCurrentItem(currentVerseNumber - 1);
            } else {
                viewPager.setCurrentItem(0 );
            }
        }
    }


    private void addToBookmark(int verseNumber){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context,R.style.AlertDialogTheme);

        String message = "မှတ်လိုသောစာသား ရိုက်ထည့်ပါ။";
        String comfirm = "သိမ်းမယ်";
        String cancel = "မလုပ်တော့ဘူး";
        if (!MDetect.isUnicode()) {
            message = Rabbit.uni2zg(message);
            comfirm = Rabbit.uni2zg(comfirm);
            cancel = Rabbit.uni2zg(cancel);
        }

        dialogBuilder.setMessage(message);
        final EditText input = new EditText(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        dialogBuilder.setMessage(message)
                .setView(input)
                .setCancelable(true)
                .setPositiveButton(comfirm,
                        (dialog, id) -> {
                    String note = input.getText().toString();
                            DBOpenHelper.getInstance(context).
                                    addToBookmark(verseNumber, note);
                        })
                .setNegativeButton(cancel, (dialog, id) -> {
                });
        dialogBuilder.show();
        input.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                input.post(() -> {
                    InputMethodManager inputMethodManager= (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.showSoftInput(input, InputMethodManager.SHOW_IMPLICIT);
                });
            }
        });
        input.requestFocus();

    }

    private void showSettingDialog(){
        FragmentManager fm = getSupportFragmentManager();
        SettingDialogFragment settingDialog = new SettingDialogFragment();
        settingDialog.show(fm, "Setting");
    }

}
