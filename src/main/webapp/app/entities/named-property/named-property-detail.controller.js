(function() {
    'use strict';

    angular
        .module('dexterApp')
        .controller('NamedPropertyDetailController', NamedPropertyDetailController);

    NamedPropertyDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'NamedProperty', 'File', 'Document'];

    function NamedPropertyDetailController($scope, $rootScope, $stateParams, entity, NamedProperty, File, Document) {
        var vm = this;
        vm.namedProperty = entity;
        
        var unsubscribe = $rootScope.$on('dexterApp:namedPropertyUpdate', function(event, result) {
            vm.namedProperty = result;
        });
        $scope.$on('$destroy', unsubscribe);

    }
})();
