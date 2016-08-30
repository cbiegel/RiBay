/**
 * Created by Chris on 15.04.2016.
 */
'use strict';

angular.module('myApp.register', ['ngRoute', 'ngAnimate', 'ui.bootstrap'])

    .config(['$routeProvider', function ($routeProvider) {
        $routeProvider.when('/register', {
            templateUrl: 'register/register.html',
            controller: 'registerCtrl'
        });
    }])

    .service('registerService', ['$timeout', '$http', function ($timeout, $http) {

        this.register = function (email, name, password, callback) {

            var dataObject = {
                uuid: null,
                emailAddress: email,
                password: password,
                name: name
            };

            $http.post('/auth/register', dataObject).then(
                function success(response) {
                    //  register successful
                    callback({
                        user: response.data,
                        message: 'Successfully registered! Welcome, ' + response.data.name + "."
                    });
                },
                function error(response) {
                    callback({message: 'A user with this e-mail address already exists.'});
                });
        };

    }])

    .controller('registerCtrl', ['$scope', '$location', '$filter', '$q', 'registerService', function ($scope, $location, $filter, $q, registerService) {
        $scope.register = function () {
            $scope.dataLoading = true;
            $scope.registerSuccessful = false;
            registerService.register($scope.email, $scope.name, $scope.password, function (response) {

                if (response.user) {
                    $scope.registerSuccessful = true;
                    $scope.successMessage = response.message;
                    $scope.dataLoading = false;
                    $location.url("/");
                }
                else {
                    $scope.error = response.message;
                    $scope.dataLoading = false;
                }
            });
        };
    }]);
