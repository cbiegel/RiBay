/**
 * Created by Chris on 09.08.2016.
 */

'use strict';

angular.module('myApp.editUser', [])

    .config(['$routeProvider', function ($routeProvider) {
        $routeProvider.when('/userSettings', {
            templateUrl: 'userSettings/userSettings.html',
            controller: 'userSettingsCtrl'
        });
    }])

    .service('userSettingsService', ['$http', function ($http) {
        this.editUsername = function(dataObject, callback) {
            $http.post('/user/editName', dataObject).then(function (config) {
                if (config.data != "") {
                    var response = {message: 'Successfully changed user name.'};
                    callback(response);
                }
                else {
                    var response = {message: 'An error occurred while changing the user name. Please try again.'};
                    callback(response);
                }
            });
        };

        this.editPassword = function(dataObject, callback) {
            $http.post('/user/editPassword', dataObject).then(function (config) {
                if (config.data != "") {
                    var response = {message: 'Successfully changed password.'};
                    callback(response);
                }
                else {
                    var response = {message: 'An error occurred while changing the password. Please try again.'};
                    callback(response);
                }
            });
        };
    }])

    .controller('userSettingsCtrl', ['$scope', '$route', '$window', 'UserService', 'userSettingsService', function ($scope, $route, $window, UserService, userSettingsService) {
        $scope.user = undefined;
        $scope.newUsername = "";
        $scope.newPassword = "";
        $scope.isEditingName = false;
        $scope.isEditingPassword = false;

        if (UserService.getLoggedInUser() != null) {
            $scope.user = UserService.getLoggedInUser();
        }

        $scope.editUsername = function() {
            if($scope.newUsername != "") {
                $scope.user.name = $scope.newUsername;
                userSettingsService.editUsername($scope.user, function(response) {
                    $scope.switchEditingName();
                    $scope.user = UserService.getLoggedInUser();
                    //$route.reload();
                    $window.location.reload();
                });
            }
        }

        $scope.editPassword = function() {
            if($scope.newPassword != "") {
                $scope.user.password = $scope.newPassword;
                userSettingsService.editPassword($scope.user, function(response) {
                    $scope.switchEditingName();
                    $scope.user = UserService.getLoggedInUser();
                    //$route.reload();
                    $window.location.reload();
                });
            }
        }

        $scope.switchEditingName = function() {
            $scope.isEditingName = !$scope.isEditingName;
        }

        $scope.switchEditingPassword = function() {
            $scope.isEditingPassword = !$scope.isEditingPassword;
        }
    }]);

