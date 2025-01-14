package brcomncn.estante.model;

import com.google.gson.annotations.SerializedName;

public class IsbnResponse {
    private String isbn;
    private String title;
    private String subtitle;
    private String publisher;
    private String synopsis;
    private Integer year;
    private Integer pages;
    private String[] authors;
    private String provider;

    // Getters e Setters
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSubtitle() { return subtitle; }
    public void setSubtitle(String subtitle) { this.subtitle = subtitle; }

    public String getPublisher() { return publisher; }
    public void setPublisher(String publisher) { this.publisher = publisher; }

    public String getSynopsis() { return synopsis; }
    public void setSynopsis(String synopsis) { this.synopsis = synopsis; }

    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }

    public Integer getPages() { return pages; }
    public void setPages(Integer pages) { this.pages = pages; }

    public String[] getAuthors() { return authors; }
    public void setAuthors(String[] authors) { this.authors = authors; }

    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }
}
