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

    .service('statusService', ['$http', 'waitingService', function ($http, waitingService) {

        this.getBuckets = function (callback) {
            waitingService.startWaiting();
            $http.get('/status/db/buckets').then(function (response) {
                callback(response.data);
                waitingService.endWaiting();
            }, function () {
                waitingService.endWaiting();
            });
        };

        this.getKeys = function (bucket, callback) {
            waitingService.startWaiting();
            $http.get('/status/db/keys?bucket=' + bucket).then(function (response) {
                callback(response.data);
                waitingService.endWaiting();
            }, function () {
                waitingService.endWaiting();
            });
        }

        this.getValue = function (bucket, key, callback) {
            waitingService.startWaiting();
            $http.get('/status/db/value?bucket=' + bucket + '&key=' + key).then(function (response) {
                callback(response.data);
                waitingService.endWaiting();
            }, function () {
                waitingService.endWaiting();
            });
        }

        this.getBucketProperties = function (bucket, callback) {
            $http.get('/status/db/bucket_properties?bucket=' + bucket).success(callback);
        }

        this.getClusterStatus = function (callback) {
            $http.get('/status/db/cluster').success(callback);
        };

    }])
    .controller('statusCtrl', ['$scope', 'statusService', function ($scope, statusService) {

        $scope.buckets = undefined;
        $scope.buckets_page = 1;
        $scope.bucket_properties = undefined;
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
                statusService.getBucketProperties(bucket, function (data) {
                    $scope.bucket_properties = data;
                });
            }
            else {
                $scope.keys = undefined;
                $scope.bucket_properties = undefined;
            }
        };

        $scope.selectKey = function (key) {
            $scope.selectedKey = key;

            if (key) {
                if ($scope.selectedBucket == "images") {
                    $scope.value = "/image/" + key;
                }
                else {
                    statusService.getValue($scope.selectedBucket, key, function (data) {
                        $scope.value = data;
                    });
                }
            }
            else {
                $scope.value = undefined;
            }
        };

        statusService.getClusterStatus(function (data) {
            $scope.clusterStatus = data;
        });


    }]);


