<?php

require_once '/db_handler.php';
use PHPMailer\PHPMailer\PHPMailer;
use PHPMailer\PHPMailer\Exception;
use PHPMailer\PHPMailer\POP3;
require 'C:\xampp\composer\vendor\autoload.php';

      $function = $_GET["function"];
      if ($function == "resetPasswordRequest")
      {
        $email = $_GET["email"];
        resetPasswordRequest($email);
      } else if ($function == "resetPasswordRequest");
        {
          $email = $_GET["email"];
          $code = $_GET["code"];
          $password = $_GET["password"];
          //resetPassword($email, $code, $password);
        }

function resetPasswordRequest($email){

     $db = new DBHandler();
    $result =  $db -> passwordResetRequest($email);

    if(!$result){

      $response["result"] = "failure";
      $response["message"] = "Reset Password Failure";
      return json_encode($response);

    } else {

      $mail_result = $this -> sendEmail($result["email"], $result["temp_password"]);

      if($mail_result){

        $response["result"] = "success";
        $response["message"] = "Check your mail for reset password code.";
        return json_encode($response);

      } else {

        $response["result"] = "failure";
        $response["message"] = "Reset Password Failure";
        return json_encode($response);
      }
    }
}

function resetPassword($email,$code,$password){

     $db = new DBHandler();

    $result =  $db -> resetPassword($email,$code,$password);

    if(!$result){

      $response["result"] = "failure";
      $response["message"] = "Reset Password Failure";
      return json_encode($response);

    } else {

      $response["result"] = "success";
      $response["message"] = "Password Changed Successfully";
      return json_encode($response);
    }
}

function sendEmail($email,$temp_password){

 $mail = new PHPMailer(TRUE);
 
  try {
    $mail->setFrom('marenakms@gmail.com', 'The KidSelf Admin');
    $mail->addAddress($email, 'Parent 1');
    $mail->Subject = $child . "Reset your password!";
    $mail->isHTML(TRUE);
    $mail->Body = '<html>Hello, we are from Administration. We have received the message from you, that the password is forgotten. You can provide the following temporary code: <strong>' . $temp_password . '</strong> to create a new password inside application The KidSelf.</html>';

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

}

