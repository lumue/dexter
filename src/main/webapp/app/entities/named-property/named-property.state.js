(function() {
    'use strict';

    angular
        .module('dexterApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('named-property', {
            parent: 'entity',
            url: '/named-property',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'NamedProperties'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/named-property/named-properties.html',
                    controller: 'NamedPropertyController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
            }
        })
        .state('named-property-detail', {
            parent: 'entity',
            url: '/named-property/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'NamedProperty'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/named-property/named-property-detail.html',
                    controller: 'NamedPropertyDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                entity: ['$stateParams', 'NamedProperty', function($stateParams, NamedProperty) {
                    return NamedProperty.get({id : $stateParams.id});
                }]
            }
        })
        .state('named-property.new', {
            parent: 'named-property',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/named-property/named-property-dialog.html',
                    controller: 'NamedPropertyDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                name: null,
                                value: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('named-property', null, { reload: true });
                }, function() {
                    $state.go('named-property');
                });
            }]
        })
        .state('named-property.edit', {
            parent: 'named-property',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/named-property/named-property-dialog.html',
                    controller: 'NamedPropertyDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['NamedProperty', function(NamedProperty) {
                            return NamedProperty.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('named-property', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('named-property.delete', {
            parent: 'named-property',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/named-property/named-property-delete-dialog.html',
                    controller: 'NamedPropertyDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['NamedProperty', function(NamedProperty) {
                            return NamedProperty.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('named-property', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
