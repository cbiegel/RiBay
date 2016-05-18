/**
 * Created by Christian on 29.12.2015.
 */

'use strict';

angular.module('myApp.search', [])

    .config(['$routeProvider', function ($routeProvider) {
        $routeProvider.when('/search/:text', {
            templateUrl: 'search/search.html',
            controller: 'searchCtrl'
        });
    }])

    .service('searchService', ['$http', 'imageService', function ($http, imageService) {

        // TODO add parameters for filter and sort-by
        this.search = function (query, callback) {

            $http.post('/article/search', query).then(
                function success(response) {
                    var responseData = response.data;

                    var data = {
                        suggestion: undefined, // TODO get real suggestion from backend
                        start: ((query.pageInfo.page_no - 1) * query.pageInfo.page_size) + 1,
                        end: ((query.pageInfo.page_no - 1) * query.pageInfo.page_size) + responseData.articles.length,
                        total_size: responseData.numResults,
                        list: responseData.articles
                    };

                    data.list.forEach(function (item) {
                        // resolve images
                        item.image = imageService.createImageURLFromId(item.image);

                        // add float attribute
                        item.mediumRating = (item.sumRatings / item.votes).toFixed(0); // round
                    });

                    callback(data);
                },
                function error(response) {
                    // TODO: handle other error
                }
            );
        };

    }])

    .controller('searchCtrl', ['$scope', '$routeParams', '$location', '$anchorScroll', 'searchService', function ($scope, $routeParams, $location, $anchorScroll, searchService) {

        $scope.query = undefined;

        $scope.$watch("query", function () {

            // when query or its inner members changes -> load articles from server
            searchService.search($scope.query, function (data) {
                $scope.result = data;
            });

        }, true);

        $scope.query = {
            text: decodeURIComponent($routeParams.text),
            movie: undefined, // TODO make configurable through ui
            imageOnly: true,
            genre: undefined, // TODO make configurable through ui
            price_low: 300, // 3 euro // TODO make configurable through ui
            price_high: 2000, // 20 euro // TODO make configurable through ui
            rating_low: undefined, // TODO make configurable through ui
            rating_high: undefined, // TODO make configurable through ui
            votes_low: undefined, // TODO make configurable through ui
            votes_high: undefined, // TODO make configurable through ui
            pageInfo: {
                page_no: 1,
                page_size: 20
            }
            // TODO sort by
        };

        // TODO replace categories with genre multi-select
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

        $scope.onPageChange = function () {
            $anchorScroll('top');
        };

        $scope.searchAgain = function (text) {
            alert("search again " + text);
        };

    }]);
