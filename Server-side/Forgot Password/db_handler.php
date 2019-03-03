<?php

class DBHandler{

 private $conn;
 
    //Constructor
    function __construct()
    {
        require_once dirname(__FILE__) . '/config.php';
        require_once dirname(__FILE__) . '/db_connect.php';
        // opening db connection
        $db = new DbConnect();
        $this->conn = $db->connect();
    }

 public function changePassword($email, $password_parent){

    $sql = 'UPDATE parent SET password_parent = :password_parent WHERE email = :email';
    $query = $this -> conn -> prepare($sql);
    $query -> execute(array(':email' => $email, ':password_parent' => $password_parent));

    if ($query) {
        
        return true;

    } else {

        return false;
    }
 }

 public function passwordResetRequest($email){

    $random_string = substr(str_shuffle(str_repeat("0123456789abcdefghijklmnopqrstuvwxyz", 6)), 0, 6);
    $hash = $this->getHash($random_string);
    $encrypted_temp_password = $hash["encrypted"];
    $salt = $hash["salt"];

    $sql = "SELECT COUNT(*) from password_reset_request WHERE email = '" . $email . "';";
    $query = $this -> conn -> prepare($sql);
    $query -> execute();

    if($query){

        $row_count = $query -> fetch();

        if ($row_count == 0){


            $insert_sql = 'INSERT INTO password_reset_request SET email =:email,encrypted_temp_password =:encrypted_temp_password,
                    salt =:salt,created_at = :created_at';
            $insert_query = $this ->conn ->prepare($insert_sql);
            $insert_query->execute(array(':email' => $email, ':encrypted_temp_password' => $encrypted_temp_password, 
            ':salt' => $salt, ':created_at' => date("Y-m-d H:i:s")));

            if ($insert_query) {

                $user["email"] = $email;
                $user["temp_password"] = $random_string;

                return $user;

            } else {
                return false;
            }


        } else {

            $update_sql = 'UPDATE password_reset_request SET email =:email, encrypted_temp_password =:encrypted_temp_password,
                    salt =:salt, created_at = :created_at';
            $update_query = $this -> conn -> prepare($update_sql);
            $update_query -> execute(array(':email' => $email, ':encrypted_temp_password' => $encrypted_temp_password, 
            ':salt' => $salt, ':created_at' => date("Y-m-d H:i:s")));

            if ($update_query) {
        
                $user["email"] = $email;
                $user["temp_password"] = $random_string;
                return $user;

            } else {

                return false;
            }}
    } else {
        return false;
    }
 }

 public function resetPassword($email,$code,$password){


    $sql = 'SELECT * FROM password_reset_request WHERE email = :email';
    $query = $this -> conn -> prepare($sql);
    $query -> execute(array(':email' => $email));
    $data = $query -> fetchObject();
    $salt = $data -> salt;
    $db_encrypted_temp_password = $data -> encrypted_temp_password;

    if ($this -> verifyHash($code.$salt,$db_encrypted_temp_password) ) {

        $old = new DateTime($data -> created_at);
        $now = new DateTime(date("Y-m-d H:i:s"));
        $diff = $now->getTimestamp() - $old->getTimestamp();
        
        if($diff < 120) {

            return $this -> changePassword($email, $password);

        } else {

            false;
        }
    } else {
        return false;
    }

 }

 public function getHash($password) {

     $salt = sha1(rand());
     $salt = substr($salt, 0, 10);
     $encrypted = password_hash($password.$salt, PASSWORD_DEFAULT);
     $hash = array("salt" => $salt, "encrypted" => $encrypted);

     return $hash;

}

 public function verifyHash($password, $hash) {
    return password_verify ($password, $hash);
}

}




