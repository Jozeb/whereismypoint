<?php
 

$response = array();
 

require "db_connect.php"; //establishes a connection with database

$nuid		=	$_POST["nuid"];
$location	=	$_POST["current_location"];

$sql_query = 	"Update Main_table 
					set current_location = '$location' 
					where nu_id like '$nuid'";					
mysqli_query($con,$sql_query);

mysqli_close($con);

?>
