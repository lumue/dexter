(function() {
    'use strict';

    angular
        .module('dexterApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('document', {
            parent: 'entity',
            url: '/document',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Documents'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/document/documents.html',
                    controller: 'DocumentController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
            }
        })
        .state('document-detail', {
            parent: 'entity',
            url: '/document/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Document'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/document/document-detail.html',
                    controller: 'DocumentDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                entity: ['$stateParams', 'Document', function($stateParams, Document) {
                    return Document.get({id : $stateParams.id});
                }]
            }
        })
        .state('document.new', {
            parent: 'document',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/document/document-dialog.html',
                    controller: 'DocumentDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                name: null,
                                type: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('document', null, { reload: true });
                }, function() {
                    $state.go('document');
                });
            }]
        })
        .state('document.edit', {
            parent: 'document',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/document/document-dialog.html',
                    controller: 'DocumentDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Document', function(Document) {
                            return Document.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('document', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('document.delete', {
            parent: 'document',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/document/document-delete-dialog.html',
                    controller: 'DocumentDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Document', function(Document) {
                            return Document.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('document', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
