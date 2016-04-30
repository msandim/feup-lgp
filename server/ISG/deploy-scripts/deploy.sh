cd /feup-lgp/server/ISG

# kill previous running app (if any)
kill $(cat target/universal/ISG-1.0-SNAPSHOT/RUNNING_PID)

git pull

./activator dist

cd target/universal/
rm -r isg-1.0-SNAPSHOT
unzip isg-1.0-SNAPSHOT.zip
cd isg-1.0-SNAPSHOT/bin/

./isg -Dplay.evolutions.db.default.autoApply=true -Dplay.crypto.secret="Hcey3kRQ@?=_S__2H7pvQ@mkxJ8kF`KAe7X<<K3:56@X^B>m/Owzevh<C@D`be/J" &
