#!/bin/bash

aws_functions=("getFeed" "follow" "getFollowers" "getStory" "getFollowingCount" "getFollowees" "register" "logout" "isFollower" "getUser" "postStatus" "login" "unfollow" "getFollowersCount")

aws s3 cp ./server/build/libs/server-all.jar s3://lmeurshandlerjar/server-all.jar

for val in "${aws_functions[@]}"; do
  echo "$val"
  aws lambda update-function-code --function-name "$val" --s3-bucket lmeurshandlerjar --s3-key server-all.jar > /dev/null
done