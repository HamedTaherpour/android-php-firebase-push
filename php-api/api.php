<!--
    Author: Hamed Taherpour
    Created: 12/11/2019
-->
<?php

define( 'API_ACCESS_KEY', '--firebase panel/Cloud Messaging/Server key--' );

$fcmMsg = array(
	'data' => $_POST['json']
);

$fcmFields = array(
	'to' => '/topics/all_user',
	'priority' => 'high',
	'data' => $fcmMsg
);

$headers = array(
	'Authorization: key=' . API_ACCESS_KEY,
	'Content-Type: application/json',
	'Charset: UTF-8'
);

$ch = curl_init();
curl_setopt( $ch,CURLOPT_URL, 'https://fcm.googleapis.com/fcm/send' );
curl_setopt( $ch,CURLOPT_POST, true );
curl_setopt( $ch,CURLOPT_HTTPHEADER, $headers );
curl_setopt( $ch,CURLOPT_RETURNTRANSFER, true );
curl_setopt( $ch,CURLOPT_SSL_VERIFYPEER, false );
curl_setopt( $ch,CURLOPT_POSTFIELDS, json_encode( $fcmFields ) );
$result = curl_exec($ch );
curl_close( $ch );
echo $result . "\n\n";
?>