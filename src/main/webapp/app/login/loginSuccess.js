/**
 * Created by Chris on 15.01.2016.
 */
'use strict';

angular.module('myApp.loginSuccess', ['ngRoute', 'ngAnimate', 'ui.bootstrap'])

.config(['$routeProvider', function ($routeProvider) {
    $routeProvider.when('/loginSuccess', {
        templateUrl: 'login/loginSuccess.html',
        controller: 'loginSuccessCtrl'
        });
    }])


    .controller('loginSuccessCtrl', ['$scope', function ($scope) {

    }])

