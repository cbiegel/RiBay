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
    'myApp.filter',
    'myApp.directive',
    'myApp.home',
    'myApp.search',
    'myApp.product',
    'myApp.cart',
    'myApp.login',
    'myApp.loginSuccess',
    'myApp.status',
    'myApp.version',
    'myApp.register',
    'myApp.admin',
    'myApp.checkout',
    'myApp.orders',
    'myApp.editUser'
])

    .service('UserService', ['$cookies', function ($cookies) {

        var self = this;

        this.getSession = function () {
            var sessionString = $cookies.get('session');
            if (sessionString) {
                // object in session is double escaped. so double parse the string
                var sessionObject = JSON.parse(JSON.parse(sessionString));
                return sessionObject;
            }
            else {
                // no session
                return null;
            }
        };

        this.getLoggedInUser = function () {
            var session = self.getSession();
            return (session == null) ? null : session.user;
        };

        this.isLoggedIn = function () {
            return self.getLoggedInUser() != null;
        };

    }])

    .run(['$rootScope', 'UserService', function ($rootScope, UserService) {
        $rootScope.$on("$locationChangeStart", function (event, next, current) {
            // when changing view -> check if logged in and set as global variable
            $rootScope.loggedIn = UserService.getLoggedInUser();
        });
    }])

    .config(['$routeProvider', function ($routeProvider) {
        $routeProvider.otherwise({redirectTo: '/'});
    }])

    .service('waitingService', ['$rootScope', '$timeout', function ($rootScope, $timeout) {

        var delay = 200; // delay after the loading animation will be shown

        $rootScope.blocking = false; // non-blocking at start
        $rootScope.showLoadingAnimation = false; // no loading animation at start

        var promise = null; // no pending promise at start

        this.startWaiting = function () {
            // block user interaction immediately
            $rootScope.blocking = true;

            // show loading animation after some delay. only do that if there is no pending start of animation yet
            if (!promise) {
                promise = $timeout(function () {
                    $rootScope.showLoadingAnimation = true;
                }, delay);
            }
        }

        this.endWaiting = function () {
            // cancel delayed start of loading animation
            if (promise) {
                $timeout.cancel(promise);
                promise = null;
            }

            // release block
            $rootScope.blocking = false;

            // remove loading animation
            $rootScope.showLoadingAnimation = false;
        }
    }])

    .controller('searchController', function ($scope, $location, $http) {
        $scope.category = {
            options: [
                {
                    /* name depends on language, value could be a key for db */
                    name: 'All',
                    value: ''
                },
                {
                    name: 'option1Name',
                    value: 'option1Value'
                }
            ],

            selected: {
                name: 'All',
                value: ''
            }
        };

        $scope.searchText = '';

        $scope.getArticles = function (val) {
            // TODO lazy load article suggestions
            /*
             return $http.get('//maps.googleapis.com/maps/api/geocode/json', {
             params: {
             address: val,
             sensor: false
             }
             }).then(function (response) {
             return response.data.results.map(function (item) {
             return item.formatted_address;
             });
             });
             */
            return [];
        };

        $scope.search = function () {
            // switch to search view
            // TODO handle 'null' input
            // TODO handle input 'all' as category
            // TODO no category!
            $location.path('search/' + encodeURIComponent($scope.searchText));

            // reset textfield content
            // $scope.searchText = '';
        };
    })

    .controller('navController', ['$rootScope', '$scope', '$http', function ($rootScope, $scope, $http) {

        $scope.logout = function () {
            $http.post('/auth/logout').success(function (data) {
                $rootScope.loggedIn = undefined;
            });
        };

    }]);
