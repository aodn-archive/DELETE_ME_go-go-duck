<!DOCTYPE html>
<html>
    <head>
        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.1/css/bootstrap.min.css">
        <link rel="stylesheet" href="${resource(dir: 'css', file: 'imos.css')}" type="text/css">
    </head>
    <body>
        <div class="imosHeader">
            <div class="container">
                <a class="btn" role="button" href="https://imos.aodn.org.au/imos123/home">
                    <img src="https://static.emii.org.au/images/logo/IMOS-Ocean-Portal-logo.png" alt="IMOS logo">
                </a>
            </div>
        </div>
        <div class="container">
            <h2><g:message code="aggregation.status" default="Aggregation Status"/></h2>
            <dl>
                <g:labelledContent labelCode="job.id.label">${job.uuid}</g:labelledContent>
                <g:labelledContent labelCode="job.createdTimestamp.label"><joda:format value="${job.createdTimestamp}" /></g:labelledContent>
                <g:labelledContent labelCode="job.status.label"><g:message code="job.status.${job.status}" default="${job.status.toString()}" /></g:labelledContent>

                <g:if test="${job.reason != job.reason.NONE}">
                    <g:labelledContent labelCode="job.reason.label"><g:message code="job.reason.${job.reason}" default="${job.reason.toString()}" /></g:labelledContent>
                </g:if>

                <g:labelledContent if="${job.queuePosition}" labelCode="job.queuePosition.label">${job.queuePosition}</g:labelledContent>
                <g:labelledContent if="${job.aggrUrl}" labelCode="job.aggrUrl.label">
                    <a href="${job.aggrUrl}">${job.aggrUrl}</a>
                </g:labelledContent>
                <g:labelledContent if="${job.report}" labelCode="job.report.label">
                    <pre>${job.report}</pre>
                </g:labelledContent>
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
                           please contact <a href="mailto:info@aodn.org.au">info@aodn.org.au</a></p>
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
