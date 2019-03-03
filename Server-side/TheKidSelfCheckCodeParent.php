<?php 
require "TheKidSelfConnection.php";
$result = "";
$codeParent=$_GET["codeParent"];
$sqlInternal = "SELECT code_parent FROM parent";
$all_parents = array(); 

$stmt = $conn->prepare($sqlInternal);
$stmt->execute();
$stmt->bind_result($code_parent);

    while($stmt->fetch()){
	$temp = $code_parent;
 	array_push($all_parents, $temp);
	}
$conn->close();

if (in_array($codeParent, $all_parents))
  {
    $result = "1"; //Already exist
  }
else
  {
    $result = "0";
  }
echo $result;

?>