(function() {
    'use strict';

    angular
        .module('dexterApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('file', {
            parent: 'entity',
            url: '/file',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Files'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/file/files.html',
                    controller: 'FileController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
            }
        })
        .state('file-detail', {
            parent: 'entity',
            url: '/file/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'File'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/file/file-detail.html',
                    controller: 'FileDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                entity: ['$stateParams', 'File', function($stateParams, File) {
                    return File.get({id : $stateParams.id});
                }]
            }
        })
        .state('file.new', {
            parent: 'file',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/file/file-dialog.html',
                    controller: 'FileDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                path: null,
                                hash: null,
                                created: null,
                                lastmodified: null,
                                lastseen: null,
                                firstseen: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('file', null, { reload: true });
                }, function() {
                    $state.go('file');
                });
            }]
        })
        .state('file.edit', {
            parent: 'file',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/file/file-dialog.html',
                    controller: 'FileDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['File', function(File) {
                            return File.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('file', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('file.delete', {
            parent: 'file',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/file/file-delete-dialog.html',
                    controller: 'FileDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['File', function(File) {
                            return File.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('file', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
