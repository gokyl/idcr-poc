'use strict';

angular.module('openehrPocApp')
  .controller('MedicationsDetailCtrl', function ($scope, $stateParams, $modal, $location, PatientService, Medication) {

    PatientService.get($stateParams.patientId).then(function (patient) {
      $scope.patient = patient;
    });

    Medication.all($stateParams.medicationIndex).then(function (result) {
      $scope.result = result.data;
      $scope.medication = $scope.result.medications[$stateParams.medicationIndex];
      $scope.medicationSelectionNumber = $stateParams.medicationIndex + 1;
    });

    $scope.edit = function () {
      var modalInstance = $modal.open({
        templateUrl: 'views/medications/medications-modal.html',
        size: 'lg',
        controller: 'MedicationsModalCtrl',
        resolve: {
          modal: function () {
            return {
              title: 'Edit Medication'
            };
          },
          medication: function () {
            return angular.copy($scope.medication);
          },
          patient: function () {
            return $scope.patient;
          }
        }
      });

      modalInstance.result.then(function (medication) {
        $scope.result.medications[$stateParams.medicationIndex] = medication;

        Medication.create($scope.patient.id, $scope.result).then(function () {
          $location.path('/patients/' + $scope.patient.id + '/medications');
        });
      });
    };

  });
