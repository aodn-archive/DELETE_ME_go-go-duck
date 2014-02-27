class UrlMappings {
	static mappings = {
        "/$controller/$action?/$id?"{
            constraints {
                // apply constraints here
            }
        }

        ["aggr", "job"].each {
            resource ->
                "/${resource}/$uuid"(resource: resource) {
                    action = [GET: "show"]
                }
                "/${resource}"(controller: resource, parseRequest: true) {
                    action = [GET: "list", POST: "save"]
                }
        }

        "/"(view: "/index")
    }
}
