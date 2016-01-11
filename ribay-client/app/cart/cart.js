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

    .controller('cartCtrl', ['$scope', function ($scope) {

        // TODO get data from backend rest api
        $scope.cart = [{
            id: "abc",
            image: "http://placehold.it/200x400",
            name: "Name of article",
            price: 12.34,
            quantity: 2
        }, {
            id: "def",
            image: "http://placehold.it/400x200",
            name: "Other article",
            price: 5.99,
            quantity: 1
        }, {
            id: "ghi",
            image: "http://placehold.it/50x50",
            name: "Third article",
            price: 1.99,
            quantity: 10
        }];

        $scope.deleteItem = function (item) {
            // TODO delete item from cart
            alert("delete " + item.id);
        };

        $scope.setQuantity = function (item, quantity) {
            // TODO set quantity of item
            alert("set quantity of " + item.id + " to " + quantity);
        };

        var amount = 0;
        angular.forEach($scope.cart, function (item) {
            amount += item.quantity;
        });

        var price = 0;
        angular.forEach($scope.cart, function (item) {
            price += (item.quantity * item.price);
        });

        $scope.subtotal = {
            amount: amount,
            price: price
        };

        $scope.checkout = function () {
            // TODO proceed to checkout
            alert("Checkout");
        }

    }]);
