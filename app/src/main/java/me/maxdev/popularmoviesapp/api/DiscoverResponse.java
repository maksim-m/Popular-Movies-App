package me.maxdev.popularmoviesapp.api;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DiscoverResponse<T> {

    @SerializedName("page")
    private long page;

    @SerializedName("results")
    private List<T> results;

    @SerializedName("total_pages")
    private long totalPages;

    @SerializedName("total_results")
    private long totalResults;

    public DiscoverResponse(long page, List<T> results, long totalPages, long totalResults) {
        this.page = page;
        this.results = results;
        this.totalPages = totalPages;
        this.totalResults = totalResults;
    }

    public long getPage() {
        return page;
    }

    public void setPage(long page) {
        this.page = page;
    }

    public List<T> getResults() {
        return results;
    }

    public void setResults(List<T> results) {
        this.results = results;
    }

    public long getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(long totalPages) {
        this.totalPages = totalPages;
    }

    public long getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(long totalResults) {
        this.totalResults = totalResults;
    }
}
