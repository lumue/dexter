(function() {
    'use strict';
    angular
        .module('dexterApp')
        .factory('NamedProperty', NamedProperty);

    NamedProperty.$inject = ['$resource'];

    function NamedProperty ($resource) {
        var resourceUrl =  'api/named-properties/:id';

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
