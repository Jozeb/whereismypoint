<?php

// Include connection.php
include_once('connection.php');

	$data = file_get_contents('php://input');
	$array = (array) json_decode($data,true);
	
	
    $array1 = $array['shops'];
	if($array1 != null){
	foreach ($array1 as $k=>$v){
   
	$shopKeeperName = $v['SHOPKEEPER_NAME'];
	$shopKeeperCnic = $v['SHOPKEEPER_CNIC'];
	$shopKeeperPhone = $v['SHOPKEEPER_PHONE']; 
	$shopKeeperEmail = $v['SHOPKEEPER_EMAIL']; 
	
	$shopName = $v['SHOP_NAME']; 
	$shopAddress = $v['SHOP_ADDRESS'];
	$shopDesc = $v['SHOP_DESCRIPTION'];
	$shopLocation = $v['SHOP_LOCATION'];
	$shopTimeOpen = $v['SHOP_TIMEOPEN'];
	$shopTimeClosed = $v['SHOP_TIMECLOSED'];
	
	$latitude = (double) $v['GPS_LATITUDE'];
	$longitude = (double) $v['GPS_LONGITUDE'];
	$time = $v['GPS_TIME'];
	
	
	// Insert data into data base
	$sql = "INSERT INTO `resolve.pk`.`data` (`shopKeeperName`, `shopKeeperCnic`, `shopKeeperPhone`, `shopKeeperEmail`, `shopName`, `shopAddress`,`shopDesc`,`shopLocation`,`shoptimeopen`,`shoptimeclosed`,`latitude`,`longitude`) VALUES ('$shopKeeperName', '$shopKeeperCnic', '$shopKeeperPhone', '$shopKeeperEmail', '$shopName', '$shopAddress','$shopDesc','$shopLocation','$shopTimeOpen','$shopTimeClosed','$latitude','$longitude');";
	$qur = mysqli_query($conn,$sql);
	if($qur){
		$json = array("status" => 1, "msg" => "Done data added!");
	}else{
		$json = array("status" => 0, "msg" => "Error adding data!");
	}
	
	}
	
	}
	else{
		$json = array("status" => 0, "msg" => "NO DATA FOUND");
	}

@mysqli_close($conn);

/* Output header */
	header('Content-type: application/json');
	echo json_encode($json);