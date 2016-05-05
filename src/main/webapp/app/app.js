'use strict';

// Declare app level module which depends on views, and components
angular.module('myApp', [
        'ngRoute',
        'ngCookies',
        'ngAnimate',
        'jsonFormatter',
        'ui.bootstrap',
        'myApp.home',
        'myApp.view1',
        'myApp.view2',
        'myApp.view3',
        'myApp.search',
        'myApp.product',
        'myApp.cart',
        'myApp.login',
        'myApp.loginSuccess',
        'myApp.status',
        'myApp.version',
        'myApp.register'
    ])

    .run(['$rootScope', '$location', '$log', '$http', function ($rootScope, $location, $log, $http) {
        $rootScope.$on("$locationChangeStart", function (event, next, current) {
            $log.info("location changing to:" + next);

            // when changing view -> check if logged in and set as global variable
            $http.get('/auth/loggedin').success(function (data) {
                $rootScope.loggedIn = data;
            });
        });
    }])

    .config(['$routeProvider', function ($routeProvider) {
        $routeProvider.otherwise({redirectTo: '/'});
    }])

    .service('imageService', ['$http', function ($http) {

        this.createImageURLFromId = function (imageId) {
            return "/image/" + imageId;
        };

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
            // TODO lazy load articles
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
        };

        $scope.search = function () {
            // switch to search view
            // TODO handle 'null' input
            // TODO handle input 'all' as category
            $location.path('search/' + encodeURIComponent($scope.searchText) + '/' + encodeURIComponent($scope.category.selected.value));

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

    }])

    .directive('myEnter', function () {
        return function (scope, element, attrs) {
            element.bind("keydown keypress", function (event) {
                if (event.which === 13) {
                    scope.$apply(function () {
                        scope.$eval(attrs.myEnter);
                    });

                    event.preventDefault();
                }
            });
        };
    })

    // http://stackoverflow.com/questions/11873570/angularjs-for-loop-with-numbers-ranges
    .filter('range', function () {
        return function (input, total) {
            total = parseInt(total);

            for (var i = 0; i < total; i++) {
                input.push(i);
            }

            return input;
        };
    })

    // http://stackoverflow.com/questions/15266671/angular-ng-repeat-in-reverse
    .filter('reverse', function () {
        return function (items) {
            return items.slice().reverse();
        };
    });

