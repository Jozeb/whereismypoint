<?php

define('DB_HOST', getenv('OPENSHIFT_MYSQL_DB_HOST'));
define('DB_PORT', getenv('OPENSHIFT_MYSQL_DB_PORT'));
define('DB_USER', getenv('OPENSHIFT_MYSQL_DB_USERNAME'));
define('DB_PASS', getenv('OPENSHIFT_MYSQL_DB_PASSWORD'));
define('DB_NAME', getenv('OPENSHIFT_GEAR_NAME'));
define('DB_APP', getenv('OPENSHIFT_APP_NAME'));

$dbhost = constant("DB_HOST"); // Host name 
$dbport = constant("DB_PORT"); // Host port
$dbusername = constant("DB_USER"); // Mysql username 
$dbpassword = constant("DB_PASS"); // Mysql password 
$db_name = constant("DB_NAME"); // Database name 
$db_appname = constant("DB_APP"); //Database App name 


$con = mysqli_connect(getenv('OPENSHIFT_MYSQL_DB_HOST'), getenv('OPENSHIFT_MYSQL_DB_USERNAME'), getenv('OPENSHIFT_MYSQL_DB_PASSWORD'), "", getenv('OPENSHIFT_MYSQL_DB_PORT')) or die("Error: " . mysqli_error($mysqlCon));
mysqli_select_db($con, 'hci_project') or die("Error: " . mysqli_error($mysqlCon));


//$con = mysqli_connect($server_name,$mysql_user,$mysql_pass,$db_name);
/*
if(!$con){
	echo "Connection Error...".mysqli_connect_error();
}else{
	echo "<h3>Database connection Success....</h3>";
}*/





// git config --global push.default simple

//$mysqlCon = mysqli_connect(getenv('OPENSHIFT_MYSQL_DB_HOST'), getenv('OPENSHIFT_MYSQL_DB_USERNAME'), getenv('OPENSHIFT_MYSQL_DB_PASSWORD'), "", getenv('OPENSHIFT_MYSQL_DB_PORT')) or die("Error: " . mysqli_error($mysqlCon));
//mysqli_select_db($mysqlCon, getenv('store')) or die("Error: " . mysqli_error($mysqlCon));
?>


 
