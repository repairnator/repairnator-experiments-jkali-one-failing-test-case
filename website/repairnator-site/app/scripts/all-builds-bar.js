var simplifyStatuses = function (dataArray) {
  var success = 'Successful Bug Reproduction';
  var withoutFailure = 'Test without failure';
  var errorTesting = 'Error when testing';
  var errorCompiling = 'Error when compiling';
  var errorCheckout = 'Error when checking out';
  var errorCloning = 'Error when cloning';

  var statusMap = {
    'PATCHED': success,
    'test failure': success,
    'test errors': success,
    'NOTBUILDABLE': errorCompiling,
    'SOURCEDIRNOTCOMPUTED': errorCompiling,
    'NOTCLONABLE': errorCloning,
    'NOTFAILING': withoutFailure,
    'NOTTESTABLE': errorTesting,
    'TESTABLE': errorTesting,
    'BUILDNOTCHECKEDOUT': errorCheckout,
    'TESTDIRNOTCOMPUTED': errorTesting
  };

  var total = 0;
  dataArray.forEach(element => {total += element.counted});

  var dataNewName = dataArray.reduce(function (acc, elem) {
    var status = statusMap[elem._id];
    if (acc[status] === undefined) {
      acc[status] = elem.counted;
    } else {
      acc[status] += elem.counted;
    }
    return acc;
  }, {});

  var finalArray = Object.keys(dataNewName).reduce(function (acc, id) {
    acc.push({
      'name': id,
      'y': dataNewName[id],
      'percentage': (dataNewName[id] / total)*100
    });
    return acc;
  }, []);

  return finalArray.sort(function (a, b) {
    return b.y - a.y;
  });
};
$.get('https://repairnator.lille.inria.fr/repairnator-mongo-api/inspectors/statusStats', function (data) {
  var htmlElement = $('<div></div>');
  $('#charts').append(htmlElement);

  var total = 0;
  data.forEach(element => {total += element.counted});
  Highcharts.chart({
    chart: {
      plotBackgroundColor: null,
      plotBorderWidth: null,
      plotShadow: false,
      type: 'bar',
      renderTo: htmlElement[0]
    },
    colors: ["#7cb5ec", "#828282", "#90ed7d", "#f7a35c", "#8085e9", "#f15c80", "#e4d354", "#2b908f", "#f45b5b", "#91e8e1"],
    title: {
      text: 'Build statuses (all times - '+total+' builds)'
    },
    xAxis: {
      type: 'category'
    },
    tooltip: {
      pointFormat: '{series.name}: <b> ({point.y})</b>'
    },
    plotOptions: {
      bar: {
        allowPointSelect: true,
        cursor: 'pointer',
        dataLabels: {
          enabled: true,
          format: '{point.y} ({point.percentage:.1f}%)',
          style: {
            color: (Highcharts.theme && Highcharts.theme.contrastTextColor) || 'black'
          }
        }
      }
    },
    series: [{
      name: 'Statuses',
      colorByPoint: true,
      data: simplifyStatuses(data)
    }],
    legend: {
      enabled: false
    }
  });
});
