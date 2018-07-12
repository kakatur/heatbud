
/**********************************************/
/************ javascript functions ************/
/**********************************************/

// Google query api: https://developers.google.com/analytics/devguides/reporting/core/dimsmets
function createCharts(startDate) {

	  var chart1 = new gapi.analytics.googleCharts.DataChart({
	    reportType: 'ga',
	    query: {
	      'ids': 'ga:82697917',
	      'dimensions': 'ga:date',
	      'metrics': 'ga:users, ga:pageviews',
	      'start-date': startDate,
	      'end-date': 'yesterday',
	    },
	    chart: {
	      type: 'LINE',
	      container: 'chart1'
	    }
	  });
	  chart1.execute();

	  var chart2 = new gapi.analytics.googleCharts.DataChart({
	    reportType: 'ga',
	    query: {
	      'ids': 'ga:82697917',
	      'dimensions': 'ga:country',
	      'metrics': 'ga:users, ga:pageviews',
	      'start-date': startDate,
	      'end-date': 'yesterday',
	    },
	    chart: {
	      type: 'GEO',
	      container: 'chart2'
	    }
	  });
	  chart2.execute();

	  var chart3 = new gapi.analytics.googleCharts.DataChart({
	    reportType: 'ga',
	    query: {
	      'ids': 'ga:82697917',
	      'dimensions': 'ga:source',
	      'metrics': 'ga:users, ga:pageviews',
	      'start-date': startDate,
	      'end-date': 'yesterday',
	      'sort': '-ga:users',
	    },
	    chart: {
	      type: 'TABLE',
	      container: 'chart3'
	    }
	  });
	  chart3.execute();
}
