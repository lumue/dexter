(function() {
    'use strict';

    angular
        .module('dexterApp')
        .factory('FileSearch', FileSearch);

    FileSearch.$inject = ['$resource'];

    function FileSearch($resource) {
        var resourceUrl =  'api/_search/files/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
