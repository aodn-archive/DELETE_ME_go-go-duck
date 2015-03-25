<!DOCTYPE html>
<html>
    <head>
        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.1/css/bootstrap.min.css">
        <link rel="stylesheet" href="${resource(dir: 'css', file: 'imos.css')}" type="text/css">
    </head>
    <body>
        <div class="imosHeader">
            <div class="container">
                <a class="btn" role="button" href="https://imos.aodn.org.au/imos123/home?uuid=8af21108-c535-43bf-8dab-c1f45a26088c">
                    <img src="http://static.emii.org.au/images/logo/IMOS-Ocean-Portal-logo.png" alt="IMOS logo">
                </a>
            </div>
        </div>
        <div class="container">
            <dl>
                <dt><g:message code="job.id.label" default="Id"/></dt>
                <dd>${job.uuid}</dd>
                <dt><g:message code="job.createdTimestamp.label" default="Submitted" /></dt>
                <dd><joda:format value="${job.createdTimestamp}" /></dd>
                <dt><g:message code="job.status.label" default="Status" /></dt>
                <dd>${job.status}</dd>
                <g:if test="${job.queuePosition}">
                    <dt><g:message code="job.queuePosition.label" default="Position In Queue" /></dt>
                    <dd>${job.queuePosition}</dd>
                </g:if>
                <g:if test="${job.aggrUrl}">
                    <dt><g:message code="job.aggrUrl.label" default="Download URL" /></dt>
                    <dd><a href="${job.aggrUrl}">${job.aggrUrl}</a></dd>
                </g:if>
                <g:if test="${job.report}">
                    <dt><g:message code="job.report.label" default="Report" /></dt>
                    <dd><pre>${job.report}</pre></dd>
                </g:if>
            </dl>
        </div>
        <div class="jumbotronFooter voffset5">
            <div class="container">
                <footer class="row">
                    <div class="col-md-4">
                        <p>If you've found this information useful, see something wrong, or have a suggestion,
                           please let us
                           know.
                           All feedback is very welcome. For help and information about this site
                           please contact <a href="mailt:info@emii.org.au">info@emii.org.au</a></p>
                    </div>
                    <div class="col-md-8">
                        <p>Use of this web site and information available from it is subject to our <a href="http://imos.org.au/imostermsofuse0.html">
                           Conditions of use
                        </a></p>
                        <p>© 2014 IMOS</p>
                    </div>
                </footer>
            </div>
        </div>
    </body>
</html>
