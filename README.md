# Go Go Duck

[![Build Status](https://travis-ci.org/aodn/go-go-duck.png?branch=master)](https://travis-ci.org/aodn/go-go-duck)

## Usage

Supposing you have a JSON document such as the one at [doc/example_job_request.json](doc/example_job_request.json), which looks like this:

```
{
  layerName: 'acorn_hourly_avg_rot_qc_timeseries_url',
  emailAddress: 'gogo@duck.com',
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

then the service can be called/test like this:

```
# Bring up a VM containing NetCDF command line dependencies
vagrant up

# Start the web-app
grails run-app

# Send a request
curl -v --data @doc/example_job_request.json --header "Content-Type: application/json" http://localhost:8080/go-go-duck/job/save
```