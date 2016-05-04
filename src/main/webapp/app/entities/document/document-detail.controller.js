(function() {
    'use strict';

    angular
        .module('dexterApp')
        .controller('DocumentDetailController', DocumentDetailController);

    DocumentDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'Document', 'File', 'NamedProperty'];

    function DocumentDetailController($scope, $rootScope, $stateParams, entity, Document, File, NamedProperty) {
        var vm = this;
        vm.document = entity;
        
        var unsubscribe = $rootScope.$on('dexterApp:documentUpdate', function(event, result) {
            vm.document = result;
        });
        $scope.$on('$destroy', unsubscribe);

    }
})();
