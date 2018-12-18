<?PHP
mb_internal_encoding('UTF-8');
mb_http_output('UTF-8');
$hostname="localhost";
$database="bdr";
$username="luis";
$password="12345";
$json=array();
$json2 = array();

	if(isset($_GET["Start"])){
		$Start = $_GET['Start'];

		$connection=mysqli_connect($hostname,$username,$password,$database);
		
		$sql="SELECT * FROM place LIMIT $Start,4";
		$result=mysqli_query($connection,$sql);

		if (!$connection->set_charset("utf8")) {
    		printf("Error cargando el conjunto de caracteres utf8: %s\n", $connection->error);
   		 	exit();
		} 
	
		if($sql){
			$x = 0;
			$json['OPERATIONS'][0]="GET_PLACES";

			while($reg = mysqli_fetch_object($result)){
				$jsonTuple1['IdPlace'] = $reg->IdPlace;
				$jsonTuple1['Email'] = $reg->Email;
				$jsonTuple1['MadeBy'] = $reg->MadeBy;
				$jsonTuple1['Name'] = $reg->Name;
				$jsonTuple1['Description'] = $reg->Description;
				$jsonTuple1['Localitation'] = $reg->Localitation;
				$jsonTuple1['Image'] = $reg->Image;
				$jsonTuple1['RedMovility'] = $reg->RedMovility;
				$jsonTuple1['RedVision'] = $reg->RedVision;
				$jsonTuple1['ColourBlind'] = $reg->ColourBlind;
				$jsonTuple1['Deaf'] = $reg->Deaf;
				$jsonTuple1['Foreigner'] = $reg->Foreigner;

				$json['GET_PLACES'][$x]=$jsonTuple1;
				$x++;

			}

			mysqli_close($connection);
			echo json_encode($json, JSON_UNESCAPED_UNICODE);
		}
	}
?>