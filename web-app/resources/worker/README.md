# GoGoDuck

GoGoDuck is a simple NetCDF file aggregator, using nco tools (ncks, ncrcat, ncatted, etc).

## Requirements

nco tools, a recent version!

## Usage:
```
$ ./gogoduck.sh --help
```

## On IMOS Data

### ACORN

Running on some ACORN data (rot qc):
```
$ ./gogoduck.sh -g 'http://geoserver-123.aodn.org.au/geoserver' -p acorn_hourly_avg_rot_qc_timeseries_url -s "TIME,2013-11-20T00:30:00.000Z,2013-11-20T10:30:00.000Z;LATITUDE,-33.433849,-32.150743;LONGITUDE,114.15197,115.741219" -o output.nc
```

### GSLA

Running on GSLA data:
```
./gogoduck.sh -g 'http://geoserver-123.aodn.org.au/geoserver' -p gsla_nrt00_timeseries_url -s "TIME,2011-10-10T00:00:00.000Z,2011-10-20T00:00:00.000Z;LATITUDE,-33.433849,-32.150743;LONGITUDE,114.15197,115.741219" -o output.nc
```

### CARS

When running on CARS data, it handles one big NetCDF file. If it runs with no
opendap mounted, such as on your development machine - it'll be slow! However
if it runs when opendap is mounted, things will be significantly faster.

Running on CARS data:
```
./gogoduck.sh -g 'http://geoserver-123.aodn.org.au/geoserver' -p cars -s "TIME,2009-01-01T00:00:00.000Z,2009-12-25T23:04:00.000Z;LATITUDE,-33.433849,-32.150743;LONGITUDE,114.15197,115.741219" -o output.nc
```

Running on CARS data with depth (notice the floating point for the depth parameter):
```
./gogoduck.sh -g 'http://geoserver-123.aodn.org.au/geoserver' -p cars -s "TIME,2009-01-01T00:00:00.000Z,2009-12-25T23:04:00.000Z;LATITUDE,-33.433849,-32.150743;LONGITUDE,114.15197,115.741219;DEPTH,0.0,100.0" -o output.nc
```

## Unit Tests

Run:
```
$ ./shunit2_test.sh
```

