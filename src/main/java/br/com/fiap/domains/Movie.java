package br.com.fiap.domains;

import com.google.gson.annotations.SerializedName;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * @author  Vinicius Bezerra
 * <a href='https://developers.themoviedb.org/3/'>Link Documentação</a>
 * Classe responsável por representar um filme
 */
public class Movie {

    @SerializedName("poster_path")
    private String poster;
    private Boolean adult;
    private String overview;
    @SerializedName("release_date")
    private LocalDate releaseDate;
    @SerializedName("genre_ids")
    private List<Integer> genreIds;
    private Integer id;
    @SerializedName("original_title")
    private String originalTitle;

    @SerializedName("original_language")
    private String originalLanguage;

    private String title;

    @SerializedName("backdrop_path")
    private String backdropPath;

    private Double popularity;

    @SerializedName("vote_count")
    private Integer voteCount;
    private Boolean video;
    @SerializedName("vote_average")
    private Double voteAverage;

    private String genreNames;

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public boolean isAdult() {
        return adult;
    }

    public void setAdult(Boolean adult) {
        this.adult = adult;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    public List<Integer> getGenreIds() {
        return genreIds;
    }

    public void setGenreIds(List<Integer> genreIds) {
        this.genreIds = genreIds;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getOriginalLanguage() {
        return originalLanguage;
    }

    public void setOriginalLanguage(String originalLanguage) {
        this.originalLanguage = originalLanguage;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    public double getPopularity() {
        return popularity;
    }

    public void setPopularity(Double popularity) {
        this.popularity = popularity;
    }

    public Integer getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(Integer voteCount) {
        this.voteCount = voteCount;
    }

    public boolean isVideo() {
        return video;
    }

    public void setVideo(Boolean video) {
        this.video = video;
    }

    public String getGenreNames() {
        if (genreNames == null) {
            return "";
        }
        return genreNames;
    }

    public void setGenreNames(String genreNames) {
        this.genreNames = genreNames;
    }

    public Boolean getAdult() {
        return adult;
    }

    public Boolean getVideo() {
        return video;
    }

    public Double getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(Double voteAverage) {
        this.voteAverage = voteAverage;
    }

    private String getTextReleaseDateFormatBrazilian() {
        if (this.releaseDate == null || this.releaseDate.equals("")) return "";

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        return this.releaseDate.format(formatter);
    }

    public String showInfoMovie() {
        return "Titulo: " + this.getTitle() + '\n' +
                "Genero: " + this.getGenreNames() + '\n' +
                "Descricao: " + this.getOverview() + '\n' +
                "Data de Lancamento: " + this.getTextReleaseDateFormatBrazilian() + '\n' +
                "Nota: " + this.getVoteAverage();
    }

}
