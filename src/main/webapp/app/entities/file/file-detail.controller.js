(function() {
    'use strict';

    angular
        .module('dexterApp')
        .controller('FileDetailController', FileDetailController);

    FileDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'File', 'NamedProperty'];

    function FileDetailController($scope, $rootScope, $stateParams, entity, File, NamedProperty) {
        var vm = this;
        vm.file = entity;
        
        var unsubscribe = $rootScope.$on('dexterApp:fileUpdate', function(event, result) {
            vm.file = result;
        });
        $scope.$on('$destroy', unsubscribe);

    }
})();
