<?php

$response = array();

require "db_connect.php"; //establishes a connection with database


$sql_query = 	"Select * from driver;";

$result = mysqli_query($con,$sql_query);
 

	if (mysqli_num_rows($result) >0) {
		// looping through all results
		// products node
		$response["driver"] = array();
    
		while($row = mysqli_fetch_array($result)){
			$driver = array();
			$driver["name"] 			= $row["name"];
			$driver["point_number"] 	= $row["point_number"];
			$driver["cellnumber"] 		= $row["cellnumber"];
			
			array_push($response["driver"],$driver);
 
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
