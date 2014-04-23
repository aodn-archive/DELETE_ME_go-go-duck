#!/bin/bash

# Written by Dan Fruehauf <dan.fruehauf@utas.edu.au>
# Inspiration and help by Guillaume Galibert <guillaume.galibert@utas.edu.au>

# Make it easy to use a different temporary directory
export TMPDIR=/tmp

# load the logging library
source `dirname $0`/logger.sh

# relative path to where all profiles (plugins) are installed
declare -r PROFILES_DIR=profiles

# finds the correct profile to run for the given layer, starts with:
# acorn_hourly_avg_sag_nonqc_timeseries_url
# acorn_hourly_avg_sag_nonqc_timeseries
# acorn_hourly_avg_sag_nonqc
# acorn_hourly_avg_sag
# acorn_hourly_avg
# acorn_hourly
# acorn
# default
# $1 - profile name
_get_profile_module() {
    local profile_name=$1; shift
    local profile_base_dir=`dirname $0`"/$PROFILES_DIR"
    local profile_plugin=$profile_name

    # if the plugin exists - simply go with it
    test -f "$profile_base_dir/$profile_plugin" && echo $profile_base_dir/$profile_plugin && return

    while echo $profile_plugin | grep -q _; do
        # removes last part with an underscore:
        # acorn_hourly_avg -> acorn_hourly
        profile_plugin=${profile_plugin%_*}

        test -f "$profile_base_dir/$profile_plugin" && echo $profile_base_dir/$profile_plugin && return
    done

    echo $profile_base_dir/default
}

# returns a list of URLs from the given GeoServer URL
# $1 - profile module
# $2 - profile
# $3 - output (result) file
# $4 - subset
_get_list_of_urls() {
    local profile_module=$1; shift
    local profile=$1; shift
    local output_file=$1; shift
    local subset="$1"; shift
    (source $profile_module && get_list_of_urls $profile $output_file "$subset")
}

# enforce file limit, if the file given has more lines than the limit given, we
# return 1 (false) and gogoduck should quit
# $1 - url list
# $2 - limit
_enforce_file_limit() {
    url_list="$1"; shift
    local -i limit=$1; shift
    local -i num_urls=`cat $url_list | wc -l`
    if [ $num_urls -gt $limit ]; then
        logger_warn "Cannot process '$num_urls' files, we allow only '$limit'"
        logger_user "Cannot process '$num_urls' files, we allow only '$limit'"
        return 1
    else
        return 0
    fi
}

# returns 0 if file has .gz suffix, 1 otherwise
# $1 - file to check
_is_gzipped() {
    local file="$1"; shift
    [ "${file: -3}" = ".gz" ]
}

# downloads all files (or link them)
# $1 - directory to download files to
# $2 - file containing list of URLs to download
_get_files() {
    local dir=$1; shift
    local url_file=$1; shift

    local url
    for url in `cat $url_file`; do
        local file_basename=`basename $url`
        if [ ${url:0:7} = "file://" ]; then
            # trim file:// part
            url=${url:7}

            if _is_gzipped $url; then
                logger_info "gunzipping: '$url' to '$dir/$file_basename'"
                gunzip -c $url > $dir/$file_basename
            else
                logger_info "Linking file: '$url' -> '$dir'"
                if test -f $url; then
                    ln -s $url $dir/
                else
                    logger_warn "Failed accessing: '$url'"
                    return 1
                fi
            fi

        else
            logger_info "Downloading file: '$url'"
            if ! (cd $dir && curl -s -O "$url"); then
                logger_warn "Failed downloading: '$url'"
                return 1
            fi

            # if suffix is .gz, gunzip it!
            if _is_gzipped $url; then
                logger_info "gunzipping: '$dir/$file_basename'"
                gunzip $dir/$file_basename
            fi
        fi
    done
}

# returns 0 if list of URLs is sane, 1 otherwise
# $1 - file containing list of URLs
_is_list_of_urls_sane() {
    local url_file=$1; shift

    # check for ServiceExceptionReport - usually if the layer doesn't exist
    if grep -q 'ServiceExceptionReport' $url_file; then
        logger_user "Could not obtain list of URLs, does the layer exist?"
        logger_warn "Could not obtain list of URLs, server returned ServiceExceptionReport"
        return 1
    fi

    # check if it's an empty file
    if [ `cat $url_file | wc -l` -eq 0 ]; then
        logger_user "List of URLs obtained is empty, are your subseting parameters sane?"
        logger_warn "Could not obtain list of URLs, URL list is empty!"
        return 1
    fi
}

