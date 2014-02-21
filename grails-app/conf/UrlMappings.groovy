class UrlMappings {
	static mappings = {

        ["aggr", "job"].each {
            resource ->
                "/${resource}/$id"(resource: resource) {
                    action = [GET: "show"]
                }
                "/${resource}"(controller: resource, parseRequest: true) {
                    action = [GET: "list", POST: "save"]
                }
        }
    }
}
