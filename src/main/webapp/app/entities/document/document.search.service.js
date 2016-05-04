(function() {
    'use strict';

    angular
        .module('dexterApp')
        .factory('DocumentSearch', DocumentSearch);

    DocumentSearch.$inject = ['$resource'];

    function DocumentSearch($resource) {
        var resourceUrl =  'api/_search/documents/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
