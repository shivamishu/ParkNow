console.log('Loading function');
const redis = require('redis');
const searchOptions = {
  radius: 800,
  unit: 'm',
  count: 10
};

const redisOptions = {
    host: process.env.REDIS_HOST,
    port: process.env.REDIS_PORT
}
redis.debug_mode = true;

exports.handler = function(event, context, callback) {
    console.log(JSON.stringify(event, null, 2));
    event.Records.forEach(function(record) {
        console.log(record.eventID);
        console.log(record.eventName);
        console.log('DynamoDB Record: %j', record.dynamodb);
        const latitude = record.dynamodb.NewImage.latitude.N;
        const longitude = record.dynamodb.NewImage.longitude.N;
        const cordskey =  record.dynamodb.NewImage.id.S;
        console.log('redis connected lng_lat_rediskey  ' + longitude  +'    '+  latitude +'   '+  cordskey );
        const client = redis.createClient(redisOptions);
        console.log('after client initialization');
        client.on('connect', function(result) {console.log('connected');});
         client.send_command('GEOADD',
            ['parkingstalls',
                longitude,
                latitude,
                cordskey
            ],
//         client.send_command('GEORADIUS',
//   ['parkingstalls',
//   -115.171089,
//   36.122785,
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

    });
    callback(null, "message1");
};
