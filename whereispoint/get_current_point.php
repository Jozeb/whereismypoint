<?php
 

$response = array();
 

require "db_connect.php"; //establishes a connection with database

$point_number	=	$_POST["point_number"];

$sql_query = 	"SELECT * FROM `Main_table` 
						WHERE point_number = $point_number;";

$result = mysqli_query($con,$sql_query);
 
// check for empty result
if (mysqli_num_rows($result) >0) {
    // looping through all results
    // products node
    
	 $response["users"] = array();
 
        // temp user array
    while ($row = mysqli_fetch_array($result)) {
		$product = array();
		$product["nuid"] 				= $row["nu_id"];
		$product["point_number"] 		= $row["point_number"];
		$product["phone_number"] 		= $row["phone_number"];
		$product["home_location"] 		= $row["home_location"];
		$product["current_location"] 	= $row["current_location"];
		
		array_push($response["users"], $product);
    }
    
 
    // success
    $response["success"] = 1;
 
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
