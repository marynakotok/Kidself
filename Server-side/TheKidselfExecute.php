<?php

$sql=$_POST["sql"];
execute($sql);

function execute($sqlInternal) {
require "TheKidSelfConnection.php";
if ($conn->query($sqlInternal) !== TRUE) 
{ 
    echo "Error: " . $sql . "<br>" . $conn->error; 
} 
$conn->close();
}

?>