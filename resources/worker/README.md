# GoGoDuck

GoGoDuck is a simple NetCDF file aggregator, using nco tools (ncks, ncrcat, ncatted, etc).

## Requirements

nco tools, a recent version!

## Usage:
```
$ ./gogoduck.sh --help
```

## On IMOS Data

Running on some ACORN data (rot qc):
```
$ ./gogoduck.sh -p acorn_hourly_avg_rot_qc_timeseries_url -s "TIME,2013-11-20T00:30:00.000Z,2013-11-20T10:30:00.000Z;LATITUDE,-33.433849,-32.150743;LONGITUDE,114.15197,115.741219" -o output.nc
```

Running on GSLA data:
```
./gogoduck.sh -p gsla_nrt00_timeseries_url -s "TIME,2011-10-10T00:00:00.000Z,2011-10-20T00:00:00.000Z;LATITUDE,-33.433849,-32.150743;LONGITUDE,114.15197,115.741219" -o output.nc
```

## Unit Tests

Run:
```
$ ./shunit2_test.sh
```

