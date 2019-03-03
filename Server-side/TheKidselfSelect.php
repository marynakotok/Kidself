<?php

$sql= $_GET["sql"];
$table= $_GET["table"];

switch ($table) {
    case "all_users":
        all_users($sql);
        break;
    case "child":
        child($sql);
        break;
    case "parent":
        parent($sql);
        break;
    case "family":
        family($sql);
        break;
    case "group_conversation":
        group_conversation($sql);
        break;
    case "member_group":
        member_group($sql);
        break;
    case "message":
        message($sql);
        break;
    case "zone":
        zone($sql);
        break;
    case "geolocation":
        geolocation($sql);
        break;
    case "task":
        task($sql);
        break;
    case "request":
        request($sql);
        break;
}

function all_users($sqlInternal) {
require "TheKidSelfConnection.php";
$all_users = array(); 
$stmt = $conn->prepare($sqlInternal);
$stmt->execute();
$stmt->bind_result($code_user);
    while($stmt->fetch()){
		$temp = [
		 'code_user'=>$code_user
		];
 	array_push($all_users, $temp);
	}
echo json_encode($all_users);
$conn->close();
}

//--------------------------------------

function child($sqlInternal) {
require "TheKidSelfConnection.php";
$child = array(); 
$stmt = $conn->prepare($sqlInternal);
$stmt->execute();
$stmt->bind_result($code_child, $name, $sex, $bd, $created_on, $last_login);
    while($stmt->fetch()){
		$temp = [
		 'code_child'=>$code_child,
		 'name'=>$name,
		 'sex'=>$sex,
		 'bd'=>$bd,
		 'created_on'=>$created_on,
		 'last_login'=>$last_login
		];
 	array_push($child, $temp);
	}
echo json_encode($child);
$conn->close();
}

//--------------------------------------

function parent($sqlInternal) {
require "TheKidSelfConnection.php";
$parent = array(); 
$stmt = $conn->prepare($sqlInternal);
$stmt->execute();
$stmt->bind_result($code_parent, $name, $sex, $bd, $email, $password_parent, $created_on, $last_login);
    while($stmt->fetch()){
		$temp = [
		 'code_parent'=>$code_parent,
		 'name'=>$name,
		 'sex'=>$sex,
		 'bd'=>$bd,
		 'email'=>$email,
		 'password_parent'=>$password_parent,
		 'created_on'=>$created_on,
		 'last_login'=>$last_login
		];
 	array_push($parent, $temp);
	}
echo json_encode($parent);
$conn->close();
}

//--------------------------------------

function family($sqlInternal) {
require "TheKidSelfConnection.php";
$family = array(); 
$stmt = $conn->prepare($sqlInternal);
$stmt->execute();
$stmt->bind_result($parent_1, $parent_2, $child_1, $child_2, $child_3, $child_4, $child_5, $child_6);
    while($stmt->fetch()){
		$temp = [
		 'parent_1'=>$parent_1,
		 'parent_2'=>$parent_2,
		 'child_1'=>$child_1,
		 'child_2'=>$child_2,
		 'child_3'=>$child_3,
		 'child_4'=>$child_4,
		 'child_5'=>$child_5,
		 'child_6'=>$child_6,
		];
 	array_push($family, $temp);
	}
echo json_encode($family);
$conn->close();
}

//--------------------------------------

function group_conversation($sqlInternal) {
require "TheKidSelfConnection.php";
$group_conversation = array(); 
$stmt = $conn->prepare($sqlInternal);
$stmt->execute();
$stmt->bind_result($id, $photo, $name_group, $subject, $created_on, $admin);
    while($stmt->fetch()){
		$temp = [
		 'id'=>$id,
		 'photo'=>$photo,
		 'name_group'=>$name_group,
		 'subject'=>$subject,
		 'created_on'=>$created_on,
		 'admin'=>$admin
		];
 	array_push($group_conversation, $temp);
	}
echo json_encode($group_conversation);
$conn->close();
}

//--------------------------------------

function member_group($sqlInternal) {
require "TheKidSelfConnection.php";
$member_group = array(); 
$stmt = $conn->prepare($sqlInternal);
$stmt->execute();
$stmt->bind_result($group_conversation, $member);
    while($stmt->fetch()){
		$temp = [
		 'group_conversation'=>$group_conversation,
		 'member'=>$member
		];
 	array_push($member_group, $temp);
	}
echo json_encode($member_group);
$conn->close();
}

//--------------------------------------

function message($sqlInternal) {
require "TheKidSelfConnection.php";
$all_message = array(); 
$stmt = $conn->prepare($sqlInternal);
$stmt->execute();
$stmt->bind_result($id, $group_conversation, $created_on, $message, $sender, $status_message);
    while($stmt->fetch()){
		$temp = [
		 'id'=>$id,
		 'group_conversation'=>$group_conversation,
		 'created_on'=>$created_on,
		 'message'=>$message,
		 'sender'=>$sender,
		 'status_message'=>$status_message
		];
 	array_push($all_message, $temp);
	}
echo json_encode($all_message);
$conn->close();
}

//--------------------------------------

function zone($sqlInternal) {
require "TheKidSelfConnection.php";
$zone = array(); 
$stmt = $conn->prepare($sqlInternal);
$stmt->execute();
$stmt->bind_result($id, $name, $longitude, $latidute, $radius);
    while($stmt->fetch()){
		$temp = [
		 'id'=>$id,
		 'name'=>$name,
		 'longitude'=>$longitude,
		 'latidute'=>$latidute,
		 'radius'=>$radius
		];
 	array_push($zone, $temp);
	}
echo json_encode($zone);
$conn->close();
}

//--------------------------------------

function geolocation($sqlInternal) {
require "TheKidSelfConnection.php";
$geolocation = array(); 
$stmt = $conn->prepare($sqlInternal);
$stmt->execute();
$stmt->bind_result($id, $adress, $longitude, $latidute, $child);
    while($stmt->fetch()){
		$temp = [
		 'id'=>$id,
		 'adress'=>$adress,
		 'longitude'=>$longitude,
		 'latidute'=>$latidute,
		 'child'=>$child
		];
 	array_push($geolocation, $temp);
	}
echo json_encode($geolocation);
$conn->close();
}

//--------------------------------------

function task($sqlInternal) {
require "TheKidSelfConnection.php";
$task = array(); 
$stmt = $conn->prepare($sqlInternal);
$stmt->execute();
$stmt->bind_result($id, $title, $description, $parent, $child, $status_task, $ringtone, $start_time, $end_time, $created_on);
    while($stmt->fetch()){
		$temp = [
		 'id'=>$id,
		 'title'=>$title,
		 'description'=>$description,
		 'parent'=>$parent,
		 'ringtone'=>$ringtone,
		 'start_time'=>$start_time,
		 'end_time'=>$end_time,
		 'child'=>$child,
		 'created_on'=>$created_on,
		 'status_task'=>$status_task
		];
 	array_push($task, $temp);
	}
echo json_encode($task);
$conn->close();
}

//--------------------------------------

function request($sqlInternal) {
require "TheKidSelfConnection.php";
$request = array(); 
$stmt = $conn->prepare($sqlInternal);
$stmt->execute();
$stmt->bind_result($id, $title, $reason, $status_request, $parent, $child, $created_on);
    while($stmt->fetch()){
		$temp = [
		 'id'=>$id,
		 'title'=>$title,
		 'reason'=>$reason,
		 'status_request'=>$status_request,
		 'parent'=>$parent,
		 'child'=>$child,
                 'created_on'=>$created_on
		];
 	array_push($request, $temp);
	}
echo json_encode($request);
$conn->close();
}


?>