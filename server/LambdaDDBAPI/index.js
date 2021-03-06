var AWS = require('aws-sdk');
var dynamo = new AWS.DynamoDB.DocumentClient();

exports.handler = function(event, context, callback) {
    console.log(JSON.stringify(event, null, 2));
    var operation = event.operation;
    if (event.tableName) {
        event.payload.TableName = event.tableName;
        event.payload.Item = event.payload.item;
        const DateNow = Date.now();
        event.payload.Item.lastmodifiedtimestamp = new Date(DateNow).toString();
    }

    switch (operation) {
        case 'create':
            dynamo.put(event.payload, callback);
            break;
        case 'read':
            dynamo.get(event.payload, callback);
            break;
        case 'update':
            var id = event.payload.Item.id;
            var parkingstatus = event.payload.Item.parkingstatus;
            // var lastmodifiedtimestamp = event.payload.Item.lastmodifiedtimestamp;
            const DateNow = Date.now();
            var lastmodifiedtimestamp = new Date(DateNow).toString();


            var userid = event.payload.Item.userid;
            var params = {
                TableName: event.payload.TableName,
                Key: { "id": id },
                UpdateExpression: "set parkingstatus = :parkingstatus,lastmodifiedtimestamp = :lastmodifiedtimestamp,userid = :userid",
                ExpressionAttributeValues: {
                    ":parkingstatus": parkingstatus,
                    ":lastmodifiedtimestamp": lastmodifiedtimestamp,
                    ":userid": userid
                },
                ReturnValues: "UPDATED_NEW"
            };
            console.log("!!!!!!!!!!!!!!!!!!!!!!!!!" + params);
            dynamo.update(params, callback);
            break;
        default:
            callback('Unknown operation: ${operation}');

    }
};
