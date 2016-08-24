Database schema:
===========

| bucket_type  | bucket_name                 | key            | value                 | 2i                       | search_index      | comment
| ------------ | --------------------------- | -------------- | --------------------- | ------------------------ | ----------------- | -------
| default      | session_login               | {sessionId}    | User                  |                          |                   | Maps session to logged in user. Irrelevant because session cookie contains all the data about the user already. So there is no need to store the user for a session.
| default      | users                       | {userId}       | User                  | index_email              |                   | Stores user login data. Uses index to check if user already exist and for efficient login query.
| default      | lastaccess                  | {sessionId}    | byte[]                | time                     |                   | Stores timestamp of the last access of each session. Use index range query to look for expired sessions so that the application can start a cleanup job for that session.
| default      | images                      | {imageId}      | byte[]                |                          |                   | Stores images as a convenient file store including mimeType.
| ribay_orders | {userId},0                  | {orderId}      | OrderFinished         | index_timestamp          |                   | Stores finished orders. Use index range query to find recent orders.
| ribay_crdt   | session_visited_articles    | {sessionId}    | RiakMap               |                          |                   | Stores last visited articles for a session. Maps article to timestamp of last access
| default      | recommendation_article      | {articleId}    | List<ArticleShortest> |                          |                   | Stores article recommendations for an article. Depends on frequent itemsets that contain the article specified by the key.
| default      | recommendation_user         | {userId}       | List<ArticleShortest> |                          |                   | Stores article recommendations for a user. Depends on frequent itemsets that contain articles that the user has bought recently.
| default      | articles                    | {articleId}    | Article               |                          |                   | Stores static article data (like title, description, genre, imageId, releases, ...)
| ribay_crdt   | article_dynamic             | {articleId}    | RiakMap               |                          |                   | Stores dynamic article data (like price, stock, ratings, ...)
| ribay_crdt   | article_search              | {articleId}    | RiakMap               |                          | articles          | Stores static and dynamic article data optimized for a search index for the search use case
| default      | article_typeahead           | {articleId}    | ArticleTypeahead      |                          | article_typeahead | Stores static textual article data optimized for a search index for the typeahead use case.
| default      | article_reviews_{articleId} | {userId}       | ArticleReview         | index_date, index_rating |                   | Stores reviews for articles. Uses index range queries for recent reviews or reviews with a minimum rating.
| ribay_crdt   | session_cart                | {sessionId}    | RiakMap               |                          |                   | Stores the shopping cart of a session. Maps articleIds to a tuple of static data, price and quantity.
