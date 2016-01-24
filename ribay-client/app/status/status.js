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
        $scope.commands = ['List Buckets', 'Cluster Status'];
        $scope.executeCommand = function (command) {
            switch (command) {
                case $scope.commands[0]:
                    statusService.getBuckets(function (data) {
                        $scope.result = data;
                    });
                    break;
                case $scope.commands[1]:
                    statusService.getClusterStatus(function (data) {
                        $scope.result = data;
                    });
                    break;
                default:
                    console.log("Error: Unknown status command.");
            }
        }
    }]);