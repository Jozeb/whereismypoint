<?php
 

$response = array();
 

require "db_connect.php"; //establishes a connection with database

$nuid	=	$_POST["nuid"];
$pass	=	$_POST["pass"];

$sql_query = 	"SELECT * FROM `Main_table` 
						WHERE nu_id like '$nuid'
						AND pass like '$pass';";


$result = mysqli_query($con,$sql_query);
 
// check for empty result
if (mysqli_num_rows($result) >0) {
    // looping through all results
    // products node
    $response["user"] = array();
    
	$row = mysqli_fetch_array($result);
	$response["nuid"] 			= $row["nu_id"];
	$response["point_number"] 	= $row["point_number"];
	$response["phone_number"] 	= $row["phone_number"];
	$response["home_location"] 	= $row["home_location"];
	
    
 
    // success
    $response["success"] = 1;
	
	
	$sql_query = 	"Update `Main_table` 
						Set isLoggedin = 1
						where nu_id like '$nuid'";
						
	mysqli_query($con,$sql_query);
 
    // echoing JSON response
    echo json_encode($response);
} else {
    // no products found
    $response["success"] = 0;
    $response["message"] = "No result found";
 
    // echo no users JSON
    echo json_encode($response);
	
}

mysqli_close($con);

?>
