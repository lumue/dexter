'use strict';

describe('Controller Tests', function() {

    describe('File Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockFile, MockNamedProperty;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockFile = jasmine.createSpy('MockFile');
            MockNamedProperty = jasmine.createSpy('MockNamedProperty');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'File': MockFile,
                'NamedProperty': MockNamedProperty
            };
            createController = function() {
                $injector.get('$controller')("FileDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'dexterApp:fileUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
