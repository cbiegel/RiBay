/**
 * Created by CD on 28.04.2016.
 */

angular.module('myApp.home', ['ngRoute'])

    .config(['$routeProvider', function ($routeProvider) {
        $routeProvider.when('/', {
            templateUrl: 'home/home.html',
            controller: 'HomeCtrl'
        });
    }])

    .service('homeService', ['$http', 'imageService', function ($http, imageService) {

        this.getLastVisitedArticles = function (callback) {
            $http.get('/article/lastvisited').success(function (data) {

                data.forEach(function (article) {
                    // resolve images
                    article.image = imageService.createImageURLFromId(article.image);
                });

                callback(data);
            });
        }

    }])

    .controller('HomeCtrl', ['$scope', '$window', 'homeService', function ($scope, $window, homeService) {
        $scope.focused = undefined;

        $scope.lastVisited = undefined;

        homeService.getLastVisitedArticles(function (data) {
            $scope.lastVisited = data;
        });

        $scope.showDetails = function (item) {
            $window.alert(item);
        };
    }]);