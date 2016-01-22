/**
 * Created by Chris on 22.01.2016.
 */

angular.module('myApp.status', ['ngRoute', 'ngAnimate', 'ui.bootstrap'])

    .config(['$routeProvider', function ($routeProvider) {
        $routeProvider.when('/status', {
            templateUrl: 'status/status.html',
            controller: 'statusCtrl'
        });
    }])

    .controller('statusCtrl', ['$scope', '$http', 'Backend', function ($scope, $http, Backend) {
        $scope.commands = ['List Buckets', 'Cluster Status'];
        $scope.executeCommand = function(command) {
            switch(command) {
                case 'List Buckets':
                    $http.get(Backend.host + '/status/db/buckets').success(function (data) {
                        $scope.result = data;
                    })
                    break;
                case 'Cluster Status':
                    $http.get(Backend.host + '/status/db/cluster').success(function (data) {
                        $scope.result = data;
                    })
                    break;
                default:
                console.log("Error: Unknown status command.");
            }
        }
    }]);