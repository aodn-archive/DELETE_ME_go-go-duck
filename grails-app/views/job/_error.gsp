<g:eachError bean="${job}">
    <g:if test="${it.getField() == "emailAddress"}">
        <g:message code="job.error.email" />
    </g:if>
</g:eachError>

