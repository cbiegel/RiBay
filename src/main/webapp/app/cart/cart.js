/**
 * Created by Christian on 11.01.2016.
 */

'use strict';

angular.module('myApp.cart', [])

    .config(['$routeProvider', function ($routeProvider) {
        $routeProvider.when('/cart', {
            templateUrl: 'cart/cart.html',
            controller: 'cartCtrl'
        });
    }])

    .service('cartService', ['$http', function ($http) {

        this.getCart = function (callback) {
            $http.get('/cart').success(callback);
        };

        this.deleteItem = function (item) {
            // TODO delete item from cart
            alert("delete " + item.id);
        };

        this.setQuantity = function (item, quantity) {
            // TODO set quantity of item
            alert("set quantity of " + item.id + " to " + quantity);
        };

        this.checkout = function () {
            // TODO proceed to checkout
            alert("Checkout");
        };

    }])

    .controller('cartCtrl', ['$scope', 'cartService', function ($scope, cartService) {

        $scope.cart = undefined;
        $scope.subtotal = undefined;
        $scope.isEmpty = true;

        cartService.getCart(function (data) {
            $scope.cart = data.articles;
            $scope.isEmpty = (data.articles.length == 0);

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

        $scope.deleteItem = cartService.deleteItem;

        $scope.setQuantity = cartService.setQuantity;

        $scope.checkout = cartService.checkout;

    }]);
