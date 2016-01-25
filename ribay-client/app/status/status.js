/**
 * Created by Chris on 22.01.2016.
 */

angular.module('myApp.status', [])

    .config(['$routeProvider', function ($routeProvider) {
        $routeProvider.when('/status', {
            templateUrl: 'status/status.html',
            controller: 'statusCtrl'
        });
    }])

    .service('statusService', ['$http', 'Backend', function ($http, Backend) {

        this.getBuckets = function (callback) {
            $http.get(Backend.host + '/status/db/buckets').success(callback);
        };

        this.getClusterStatus = function (callback) {
            $http.get(Backend.host + '/status/db/cluster').success(callback);
        };

    }])

    .controller('statusCtrl', ['$scope', 'statusService', function ($scope, statusService) {

        statusService.getBuckets(function (data) {
            $scope.buckets = data;
        });

        statusService.getClusterStatus(function (data) {
            $scope.clusterStatus = data;
        });


    }]);