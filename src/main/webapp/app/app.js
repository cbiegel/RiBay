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
    'myApp.register',
    'myApp.admin',
    'myApp.checkout'
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
        }

        this.getLoggedInUser = function () {
            var session = self.getSession();
            return (session == null) ? null : session.user;
        }

    }])

    .run(['$rootScope', '$location', '$log', 'UserService', function ($rootScope, $location, $log, UserService) {
        $rootScope.$on("$locationChangeStart", function (event, next, current) {
            $log.debug("location changing to:" + next);

            // when changing view -> check if logged in and set as global variable
            $rootScope.loggedIn = UserService.getLoggedInUser();
        });
    }])

    .config(['$routeProvider', function ($routeProvider) {
        $routeProvider.otherwise({redirectTo: '/'});
    }])

    .service('imageService', [function () {

        this.createImageURLFromId = function (imageId) {
            if (imageId) {
                if (imageId.indexOf("http") == 0) {
                    // image link in db, not the image data itself
                    return imageId;
                }
                else {
                    //resolve path
                    return "/image/" + imageId;
                }
            }
            else {
                // attribute not set
                return undefined;
            }
        };

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
    })

    .filter('imageIdToUrl', ['imageService', function (imageService) {
        return function (imageId) {
            return imageService.createImageURLFromId(imageId);
        };
    }]);

Array.prototype.toMap = function (keyFunction, valueFunction) {
    var result = new Map();
    this.forEach(function (elem) {
        var key = keyFunction(elem);
        var value = valueFunction(elem);
        result.set(key, value);
    });
    return result;
}

// returns this map without the other map (identified by their keys)
Map.prototype.without = function (otherMap) {
    var result = new Map();
    this.forEach(function (value, key) {
        // the result only contains pairs that are in this map but not in the other map
        if (!otherMap.has(key)) {
            result.set(key, value);
        }
    });
    return result;
}

Map.prototype.valueList = function () {
    var result = [];
    this.forEach(function (value) {
        result.push(value);
    });
    return result;
}
