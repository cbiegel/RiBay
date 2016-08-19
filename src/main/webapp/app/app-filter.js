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

    // http://stackoverflow.com/questions/11540157/using-comma-as-list-separator-with-angularjs
    .filter('joinBy', function () {
        return function (input, delimiter) {
            return (input || []).join(delimiter || ',');
        };
    })

    .filter('imageIdToUrl', function () {
        return function (imageId) {
            if (imageId) {
                if (imageId.indexOf("http") == 0) {
                    // image link in db, not the image data itself
                    return imageId;
                }
                else if(imageId.indexOf("{") == 0)
                {
                    // imageId is a JSON string?
                    return undefined;
                }
                else {
                    //resolve path
                    return "/image/" + imageId;
                }
            }
            else {
                // attribute not set
                return undefined;
            }
        };
    })

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