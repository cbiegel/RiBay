angular.module('myApp.filter', [])

// http://stackoverflow.com/questions/11873570/angularjs-for-loop-with-numbers-ranges
    .filter('range', function () {
        return function (input, total) {
            total = parseInt(total);

            for (var i = 0; i < total; i++) {
                input.push(i);
            }

            return input;
        };
    })

    // http://stackoverflow.com/questions/15266671/angular-ng-repeat-in-reverse
    .filter('reverse', function () {
        return function (items) {
            return items.slice().reverse();
        };
    })

    .filter('imageIdToUrl', ['imageService', function (imageService) {
        return function (imageId) {
            return imageService.createImageURLFromId(imageId);
        };
    }])

    .filter('sumPrice', function () {
        return function (articles) {
            return articles.map(function (article) {
                return article.price * article.quantity;
            }).reduce(function (a, b) {
                return a + b;
            }, 0);
        };
    })

    .filter('articleIdToUrl', function () {
        return function (articleId) {
            return '#/product/' + articleId;
        };
    });