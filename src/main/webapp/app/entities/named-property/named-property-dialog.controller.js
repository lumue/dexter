(function() {
    'use strict';

    angular
        .module('dexterApp')
        .controller('NamedPropertyDialogController', NamedPropertyDialogController);

    NamedPropertyDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'NamedProperty', 'File', 'Document'];

    function NamedPropertyDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, NamedProperty, File, Document) {
        var vm = this;
        vm.namedProperty = entity;
        vm.files = File.query();
        vm.documents = Document.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        var onSaveSuccess = function (result) {
            $scope.$emit('dexterApp:namedPropertyUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        };

        var onSaveError = function () {
            vm.isSaving = false;
        };

        vm.save = function () {
            vm.isSaving = true;
            if (vm.namedProperty.id !== null) {
                NamedProperty.update(vm.namedProperty, onSaveSuccess, onSaveError);
            } else {
                NamedProperty.save(vm.namedProperty, onSaveSuccess, onSaveError);
            }
        };

        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
    }
})();
