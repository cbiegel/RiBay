/**
 * Created by Christian on 29.12.2015.
 */

'use strict';

angular.module('myApp.search', ['ngRoute'])

    .config(['$routeProvider', function ($routeProvider) {
        $routeProvider.when('/search/:text/:category', {
            templateUrl: 'search/search.html',
            controller: 'searchCtrl'
        });
        $routeProvider.when('/search/:text', {
            templateUrl: 'search/search.html',
            controller: 'searchCtrl'
        });
    }])

    .controller('searchCtrl', ['$scope', '$routeParams', function ($scope, $routeParams) {
        $scope.search = {
            category: decodeURIComponent($routeParams.category) || '',
            text: decodeURIComponent($routeParams.text)
        };

        $scope.result = {
            start: 1, // TODO
            end: 1, // TODO
            size: 0, // TODO
            list: [] // TODO
        };

    }]);
