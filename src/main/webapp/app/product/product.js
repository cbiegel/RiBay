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

    .service('productService', ['$http', function ($http) {

        this.getProductDetails = function (id, callback) {

            $http.get('/article/info?articleId=' + id).then(function(config) {
                if(config.data != "") {

                    var data = {
                        id: config.data.id,
                        title: config.data.title,
                        rate: config.data.rating,
                        votes: config.data.votes,
                        year: config.data.year,
                        releases: config.data.releases,
                        genres: config.data.genre,
                        actors: config.data.actors,
                        runtime: config.data.runtime,
                        price: "13,37€",
                        quantity: 1337,
                        details: config.data.plot,
                        images: [
                                    {src: '//placehold.it/300x300'},
                                    {src: '//placehold.it/400x300'},
                                    {src: '//placehold.it/300x400'}
                                ]
                    };

                    // convert release timestamps from unix time to date string
                    for(var i=0; i < data.releases.length; i++) {
                        var date = new Date(data.releases[i].date);
                        data.releases[i].date = "" + date.getFullYear() + "/" + date.getMonth() + "/" + date.getDay();
                    }
                    callback(data);
                }
                 else {
                    // TODO: Article with articleId wasn't found in app server / backend -> display appropriate error message
                }
            });

        this.getReviews = function (id, callback) {
            // TODO get data from backend
            var data = [{
                name: "Max Mustermann",
                time: Date.now(),
                title: "Tolles Produkt",
                text: "Gutes Produkt.\r\nSchnelle Lieferung, einwandfrei.",
                rating: 4
            }, {
                name: "Foo Bar",
                time: Date.now(),
                title: "Schlechtes Produkt",
                text: "Schlechtes Produkt.\r\nLangsame Lieferung :(.",
                rating: 1
            }];
            callback(data);
        };

        this.addToCart = function (id, quantity, callback) {
            // TODO integrate actual shopping cart
            callback();
        };

    }}])

    .controller('productCtrl', ['$scope', '$routeParams', '$timeout', 'productService', function ($scope, $routeParams, $timeout, productService) {

        var id = $routeParams.productid;
        $scope.product = "";

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

        $scope.switchBool = function (boolValue) {
            $scope[boolValue] = !$scope[boolValue];
        };

        productService.getReviews(id, function (data) {
            $scope.reviews = data;
        });

        // return only the first n entries of the actors
        $scope.getFirstNActors = function (n) {
            if($scope.product.actors) {
                var limit = Math.min(n, $scope.product.actors.length);
                var result = new Array(limit);
                for (var i = 0; i < limit; i++) {
                    result[i] = $scope.product.actors[i];
                }
                return result;
            }
        }

    }]);
