'use strict';

// Declare app level module which depends on views, and components
angular.module('myApp', [
    'ngRoute',
    'ngCookies',
    'ngAnimate',
    'jsonFormatter',
    'rzModule',
    'checklist-model',
    'ui.bootstrap',
    'picardy.fontawesome',
    'myApp.constants',
    'myApp.service',
    'myApp.controller',
    'myApp.filter',
    'myApp.directive',
    'myApp.home',
    'myApp.search',
    'myApp.product',
    'myApp.cart',
    'myApp.login',
    'myApp.status',
    'myApp.version',
    'myApp.register',
    'myApp.admin',
    'myApp.checkout',
    'myApp.orders',
    'myApp.editUser'
])

    .run(['$rootScope', 'UserService', function ($rootScope, UserService) {
        $rootScope.$on("$locationChangeStart", function (event, next, current) {
            // when changing view -> check if logged in and set as global variable
            $rootScope.loggedIn = UserService.getLoggedInUser();
        });
    }])

    .config(['$routeProvider', function ($routeProvider) {
        $routeProvider.otherwise({redirectTo: '/'});
    }]);
