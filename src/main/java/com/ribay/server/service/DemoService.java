package com.ribay.server.service;

import com.basho.riak.client.api.commands.kv.StoreValue;
import com.basho.riak.client.core.query.Location;
import com.basho.riak.client.core.query.Namespace;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.util.Converter;
import com.fasterxml.jackson.databind.util.StdConverter;
import com.github.fge.jackson.JacksonUtils;
import com.ribay.server.db.MyRiakClient;
import com.ribay.server.material.*;
import com.ribay.server.repository.ArticleRepository;
import com.ribay.server.util.RibayProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@RestController
public class DemoService {

    private final Logger logger = LoggerFactory.getLogger(DemoService.class);

    public static final String DEMO_SESSION_ID = "demo_key";

    @Autowired
    private MyRiakClient client;

    @Autowired
    private RibayProperties properties;

    @Autowired
    private ArticleRepository articleRepository;

    /**
     * Creates demo objects for demo UI. This will override the existing data in the demo buckets
     *
     * @throws Exception
     */
    @RequestMapping(path = "/demo/create", method = RequestMethod.POST)
    public void createDemo() throws Exception {
        logger.info("create demo");

//        createDemoArticles(); // NOTE: This operation takes some time! (first test: 49 minutes!!!)
//        createDemoCart();

        // TODO create more demo data
    }

    private void createDemoArticles() throws Exception {
        ObjectMapper mapper = JacksonUtils.newMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(Date.class, new JsonDeserializer<Date>() {
            @Override
            public Date deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
                JsonNode node = jp.getCodec().readTree(jp);
                JsonNode nested = node.findValue("$date");
                return (nested == null) ? null : new Date(nested.longValue());
            }
        });
        mapper.registerModule(module);
        mapper.setConfig(mapper.getDeserializationConfig() //
                .with(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY) //
                .with(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT) //
                .with(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT) //
        );

        Converter<MoviesJSONEntry, Article> converter = new MoviesJSONConverter();

        InputStream articleStream = DemoService.class.getClassLoader().getResourceAsStream("movies.json");
        try (BufferedReader br = new BufferedReader(new InputStreamReader(articleStream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                MoviesJSONEntry jsonEntry = mapper.readValue(line, MoviesJSONEntry.class);
                Article article = converter.convert(jsonEntry);
                articleRepository.storeArticle(article);
            }
        }
    }

//    private void createDemoCart() throws Exception {
//        Cart demoCart = new Cart(Arrays.asList(
//                new ArticleShort("abc", "My first article in shopping cart",
//                        "http://placehold.it/200x400", 12.34, 2),
//                new ArticleShort("def", "2nd article", "http://placehold.it/400x200", 5.99, 1),
//                new ArticleShort("ghi", "last article in this cart", "http://placehold.it/50x50",
//                        1.99, 10)));
//
//        Namespace cartBucket = new Namespace(properties.getBucketCart());
//        Location cartObjectLocation = new Location(cartBucket, DEMO_SESSION_ID);
//
//        StoreValue storeOp = new StoreValue.Builder(demoCart).withLocation(cartObjectLocation)
//                .build();
//
//        client.execute(storeOp);
//    }

    public static class MoviesJSONConverter extends StdConverter<MoviesJSONEntry, Article> {

        @Override
        public Article convert(MoviesJSONEntry value) {
            Article article = new Article();
            article.setId(value.get_id());
            article.setActors(value.getActors());
            article.setGenre(value.getGenre());
            article.setMovie(value.isMovie());
            article.setPlot(value.getPlot());
            article.setRating(value.getRating());
            article.setReleases(value.getReleases());
            article.setRuntime(value.getRuntime());
            article.setTitle(value.getTitle());
            article.setVotes(value.getVotes());
            article.setYear(value.getYear());
            article.setComment(value.getComment());
            article.setTweets(value.getTweets());
            return article;
        }
    }

    public static class MoviesJSONEntry {
        private String _id;
        private List<String> actors;
        private List<String> genre;
        private boolean movie;
        private String plot;
        private float rating;
        private List<Release> releases;
        private String runtime;
        private String title;
        private int votes;
        private String year;
        private String comment;
        private List<Tweet> tweets;

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public List<String> getActors() {
            return actors;
        }

        public void setActors(List<String> actors) {
            this.actors = actors;
        }

        public List<String> getGenre() {
            return genre;
        }

        public void setGenre(List<String> genre) {
            this.genre = genre;
        }

        public boolean isMovie() {
            return movie;
        }

        public void setMovie(boolean movie) {
            this.movie = movie;
        }

        public String getPlot() {
            return plot;
        }

        public void setPlot(String plot) {
            this.plot = plot;
        }

        public float getRating() {
            return rating;
        }

        public void setRating(float rating) {
            this.rating = rating;
        }

        public List<Release> getReleases() {
            return releases;
        }

        public void setReleases(List<Release> releases) {
            this.releases = releases;
        }

        public String getRuntime() {
            return runtime;
        }

        public void setRuntime(String runtime) {
            this.runtime = runtime;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public int getVotes() {
            return votes;
        }

        public void setVotes(int votes) {
            this.votes = votes;
        }

        public String getYear() {
            return year;
        }

        public void setYear(String year) {
            this.year = year;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }

        public List<Tweet> getTweets() {
            return tweets;
        }

        public void setTweets(List<Tweet> tweets) {
            this.tweets = tweets;
        }
    }

}
