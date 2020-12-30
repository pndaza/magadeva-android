package mm.pndaza.maghadeva.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.LinearLayout;

import androidx.viewpager.widget.PagerAdapter;

import java.util.ArrayList;

import mm.pndaza.maghadeva.R;
import mm.pndaza.maghadeva.activity.ReadBookActivity;
import mm.pndaza.maghadeva.model.Verse;
import mm.pndaza.maghadeva.utils.MDetect;
import mm.pndaza.maghadeva.utils.MyanNumber;
import mm.pndaza.maghadeva.utils.SharePref;

public class PageAdapter extends PagerAdapter {

    private static final String TAG = "PageAdapter";
    Context context;
    LayoutInflater layoutInflater;
    ArrayList<Verse> pages;
    boolean bar_status;
    LinearLayout book_toolbar;

    private static String style;


    public PageAdapter(Context context, ArrayList<Verse> pages) {
        this.context = context;
        this.pages = pages;
        bar_status = true;
        layoutInflater = LayoutInflater.from(this.context);
        style = getStyle();
    }

    // Returns the number of pages to be displayed in the ViewPager.
    @Override
    public int getCount() {
        return pages.size();
    }

    // Returns true if a particular object (page) is from a particular page
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    // This method should create the page for the given position passed to it as an argument.
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        // Inflate the layout for the page
        View itemView = layoutInflater.inflate(R.layout.page, container, false);

        book_toolbar = ((ReadBookActivity) context).findViewById(R.id.control_bar);

        String fullCatName = pages.get(position).getFullCatName();
        String content = pages.get(position).getVerseContent();

        int pagenum = pages.get(position).getVerseNumber();
        String formatedContent = formatContent(fullCatName, content, pagenum);

        //find and populate data into webview
        WebView webView = itemView.findViewById(R.id.wv_page);
        webView.loadDataWithBaseURL("file:///android_asset/web/",
                formatedContent, "text/html", "UTF-8", null);

        // Add the page to the container
        container.addView(itemView);
        // Return the page
        return itemView;
    }

    // Removes the page from the container for the given position.
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    private String formatContent(String fullCatName, String content, int pageNumber) {

        StringBuilder sb = new StringBuilder();
        sb.append("<html>\n")
                .append("<head>\n")
                .append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"></meta>\n" )
                .append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n")
                .append(getStyle())
                .append("<body>\n")
                .append("<p class=\"cat\">" + MDetect.getDeviceEncodedText(fullCatName) + "</p>")
                .append("<hr>")
                .append("<p> <span class=\"vnum\">")
                .append(MyanNumber.toMyanmar(pageNumber) + "။   ။ </span>")
                .append(MDetect.getDeviceEncodedText(content))
                .append("</p>")
                .append("</body></html>");

        return sb.toString();
    }

    private String getStyle(){

        SharePref sharePref = SharePref.getInstance(context);
        float fontSize = sharePref.getPrefFontSize();
        boolean nightModeState = sharePref.getPrefNightModeState();
        StringBuilder styleBuilder = new StringBuilder();
        if(MDetect.isUnicode()){
            styleBuilder.append("<link rel='stylesheet' type='text/css' media='screen' href='unicode.css'>");
        } else {
            styleBuilder.append("<link rel='stylesheet' type='text/css' media='screen' href='zawgyi.css'>");
        }
        styleBuilder.append("<style>");
        if(nightModeState){
            styleBuilder.append("body { background-color: black; color: ivory; font-size: "
                                + fontSize + "em }" );
            } else {
            styleBuilder.append("body { background-color: white; color: black; font-size: " + fontSize + "em }");
        }
        styleBuilder.append(".cat{ color: brown }");
        styleBuilder.append(".vnum{ color: red }");
        styleBuilder.append(".highlight{ background-color: magenta; color: white }");
        styleBuilder.append("</style>");

        return styleBuilder.toString();
    }


}
