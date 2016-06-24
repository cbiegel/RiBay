/**
 * Created by CD on 24.06.2016.
 */

'use strict';

angular.module('myApp.admin', [])

    .config(['$routeProvider', function ($routeProvider) {
        $routeProvider.when('/admin', {
            templateUrl: 'admin/admin.html',
            controller: 'adminCtrl'
        });
    }])

    .controller('adminCtrl', [function () {

    }])
