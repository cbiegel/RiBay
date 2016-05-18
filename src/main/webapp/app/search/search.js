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
                    var data = {
                        // TODO get real data from backend
                        suggestion: "My suggestion", // set undefined if no suggestion
                        page_no: query.page_no,
                        page_size: query.page_size,
                        start: 1, // TODO
                        end: 20, // TODO
                        total_size: 200, // TODO
                        list: response.data
                    };

                    data.list.forEach(function (currentValue) {
                        if (currentValue.image) {
                            // resolve images
                            var url = imageService.createImageURLFromId(currentValue.image);
                            currentValue.image = url;
                        }
                    });

                    callback(data);
                },
                function error(response) {
                    // TODO: handle other error
                }
            );
        };

    }])

    .controller('searchCtrl', ['$scope', '$routeParams', 'searchService', function ($scope, $routeParams, searchService) {
        $scope.query = {
            text: decodeURIComponent($routeParams.text),
            movie: undefined,
            imageOnly: true,
            genre: undefined,
            price_low: 300, // 3 euro
            price_high: 2000, // 20 euro
            rating_low: undefined,
            rating_high: undefined,
            votes_low: undefined,
            votes_high: undefined,
            pageInfo: {
                page_no: 1,
                page_size: 20
            }
            // TODO make all field configurable through ui
            // TODO sort by
        };

        searchService.search($scope.query, function (data) {
            $scope.result = data;
        });

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
