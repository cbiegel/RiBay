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

    .service('statusService', ['$http', function ($http) {

        this.getBuckets = function (callback) {
            $http.get('/status/db/buckets').success(callback);
        };

        this.getKeys = function (bucket, callback) {
            $http.get('/status/db/keys?bucket=' + bucket).success(callback);
        }

        this.getValue = function (bucket, key, callback) {
            $http.get('/status/db/value?bucket=' + bucket + '&key=' + key).success(callback);
        }

        this.getClusterStatus = function (callback) {
            $http.get('/status/db/cluster').success(callback);
        };

    }])

    .controller('statusCtrl', ['$scope', 'statusService', function ($scope, statusService) {

        $scope.buckets = undefined;
        $scope.buckets_page = 1;
        $scope.keys = undefined;
        $scope.keys_page = 1;
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
                    $scope.keys_page = 1; // reset page on bucket change
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