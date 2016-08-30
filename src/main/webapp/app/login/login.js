/**
 * Created by Chris on 15.01.2016.
 */

'use strict';

angular.module('myApp.login', ['ngRoute', 'ngAnimate', 'ui.bootstrap'])

    .config(['$routeProvider', function ($routeProvider) {
        $routeProvider.when('/login', {
            templateUrl: 'login/login.html',
            controller: 'loginCtrl'
        });
    }])

    .service('loginService', ['$timeout', '$http', function ($timeout, $http) {

        this.login = function (emailAddress, password, callback) {
            var data = {
                emailAddress: emailAddress,
                password: password
            };

            $http.post('/auth/login', data).then(
                function success(response) {
                    // login successful
                    callback({user: response.data});
                },
                function error(response) {
                    // login failed
                    callback({message: 'E-Mail address or password is incorrect'});
                });
        };
    }])

    .controller('loginCtrl', ['$scope', '$location', '$filter', '$q', 'loginService', function ($scope, $location, $filter, $q, loginService) {
        $scope.login = function () {
            $scope.dataLoading = true;
            loginService.login($scope.email, $scope.password, function (response) {
                if (response.user) {
                    $location.url('/');
                }
                else {
                    $scope.error = response.message;
                    $scope.dataLoading = false;
                }
            });
        };
    }]);
