package mm.pndaza.maghadeva.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import mm.pndaza.maghadeva.model.Bookmark;
import mm.pndaza.maghadeva.model.Category;
import mm.pndaza.maghadeva.model.Search;
import mm.pndaza.maghadeva.model.SubCategory;
import mm.pndaza.maghadeva.model.Verse;

public class DBOpenHelper extends SQLiteOpenHelper {

    private  static DBOpenHelper sInstance;
    private static final String DATABASE_NAME = "magha.db";
    private static final int DATABASE_VERSION = 3;


    public static synchronized DBOpenHelper getInstance(Context context) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.

        if (sInstance == null) {
            sInstance = new DBOpenHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    private DBOpenHelper(Context context) {
        super(context, context.getFilesDir()+ "/databases/" + DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public ArrayList<Category> getCategory(){
        ArrayList<Category> catList = new ArrayList<>();
        Cursor cursor = getReadableDatabase().rawQuery(
                "SELECT id,name FROM category", null);
        if( cursor != null && cursor.moveToFirst()){
            do {
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String name = cursor.getString(cursor.getColumnIndex("name"));
                catList.add(new Category(id, name));
            } while (cursor.moveToNext());
        }
        return catList;
    }

    public ArrayList<SubCategory> getSubCategory(){
        ArrayList<SubCategory> subCatList = new ArrayList<>();
        Cursor cursor = getReadableDatabase().rawQuery(
                "SELECT id,name, cat_id FROM subcategory", null);
        if( cursor != null && cursor.moveToFirst()){
            do {
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String name = cursor.getString(cursor.getColumnIndex("name"));
                int cat_id = cursor.getInt(cursor.getColumnIndex("cat_id"));
                subCatList.add(new SubCategory(id, name, cat_id));
            } while (cursor.moveToNext());
        }
        return subCatList;
    }

    public ArrayList<SubCategory> getSubCategory(int cat_id){
        ArrayList<SubCategory> subCatList = new ArrayList<>();
        Cursor cursor = getReadableDatabase().rawQuery(
                "SELECT id,name FROM subcategory Where cat_id = " + cat_id , null);
        if( cursor != null && cursor.moveToFirst()){
            do {
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String name = cursor.getString(cursor.getColumnIndex("name"));
                subCatList.add(new SubCategory(id, name, cat_id));
            } while (cursor.moveToNext());
        }
        return subCatList;
    }

    public int getFirstVerseNumber(int subCatId){
        Cursor cursor = getReadableDatabase().rawQuery(
                "SELECT id FROM verses Where sub_cat_id = " + subCatId + " LIMIT 1", null);
        if( cursor != null && cursor.moveToFirst()){
                return cursor.getInt(cursor.getColumnIndex("id"));
        }
        return 0;
    }

    public ArrayList<Verse> getAllVerse(){

        ArrayList<Verse> verses = new ArrayList<>();
        ArrayList<Category> categories = getCategory();
        ArrayList<SubCategory> subCategories = getSubCategory();
        /*ArrayList<String> fullNames = new ArrayList<>();
        for (SubCategory subCategory: subCategories){
            fullNames
        }*/

        Cursor cursor = getReadableDatabase().rawQuery(
                "SELECT id,content, sub_cat_id FROM verses", null);
        if( cursor != null && cursor.moveToFirst()){
            do {
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String content = cursor.getString(cursor.getColumnIndex("content"));
                int subCabId = cursor.getInt(cursor.getColumnIndex("sub_cat_id"));
                String CatName = getFullCategoryName(subCabId, categories, subCategories);

                verses.add(new Verse(id,content, CatName));
            } while (cursor.moveToNext());
        }

        return  verses;
    }

    public ArrayList<Search> search(String query){

        ArrayList<Search> verses = new ArrayList<>();

        Cursor cursor = getReadableDatabase().rawQuery(
                "SELECT id,content, sub_cat_id FROM verses WHERE content LIKE '%"
                        + query + "%'", null);
        if( cursor != null && cursor.moveToFirst()){
            do {
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String content = cursor.getString(cursor.getColumnIndex("content"));
                verses.add(new Search(id,content));
            } while (cursor.moveToNext());
        }

        return  verses;

    }
    private String getFullCategoryName(int subCatId, ArrayList<Category> categories, ArrayList<SubCategory> subCategories){

        String fullName;
        String catName = "";
        String subCatName = "";
        int catId = 0;

        for ( SubCategory subCategory: subCategories){
            if(subCategory.getId() == subCatId){
                subCatName = subCategory.getName();
                catId = subCategory.getCatId();
                break;
            }
        }

        for ( Category category: categories){
            if( category.getId() == catId){
                catName = category.getName();
                break;
            }
        }

        fullName = catName.split("-")[0] + "\u25ba " + subCatName;

        return fullName;

    }

    public ArrayList<Bookmark> getBookmarks(){
        ArrayList<Bookmark> bookmarkList = new ArrayList<>();
        Cursor cursor = getReadableDatabase()
                .rawQuery(" SELECT verse_number, note FROM bookmark", null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    int verseNumber = cursor.getInt(cursor.getColumnIndex("verse_number"));
                    String note = cursor.getString(cursor.getColumnIndex("note"));
                    bookmarkList.add(new Bookmark(verseNumber, note));
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return bookmarkList;
    }

    public void addToBookmark(int verseNumber, String note){
        getWritableDatabase()
                .execSQL("INSERT INTO bookmark (verse_number, note) VALUES (?,?)",
                        new Object[]{verseNumber, note});
    }

    public void removeFromBookmark(int rowid){
        getWritableDatabase().execSQL("DELETE FROM bookmark WHERE rowid = " + rowid);
    }

    public void removeAllBookmarks(){
        getWritableDatabase().execSQL("DELETE FROM bookmark");
    }

    public int getDatabaseVersion(){
        return DATABASE_VERSION;
    }
}
