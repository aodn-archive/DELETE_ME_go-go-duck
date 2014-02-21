class UrlMappings {
	static mappings = {

        ["job"].each {
            resource ->
                "/${resource}"(controller: resource, parseRequest: true) {
                    action = [GET: "list", POST: "save"]
                }
                "/${resource}/$id"(resource: resource)
        }
    }
}
