package mm.pndaza.maghadeva.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import mm.pndaza.maghadeva.R;
import mm.pndaza.maghadeva.model.Search;
import mm.pndaza.maghadeva.utils.MDetect;
import mm.pndaza.maghadeva.utils.MyanNumber;


public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    private ArrayList<Search> searchResults;
    private String queryWord;
    private View.OnClickListener onClickListener;

    public SearchAdapter(ArrayList<Search> searchResults, String queryWord) {
        this.searchResults = searchResults;
        this.queryWord = queryWord;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        // reuse layout because layout are same
        View wordListItemView = inflater.inflate(R.layout.searchlist_row_item, parent, false);
        return new ViewHolder(wordListItemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Search search = searchResults.get(position);
        int verseNumber = search.getVerseNumber();
        String verseBrief = search.getVerseBrief();
        holder.tvVerseNumber.setText(MDetect.getDeviceEncodedText("လင်္ကာပိုဒ်ရေနံပါတ် - "
                + MyanNumber.toMyanmar(verseNumber)));
//        holder.tvVerseBrief.setText(verseBrief);
        Log.d("searchAdapter", "verseNumber " + verseNumber);
        holder.tvVerseBrief.setText(getHighLightedString(getVerseBrief(verseBrief, queryWord),queryWord));
    }

    @Override
    public int getItemCount() {
        return searchResults.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvVerseNumber;
        TextView tvVerseBrief;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvVerseNumber = itemView.findViewById(R.id.tv_verseNumber);
            tvVerseBrief = itemView.findViewById(R.id.tv_verseBrief);

            itemView.setTag(this);
            itemView.setOnClickListener(onClickListener);
        }
    }

    public void setOnClickListener(View.OnClickListener clickListener) {
        onClickListener = clickListener;
    }

    private SpannableString getHighLightedString(String brief, String query) {
        brief = MDetect.getDeviceEncodedText(brief);
        query = MDetect.getDeviceEncodedText(query);
        int start_index = brief.indexOf(query);
        int end_index = start_index + query.length();
        SpannableString highlightedText = new SpannableString(brief);
        // highlight query words
        // set foreground color for query words
        if (start_index != -1) {
            highlightedText.setSpan(
                    new ForegroundColorSpan(Color.WHITE), start_index, end_index,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            );
            // set background color for query words
            highlightedText.setSpan(
                    new BackgroundColorSpan(Color.MAGENTA), start_index, end_index,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            );
        }
        return highlightedText;
    }

    private String getVerseBrief(String verseContent, String queryWord){

        int length = verseContent.length();
        int startIndexOfQuery = verseContent.indexOf(queryWord);
        int endIndexOfQuery = startIndexOfQuery + queryWord.length();
        int briefCharCount = 65;
        int counter = 1;

        while (startIndexOfQuery - counter >= 0 && counter < briefCharCount) {
            counter++;
        }
        int startIndexOfBrief = startIndexOfQuery - (counter - 1);

        counter = 1; //reset counter
        while (endIndexOfQuery + counter < length && counter < briefCharCount) {
            counter++;
        }
        int endIndexOfBrief = endIndexOfQuery + (counter - 1);
        String verseBrief = verseContent.substring(startIndexOfBrief, endIndexOfBrief);
        int indexOfQuery = verseBrief.indexOf(queryWord);
        int firstIndexOfParda = verseBrief.indexOf("၊") + 1;
        if( firstIndexOfParda > indexOfQuery){
            firstIndexOfParda = indexOfQuery - 1;
        }
        int lastIndexOfParda = verseBrief.lastIndexOf("၊");
        if( lastIndexOfParda < indexOfQuery){
            lastIndexOfParda = verseBrief.length();
        }
/*
        Log.d("searchAdapter", "startIndexOfBrief - " + startIndexOfBrief);
        Log.d("searchAdapter", "endIndexOfBrief - " + endIndexOfBrief);*/

        return verseBrief.substring(firstIndexOfParda+1, lastIndexOfParda);

    }

}
