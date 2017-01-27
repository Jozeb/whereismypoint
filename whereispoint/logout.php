<?

require "db_connect.php"; //establishes a connection with database

$nuid=	$_POST["nuid"];

$sql_query = 	"Update `Main_table` 
						Set isLoggedin = 0
						where nu_id like '$nuid'";

if(mysqli_query($con,$sql_query)){
	echo "success";
 }else{
	 echo "error";
 }
mysqli_close($con);





?>