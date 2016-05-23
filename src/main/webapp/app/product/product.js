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

            $http.get('/article/info?articleId=' + id).then(
                function success(response) {
                    var data = {
                        id: response.data.id,
                        title: response.data.title,
                        rate: response.data.rating,
                        votes: response.data.votes,
                        year: response.data.year,
                        releases: response.data.releases,
                        genres: response.data.genre,
                        actors: response.data.actors,
                        runtime: response.data.runtime,
                        price: "13,37€",
                        quantity: 1337,
                        details: response.data.plot,
                        images: []
                    };

                    if (response.data.imageId) {
                        var url = imageService.createImageURLFromId(response.data.imageId);
                        data.images.push({src: url});
                    }

                    if (data.releases) {
                        // convert release timestamps from unix time to date string
                        for (var i = 0; i < data.releases.length; i++) {
                            var date = new Date(data.releases[i].date);
                            data.releases[i].date = date;
                        }
                    }

                    callback(data);
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
        };

        this.getReviews = function (id, callback) {

            $http.get('/article/getReviews?articleId=' + id).then(
                function success(response) {
                    var data = response.data;
                    callback(data);
                },
                function error(response) {
                }
            );
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
            // TODO integrate actual shopping cart
            callback();
        };

    }])

    .controller('productCtrl', ['$scope', '$rootScope', '$routeParams', '$timeout', '$location', 'productService', function ($scope, $rootScope, $routeParams, $timeout, $location, productService) {

        var id = $routeParams.productid;
        $scope.product = "";
        $scope.isCreatingReview = false;
        $scope.isSubmittingReview = false;
        $scope.newRatingValue = 0;
        $scope.newRatingTitle = "";
        $scope.newRatingContent = "";

        productService.getProductDetails(id, function (data) {
            $scope.product = data;
        });

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

        $scope.createReview = function () {

            // user is not logged in. Redirect to login page...
            if (!$rootScope.loggedIn) {
                $location.url('/login');
            } else {
                $scope.isCreatingReview = true;
            }
        };

        $scope.submitReview = function () {
            // id, name, value, title, content, callback
            $scope.dataLoading = true;
            productService.submitReview(id, $rootScope.loggedIn.name, $scope.newRatingValue, $scope.newRatingTitle, $scope.newRatingContent, function (response) {
                // TODO callback (display success / error message?)
                if (response.message == 'Review submitted.') {
                    $scope.dataLoading = false;
                    $scope.isCreatingReview = false;
                    $location.reload(true);
                } else {
                    $scope.dataLoading = false;
                    $scope.isCreatingReview = false;
                }
            });
        };

        $scope.switchBool = function (boolValue) {
            $scope[boolValue] = !$scope[boolValue];
        };

        productService.getReviews(id, function (data) {
            $scope.reviews = data;
        });

        // return only the first n entries of the actors
        $scope.getFirstNActors = function (n) {
            if ($scope.product.actors) {
                var limit = Math.min(n, $scope.product.actors.length);
                var result = new Array(limit);
                for (var i = 0; i < limit; i++) {
                    result[i] = $scope.product.actors[i];
                }
                return result;
            }
        }

    }]);
