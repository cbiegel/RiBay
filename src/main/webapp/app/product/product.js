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

    .service('productService', [function () {

        this.getProductDetails = function (id, callback) {
            // TODO get data from backend
            var data = {
                id: id,
                title: 'Insert product title here',
                rate: 4,
                price: "45,23€",
                quantity: 1337,
                details: "Insert the product details here",
                images: [
                    {src: '//placehold.it/300x300'},
                    {src: '//placehold.it/400x300'},
                    {src: '//placehold.it/300x400'}
                ]
            };
            callback(data);
        };

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

    }])

    .controller('productCtrl', ['$scope', '$routeParams', '$timeout', 'productService', function ($scope, $routeParams, $timeout, productService) {

        var id = $routeParams.productid;

        productService.getProductDetails(id, function (data) {
            $scope.product = data;
        });

        $scope.isSuccessAlertDisplayed = false;
        $scope.successTextAlert = "Item \"" + $scope.product.title + "\" was added to the cart.";

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

    }]);
