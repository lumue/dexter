(function() {
    'use strict';

    angular
        .module('dexterApp')
        .factory('NamedPropertySearch', NamedPropertySearch);

    NamedPropertySearch.$inject = ['$resource'];

    function NamedPropertySearch($resource) {
        var resourceUrl =  'api/_search/named-properties/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
