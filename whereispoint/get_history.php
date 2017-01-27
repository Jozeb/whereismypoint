<?php

$response = array();

require "db_connect.php"; //establishes a connection with database

$point_number	=	$_POST["point_number"];

$sql_timezone = "SET time_zone = '+0:00';";
$sql_query = 	"SELECT time, ((Hour(time)*60) + minute(time) ) as time_m,location 
						FROM  `history` 
						WHERE point_number = $point_number
						AND DAYOFMONTH( TIME ) >22
						order by ((Hour(time)*60) + minute(time) );";

mysqli_query($con,$sql_timezone);
$result = mysqli_query($con,$sql_query);
 

	if (mysqli_num_rows($result) >0) {
		// looping through all results
		// products node
		$response["history"] = array();
    
		while($row = mysqli_fetch_array($result)){
			$driver = array();
			$driver["time"] 			= $row["time"];
			$driver["time_m"] 			= $row["time_m"];
			$driver["location"] 		= $row["location"];
			
			array_push($response["history"],$driver);
 
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
