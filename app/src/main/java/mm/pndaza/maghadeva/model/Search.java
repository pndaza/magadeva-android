package mm.pndaza.maghadeva.model;

public class Search {
    private int verseNumber;
    private String verseBrief;

    public Search(int pageNumber, String brief) {
        this.verseNumber = pageNumber;
        this.verseBrief = brief;
    }

    public int getVerseNumber() {
        return verseNumber;
    }

    public String getVerseBrief() {
        return verseBrief;
    }
}
