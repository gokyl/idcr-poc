'use strict';

angular.module('openehrPocApp')
  .factory('Allergy', function ($http) {

    var patientIdOveride = 9999999000;

    var all = function (patientId) {
      return $http.get('/api/patients/' + (patientIdOveride || patientId) + '/allergies');
    };

    var update = function (patientId, composition) {
      console.log('put allergy comp:');
      console.log(composition);
      return $http.put('/api/patients/' + (patientIdOveride || patientId) + '/allergies', composition);
    };

    return {
      all: all,
      update: update
    };
  });
