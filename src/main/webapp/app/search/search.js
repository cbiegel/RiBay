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

    .controller('searchCtrl', ['$scope', '$routeParams', '$location', '$anchorScroll', 'searchService', 'waitingService', function ($scope, $routeParams, $location, $anchorScroll, searchService, waitingService) {

        $scope.query = undefined;

        $scope.$watch("query", function () {

            waitingService.startWaiting();

            // when query or its inner members changes -> load articles from server
            searchService.search($scope.query, function (data) {
                $scope.result = data;

                waitingService.endWaiting();
            });

        }, true);

        $scope.query = {
            text: decodeURIComponent($routeParams.text),
            movie: undefined, // TODO make configurable through ui
            imageOnly: true,
            genre: [],
            price_low: 0, // 0 euro
            price_high: 2000, // 20 euro
            rating_low: undefined,
            rating_high: undefined,
            votes_low: undefined, // TODO make configurable through ui
            votes_high: undefined, // TODO make configurable through ui
            pageInfo: {
                page_no: 1,
                page_size: 20
            }
            // TODO sort by
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

        $scope.genres = [
            'Action',
            'Adventure',
            'Animation',
            'Biography',
            'Comedy',
            'Crime',
            'Documentary',
            'Drama',
            'Family',
            'Fantasy',
            'Film-Noir',
            'Game-Show',
            'History',
            'Horror',
            'Lifestyle',
            'Music',
            'Musical',
            'Mystery',
            'News',
            'Reality-TV',
            'Romance',
            'Sci-Fi',
            'Short',
            'Sport',
            'Talk-Show',
            'Thriller',
            'Western',
            'War'
        ];

        $scope.priceSliderOptions = {
            min: $scope.query.price_low,
            max: $scope.query.price_high,
            floor: 0, // minimum 0 euro
            ceil: 2000, // maximum 20 euro
            step: 1, // 1 cent steps
            translate: function (value) {
                return '€' + (value / 100).toFixed(2);
            },
            onEnd: function () {
                // after releasing mouse
                $scope.query.price_low = $scope.priceSliderOptions.min;
                $scope.query.price_high = $scope.priceSliderOptions.max;
            }
        };

        $scope.sortCategory = $scope.sortCategories[0];

        $scope.onPageChange = function () {
            $anchorScroll('top');
        };

        $scope.searchAgain = function (text) {
            alert("search again " + text);
        };

    }]);
