# Go Go Duck

[![Build Status](https://travis-ci.org/aodn/go-go-duck.png?branch=master)](https://travis-ci.org/aodn/go-go-duck)

## Usage

### Request a new job

Supposing you have a JSON document such as the one at [doc/example_job_request.json](doc/example_job_request.json), which looks like this:

```
{
  layerName: 'acorn_hourly_avg_rot_qc_timeseries_url',
  emailAddress: 'gogo@duck.com',
  geoserver: 'http://geoserver-123.aodn.org.au/geoserver'
  subsetDescriptor: {
    temporalExtent: {
      start: '2013-11-20T00:30:00.000Z',
      end:   '2013-11-20T10:30:00.000Z'
    },
    spatialExtent: {
      north: '-32.150743',
      south: '-33.433849',
      east:  '115.741219',
      west:  '114.15197'
    }
  }
}
```

then a job cab be requested like this:

```
curl -v --data @doc/example_job_request.json --header "Content-Type: application/json" http://localhost:8080/go-go-duck/job
```

### Download an aggregation from a previous Job

```
curl -v -O -J --get http://localhost:8300/go-go-duck/aggr/8d0ed017
```

where `8d0ed017` is the job ID.
