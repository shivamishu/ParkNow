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
    console.log(JSON.stringify(event, null, 2));
    event.Records.forEach(function(record) {
if (record.eventName == 'INSERT') {
        console.log(record.eventID);
        console.log(record.eventName);
        console.log('DynamoDB Record: %j', record.dynamodb);
        const latitude = record.dynamodb.NewImage.latitude.N;
        const longitude = record.dynamodb.NewImage.longitude.N;
        const rediskey =  record.dynamodb.NewImage.id.S;
        console.log('redis connected lng_lat_rediskey  ' + longitude  +'    '+  latitude +'   '+  rediskey );
        //const client = redis.createClient(redisOptions);
        console.log('after client initialization');
        client.on('connect', function(result) {console.log('connected');});
        // client.set(rediskey, JSON.stringify(latitude  +","+  longitude  +","+ 'Initial'),
        //         (error, reply) => {
        //         if (error) {
        //             throw error  
        //         }else {
        //             console.log(reply);
        //             }
        //     });
        
         client.send_command('GEOADD',
            ['parkingstalls',
                longitude,
                latitude,
                rediskey
            ],
//         client.send_command('GEORADIUS',
//   ['parkingstalls',
//   -122.038906,
//   37.3358893,
//   searchOptions.radius,
//   searchOptions.unit, // m | mi | km | ft
//   'WITHDIST',
//   'WITHCOORD',
//   'COUNT',
//   searchOptions.count,
//   'ASC'],
            (error, reply) => {
                client.quit();
                if (error) {
                    throw error  
                }else {
                    console.log(reply);
                    }
            });
}
    });
    callback(null, "message1");

};
