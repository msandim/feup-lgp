
cd /feup-lgp/server/ISG

# kill previous running app (if any)
kill $(cat target/universal/isg-1.0-SNAPSHOT/RUNNING_PID)

git pull

./activator dist

cd target/universal/
rm -r isg-1.0-SNAPSHOT
unzip isg-1.0-SNAPSHOT.zip
cd isg-1.0-SNAPSHOT/bin/

ISG -Dplay.evolutions.db.default.autoApply=true -Dplay.crypto.secret="6dfq7vn`ctWLh?Qr8Y9LrvqZI4ae_mdBtI4iHa^XrV?R1TfIRq0lBExsbxsoVnGx"
