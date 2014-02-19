package au.org.emii.gogoduck.job

class TestHelper {
    static Job createJob() {
        return new Job(
            layerName: 'some layer',
            emailAddress: 'gogo@duck.com',
            subsetDescriptor: [
                temporalExtent: [
                    start: '2013-11-20T00:30:00.000Z',
                    end:   '2013-11-20T10:30:00.000Z'
                ],
                spatialExtent: [
                    north: '-32.150743',
                    south: '-33.433849',
                    east:  '114.15197',
                    west:  '115.741219'
                ]
            ]
        )
    }
}