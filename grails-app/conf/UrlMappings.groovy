class UrlMappings {
	static mappings = {

        ["aggr", "job"].each {
            resource ->
                "/${resource}"(controller: resource, parseRequest: true) {
                    action = [GET: "list", POST: "save"]
                }
                "/${resource}/$id"(resource: resource)
        }
    }
}
