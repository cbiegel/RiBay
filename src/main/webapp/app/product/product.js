/**
 * Created by Christian on 03.01.2016.
 */

'use strict';

angular.module('myApp.product', [])

    .config(['$routeProvider', function ($routeProvider) {
        $routeProvider.when('/product/:productid', {
            templateUrl: 'product/product.html',
            controller: 'productCtrl'
        });
    }])

    .service('productService', ['$http', '$window', 'imageService', function ($http, $window, imageService) {

        this.getProductDetails = function (id, callback) {

            var result = {};

            $http.get('/article/info?articleId=' + id).then(
                function success(response) {

                    result.id = response.data.id;
                    result.title = response.data.title;
                    result.year = response.data.year;
                    result.releases = response.data.releases;
                    result.genres = response.data.genre;
                    result.actors = response.data.actors;
                    result.runtime = response.data.runtime;
                    result.details = response.data.plot;
                    result.images = [];

                    if (response.data.imageId) {
                        var url = imageService.createImageURLFromId(response.data.imageId);
                        result.images.push({src: url});
                    }

                    if (result.releases) {
                        // convert release timestamps from unix time to date string
                        for (var i = 0; i < result.releases.length; i++) {
                            var date = new Date(result.releases[i].date);
                            result.releases[i].date = date;
                        }
                    }
                },
                function error(response) {
                    if (response.status == 404) {
                        // TODO: Article with articleId wasn't found in app server / backend -> display appropriate error message
                        $window.alert("Not found!");
                    }
                    else {
                        // TODO: handle other error
                    }
                }
            );

            $http.get('/article/info/dynamic?articleId=' + id).then(
                function success(response) {
                    // copy all properties into the result object
                    for (var property in response.data) {
                        result[property] = response.data[property];
                    }

                    result.averageRating = result.sumRatings / result.countRatings;
                },
                function error(response) {
                    // TODO: handle error
                }
            );

            // return result object immediately. it will be filled, once the http-calls are successful
            callback(result);
        };

        this.getReviews = function (articleId, continuation, callback) {

            if (continuation !== null) { // do no try to load more reviews after reaching end of list (null means end of list)
                var url;
                if (continuation == undefined) {
                    url = '/article/reviews?articleId=' + articleId;
                }
                else {
                    url = '/article/reviews?articleId=' + articleId + '&continuation=' + continuation;
                }

                $http.get(url).then(
                    function success(response) {
                        var data = response.data;
                        callback(data);
                    },
                    function error(response) {
                    }
                );
            }
        };

        this.submitReview = function (id, name, value, title, content, callback) {

            var dataObject = {
                articleId: id,
                author: name,
                date: Date.now(),
                ratingValue: value,
                reviewTitle: title,
                reviewContent: content
            };

            $http.post('/article/submitReview', dataObject).then(function (config) {
                // if there is a result -> register successful
                if (config.data != "") {
                    var response = {message: 'Review submitted.'};
                    callback(response);
                }
                else {
                    var response = {message: 'An error occurred when submitting your review. Please try again.'};
                    callback(response);
                }
            });
        };

        this.addToCart = function (id, quantity, callback) {
            $http.put('/cart/add/' + id + '/' + quantity, undefined).success(callback);
        };

        this.isFirstReviewForArticle = function (articleId, callback) {
            $http.get('/article/isFirstReview' + '?articleId=' + articleId).then(function (config) {
                if (config.data != "") {
                    callback(config.data);
                } else {
                    callback(null);
                }
            });
        }

    }])

    .controller('productCtrl', ['$scope', 'UserService', '$routeParams', '$timeout', '$location', 'productService', function ($scope, UserService, $routeParams, $timeout, $location, productService) {

        var id = $routeParams.productid;
        $scope.product = "";
        $scope.isCreatingReview = false;
        $scope.isSubmittingReview = false;
        $scope.isEditingReview = false;
        $scope.newRatingValue = 0;
        $scope.newRatingTitle = "";
        $scope.newRatingContent = "";
        $scope.oldRatingValue = 0;
        $scope.oldRatingTitle = "";
        $scope.oldRatingContent = "";
        $scope.oldReviewDate = "";
        $scope.isFirstReview = true;

        productService.getProductDetails(id, function (data) {
            $scope.product = data;
        });

        if(UserService.getLoggedInUser() != null) {
            productService.isFirstReviewForArticle(id, function(response) {
                if (response != null) {
                    $scope.isFirstReview = false;
                    $scope.oldRatingValue = response.ratingValue;
                    $scope.oldRatingTitle = response.reviewTitle;
                    $scope.oldRatingContent = response.reviewContent;
                    $scope.oldReviewDate = response.date;
                }
            });
        }

        $scope.isSuccessAlertDisplayed = false;
        $scope.successTextAlert = "Item \"" + $scope.product.title + "\" was added to the cart.";
        $scope.hasActors = false;

        $scope.quantity = '1';

        $scope.addToCart = function () {

            productService.addToCart(id, $scope.quantity, function () {

                $scope.isSuccessAlertDisplayed = true;
                $timeout(function () {
                    $scope.isSuccessAlertDisplayed = false;
                }, 2000);

                // TODO callback?
            });
        };

        $scope.reviews = [];
        $scope.reviews_continuation = undefined; // undefined -> initial value, non-null -> there are more reviews, null -> there are no more reviews

        var updateReviews = function (data) {
            $scope.reviews = $scope.reviews.concat(data.reviews); // append at the end of list
            $scope.reviews_continuation = data.continuation;
        };

        productService.getReviews(id, $scope.reviews_continuation, updateReviews);

        $scope.createReview = function () {

            // user is not logged in. Redirect to login page...
            if (UserService.getLoggedInUser() == null) {
                $location.url('/login');
            } else {
                $scope.isCreatingReview = true;
            }
        };

        $scope.createReview = function () {
            $scope.isEditingReview = true;
        };

        $scope.editReview = function () {
            $scope.isEditingReview = true;
        }

        $scope.submitReview = function () {
            // id, name, value, title, content, callback
            $scope.dataLoading = true;
            productService.submitReview(id, UserService.getLoggedInUser().name, $scope.newRatingValue, $scope.newRatingTitle, $scope.newRatingContent, function (response) {
                // TODO callback (display success / error message?)
                if (response.message == 'Review submitted.') {
                    $scope.dataLoading = false;
                    $scope.isCreatingReview = false;
                    $scope.isEditingReview = false;

                    $scope.reviews = []; // reset reviews
                    $scope.reviews_continuation = undefined; // reset continuation
                    productService.getReviews(id, $scope.reviews_continuation, updateReviews); // reload reviews
                } else {
                    $scope.dataLoading = false;
                    $scope.isCreatingReview = false;
                    $scope.isEditingReview = false;
                }
            });
        };

        $scope.submitEditedReview = function () {
            $scope.newRatingValue = $scope.oldRatingValue;
            $scope.newRatingTitle = $scope.oldRatingTitle;
            $scope.newRatingContent = $scope.oldRatingContent;
            $scope.oldReviewDate = Date.now();
            $scope.submitReview();
        };

        $scope.loadMoreReviews = function () {
            productService.getReviews(id, $scope.reviews_continuation, updateReviews);
        };

        $scope.switchBool = function (boolValue) {
            $scope[boolValue] = !$scope[boolValue];
        };

    }]);
