package cloud.krzysztofkin.newsapp;

/**
 * Class to holding article metadata
 */
public class Article {
    private String webTitle;
    private String sectionName;
    private String authorName;
    private String webPublicationDate;
    private String webUrl;

    /**
     * Default constructor
     *
     * @param webTitle           title without author
     * @param sectionName        section name
     * @param authorName         author name
     * @param webPublicationDate publication date
     * @param webUrl             article url
     */
    Article(String webTitle, String sectionName, String authorName, String webPublicationDate, String webUrl) {
        this.webTitle = webTitle;
        this.sectionName = sectionName;
        this.authorName = authorName;
        this.webPublicationDate = webPublicationDate;
        this.webUrl = webUrl;
    }

    public String getWebTitle() {
        return webTitle;
    }

    public String getSectionName() {
        return sectionName;
    }

    public String getAuthorName() {
        return authorName;
    }

    public String getWebPublicationDate() {
        return webPublicationDate;
    }

    public String getWebUrl() {
        return webUrl;
    }
}
