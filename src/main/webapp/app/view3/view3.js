'use strict';

angular.module('myApp.view3', [])

    .config(['$routeProvider', function ($routeProvider) {
        $routeProvider.when('/view3', {
            templateUrl: 'view3/view3.html',
            controller: 'View3Ctrl'
        });
    }])

    .controller('View3Ctrl', ['$scope', '$http', function ($scope, $http) {
        $http.get('/get-welcome?name=spanish').success(function (data) {
            $scope.greeting = data;
        });
    }]);
