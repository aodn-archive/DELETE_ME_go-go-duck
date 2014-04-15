#!/bin/bash

# sets user log file, logger_user function will log to that file
# $1 - user log file
set_user_log_file() {
    local user_log_file="$1"; shift
    # truncate user log file
    cat /dev/null > "$user_log_file" || logger_fatal "Could not create user log file '$user_log_file'"
    logger_info "User logging will be kept at '$user_log_file'"
    export USER_LOG_FILE=$user_log_file
}

# logs a message for the end user
# "$@" - message to log
logger_user() {
    test -f $USER_LOG_FILE && echo "$@" >> $USER_LOG_FILE
}

# logs an info message
# "$@" - message to log
logger_info() {
    _logger info "$@"
}

# logs a warning message
# "$@" - message to log
logger_warn() {
    _logger warn "$@"
}

# logs a fatal message and exits
# "$@" - message to log
logger_fatal() {
    _logger fatal "$@"
    exit 2
}

# returns a color for a log level
# info - green - 32
# warn - yellow - 33
# fatal - red - 31
# anything else - default - 39
# $1 - log level
_get_color_for_log_level() {
    local log_level=$1; shift
    case "$log_level" in
    info)
        echo "\e[32m"
        ;;
    warn)
        echo "\e[33m"
        ;;
    fatal)
        echo "\e[31m"
        ;;
    *)
        echo "\e[39m"
        ;;
    esac
}

# logs a message
# $1 - log level
# "$@" - message to log
_logger() {
    local log_level=$1; shift
    local msg="$@"

    # add some nice colors
    local color=`_get_color_for_log_level $log_level`
    log_level=`echo $log_level | tr "[a-z]" "[A-Z]"`
    echo -e "$color["`date`"] $log_level: $msg\e[0m"
}

