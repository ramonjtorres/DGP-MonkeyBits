<?PHP
$hostname="localhost";
$database="routability";
$username="luis";
$password="12345";
$json=array();
	if(isset($_GET["IdRoute"]) && isset($_GET["Email"])){
		$IdRoute =$_GET['IdRoute'];
		$Email = $_GET['Email'];

		$connection=mysqli_connect($hostname,$username,$password,$database);
		
		$sql = "INSERT INTO favoriteroutes (IdRoute, Email) VALUES ('{$IdRoute}', '{$Email}')";
		$result=mysqli_query($connection,$sql);

		if($sql){
			$json["OPERATION"] = "ADD_FAVORITE_ROUTE";
			mysqli_close($connection);
			echo json_encode($json);
		}	
	}
?>