/**
 * Created by CD on 29.07.2016.
 */
angular.module('myApp.checkout', [])

    .config(['$routeProvider', function ($routeProvider) {
        $routeProvider.when('/checkout/', {
            templateUrl: 'checkout/checkout.html',
            controller: 'checkoutCtrl'
        });
    }])

    // TODO use a factory for an checkout object with mothods instead?

    .service('checkoutService', ['$http', function ($http) {
        this.finishCheckout = function (order, successHandler, errorHandler) {
            $http.post('/checkout/finish', order).then(successHandler, errorHandler);
        };
    }])

    .controller('checkoutCtrl', ['$scope', '$rootScope', '$timeout', '$window', '$location', 'checkoutService', function ($scope, $rootScope, $timeout, $window, $location, checkoutService) {

        if (!$rootScope.order) {
            // no order
            $location.url('/cart/');
            return;
        }

        $scope.order = $rootScope.order;
        $scope.changes = {
            /* structure for later */
            added: undefined,
            removed: undefined,
            quantity: undefined,
            price: undefined
        };
        $scope.changes = undefined;

        $scope.backToCart = function () {
            $location.url('/cart/');
        }

        $scope.finishCheckout = function () {
            checkoutService.finishCheckout(
                $scope.order,
                function onSuccess(response) {
                    $rootScope.order = undefined;
                    // TODO on success: show finished order
                    $window.alert("success");
                },
                function onError(response) {
                    $scope.changes = undefined; // reset potential changes from last try

                    if (response.status == 409 && response.data.exceptionClass == 'CartChangedException') {
                        var newOrder = response.data.exception.newOrder;
                        handleCartChanged(newOrder);
                    }
                    else if (response.status == 410 && response.data.exceptionClass == 'OrderTooOldException') {
                        $window.alert("Order started too long ago. Please restart the checkout");
                        $location.url('/cart/');
                    }
                    else if (response.status == 412 && response.data.exceptionClass == 'EmptyCartException') {
                        $window.alert("Cart is empty. Can not complete checkout for an empty cart.");
                        $location.url('/cart/');
                    }
                    // TODO handle other errors that might happen through correct user interaction (not by manipulation or by direct rest calls)
                    else if (response.data.exception && response.data.exception.message) {
                        // handle if custom error from backend
                        $window.alert(response.data.exception.message);
                    }
                    else {
                        // handle other error
                        $window.alert('Unknown error');
                    }
                });
        };

        var handleCartChanged = function (newOrder) {
            // map articleId to article
            var oldArticlesMap = $scope.order.cart.articles.toMap(function (article) {
                return article.id
            }, function (article) {
                return article;
            });
            // map articleId to article
            var newArticlesMap = newOrder.cart.articles.toMap(function (article) {
                return article.id
            }, function (article) {
                return article;
            });

            // calculate changes
            var added = newArticlesMap.without(oldArticlesMap).valueList();
            var removed = oldArticlesMap.without(newArticlesMap).valueList();
            var quantity = collectItemsWithDifferentValuesInField(oldArticlesMap, newArticlesMap, 'quantity');
            var price = collectItemsWithDifferentValuesInField(oldArticlesMap, newArticlesMap, 'price');

            $scope.changes = {
                added: added,
                removed: removed,
                quantity: quantity,
                price: price
            };

            // update order
            $scope.order = newOrder;
        }

        var collectItemsWithDifferentValuesInField = function (map1, map2, fieldName) {
            var result = [];
            map1.forEach(function (value1, key) {
                var value2 = map2.get(key);
                // if both values are present and their specified field value is different ...
                if (value1 && value2 && (value1[fieldName] != value2[fieldName])) {
                    // ... add as tuple
                    result.push({left: value1, right: value2});
                }
            });
            return result;
        }
    }])

    .filter('sumPrice', function () {
        return function (articles) {
            return articles.map(function (article) {
                return article.price * article.quantity;
            }).reduce(function (a, b) {
                return a + b;
            }, 0);
        };
    })
