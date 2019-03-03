<?php

require '/TheKidSelfConnection.php'; // To connect to a particular database
use PHPMailer\PHPMailer\PHPMailer;
use PHPMailer\PHPMailer\Exception;
use PHPMailer\PHPMailer\POP3;
require 'C:\xampp\composer\vendor\autoload.php';

$email_parent1 = $_GET["email_parent1"];
$email_parent2 = $_GET["email_parent2"];
$child = $_GET["child"];
$longitude = $_GET["longitude"];
$latitude = $_GET["latitude"];
$address = $_GET["address"];
$time = $_GET["time"];

$mail = new PHPMailer(TRUE);

try {
  $mail->setFrom('marenakms@gmail.com', 'The KidSelf Admin');
  $mail->addAddress($email_parent1, 'Parent 1');
  if ($email_parent2 != "null")
  {
    $mail->addAddress($email_parent2, 'Parent 2');
  }
  $mail->Subject = $child . " is in danger! React on it!";
  $mail->isHTML(TRUE);
  $mail->Body = '<html>' . $child . ' is located now by adress: <strong>' . $address . '</strong>. Here you have the coordinates, where your child is : latitude: <strong>' . $latitude . '</strong>, longitude: <strong>' . $longitude . '</strong>. And, also, the time, when your child clicked the emergence button is following: <strong>' . $time . '</strong>.</html>';

   $mail->isSMTP();
   $mail->Host = 'smtp.gmail.com';
   $mail->SMTPAuth = TRUE;
   $mail->SMTPSecure = 'tls';
   $mail->Username = 'marenakms@gmail.com';
   $mail->Password = 'cbmwwzrxbciksoiz';
   $mail->Port = 587;
   $mail->SMTPOptions = array(
      'ssl' => array(
      'verify_peer' => false,
      'verify_peer_name' => false,
      'allow_self_signed' => true
      )
   );
  $mail->send();

}
catch (Exception $e)
{
   echo $e->errorMessage();
}
catch (\Exception $e)
{
   echo $e->getMessage();
}

?>