# applies subset to every netcdf file in directory
# $1 - profile module
# $2 - profile
# $3 - directory to apply subset on
# $4 - subset to apply
_apply_subset() {
    local profile_module=$1; shift
    local profile=$1; shift
    local dir=$1; shift
    local subset="$1"; shift

    if [ x"$subset" = x ]; then
        logger_warn "No subset defined"
        return 1
    fi

    logger_info "Applying profile '$profile_module'"

    # get subset from profile module
    local subset_cmd=`source $profile_module && get_subset_command $profile $subset`

    local file
    logger_user "Applying subset '$subset'"
    for file in $dir/*; do
        local tmp_file=`mktemp`
        logger_info "Applying subset '$subset_cmd' to '$file'"
        logger_user "Processing file '"`basename $file`"'"
        ncks -a -4 -O $subset_cmd $file $tmp_file

        # overwrite original file
        mv $tmp_file $file
    done
}

# aggregates netcdf files into one file
# $1 - directory aggregate
# $2 - output file
_aggregate() {
    local dir=$1; shift
    local output_file=$1; shift

    # ncrcat doesn't like to cat just one file, so take care of this corner
    # case
    if [ `ls -1 $dir/* | wc -l` -eq 1 ]; then
        mv $dir/* $output_file
    else
        ncrcat -4 -h -O $dir/* $output_file
    fi
}

# updates header of aggregated file
# $1 - profile module
# $2 - profile
# $3 - aggregated file
# $4 - subset
_update_header() {
    local profile_module=$1; shift
    local profile=$1; shift
    local aggregated_file=$1; shift
    local subset="$1"; shift
    (source $profile_module && update_header $profile $aggregated_file "$subset")
}

# gogoduck logic
# $1 - maximum amount of files to allow processing of
# $2 - profile to apply
# $3 - subset to apply
# $4 - output file
gogoduck_main() {
    local -i limit=$1; shift
    local profile="$1"; shift
    local subset="$1"; shift
    local output="$1"; shift
    local tmp_url_list=`mktemp`

    # parse profile relative to where the script was ran from
    local profile_module=`_get_profile_module $profile`

    # get a list of relevant URLs we'll work with
    if ! _get_list_of_urls $profile_module $profile $tmp_url_list "$subset"; then
        rm -f $tmp_url_list
        logger_fatal "Failed getting list of URLs"
    fi

    # check for sanity of URL list
    if ! _is_list_of_urls_sane $tmp_url_list; then
        rm -f $tmp_url_list
        logger_user "Failed to get list of URLs for '$profile'"
        logger_user "Subset parameters '$subset'"
        logger_user "GoGoDuck aggregation failed"
        logger_fatal "Could not obtain list of URLs for '$profile'"
    fi

    # enforce number of URLs limit
    if ! _enforce_file_limit $tmp_url_list $limit; then
        rm -f $tmp_url_list
        logger_user "GoGoDuck aggregation failed"
        logger_fatal "Not allowed to process that many files"
    fi

    # temporary directory and temporary result
    local tmp_dir=`mktemp -d`
    local tmp_result_file=`mktemp`

    if ! _get_files $tmp_dir $tmp_url_list; then
        rm -f $tmp_dir/* $tmp_result_file; rmdir $tmp_dir
        logger_fatal "Failed downloading URLs"
    fi

    if ! _apply_subset $profile_module $profile $tmp_dir "$subset"; then
        rm -f $tmp_dir/* $tmp_result_file; rmdir $tmp_dir
        logger_fatal "Failed applying subset"
    fi

    if ! _aggregate $tmp_dir $tmp_result_file; then
        rm -f $tmp_dir/* $tmp_result_file; rmdir $tmp_dir
        logger_fatal "Failed aggregating files"
    fi

    if ! _update_header $profile_module $profile $tmp_result_file "$subset"; then
        rm -f $tmp_dir/* $tmp_result_file; rmdir $tmp_dir
        logger_fatal "Failed updating header"
    fi

    # clean up temporary directory
    rm -f $tmp_dir/*; rmdir $tmp_dir

    mv $tmp_result_file $output
    logger_info "Result saved at '$output'"
    logger_user "GoGoDuck aggregation successful"
}

# prints usage and exit
usage() {
    echo "Usage: $0 [OPTIONS]"
    echo "Subsets and aggregates NetCDF files."
    echo "
Options:
  -s, --subset               Subset to apply, semi-colon separated.
  -p, --profile              Profile to apply.
  -o, --output               Output file to use.
  -l, --limit                Maximum amount of file to allow processing of.
  -u, --user-log             Output file for user logging."
    exit 3
}


# main
# "$@" - parameters, see usage
main() {
    # parse options with getopt
    local tmp_getops=`getopt -o hs:p:o:l:u: --long help,subset:,profile:,output:,limit:,user-log: -- "$@"`
    [ $? != 0 ] && usage

    eval set -- "$tmp_getops"
    local url subset output
    local profile=default # set default profile
    local -i limit=100 # allow up to 100 files to be processed by default
    local user_log

    # parse the options
    while true ; do
        case "$1" in
            -h|--help) usage;;
            -s|--subset) subset="$2"; shift 2;;
            -p|--profile) profile="$2"; shift 2;;
            -o|--output) output="$2"; shift 2;;
            -l|--limit) limit="$2"; shift 2;;
            -u|--user-log) user_log="$2"; shift 2;;
            --) shift; break;;
            *) usage;;
        esac
    done

    # make sure user specified output file
    [ x"$output" = x ] && usage

    # make sure user specified output file
    [ x"$profile" = x ] && usage

    # check if user logging is required
    if [ x"$user_log" != x ]; then
        set_user_log_file $user_log || usage
    fi

    gogoduck_main $limit "$profile" "$subset" "$output"
}

main "$@"
