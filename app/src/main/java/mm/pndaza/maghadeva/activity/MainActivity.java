package mm.pndaza.maghadeva.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import mm.pndaza.maghadeva.R;
import mm.pndaza.maghadeva.fragment.BookmarkFragment;
import mm.pndaza.maghadeva.fragment.HomeFragment;
import mm.pndaza.maghadeva.fragment.SearchFragment;
import mm.pndaza.maghadeva.utils.MDetect;

public class MainActivity extends AppCompatActivity implements
BookmarkFragment.OnBookmarkItemClickListener,
SearchFragment.OnSearchItemClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setSupportActionBar(findViewById(R.id.toolbar));
        MDetect.init(this);

        setTitle(MDetect.getDeviceEncodedText(getString(R.string.app_name_mm)));

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if( savedInstanceState == null) {
            fragmentTransaction.replace(R.id.fragment_layout, new HomeFragment());
            fragmentTransaction.commit();
        }

        BottomNavigationView navView = findViewById(R.id.navigation);
        navView.setOnNavigationItemSelectedListener(item -> {
            Fragment selectedFragment;
            switch (item.getItemId()) {
                case R.id.navigation_bookmark:
                    selectedFragment = new BookmarkFragment();
                    break;
                case R.id.navigation_search:
                    selectedFragment = new SearchFragment();
                    break;
                default:
                    selectedFragment = new HomeFragment();
                    break;
            }

            FragmentTransaction fragmentTransaction2 = getSupportFragmentManager().beginTransaction();
            fragmentTransaction2.replace(R.id.fragment_layout, selectedFragment);
            fragmentTransaction2.commit();

            return true;
        });
    }

    @Override
    public void onBookmarkItemClick(int verseNumber) {

        startReadBookActivity( verseNumber, null);
    }

    private void startReadBookActivity(int verseNumber, String queryWord){
        Intent intent = new Intent(this, ReadBookActivity.class);
        intent.putExtra("verse_number", verseNumber);
        intent.putExtra("query_word", queryWord);
        startActivity(intent);
    }

    @Override
    public void onSearchItemClick(int verseNumber, String queryWord) {
        startReadBookActivity( verseNumber, queryWord);
    }
}
