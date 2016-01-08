/**
 * Created by Christian on 03.01.2016.
 */

'use strict';

angular.module('myApp.product', ['ngRoute', 'ngAnimate', 'ui.bootstrap'])

    .config(['$routeProvider', function ($routeProvider) {
        $routeProvider.when('/product/:productid', {
            templateUrl: 'product/product.html',
            controller: 'productCtrl'
        });
    }])

    .controller('productCtrl', ['$scope', '$routeParams', '$timeout', function ($scope, $routeParams, $timeout) {

        $scope.product = {
            id: $routeParams.productid,
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
            // TODO get data from backend
        };


        $scope.isSuccessAlertDisplayed = false;
        $scope.successTextAlert = "Item \"" + $scope.product.title + "\" was added to the cart.";

        $scope.quantity = '1';

        $scope.addToCart = function () {

            $scope.isSuccessAlertDisplayed = true;
            $timeout(function() {
                $scope.isSuccessAlertDisplayed = false;
            }, 2000);

            // TODO integrate actual shopping cart
        };

        $scope.switchBool = function (boolValue) {
            $scope[boolValue] = !$scope[boolValue];
        };

    }])
    .controller('reviewCtrl', ['$scope', '$routeParams', function ($scope, $routeParams) {

        $scope.reviews = [{
            name: "Max Mustermann",
            time: Date.now(),
            title: "Tolles Produkt",
            text: "Gutes Produkt.\r\nSchnelle Lieferung, einwandfrei.",
            rating: 4
        },
            {
                name: "Foo Bar",
                time: Date.now(),
                title: "Schlechtes Produkt",
                text: "Schlechtes Produkt.\r\nLangsame Lieferung :(.",
                rating: 1
            }];

    }]);


