'use strict';

// Declare app level module which depends on views, and components
angular.module('myApp', [
    'ngRoute',
    'myApp.view1',
    'myApp.view2',
    'myApp.view3',
    'myApp.search',
    'myApp.version'
]).
    config(['$routeProvider', function ($routeProvider) {
        $routeProvider.otherwise({redirectTo: '/view1'});
    }])

    .controller('searchController', function ($scope, $location) {
        $scope.category = {
            optionAll: {
                /* name depends on language, value could be a key for db */
                name: 'All',
                value: ''
            },
            option1: {
                name: 'option1Name',
                value: 'option1Value'
            },

            selected: ''
        };
        $scope.searchText = '';

        $scope.search = function () {
            // switch to search view
            // TODO handle 'null' input
            // TODO handle input 'all' as category
            $location.path('search/' + encodeURIComponent($scope.searchText) + '/' + encodeURIComponent($scope.category.selected));

            // reset textfield content
            // $scope.searchText = '';
        };
    });
