package mm.pndaza.maghadeva.model;

public class Verse {
    int verseNumber;
    String verseContent;
    String fullCatName;

    public Verse(int verseNumber, String verseContent, String catName) {
        this.verseNumber = verseNumber;
        this.verseContent = verseContent;
        fullCatName = catName;
    }

    public int getVerseNumber() {
        return verseNumber;
    }

    public String getVerseContent() {
        return verseContent;
    }

    public String getFullCatName() {
        return fullCatName;
    }
}
