angular.module('myApp.controller', [])

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
