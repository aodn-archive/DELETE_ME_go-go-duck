# Go Go Duck

[![Build Status](https://travis-ci.org/aodn/go-go-duck.png?branch=master)](https://travis-ci.org/aodn/go-go-duck)

## Development

Grails commands are wrapped by gradle, e.g. to `run-app`:

```
$ ./gradlew grails-run-app
```

or to `test-app`:

```
$ ./gradlew grails-test-app -PgrailsArgs="unit:"
```

## Usage

### Request a new job

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

then a job cab be requested like this:

```
curl -v --data @doc/example_job_request.json --header "Content-Type: application/json" http://localhost:8080/go-go-duck/job
```

### Download an aggregation from a previous Job

```
curl -v -O -J --get http://localhost:8080/go-go-duck/aggr/8d0ed017
```

where `8d0ed017` is the job ID.

## Origin Of The Name

Originally there was AODAAC, which is the "Australian Oceans DAAC", a heavy
weight aggregator which /should/ do the same job as GoGoDuck and much more.

As a "plan B" for the AODAAC integration, which didn't happen on time, came
BODAAC. Which didn't provide users with aggregated data, but was a 'hack' so
they can still get to the data a bit easier.

GODAAC was born as a proof of concept, showing that aggregating NetCDF files is
not rocket science. The G comes from the inspiration by gg (Guillaume Galibert).
GODAAC was then renamed to GoGoDuck, which sounds almost the same. No ducks
were killed in the process of developing GoGoDuck.
