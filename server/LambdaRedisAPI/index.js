console.log('Loading function');
const redis = require('redis');
const searchOptions = {
    radius: 800,
    unit: 'ft',
    count: 10
};

const redisOptions = {
    host: process.env.REDIS_HOST,
    port: process.env.REDIS_PORT
}
redis.debug_mode = true;
const client = redis.createClient(redisOptions);

exports.handler = function(event, context, callback) {

    var operation = event.operation;

    console.log(JSON.stringify(event, null, 2));

    switch (operation) {
        case 'read':
            var rediskey = event.payload.Key.id;
            var arr = rediskey.split("|");
            var longitude = arr[1];
            var latitude = arr[0];
            console.log('after client initialization');
            client.on('connect', function(result) { console.log('connected'); });
            client.send_command('GEORADIUS', ['parkingstalls', longitude,
                    latitude,
                    searchOptions.radius,
                    searchOptions.unit, // m | mi | km | ft
                    'WITHDIST',
                    'WITHCOORD',
                    'COUNT',
                    searchOptions.count,
                    'ASC'
                ],
                (error, reply) => {
                    if (error) {
                        throw error
                    }
                    else {
                        console.log(reply);
                       callback(reply) 
                    }
                });

            break;
        default:
            callback('Unknown operation: ${operation}');

    }

};
                 //   client.quit();
