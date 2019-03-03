<?php
 
error_reporting(-1);
ini_set('display_errors', 'On');
 
require_once '../include/db_handler.php';
require_once '../libs/gcm/gcm.php';
require '.././libs/Slim/Slim.php';

\Slim\Slim::registerAutoloader();

$app = new \Slim\Slim();

// User id from db - Global Variable
$code = NULL;
 
/*
 * URL: /send
 * Method: POST
 * parameters: id, message
 * */
 
//This is used to send message on the chat room
$app->post('/send', function () use ($app) {
 
    //Verifying required parameters
    verifyRequiredParams(array('code_user', 'code_family', 'message'));
 
    //Getting request parameters
    $code_user = $app->request()->post('code_user');
    $message = $app->request()->post('message');
    $code_family = $app->request()->post('code_family');
 
    //Creating a gcm object
    $gcm = new GCM();
 
    //Creating db object
    $db = new DbHandler();
 
    //Creating response array
    $response = array();

    $getname = $db->getName($code_user);
    while($rowName = mysqli_fetch_array($getname)){
    $name=$rowName['name'];
    }
 
    //Creating an array containing message data
    $pushdata = array();
    //Adding title which would be the username
    $pushdata['title'] = $name;
    //Adding the message to be sent
    $pushdata['message'] = $message;
    //Adding user id to identify the user who sent the message
    $pushdata['code_user']=$code_user;
    //Adding user id to identify the family
    $pushdata['code_family']=$code_family;
 
    //If message is successfully added to database
    if ($db->addMessage($code_user, $code_family, $message)) {
        //Sending push notification with gcm object
        $gcm->sendMessage($db->getRegistrationTokens($code_user), $pushdata);
        $response['error'] = false;
        $response['message'] = "Message is sent";
    } else {
        $response['error'] = true;
        $response['message'] = "Coud not send message";
    }
    echoResponse(200, $response);
});
 
/*
 * URL: /storegcmtoken/:id
 * Method: PUT
 * Parameters: token
 * */
 
//This will store the gcm token to the database
$app->put('/storegcmtoken/:id', function ($code_user) use ($app) {

    verifyRequiredParams(array('token'));
    $token = $app->request()->put('token');
    $db = new DbHandler();
    $response = array();
    if ($db->storeGCMToken($code_user, $token)) {
        $response['error'] = false;
        $response['message'] = "token stored";
    } else {
        $response['error'] = true;
        $response['message'] = "Could not store token";
    }
    echoResponse(200, $response);
});
 
/*
 * URL: /messages
 * Method: GET
 * */
 
//This will fetch all the messages available on the database to display on the thread
$app->get('/messages/:id', function ($code_family) use ($app){
    $db = new DbHandler();
    $messages = $db->getMessages($code_family);
    $response = array();
    $response['error']=false;
    $response['messages'] = array();
    while($row = mysqli_fetch_array($messages)){
        $temp = array();
        $temp['message_id']=$row['message_id'];
        $temp['message']=$row['message'];
        $temp['code_user']=$row['code_user'];
        $temp['created_on']=$row['created_on'];
        $temp['name']=$row['name'];
        $temp['code_family']=$row['code_family'];
        array_push($response['messages'],$temp);
    }
    echoResponse(200,$response);
});
 
 
//Function to display the response in browser
function echoResponse($status_code, $response)
{
    $app = \Slim\Slim::getInstance();
    // Http response code
    $app->status($status_code);
    // setting response content type to json
    $app->contentType('application/json');
    echo json_encode($response);
}
 
 
//Function to verify required parameters
function verifyRequiredParams($required_fields)
{
    $error = false;
    $error_fields = "";
    $request_params = $_REQUEST;
    // Handling PUT request params
    if ($_SERVER['REQUEST_METHOD'] == 'PUT') {
        $app = \Slim\Slim::getInstance();
        parse_str($app->request()->getBody(), $request_params);
    }
    foreach ($required_fields as $field) {
        if (!isset($request_params[$field]) || strlen(trim($request_params[$field])) <= 0) {
            $error = true;
            $error_fields .= $field . ', ';
        }
    }
 
    if ($error) {
        // Required field(s) are missing or empty
        // echo error json and stop the app
        $response = array();
        $app = \Slim\Slim::getInstance();
        $response["error"] = true;
        $response["message"] = 'Required field(s) ' . substr($error_fields, 0, -2) . ' is missing or empty';
        echoResponse(400, $response);
        $app->stop();
    }
}
 
$app->run();