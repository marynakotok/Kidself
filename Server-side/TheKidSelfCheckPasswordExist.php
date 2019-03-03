<?php 
require "TheKidSelfConnection.php";
$result = "";
$codeToCheck=$_GET["codeToCheck"];
$sqlInternal = "SELECT code_user FROM all_users";
$all_users = array(); 

$stmt = $conn->prepare($sqlInternal);
$stmt->execute();
$stmt->bind_result($code_user);

    while($stmt->fetch()){
	$temp = $code_user;
 	array_push($all_users, $temp);
	}
$conn->close();

if (in_array($codeToCheck, $all_users))
  {
    $result = "1"; //Already exist
  }
else
  {
    $result = "0";
  }
echo $result;

?>