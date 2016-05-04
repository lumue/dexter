(function() {
    'use strict';

    angular
        .module('dexterApp')
        .controller('FileDialogController', FileDialogController);

    FileDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'File', 'NamedProperty'];

    function FileDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, File, NamedProperty) {
        var vm = this;
        vm.file = entity;
        vm.namedproperties = NamedProperty.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        var onSaveSuccess = function (result) {
            $scope.$emit('dexterApp:fileUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        };

        var onSaveError = function () {
            vm.isSaving = false;
        };

        vm.save = function () {
            vm.isSaving = true;
            if (vm.file.id !== null) {
                File.update(vm.file, onSaveSuccess, onSaveError);
            } else {
                File.save(vm.file, onSaveSuccess, onSaveError);
            }
        };

        vm.clear = function() {
            $uibModalInstance.dismiss('cancel');
        };

        vm.datePickerOpenStatus = {};
        vm.datePickerOpenStatus.created = false;
        vm.datePickerOpenStatus.lastmodified = false;
        vm.datePickerOpenStatus.lastseen = false;
        vm.datePickerOpenStatus.firstseen = false;

        vm.openCalendar = function(date) {
            vm.datePickerOpenStatus[date] = true;
        };
    }
})();
