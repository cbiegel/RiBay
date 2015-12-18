'use strict';

angular.module('myApp.view3', ['ngRoute'])

    .config(['$routeProvider', function ($routeProvider) {
        $routeProvider.when('/view3', {
            templateUrl: 'view3/view3.html',
            controller: 'View3Ctrl'
        });
    }])

    .controller('View3Ctrl', [function () {
    }])

    .controller('myCtrl', function ($scope, $http) {
        $http.get('http://localhost:8080/get-welcome?name=spanish').
        success(function (data) {
            $scope.greeting = data;
        });
    });
