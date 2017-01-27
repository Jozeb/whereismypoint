<?php
 

$response = array();
 

require "db_connect.php"; //establishes a connection with database

$nuid				=	$_POST["nuid"];
$pass				=	$_POST["password"];
$point_number		=	$_POST["point_number"];
$phone_number		=	$_POST["phone_number"];
$home_location		=	$_POST["home_location"];

$sql_query = 	"UPDATE `Main_table` 
					SET 
					`pass`= '$pass',
					`point_number`=$point_number,
					`phone_number`='$phone_number',
					`home_location`='$home_location'
					WHERE nu_id like '$nuid';";					
					
mysqli_query($con,$sql_query);

mysqli_close($con);

?>
