<?php
 

$response = array();
 

require "db_connect.php"; //establishes a connection with database

$nuid				=	$_POST["nuid"];
$pass				=	$_POST["password"];
$point_number		=	$_POST["point_number"];
$phone_number		=	$_POST["phone_number"];
$home_location		=	$_POST["home_location"];


$sql_query = 	"INSERT INTO Main_table (`nu_id`, `pass`, `point_number`, `phone_number`, `home_location`, `current_location`) values ('$nuid', '$pass',$point_number,'$phone_number', '$home_location','$home_location');";

if(mysqli_query($con,$sql_query)){
	echo "success";
 }else{
	 echo "error";
 }


mysqli_close($con);

?>
