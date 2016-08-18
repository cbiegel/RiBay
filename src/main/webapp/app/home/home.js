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

    .service('homeService', ['$http', 'UserService', function ($http, UserService) {

        this.getLastVisitedArticles = function () {
            var result = [];
            $http.get('/article/lastVisited').then(function (response) {
                result.pushArray(response.data); // append
            });
            return result;
        };

        this.getRecommendations = function () {
            var result = [];
            if (UserService.isLoggedIn()) {
                $http.get('/article/recommended').then(function (response) {
                    result.pushArray(response.data); // append
                });
            }
            return result;
        };

    }])

    .controller('HomeCtrl', ['$scope', '$window', 'homeService', function ($scope, $window, homeService) {
        $scope.lastVisited = homeService.getLastVisitedArticles();
        $scope.recommendations = homeService.getRecommendations();
        $scope.wishList = []; // TODO implement?
        $scope.inspiredByWishList = []; // TODO implement?

        $scope.showDetails = function (item) {
            $window.alert(item);
        };
    }])

    .directive('homeItemrow', function () {
        return {
            restrict: 'E',
            scope: {
                caption: '=',
                items: '=',
                onDetails: '='
            },
            templateUrl: 'home/templates/home-itemrow.html'
        };
    });
