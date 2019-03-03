<?php 
require "TheKidSelfConnection.php";
$result = "";
$codeChild=$_GET["codeChild"];
$sqlInternal = "SELECT code_child FROM child";
$all_children = array(); 

$stmt = $conn->prepare($sqlInternal);
$stmt->execute();
$stmt->bind_result($code_child);

    while($stmt->fetch()){
	$temp = $code_child;
 	array_push($all_children, $temp);
	}
$conn->close();

if (in_array($codeChild, $all_children))
  {
    $result = "1"; //exists
  }
else
  {
    $result = "0";
  }
echo $result;

?>