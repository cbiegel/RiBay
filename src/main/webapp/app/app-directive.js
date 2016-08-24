angular.module('myApp.directive', [])

    .directive('myEnter', function () {
        return function (scope, element, attrs) {
            element.bind("keydown keypress", function (event) {
                if (event.which === 13) {
                    scope.$apply(function () {
                        scope.$eval(attrs.myEnter);
                    });

                    event.preventDefault();
                }
            });
        };
    })

    // dynamically set target attribute
    .directive('myTarget', function () {
        return {
            restrict: 'A',
            link: function (scope, element, attrs) {
                var target = attrs.myTarget;
                if (target) {
                    element.attr("target", target);
                }
            }
        };
    });