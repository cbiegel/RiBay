/**
 * Created by Christian on 11.01.2016.
 */

'use strict';

angular.module('myApp.cart', ['ngRoute', 'ngAnimate', 'ui.bootstrap'])

    .config(['$routeProvider', function ($routeProvider) {
        $routeProvider.when('/cart', {
            templateUrl: 'cart/cart.html',
            controller: 'cartCtrl'
        });
    }])

    .controller('cartCtrl', ['$scope', '$http', function ($scope, $http) {

        $scope.cart = undefined;
        $scope.subtotal = undefined;

        $http.get('http://localhost:8080/cart/get/').success(function (data) {
            $scope.cart = data.articles;

            var total_amount = 0;
            angular.forEach($scope.cart, function (item) {
                total_amount += item.quantity;
            });

            var total_price = 0;
            angular.forEach($scope.cart, function (item) {
                total_price += (item.quantity * item.price);
            });

            $scope.subtotal = {
                amount: total_amount,
                price: total_price
            };
        });

        $scope.deleteItem = function (item) {
            // TODO delete item from cart
            alert("delete " + item.id);
        };

        $scope.setQuantity = function (item, quantity) {
            // TODO set quantity of item
            alert("set quantity of " + item.id + " to " + quantity);
        };

        $scope.checkout = function () {
            // TODO proceed to checkout
            alert("Checkout");
        }

    }]);
