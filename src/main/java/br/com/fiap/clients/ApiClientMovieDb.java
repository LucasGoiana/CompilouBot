package br.com.fiap.clients;


import br.com.fiap.config.PropertiesLoaderImpl;
import br.com.fiap.custom.LocalDateDeserializer;
import br.com.fiap.custom.LocalDateSerializer;
import br.com.fiap.domains.Movie;
import br.com.fiap.errors.AuthenticationFailedError;
import br.com.fiap.errors.ClientRequestError;
import br.com.fiap.errors.MovieNotFoundError;
import br.com.fiap.errors.UnprocessableEntityError;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author  Vinicius Bezerra
 * <a href='https://developers.themoviedb.org/3/'>Link Documentação</a>
 */

public class ApiClientMovieDb {


    private final Map<Integer, String> genres;
    private final Gson gson;

    /**
     * Carrega as informações ao construtor e carrega o gênero.
     */
    public ApiClientMovieDb() {

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDate.class, new LocalDateSerializer());
        gsonBuilder.registerTypeAdapter(LocalDate.class, new LocalDateDeserializer());
        gson = gsonBuilder.setPrettyPrinting().create();

        genres = getGenresMovies();
    }

    /**
     * Metódo utilizado para buscar os filmes na Api MovieDb, pelo nome do filme enviado pelo usuário.
     * @param nameMovie
     * @return Retorna uma lista de Filmes e seus Banners.
     * @throws IOException
     * @throws MovieNotFoundError
     * @throws AuthenticationFailedError
     * @throws UnprocessableEntityError
     */
    public List<Movie> getMovieByName(String nameMovie) throws IOException, MovieNotFoundError, AuthenticationFailedError, UnprocessableEntityError {

        Map<String, String> params = Map.of(
        "include_adult", PropertiesLoaderImpl.getValue("api.moviedb.include_adult"),
        "page", PropertiesLoaderImpl.getValue("api.moviedb.page"),
        "region", PropertiesLoaderImpl.getValue("api.moviedb.region"),
        "query", nameMovie);

        HttpUrl.Builder httpBuilder = generateHttpBuilder("search/movie", params);

        Response response = executeRequest(httpBuilder);
        validateHttpCodeResponse(response,200);
        List<Movie> movies = toJsonFromMovie(response,"results");

        if (movies.size() == 0) throw new MovieNotFoundError("Filme não encontrado");

        return movies.stream().filter((movie) -> {
            return (movie.getReleaseDate() != null);
        }).sorted((movie1, movie2) -> {
            return movie2.getReleaseDate().compareTo(movie1.getReleaseDate());
        }).limit(5).map(movie -> {
            movie.getGenreIds().forEach(genreID -> {
                String genreName = movie.getGenreNames();
                if (!genreName.equals("")) {
                    genreName += ", " + genres.getOrDefault(genreID, "");
                } else {
                    genreName += genres.getOrDefault(genreID, "");
                }
                movie.setGenreNames(genreName);
            });
            return movie;
        }).collect(Collectors.toList());
    }

    /**
     * Metódo utilizado para buscar os filmes na Api MovieDb que estão em cartaz no cinema.
     * @return Retorna uma lista de filmes que estão em cartaz.
     * @throws IOException
     * @throws MovieNotFoundError
     * @throws AuthenticationFailedError
     * @throws UnprocessableEntityError
     */
    public List<Movie> getMovieNowPlaying() throws IOException, MovieNotFoundError, AuthenticationFailedError, UnprocessableEntityError {
        Map<String, String> params = Map.of(
        "include_adult", PropertiesLoaderImpl.getValue("api.moviedb.include_adult"),
        "page", PropertiesLoaderImpl.getValue("api.moviedb.page"),
        "region", PropertiesLoaderImpl.getValue("api.moviedb.region"));

        HttpUrl.Builder httpBuilder = generateHttpBuilder("movie/now_playing", params);

        Response response = executeRequest(httpBuilder);

        validateHttpCodeResponse(response,200);
        List<Movie> movies = toJsonFromMovie(response,"results");


        return movies.stream().sorted((movie1, movie2) -> {
            return movie2.getVoteAverage().compareTo(movie1.getVoteAverage());
        }).limit(5).map(movie -> {
            movie.getGenreIds().forEach(genreID -> {
                String genreName = movie.getGenreNames();
                if (!genreName.equals("")) {
                    genreName += ", " + genres.getOrDefault(genreID, "");
                } else {
                    genreName += genres.getOrDefault(genreID, "");
                }
                movie.setGenreNames(genreName);
            });
            return movie;
        }).collect(Collectors.toList());
    }

    /**
     * Metódo utilizado para Buscar os Filmes na Api MovieDb que são atualmente os mais populares.
     * @return Retorna uma lista de filmes populares·
     * @throws IOException
     * @throws MovieNotFoundError
     * @throws AuthenticationFailedError
     * @throws UnprocessableEntityError
     */
    public List<Movie> getMoviePopular() throws IOException, MovieNotFoundError, AuthenticationFailedError, UnprocessableEntityError {
        Map<String, String> params = Map.of(
        "region", PropertiesLoaderImpl.getValue("api.moviedb.region"),
        "page", PropertiesLoaderImpl.getValue("api.moviedb.page"));

        HttpUrl.Builder httpBuilder = generateHttpBuilder("movie/popular", params);

        Response response = executeRequest(httpBuilder);
        validateHttpCodeResponse(response,200);
        List<Movie> movies = toJsonFromMovie(response,"results");

        return movies.stream().sorted((movie1, movie2) -> {
            return movie2.getVoteAverage().compareTo(movie1.getVoteAverage());
        }).limit(5).map(movie -> {
            movie.getGenreIds().forEach(genreID -> {
                String genreName = movie.getGenreNames();
                if (!genreName.equals("")) {
                    genreName += ", " + genres.getOrDefault(genreID, "");
                } else {
                    genreName += genres.getOrDefault(genreID, "");
                }
                movie.setGenreNames(genreName);
            });
            return movie;
        }).collect(Collectors.toList());
    }

    /**
     * Metódo utilizado para buscar os Filmes similares na Api MovieDb, pelo Nome do Filme enviado pelo Usuário.
     * @param nameMovie
     * @return Retorna uma lista de Filmes similares
     * @throws IOException
     * @throws MovieNotFoundError
     * @throws AuthenticationFailedError
     * @throws UnprocessableEntityError
     */
    public List<Movie> getMovieSimilar(String nameMovie) throws IOException, MovieNotFoundError, AuthenticationFailedError, UnprocessableEntityError {

        List<Movie> movies = getMovieByName(nameMovie);
        List<Movie> similarMovie = new ArrayList<>();

        for (Movie movie : movies) {

            Map<String, String> params = Map.of(
            "region", PropertiesLoaderImpl.getValue("api.moviedb.region"),
            "page", PropertiesLoaderImpl.getValue("api.moviedb.page"));
            String service = String.format("movie/%s/similar", movie.getId());
            HttpUrl.Builder httpBuilder = generateHttpBuilder(service, params);
            Response response = executeRequest(httpBuilder);
            validateHttpCodeResponse(response,200);
            similarMovie.addAll(toJsonFromMovie(response,"results"));

        }

        return similarMovie.stream().
                collect(Collectors.collectingAndThen(Collectors.toList(), collected -> {
                    Collections.shuffle(collected);
                    return collected.stream();
                }))
                .limit(5)
                .map(movie -> {
                    movie.getGenreIds().forEach(genreID -> {
                        String genreName = movie.getGenreNames();
                        if (!genreName.equals("")) {
                            genreName += ", " + genres.getOrDefault(genreID, "");
                        } else {
                            genreName += genres.getOrDefault(genreID, "");
                        }
                        movie.setGenreNames(genreName);
                    });
                    return movie;
                })
                .collect(Collectors.toList());
    }

    /**
     * Metódo responsável por buscar todos os genêros da Api MovieDB,
     * caso não encontre, o filme será retornado sem genêro.
     * @return Retorna uma map contendo todos os genêros encontrados <Id do genêro, Nome do genêro >.
     */
    private Map<Integer, String> getGenresMovies() {

        HttpUrl.Builder httpBuilder = generateHttpBuilder("genre/movie/list", null);
        Map<Integer, String> genres = new HashMap<>();
        try {
            Response response = executeRequest(httpBuilder);
            validateHttpCodeResponse(response,200);
            JsonObject obj = JsonParser.parseString(response.body().string()).getAsJsonObject();
            JsonArray resultGenresArray = obj.getAsJsonArray("genres");
            resultGenresArray.forEach(genresIterator -> {
                JsonObject object = (JsonObject) genresIterator;
                genres.put(object.get("id").getAsInt(), object.get("name").getAsString());
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return genres;

    }

    /**
     * Método utilizado para gerar a URI.
     * @param service  Endpoint da Api
     * @param params Recebe os parâmetros para montar uma Query String
     * @return Retorna a URI montada pelo metódo.
     */
    private HttpUrl.Builder generateHttpBuilder(String service, Map<String, String> params) {
        HttpUrl.Builder httpBuilder = HttpUrl.parse(PropertiesLoaderImpl.getValue("api.moviedb.url") + service).newBuilder();
        httpBuilder.addQueryParameter("api_key", PropertiesLoaderImpl.getValue("api.moviedb.api_key"));
        httpBuilder.addQueryParameter("language", PropertiesLoaderImpl.getValue("api.moviedb.language"));

        if (null != params) {
            for (Map.Entry<String, String> param : params.entrySet()) {
                httpBuilder.addQueryParameter(param.getKey(), param.getValue());
            }
        }


        return httpBuilder;
    }

    /**
     * Metódo que executa a requisição
     * @param httpBuilder
     * @return Retorna a resposta da requisição.
     * @throws IOException
     */
    private Response executeRequest(HttpUrl.Builder httpBuilder) throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();

        Request request = new Request.Builder()
                .url(httpBuilder.build())
                .method("GET", null)
                .build();

        return client.newCall(request).execute();
    }

    /**
     * Convert o json para um objeto Movie
     * @param response
     * @param nameField
     * @return Retorna uma lista de movies
     * @throws IOException
     */
    private List<Movie> toJsonFromMovie(Response response,String nameField) throws IOException {
        JsonObject obj = JsonParser.parseString(response.body().string()).getAsJsonObject();
        Type listType = new TypeToken<List<Movie>>() {
        }.getType();
        return gson.fromJson(obj.get(nameField).toString(), listType);
    }

    /**
     * Metódo utilizado para validar erros durante a requisição da api.
     * @param response
     * @param statusCodeSuccess
     * @throws MovieNotFoundError
     * @throws AuthenticationFailedError
     * @throws UnprocessableEntityError
     */
    private void validateHttpCodeResponse(Response response, Integer statusCodeSuccess) throws MovieNotFoundError, AuthenticationFailedError, UnprocessableEntityError {
        if (response.code() == 404) throw new MovieNotFoundError("Filme não encontrado");
        if (response.code() == 401) throw new AuthenticationFailedError("Autenticação Falhou na api de filmes");
        if (response.code() == 422) throw new UnprocessableEntityError("Valores enviados para consulta invalidos");
        if (response.code() != statusCodeSuccess) throw new ClientRequestError("Erro inesperado ao consultar api");
    }


}
