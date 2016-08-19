angular.module('myApp.controller', [])

    .controller('searchController', function ($scope, $location, $http) {
        $scope.category = {
            options: [
                {
                    /* name depends on language, value could be a key for db */
                    name: 'All',
                    value: ''
                } // TODO more options
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
            $location.path('search/text/' + encodeURIComponent($scope.searchText));

            // reset textfield content
            // $scope.searchText = '';
        };
    })

    .controller('navController', ['$rootScope', '$scope', '$http', '$location', 'genres', function ($rootScope, $scope, $http, $location, genres) {

        $scope.logout = function () {
            $http.post('/auth/logout').then(function success() {
                $rootScope.loggedIn = undefined;
                $location.url("/");
            });
        };

        $scope.usermenu = [
            {url: '#/orders', caption: 'Your Orders'},
            {url: '#/userSettings', caption: 'Your Account'}
        ];

        $scope.genreMenu = genres.map(function (genre) {
            return {url: '#/search/genre/' + encodeURIComponent(genre), caption: genre};
        });

    }])

    .directive('navButton', function () {
        return {
            restrict: 'E',
            scope: {
                href: '=',
                icon: '=',
                caption1: '=',
                caption2: '='
            },
            templateUrl: 'templates/headline-nav-button.html'
        };
    })

    .directive('navButtonDropdown', function () {
        return {
            restrict: 'E',
            scope: {
                caption1: '=',
                caption2: '=',
                items: '='
            },
            templateUrl: 'templates/headline-nav-button-dropdown.html'
        };
    });
