<?php 
require "TheKidSelfConnection.php";
$result = "";
$emailToCheck=$_GET["emailToCheck"];
$sqlInternal = "SELECT email FROM parent";
$email_array = array(); 

$stmt = $conn->prepare($sqlInternal);
$stmt->execute();
$stmt->bind_result($email);

    while($stmt->fetch()){
	$temp = $email;
 	array_push($email_array, $temp);
	}
$conn->close();

if (in_array($emailToCheck, $email_array))
  {
    $result = "1"; //Already exist
  }
else
  {
    $result = "0";
  }
echo $result;

?>