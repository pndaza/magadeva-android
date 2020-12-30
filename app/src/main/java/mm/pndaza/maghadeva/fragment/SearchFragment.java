package mm.pndaza.maghadeva.fragment;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import mm.pndaza.maghadeva.R;
import mm.pndaza.maghadeva.adapter.SearchAdapter;
import mm.pndaza.maghadeva.db.DBOpenHelper;
import mm.pndaza.maghadeva.model.Search;
import mm.pndaza.maghadeva.utils.MDetect;
import mm.pndaza.maghadeva.utils.Rabbit;


public class SearchFragment extends Fragment {

    public interface OnSearchItemClickListener {
        void onSearchItemClick(int pageNumber, String queryWord);
    }

    private OnSearchItemClickListener callbackListener;
    private ArrayList<Search> searchResult = new ArrayList<>();
    private SearchAdapter adapter;
    private static Context context;
    private String queryWord;

    private TextView emptyInfoView;


//    private static final String TAG = "SearchFragment";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        getActivity().setTitle(MDetect.getDeviceEncodedText("ရှာဖွေရေး"));
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = view.getContext();
        emptyInfoView = view.findViewById(R.id.empty_info);
        RecyclerView recyclerView = view.findViewById(R.id.search_result);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));

        SearchView searchInput = view.findViewById(R.id.search_input);
        searchInput.setQueryHint(MDetect.getDeviceEncodedText("ရှာလိုသောစကားလုံးကို ရိုက်ထည့်ပါ"));
        searchInput.setFocusable(true);
        searchInput.setIconified(false);
//        searchInput.requestFocusFromTouch();
        searchInput.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // todo search

                if (query.length() > 0) {
                    if (!MDetect.isUnicode()) {
                        query = Rabbit.zg2uni(query);
                    }
                    queryWord = query;
                    adapter = new SearchAdapter(searchResult, queryWord);
                    recyclerView.setAdapter(adapter);

                    adapter.setOnClickListener(view1 -> {
                        RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) view1.getTag();
                        int position = viewHolder.getAdapterPosition();
                        int verseNumber = searchResult.get(position).getVerseNumber();
//                Log.d("pageNumber" , ""+pageNumber);
                        callbackListener.onSearchItemClick(verseNumber, queryWord);
                    });


                    new searchIt().execute(query);
                    searchInput.clearFocus();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (searchResult.size() > 0) {
                    searchResult.clear();
                    adapter.notifyDataSetChanged();
                }
                emptyInfoView.setText("");
                return false;
            }
        });

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            callbackListener = (OnSearchItemClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implemented OnSearchItemClickListener");

        }
    }

    public class searchIt extends AsyncTask<String, Integer, Void> {

        @Override
        protected Void doInBackground(String... queries) {

            String query = queries[0];
            searchResult.addAll(DBOpenHelper.getInstance(context).search(query));
            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            adapter.notifyDataSetChanged();

            if (searchResult.size() < 1) {
                emptyInfoView.setText(MDetect.getDeviceEncodedText("ရှာမတွေ့ပါ"));
            }
        }
    }

}
