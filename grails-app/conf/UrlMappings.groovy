class UrlMappings {
	static mappings = {

        ["aggr", "job"].each {
            resource ->
                "/${resource}/$uuid"(resource: resource) {
                    action = [GET: "show"]
                }
                "/${resource}"(controller: resource, parseRequest: true) {
                    action = [GET: "list", POST: "save"]
                }
        }
    }
}
