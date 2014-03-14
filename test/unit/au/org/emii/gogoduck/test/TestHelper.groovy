package au.org.emii.gogoduck.test

import au.org.emii.gogoduck.job.Job

class TestHelper {
    static Job createJob() {
        def job = new Job(
            layerName: 'some_layer',
            emailAddress: 'gogo@duck.com',
            subsetDescriptor: [
                temporalExtent: [
                    start: '2013-11-20T00:30:00.000Z',
                    end:   '2013-11-20T10:30:00.000Z'
                ],
                spatialExtent: [
                    north: '-32.150743',
                    south: '-33.433849',
                    east:  '115.741219',
                    west:  '114.15197'
                ]
            ]
        )
        def serverURL = 'http://localhost:8080/gogoduck'
        job.metaClass.getServerUrl = { serverURL }

        return job
    }
}