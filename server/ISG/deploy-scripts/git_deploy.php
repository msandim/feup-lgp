<?php
try {
	$payload = json_decode(file_get_contents('php://input'));
	echo $payload;
} catch(Exception $e) {
	$payload = "cenas";
//	exit(0);
}
// log the request
file_put_contents('/var/www/html/logs/github.txt', print_r($payload), FILE_APPEND);
//if ($payload->ref === 'refs/heads/suicidal_deployment') {
	echo 'deploying from github', PHP_EOL;
	//$message = shell_exec("/var/www/html/deploy_wrapper? 2>&1");
	$message = shell_exec("./deploy_wrapper 2>&1");
	print_r($message);
//}
echo 'git_deploy.php done!', PHP_EOL;
