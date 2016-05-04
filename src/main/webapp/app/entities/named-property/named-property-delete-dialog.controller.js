(function() {
    'use strict';

    angular
        .module('dexterApp')
        .controller('NamedPropertyDeleteController',NamedPropertyDeleteController);

    NamedPropertyDeleteController.$inject = ['$uibModalInstance', 'entity', 'NamedProperty'];

    function NamedPropertyDeleteController($uibModalInstance, entity, NamedProperty) {
        var vm = this;
        vm.namedProperty = entity;
        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
        vm.confirmDelete = function (id) {
            NamedProperty.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        };
    }
})();
