# aws s3 cp server/build/libs/server-all.jar s3://lmeurshandlerjar/server-all.jar >> ./log.txt
aws lambda update-function-code --function-name login --s3-bucket lmeurshandlerjar --s3-key server-all.jar >> ./log.txt
aws lambda update-function-code --function-name register --s3-bucket lmeurshandlerjar --s3-key server-all.jar >> ./log.txt
aws lambda update-function-code --function-name logout --s3-bucket lmeurshandlerjar --s3-key server-all.jar >> ./log.txt
aws lambda update-function-code --function-name getUser --s3-bucket lmeurshandlerjar --s3-key server-all.jar >> ./log.txt
aws lambda update-function-code --function-name getFeed --s3-bucket lmeurshandlerjar --s3-key server-all.jar >> ./log.txt
aws lambda update-function-code --function-name getStory --s3-bucket lmeurshandlerjar --s3-key server-all.jar >> ./log.txt
aws lambda update-function-code --function-name postStatus --s3-bucket lmeurshandlerjar --s3-key server-all.jar >> ./log.txt
aws lambda update-function-code --function-name getFollowingCount --s3-bucket lmeurshandlerjar --s3-key server-all.jar >> ./log.txt
aws lambda update-function-code --function-name getFollowees --s3-bucket lmeurshandlerjar --s3-key server-all.jar >> ./log.txt
aws lambda update-function-code --function-name getFollowersCount --s3-bucket lmeurshandlerjar --s3-key server-all.jar >> ./log.txt
aws lambda update-function-code --function-name getFollowers --s3-bucket lmeurshandlerjar --s3-key server-all.jar >> ./log.txt 
aws lambda update-function-code --function-name follow --s3-bucket lmeurshandlerjar --s3-key server-all.jar >> ./log.txt 
aws lambda update-function-code --function-name unfollow --s3-bucket lmeurshandlerjar --s3-key server-all.jar >> ./log.txt 
aws lambda update-function-code --function-name isFollower --s3-bucket lmeurshandlerjar --s3-key server-all.jar >> ./log.txt 