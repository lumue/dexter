(function() {
    'use strict';

    angular
        .module('dexterApp')
        .controller('NamedPropertyController', NamedPropertyController);

    NamedPropertyController.$inject = ['$scope', '$state', 'NamedProperty', 'NamedPropertySearch'];

    function NamedPropertyController ($scope, $state, NamedProperty, NamedPropertySearch) {
        var vm = this;
        vm.namedProperties = [];
        vm.loadAll = function() {
            NamedProperty.query(function(result) {
                vm.namedProperties = result;
            });
        };

        vm.search = function () {
            if (!vm.searchQuery) {
                return vm.loadAll();
            }
            NamedPropertySearch.query({query: vm.searchQuery}, function(result) {
                vm.namedProperties = result;
            });
        };
        vm.loadAll();
        
    }
})();
