angular.module('myApp.orders', [])

    .config(['$routeProvider', function ($routeProvider) {
        $routeProvider.when('/orders/', {
            templateUrl: 'orders/orders.html',
            controller: 'ordersCtrl'
        });
    }])

    // TODO as factory

    .service('ordersService', ['$http', function ($http) {
        this.getOrders = function () {
            // result is an object that has data and a continuation
            var result = {
                data: [],
                continuation: undefined
            };
            result.hasNextPage = function () {
                // only when continuation is undefined (initial) or set (not null)
                return result.continuation !== null;
            };
            result.nextPage = function () {
                if (this.hasNextPage()) {
                    var url = '/user/orders';
                    if (result.continuation !== undefined) {
                        url += '?continuation=' + result.continuation;
                    }
                    $http.get(url).then(function (response) {
                        result.data = result.data.concat(response.data.orders); // append
                        result.continuation = response.data.continuation;
                    });
                }
            };
            return result;
        };
    }])

    .controller('ordersCtrl', ['$scope', '$rootScope', '$timeout', '$window', '$location', 'ordersService', 'UserService', function ($scope, $rootScope, $timeout, $window, $location, ordersService, UserService) {
        if (!UserService.isLoggedIn()) {
            $location.url('/login/');
            return;
        }

        $scope.orders = ordersService.getOrders();
        $scope.orders.nextPage();
    }]);
