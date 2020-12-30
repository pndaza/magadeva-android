package mm.pndaza.maghadeva.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import mm.pndaza.maghadeva.R;
import mm.pndaza.maghadeva.model.Category;
import mm.pndaza.maghadeva.model.SubCategory;
import mm.pndaza.maghadeva.utils.MDetect;

public class CategoryAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Object> list;

    private static final int SUBCATEGORY_ITEM = 0;
    private static final int CATEGORY_ITEM = 1;

    public CategoryAdapter(Context context, ArrayList<Object> list) {

        this.context = context;
        this.list = list;
    }

    @Override
    public int getItemViewType(int position) {
        if (list.get(position) instanceof SubCategory){
            return SUBCATEGORY_ITEM;
        } else {
            return CATEGORY_ITEM;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getCount() {
        return list.size(); //returns total of items in the list
    }

    @Override
    public Object getItem(int position) {
        return list.get(position); //returns list item at the specified position
    }

    @Override
    public long getItemId(int i) {
        return i;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null) {
            switch (getItemViewType(position)){
                case SUBCATEGORY_ITEM:
                    convertView = LayoutInflater.from(context).inflate(R.layout.book_list_item, parent, false);

                    break;

                case CATEGORY_ITEM:
                    convertView = LayoutInflater.from(context).inflate(R.layout.book_list_header, parent, false);
                    break;
            }

        }


        switch (getItemViewType(position)){
            case SUBCATEGORY_ITEM:

                // Lookup view for data population
                TextView tvName = convertView.findViewById(R.id.tv_list_item);
                // Populate the data into the template view using the data object
                tvName.setText(MDetect.getDeviceEncodedText(((SubCategory)list.get(position)).getName()));
                break;

            case CATEGORY_ITEM:

                // Lookup view for data population
                TextView tvHeader = convertView.findViewById(R.id.tv_list_header);
                // Populate the data into the template view using the data object
                tvHeader.setText(MDetect.getDeviceEncodedText(((Category)list.get(position)).getName()));

                break;
        }

        // Return the completed view to render on screen
        return convertView;
    }

}
