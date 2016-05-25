#!/bin/bash



cd ~/feup-lgp/server/ISG

#ls

kill $(cat target/universal/isg-1.0-SNAPSHOT/RUNNING_PID)

git pull origin dev

activator dist

cd target/universal/
rm -r isg-1.0-SNAPSHOT
unzip isg-1.0-SNAPSHOT.zip
cd isg-1.0-SNAPSHOT/bin/

./isg -Dhttp.port=80
