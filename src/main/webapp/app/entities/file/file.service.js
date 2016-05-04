(function() {
    'use strict';
    angular
        .module('dexterApp')
        .factory('File', File);

    File.$inject = ['$resource', 'DateUtils'];

    function File ($resource, DateUtils) {
        var resourceUrl =  'api/files/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    data.created = DateUtils.convertDateTimeFromServer(data.created);
                    data.lastmodified = DateUtils.convertDateTimeFromServer(data.lastmodified);
                    data.lastseen = DateUtils.convertDateTimeFromServer(data.lastseen);
                    data.firstseen = DateUtils.convertDateTimeFromServer(data.firstseen);
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
