(function() {
    'use strict';

    angular
        .module('dexterApp')
        .controller('DocumentDialogController', DocumentDialogController);

    DocumentDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', '$q', 'entity', 'Document', 'File', 'NamedProperty'];

    function DocumentDialogController ($timeout, $scope, $stateParams, $uibModalInstance, $q, entity, Document, File, NamedProperty) {
        var vm = this;
        vm.document = entity;
        vm.files = File.query({filter: 'document-is-null'});
        $q.all([vm.document.$promise, vm.files.$promise]).then(function() {
            if (!vm.document.fileId) {
                return $q.reject();
            }
            return File.get({id : vm.document.fileId}).$promise;
        }).then(function(file) {
            vm.files.push(file);
        });
        vm.namedproperties = NamedProperty.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        var onSaveSuccess = function (result) {
            $scope.$emit('dexterApp:documentUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        };

        var onSaveError = function () {
            vm.isSaving = false;
        };

        vm.save = function () {
            vm.isSaving = true;
            if (vm.document.id !== null) {
                Document.update(vm.document, onSaveSuccess, onSaveError);
            } else {
                Document.save(vm.document, onSaveSuccess, onSaveError);
            }
        };

        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };
    }
})();
