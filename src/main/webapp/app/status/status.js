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

        this.getBucketTypes = function (callback) {
            waitingService.startWaiting();
            $http.get('/status/db/bucketTypes').then(function (response) {
                callback(response.data);
                waitingService.endWaiting();
            }, function () {
                waitingService.endWaiting();
            });
        };

        this.getBuckets = function (bucketType, callback) {
            waitingService.startWaiting();
            $http.get('/status/db/buckets?bucketType=' + bucketType).then(function (response) {
                callback(response.data);
                waitingService.endWaiting();
            }, function () {
                waitingService.endWaiting();
            });
        };

        this.getKeys = function (bucketType, bucket, callback) {
            waitingService.startWaiting();
            $http.get('/status/db/keys?bucketType=' + bucketType + '&bucket=' + bucket).then(function (response) {
                callback(response.data);
                waitingService.endWaiting();
            }, function () {
                waitingService.endWaiting();
            });
        }

        this.getValue = function (bucketType, bucket, key, callback) {
            waitingService.startWaiting();
            $http.get('/status/db/value?bucketType=' + bucketType + '&bucket=' + bucket + '&key=' + key).then(function (response) {
                callback(response.data);
                waitingService.endWaiting();
            }, function () {
                waitingService.endWaiting();
            });
        }

        this.getBucketProperties = function (bucketType, bucket, callback) {
            $http.get('/status/db/bucket_properties?bucketType=' + bucketType + '&bucket=' + bucket).success(callback);
        }

        this.getClusterStatus = function (callback) {
            $http.get('/status/db/cluster').success(callback);
        };

        this.getRingStatus = function (callback) {
            $http.get('/status/db/ringstatus').success(callback);
        };

    }])
    .controller('statusCtrl', ['$scope', 'statusService', function ($scope, statusService) {

        $scope.bucketTypes = undefined;
        $scope.bucketTypes_page = 1;
        $scope.buckets = undefined;
        $scope.buckets_page = 1;
        $scope.bucket_properties = undefined;
        $scope.keys = undefined;
        $scope.keys_page = 1;
        $scope.value = undefined;
        $scope.ringstatus = undefined;
        $scope.selectedBucketType = undefined;
        $scope.selectedBucket = undefined;
        $scope.selectedKey = undefined;

        statusService.getBucketTypes(function (data) {
            $scope.bucketTypes = data;
        });

        $scope.selectBucketType = function (bucketType) {
            $scope.selectBucket(undefined);
            $scope.selectedBucketType = bucketType;

            if (bucketType) {
                statusService.getBuckets(bucketType, function (data) {
                    $scope.buckets = data;
                    $scope.buckets_page = 1; // reset page on bucket change
                });
            }
            else {
                $scope.buckets = undefined;
            }
        }

        $scope.selectBucket = function (bucket) {
            $scope.selectKey(undefined);
            $scope.selectedBucket = bucket;

            if (bucket) {
                statusService.getKeys($scope.selectedBucketType, bucket, function (data) {
                    $scope.keys = data;
                    $scope.keys_page = 1; // reset page on bucket change
                });
                statusService.getBucketProperties($scope.selectedBucketType, bucket, function (data) {
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
                    statusService.getValue($scope.selectedBucketType, $scope.selectedBucket, key, function (data) {
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

        statusService.getRingStatus(function (data) {
            $scope.ringstatus = "\r\n" + data;
        });
    }]);


