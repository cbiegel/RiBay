angular.module('myApp.service', [])

    .service('UserService', ['$cookies', function ($cookies) {

        var self = this;

        this.getSession = function () {
            var sessionString = $cookies.get('session');
            if (sessionString) {
                // object in session is double escaped. so double parse the string
                var sessionObject = JSON.parse(JSON.parse(sessionString));
                return sessionObject;
            }
            else {
                // no session
                return null;
            }
        };

        this.getLoggedInUser = function () {
            var session = self.getSession();
            return (session == null) ? null : session.user;
        };

        this.isLoggedIn = function () {
            return self.getLoggedInUser() != null;
        };

    }])

    .service('waitingService', ['$rootScope', '$timeout', function ($rootScope, $timeout) {

        var delay = 200; // delay after the loading animation will be shown

        $rootScope.blocking = false; // non-blocking at start
        $rootScope.showLoadingAnimation = false; // no loading animation at start

        var promise = null; // no pending promise at start

        this.startWaiting = function () {
            // block user interaction immediately
            $rootScope.blocking = true;

            // show loading animation after some delay. only do that if there is no pending start of animation yet
            if (!promise) {
                promise = $timeout(function () {
                    $rootScope.showLoadingAnimation = true;
                }, delay);
            }
        }

        this.endWaiting = function () {
            // cancel delayed start of loading animation
            if (promise) {
                $timeout.cancel(promise);
                promise = null;
            }

            // release block
            $rootScope.blocking = false;

            // remove loading animation
            $rootScope.showLoadingAnimation = false;
        }
    }]);
