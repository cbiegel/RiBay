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

    .service('checkoutService', ['$http', '$window', 'imageService', function ($http, $window, imageService) {
        this.finishCheckout = function (order, successHandler) {
            // TODO http call
        }

        this.handleCartChanged = function (orderOld, orderNew) {
            // TODO set order reference
            // TODO show changes
        }
    }])

    .controller('checkoutCtrl', ['$scope', '$rootScope', '$timeout', 'checkoutService', function ($scope, $rootScope, $timeout, checkoutService) {
        $scope.order = $rootScope.order;

        $scope.finishOrder = function () {
            checkoutService.finishCheckout($scope.order, function (orderFinished) {
                // TODO on success: show finished order
            });
        }
    }])
