(function() {
    'use strict';
    angular
        .module('dexterApp')
        .factory('Document', Document);

    Document.$inject = ['$resource'];

    function Document ($resource) {
        var resourceUrl =  'api/documents/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
