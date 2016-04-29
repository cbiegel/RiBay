/**
 * Created by CD on 28.04.2016.
 */

angular.module('myApp.home', ['ngRoute'])

    .config(['$routeProvider', function ($routeProvider) {
        $routeProvider.when('/', {
            templateUrl: 'home/home.html',
            controller: 'HomeCtrl'
        });
    }])

    .controller('HomeCtrl', ['$scope', '$window', function ($scope, $window) {
        $scope.focused = undefined;

        $scope.showDetails = function (item) {
            $window.alert(item);
        };
    }]);