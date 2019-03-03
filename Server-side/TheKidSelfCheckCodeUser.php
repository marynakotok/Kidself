<?php 
require "TheKidSelfConnection.php";
$codeUser=$_GET["codeUser"];
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

if (in_array($codeUser, $all_users))
  {
    echo "1";
  }
else
  {
    echo "0";
  }

?>