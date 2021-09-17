#!/bin/bash
connectionId=TODO
apnToken=TODO
username=Anonymous

curl -v \
-d "{\"aps\":{\"alert\":\"Incoming call\", \"content-available\":\"1\"}, \"Username\": \"${username}\", \"ConnectionId\": \"${connectionId}\"}" \
-H "apns-topic: <YOUR_BUNDLE_ID>.voip" \
-H "apns-push-type: voip" \
-H "apns-priority: 10" \
--http2 \
--cert app.pem \
"https://api.development.push.apple.com/3/device/${apnToken}"
