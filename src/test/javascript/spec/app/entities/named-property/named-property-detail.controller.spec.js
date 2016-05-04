'use strict';

describe('Controller Tests', function() {

    describe('NamedProperty Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockNamedProperty, MockFile, MockDocument;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockNamedProperty = jasmine.createSpy('MockNamedProperty');
            MockFile = jasmine.createSpy('MockFile');
            MockDocument = jasmine.createSpy('MockDocument');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'NamedProperty': MockNamedProperty,
                'File': MockFile,
                'Document': MockDocument
            };
            createController = function() {
                $injector.get('$controller')("NamedPropertyDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'dexterApp:namedPropertyUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
