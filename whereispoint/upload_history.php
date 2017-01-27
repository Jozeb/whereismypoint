<?php

// Include connection.php
include_once('db_connect.php');

	$data = file_get_contents('php://input');
	$array = (array) json_decode($data,true);
	
	
    $array1 = $array['points'];
	if($array1 != null){
		foreach ($array1 as $k=>$v){
   
			$nuid = $v['nuid'];
			$pointno = $v['pointno'];
			$location = $v['location']; 
			$time = $v['time']; 
	
	
			// Insert data into data base
			$sql = "INSERT INTO history values ('$nuid', $pointno, '$time','$location');";
			$qur = mysqli_query($con,$sql);
	
			if($qur){
				$json = array("status" => 1, "msg" => "Done data added!");
			}else{
				//$json = array("status" => 0, "msg" => "Error adding data!");
				echo $sql;
			}
	
		}
	
	}
	else{
		
		$json = array("status" => 0, "msg" => "NO DATA FOUND");
	}

@mysqli_close($con);

/* Output header */
	header('Content-type: application/json');
	echo json_encode($json);