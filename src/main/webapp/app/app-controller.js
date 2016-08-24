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

        $scope.getArticlesAsync = function (val) {
            return $http.get('/article/typeahead/' + encodeURIComponent(val)).then(function (response) {
                return response.data;
            });
        };

        $scope.search = function () {
            // switch to search view
            $location.path('search/text/' + encodeURIComponent($scope.searchText));

            // reset textfield content
            // $scope.searchText = '';
        };

        $scope.typeaheadOnSelect = function ($item, $model, $label, $event) {
            $location.path('product/' + $item.id);
        };

        $scope.typeaheadInputFormatter = function (item) {
            return item.name;
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
            {url: '#/orders', caption: 'Your Orders', icon: 'ship'},
            {url: '#/userSettings', caption: 'Your Account', icon: 'user'}
        ];

        $scope.genreMenu = genres.map(function (genre) {
            return {url: '#/search/genre/' + encodeURIComponent(genre), caption: genre};
        });

        $scope.toolMenu = [
            {url: '#/admin', caption: 'Manage Products', icon: 'usd'},
            {url: '#/status', caption: 'Status', icon: 'stethoscope'},
            {url: '../swagger-ui.html', target: '_blank', caption: 'API Documentation', icon: 'book'}
        ];

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
                icon: '=',
                caption1: '=',
                caption2: '=',
                items: '='
            },
            templateUrl: 'templates/headline-nav-button-dropdown.html'
        };
    });
