package mm.pndaza.maghadeva.model;

public class Bookmark {

    private int verseNumber;
    private String note;

    public Bookmark(int verseNumber, String note) {
        this.verseNumber = verseNumber;
        this.note = note;
    }

    public int getVerseNumber() {
        return verseNumber;
    }

    public String getNote() {
        return note;
    }
}
