/**
 * Created by Christian on 29.12.2015.
 */

'use strict';

angular.module('myApp.search', [])

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
            text: decodeURIComponent($routeParams.text),
            page_no: 1, // TODO from url
            page_size: 6 // TODO from url
            // TODO sort by
        };

        $scope.result = {
            suggestion: "My suggestion", // set undefined if no suggestion
            page_no: $scope.search.page_no,
            page_size: $scope.search.page_size,
            start: 1, // TODO
            end: 6, // TODO
            total_size: 200, // TODO
            list: [{
                id: "abc",
                image: "http://placehold.it/200x400",
                name: "Name of article 1",
                price: 12.34,
                rating: 4
            }, {
                id: "abc",
                image: "http://placehold.it/200x400",
                name: "Name of article 2",
                price: 12.34,
                rating: 4
            }, {
                id: "abc",
                image: "http://placehold.it/200x400",
                name: "Name of article 3",
                price: 12.34,
                rating: 4
            }, {
                id: "abc",
                image: "http://placehold.it/200x400",
                name: "Name of article 4",
                price: 12.34,
                rating: 4
            }, {
                id: "abc",
                image: "http://placehold.it/200x400",
                name: "Name of article 5",
                price: 12.34,
                rating: 4
            }, {
                id: "abc",
                image: "http://placehold.it/200x400",
                name: "Name of article 6",
                price: 12.34,
                rating: 4
            }]
        };

        $scope.sortCategories = [{
            id: "1",
            label: "Relevance"
        }, {
            id: "2",
            label: "Price: Low to High"
        }, {
            id: "3",
            label: "Price: High to Low"
        }, {
            id: "4",
            label: "Avg. Customer Review"
        }, {
            id: "5",
            label: "Newest Arrivals"
        }];

        $scope.sortCategory = $scope.sortCategories[0];

        $scope.searchAgain = function (text) {
            alert("search again " + text);
        };

    }]);
