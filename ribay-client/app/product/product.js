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

    .controller('productCtrl', ['$scope', '$routeParams', function ($scope, $routeParams) {

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


        $scope.isSuccessAlertDisplayed = false
        $scope.successTextAlert = "Item \"" + $scope.product.title + "\" was added to the cart."

        $scope.addToCart = function() {

            $scope.isSuccessAlertDisplayed = true;

            // TODO integrate actual shopping cart
        };

        $scope.switchBool = function(boolValue) {
          $scope[boolValue] = !$scope[boolValue];
        };

    }]);
