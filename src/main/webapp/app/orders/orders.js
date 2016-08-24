angular.module('myApp.orders', [])

    .config(['$routeProvider', function ($routeProvider) {
        $routeProvider
            .when('/orders/', {
                templateUrl: 'orders/orders.html',
                controller: 'ordersCtrl'
            })
            .when('/adminOrders/', {
                templateUrl: 'orders/orders.html',
                controller: 'adminOrdersCtrl'
            });
    }])

    .factory('ordersService', ['$http', function ($http) {
        return {
            getOrders: function (path) {
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
                        var url = path;
                        if (result.continuation !== undefined) {
                            url += '?continuation=' + encodeURIComponent(result.continuation);
                        }
                        $http.get(url).then(function (response) {
                            result.data = result.data.concat(response.data.orders); // append
                            result.continuation = response.data.continuation;
                        });
                    }
                };
                return result;
            }
        };
    }])

    .controller('ordersCtrl', ['$scope', '$location', 'ordersService', 'UserService', function ($scope, $location, ordersService, UserService) {
        if (!UserService.isLoggedIn()) {
            $location.url('/login/');
            return;
        }

        $scope.title = 'Your orders';
        $scope.orders = ordersService.getOrders('/user/orders');
        $scope.orders.nextPage();
        $scope.showUser = false;
    }])

    .controller('adminOrdersCtrl', ['$scope', 'ordersService', function ($scope, ordersService) {
        $scope.title = 'Recent orders';
        $scope.orders = ordersService.getOrders('/admin/orders');
        $scope.orders.nextPage();
        $scope.showUser = true;
    }]);
