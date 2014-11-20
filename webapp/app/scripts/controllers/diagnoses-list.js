'use strict';

/**
 * @ngdoc function
 * @name openehrPocApp.controller:DiagnosesListCtrl
 * @description
 * # DiagnosesListCtrl
 * Controller of the openehrPocApp
 */
angular.module('openehrPocApp')
  .controller('DiagnosesListCtrl', function ($scope, $stateParams, $location, $modal, Patient, Diagnosis, growlNotifications, $filter) {
    $scope.patient = Patient.get($stateParams.patientId);

    $scope.numPerPageOpt = [3, 5, 10, 20];
    $scope.numPerPage = $scope.numPerPageOpt[2];
    $scope.currentPage = 1;
    $scope.currentPageDiagnoses = [];

    $scope.searchKeywords = '';
    $scope.filteredDiagnoses = [];
    $scope.defaultOrder = '-dateOfOnset';
    $scope.row = $scope.defaultOrder;

    Diagnosis.byPatient($scope.patient.id).then(function (result) {
      $scope.patient.diagnoses = result;
      $scope.search();
    });

    $scope.select = function(page) {
      var end, start;
      start = (page - 1) * $scope.numPerPage;
      end = start + $scope.numPerPage;
      return $scope.currentPageDiagnoses = $scope.filteredDiagnoses.slice(start, end);
    };
    $scope.onFilterChange = function() {
      $scope.select(1);
      $scope.currentPage = 1;
      return $scope.row = $scope.defaultOrder;
    };
    $scope.onNumPerPageChange = function() {
      $scope.select(1);
      return $scope.currentPage = 1;
    };
    $scope.onOrderChange = function() {
      $scope.select(1);
      return $scope.currentPage = 1;
    };
    $scope.search = function() {
      $scope.row = $scope.defaultOrder;
      $scope.filteredDiagnoses = $filter('orderBy')($filter('filter')($scope.patient.diagnoses, $scope.searchKeywords), $scope.row);
      return $scope.onFilterChange();
    };
    $scope.order = function(rowName) {
      if ($scope.row === rowName) {
        return;
      }
      $scope.row = rowName;
      $scope.filteredDiagnoses = $filter('orderBy')($filter('filter')($scope.patient.diagnoses, $scope.searchKeywords), $scope.row);
      return $scope.onOrderChange();
    };

    $scope.go = function (path) {
      $location.path(path);
    };

    $scope.selected = function (diagnosis) {
      return diagnosis.id === $stateParams.diagnosisId;
    };

    $scope.create = function () {
      var modalInstance = $modal.open({
        templateUrl: 'views/diagnoses/diagnoses-modal.html',
        size: 'lg',
        controller: 'DiagnosesModalCtrl',
        resolve: {
          modal: function () {
            return {
              title: 'Create Diagnosis'
            };
          },
          diagnosis: function () {
            return { dateOfOnset: new Date() };
          },
          patient: function () {
            return $scope.patient;
          }
        }
      });

      modalInstance.result.then(function (diagnosis) {
        Diagnosis.createByPatient($scope.patient.id, diagnosis).then(function (result) {
          growlNotifications.add('<strong>'+ $scope.patient.fullname() + ':</strong> Diagnosis updated', 'success', 10000);
          $scope.patient.diagnoses.push(result.data);
          console.log(result);
        });
      });
    };
  });
