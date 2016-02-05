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

        this.getKeys = function (bucket, callback) {
            $http.get(Backend.host + '/status/db/keys?bucket=' + bucket).success(callback);
        }

        this.getValue = function (bucket, key, callback) {
            $http.get(Backend.host + '/status/db/value?bucket=' + bucket + '&key=' + key).success(callback);
        }

        this.getClusterStatus = function (callback) {
            $http.get(Backend.host + '/status/db/cluster').success(callback);
        };

    }])

    .controller('statusCtrl', ['$scope', 'statusService', function ($scope, statusService) {

        $scope.buckets = undefined;
        $scope.keys = undefined;
        $scope.value = undefined;

        $scope.selectedBucket = undefined;
        $scope.selectedKey = undefined;

        statusService.getBuckets(function (data) {
            $scope.buckets = data;
        });

        $scope.selectBucket = function (bucket) {
            $scope.selectKey(undefined);
            $scope.selectedBucket = bucket;

            if (bucket) {
                statusService.getKeys(bucket, function (data) {
                    $scope.keys = data;
                });
            }
            else {
                $scope.keys = undefined;
            }
        };

        $scope.selectKey = function (key) {
            $scope.selectedKey = key;

            if (key) {
                statusService.getValue($scope.selectedBucket, key, function (data) {
                    $scope.value = data;
                });
            }
            else {
                $scope.value = undefined;
            }
        };

        statusService.getClusterStatus(function (data) {
            $scope.clusterStatus = data;
        });


    }]);