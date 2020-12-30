package mm.pndaza.maghadeva.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import mm.pndaza.maghadeva.R;
import mm.pndaza.maghadeva.activity.ReadBookActivity;
import mm.pndaza.maghadeva.adapter.CategoryAdapter;
import mm.pndaza.maghadeva.db.DBOpenHelper;
import mm.pndaza.maghadeva.model.Category;
import mm.pndaza.maghadeva.model.SubCategory;
import mm.pndaza.maghadeva.utils.MDetect;
import mm.pndaza.maghadeva.utils.SharePref;

public class HomeFragment  extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        getActivity().setTitle(MDetect.getDeviceEncodedText(getString(R.string.app_name_mm)));
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initListView();

        FloatingActionButton favRecent = view.findViewById(R.id.fab_recent);
        favRecent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int recentVerseNumber = SharePref.getInstance(getContext()).getRecentVerseNumber();
                Intent intent = new Intent(getContext(), ReadBookActivity.class);
                intent.putExtra("verse_number", recentVerseNumber);
                getContext().startActivity(intent);
            }
        });
    }

    private void initListView() {

        DBOpenHelper db = DBOpenHelper.getInstance(getContext());

        ArrayList<Category> catList = db.getCategory();
        ArrayList<Object> contents = new ArrayList<>();
        Log.d("catList count ", catList.size() + "");

        for ( Category cat:catList ){
            contents.add(cat);
            contents.addAll(db.getSubCategory(cat.getId()));
        }

        CategoryAdapter categoryAdapter = new CategoryAdapter(getContext(), contents);
        final ListView listView = getView().findViewById(R.id.listView_books);
        listView.setAdapter(categoryAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> listview, View view, int position, long arg3) {
                if( listView.getItemAtPosition(position) instanceof SubCategory) {
                    SubCategory subCategory = (SubCategory) listview.getItemAtPosition(position);

                    int verseNumber = db.getFirstVerseNumber(subCategory.getId());

                    Intent intent = new Intent(getContext(), ReadBookActivity.class);
                    intent.putExtra("verse_number", verseNumber);

                    getContext().startActivity(intent);
                    }
            }
        });

    }


}
