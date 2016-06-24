/**
 * Created by CD on 24.06.2016.
 */

'use strict';

angular.module('myApp.admin', [])

    .config(['$routeProvider', function ($routeProvider) {
        $routeProvider.when('/admin', {
            templateUrl: 'admin/admin.html',
            controller: 'adminCtrl'
        });
    }])

    .service('adminService', ['$http', function ($http) {

        this.changeStock = function (product, diff) {
            var id = product.id;

            $http.put('/article/changeStock/' + id + '/' + diff + '/?returnNewValue=true').then(
                function success(response) {
                    var newValue = response.data;
                    product.stock = newValue;
                },
                function error(response) {
                    // TODO handle error
                }
            );
        }

        this.confirmPrice = function (product) {
            var id = product.id;
            var price = product.price;

            $http.put('/article/setPrice/' + id + '/' + price).then(
                function success(response) {
                    // TODO handle success
                },
                function error(response) {
                    // TODO handle error
                }
            );
        }

    }])

    .controller('adminCtrl', ['$scope', 'productService', 'adminService', function ($scope, productService, adminService) {

        $scope.searchText = '';
        $scope.searchResults = [];

        $scope.search = function () {
            var id = $scope.searchText;

            if (id && (id.length > 0)) {
                productService.getProductDetails(id, function (data) {
                    $scope.searchResults = [data];
                });
            }
            else {
                $scope.searchResults = [];
            }
        }

        $scope.changeStock = adminService.changeStock;

        $scope.confirmPrice = adminService.confirmPrice;

        // TODO remove test
        $scope.searchText = '1000560';
        $scope.search();

    }])
